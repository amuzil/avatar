package com.amuzil.magus.radix;

import com.amuzil.magus.condition.Condition;
import com.amuzil.av3.bending.element.Element;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class RadixTree {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int NO_MISMATCH = -1;
    private final Node root;
    private Node active;
    private Condition lastActivated = null;
    private Element activeElement = null;
    private ConditionPath path;

    public RadixTree(Node root) {
        this.root = root;
        this.active = root;
    }

    public RadixTree() {
        this(new Node(false));
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void registerConditions(List<Condition> conditions) {
        for (Condition condition : conditions) {
            condition.registerRunnables();
        }
    }

    public Node getRoot() {
        return root;
    }

    private int getFirstMismatchCondition(List<Condition> conditions, List<Condition> edgeCondition) {
        int LENGTH = Math.min(conditions.size(), edgeCondition.size());
        for (int i = 1; i < LENGTH; i++) {
            if (!conditions.get(i).equals(edgeCondition.get(i))) {
                return i;
            }
        }
        return NO_MISMATCH;
    }

    public void activateAllConditions() {
        activateAllConditions(root, new ArrayList<>());
    }

    private void activateAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) for (Condition condition : result)
            condition.registerRunnables();

        for (RadixBranch branch : current.branches.values())
            activateAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    public void deactivateAllConditions() {
        deactivateAllConditions(root, new ArrayList<>());
    }

    private void deactivateAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) for (Condition condition : result)
            condition.unregister();

        for (RadixBranch branch : current.branches.values())
            deactivateAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    public void resetTree() {
        deactivateAllConditions();
        for (Condition condition : root.getImmediateBranches())
            condition.registerRunnables();
//        setActive(root);
    }

    // Helpful method to debug and to see all the conditions
    public void printAllConditions() {
        printAllConditions(root, new ArrayList<>());
    }

    private void printAllConditions(Node current, List<Condition> result) {
        if (current.isComplete) System.out.println("Condition: " + result);

        for (RadixBranch branch : current.branches.values())
            printAllConditions(branch.next, Stream.concat(result.stream(), branch.path.conditions.stream()).toList());
    }

    // Helpful method to debug and to see all the branches in tree format
    public void printAllBranches() {
        printAllBranches(root, "");
    }

    private void printAllBranches(Node current, String indent) {
        int lastValue = current.totalConditions() - 1;
        int i = 0;
        for (RadixBranch branch : current.branches.values()) {
            if (i == lastValue) System.out.println(indent.replace("+", "L") + branch.path);
            else System.out.println(indent.replace("+", "|") + branch.path);
            int length1 = indent.length() / 2 == 0 ? 4 : indent.length() / 2;
            int length2 = branch.path.toString().length() / 3;
            String lineIndent = new String(new char[length2]).replace("\0", "─");
            String oldIndent, newIndent;
            if (i != lastValue) {
                length1 = length1 - 4;
                oldIndent = new String(new char[length1]).replace("\0", " ");
                newIndent = "    |" + oldIndent + "+" + lineIndent + "─>";
            } else {
                oldIndent = new String(new char[length1]).replace("\0", " ");
                newIndent = oldIndent + "+" + lineIndent + "─>";
            }
            i++;
            printAllBranches(branch.next, newIndent);
        }
    }

    private List<Condition> prioritizeConditions(List<Condition> conditions) {
        List<Condition> prioritizedConditions = new ArrayList<>();
//        for (Condition condition : conditions) {
//            if (condition instanceof MultiClientCondition) {
//                prioritizedConditions.add(condition);
//                break;
//            }
//        }
        return prioritizedConditions.isEmpty() ? conditions : prioritizedConditions;
    }

    // Add conditions to RadixTree - O(n)
    public void insert(List<Condition> conditions) {
        Node current = active;
        int currIndex = 0;

        while (currIndex < conditions.size()) {
            Condition transitionCondition = conditions.get(currIndex);
            RadixBranch currentBranch = current.getTransition(transitionCondition);
            // Iterate forward as we move through the conditions and either sprout a node or move down an existing node
            List<Condition> currCondition = conditions.subList(currIndex, conditions.size());

            // There is no associated branch with the first condition of the current path
            // so simply add the rest of the conditions and finish
            if (currentBranch == null) {
                current.branches.put(transitionCondition, new RadixBranch(new ConditionPath(currCondition)));
                break;
            }

            int splitIndex = getFirstMismatchCondition(currCondition, currentBranch.path.conditions); // uses equals
            if (splitIndex == NO_MISMATCH) {
                // The branch and leftover conditions are the same length
                // so finish and update the next node as a complete node
                if (currCondition.size() == currentBranch.path.conditions.size()) {
                    currentBranch.next.isComplete = true;
                    break;
                } else if (currCondition.size() < currentBranch.path.conditions.size()) {
                    // The leftover condition is a prefix to the edge string, so split
                    List<Condition> suffix = currentBranch.path.conditions.subList(currCondition.size() - 1, currCondition.size());
                    currentBranch.path.conditions = currCondition;
                    Node newNext = new Node(true);
                    Node afterNewNext = currentBranch.next;
                    currentBranch.next = newNext;

                    newNext.addCondition(new ConditionPath(suffix), afterNewNext);
                    break;
                } else { // currStr.length() > currentEdge.label.length()
                    // There are leftover conditions after a perfect match
                    splitIndex = currentBranch.path.conditions.size();
                }
            } else {
                // The leftover conditions and branch conditions differed, so split at point
                List<Condition> suffix = currentBranch.path.conditions.subList(splitIndex, currentBranch.path.conditions.size());
                currentBranch.path.conditions = currentBranch.path.conditions.subList(0, splitIndex);
                Node prevNext = currentBranch.next;
                currentBranch.next = new Node(false);
                currentBranch.next.addCondition(new ConditionPath(suffix), prevNext);
            }

            // Traverse the tree
            current = currentBranch.next;
            currIndex += splitIndex;
        }

        // Only register immediate children conditions
        resetTree();
    }

    // Returns matched condition path if found and null if not found - O(n)
    public List<Condition> search(List<Condition> conditions) {
        conditions = prioritizeConditions(conditions);
        List<Condition> ret = null;
        Node current = active;
        int currIndex = 0;
        while (currIndex < conditions.size()) {
            Condition currentCondition = conditions.get(currIndex);
            RadixBranch branch = current.getTransition(currentCondition); // uses hashcode
//            RadixBranch branch = current.getMatchedPath(currentCondition);
            if (branch == null) return null;

            List<Condition> currSubCondition = conditions.subList(currIndex, conditions.size());
            if (!Condition.startsWith(currSubCondition, branch.path.conditions)) return null; // uses equals

            currIndex += branch.path.conditions.size();
            current = branch.next;
            if (ret == null) ret = new ArrayList<>();
            ret = Stream.concat(ret.stream(), branch.path.conditions.stream()).toList();
        }
        return ret;
    }

    public void setOwner(Entity entity) {
//        this.owner = entity;
    }

    public ConditionPath getPath() {
        return this.path;
    }

    // Menu = radial menu or a HUD. Other activation types are self-explanatory.
    public enum ActivationType {
        MENU(0), HOTKEY(1), MULTIKEY(2), VR(3);

        final int priority;

        ActivationType(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return this.priority;
        }
    }

    // Essentially which input module to use.
    // Used for VR, multikey, and hotkey activation types.
    public enum InputType {
        KEYBOARD_MOUSE, MOUSE_MOTION, VR
    }
}