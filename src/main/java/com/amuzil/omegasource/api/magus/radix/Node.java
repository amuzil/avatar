package com.amuzil.omegasource.api.magus.radix;

import com.amuzil.omegasource.api.magus.condition.Condition;
import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Node {
    //This needs to be changed to <Condition, Node>
    private Map<Condition, Node> children;
    // Need to figure this out...
    private Pair<Condition, Node> parent;
    private final Consumer<RadixTree> onEnter;
    private final Consumer<RadixTree> onLeave;
    private final Consumer<RadixTree> onTerminate;
    private final Condition terminateCondition;
    public final HashMap<Condition, RadixBranch> branches;
    public boolean isComplete;

    /**
     * @param children           If a condition is fulfilled, the active node moves down to the mapped child node
     * @param onEnter            Called when the active node is moved down from the parent node to this node
     * @param onLeave            Called when the active node is moved down from this node to a child node
     * @param onTerminate        Called when the active node is moved up to the root node because either all children's conditions have expired or the terminate condition has been fulfilled
     * @param terminateCondition If this condition is fulfilled, the active node will be terminated. If it expires, nothing special happens. It doesn't have to expire for the branch to terminate
     */
    public Node(
            Pair<Condition, Node> parent,
            Map<Condition, Node> children,
            Consumer<RadixTree> onEnter,
            Consumer<RadixTree> onLeave,
            Consumer<RadixTree> onTerminate,
            Condition terminateCondition
    ) {
        this.parent = parent;
        this.children = children;
        this.onEnter = onEnter;
        this.onLeave = onLeave;
        this.onTerminate = onTerminate;
        this.terminateCondition = terminateCondition;
        branches = new HashMap<>();
        this.isComplete = false; // Temporary
    }

    public Node (boolean isComplete) {
        this.onEnter = null;
        this.onLeave = null;
        this.onTerminate = null;
        this.terminateCondition = null;
        this.branches = new HashMap<>();
        this.isComplete = isComplete;
    }

    public RadixBranch getTransition(Condition transitionCondition) {
        return branches.get(transitionCondition);
    }

    public void addCondition(ConditionPath conditionPath, Node next) {
        branches.put(conditionPath.conditions.get(0), new RadixBranch(conditionPath, next));
    }

    public int totalConditions() {
        return branches.size();
    }

    public Set<Condition> getImmediateBranches() {
        return branches.keySet();
    }

    @Override
    public String toString() {
        return "Node[ isComplete=" + isComplete + ", branches=" + branches + "]";
    }

    // ---------- Cali's RadixTree Impl ----------

    public Map<Condition, Node> children() {
        return children;
    }

    public Pair<Condition, Node> parent() {
        return this.parent;
    }

    public Map<Condition, Node> getImmediateChildren() {
        return children().entrySet().stream()
                .filter(entry ->
                    entry.getValue().parent().getSecond().equals(this)
                            && entry.getValue().parent().getSecond()
                            .terminateCondition().equals(this.terminateCondition())) // Ensure the child's parent is the current node
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Consumer<RadixTree> onTerminate() {
        return onTerminate;
    }

    public Condition terminateCondition() {
        return terminateCondition;
    }
}