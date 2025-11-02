package com.amuzil.av3.api.magus.radix;

import com.amuzil.av3.api.magus.condition.Condition;

import java.util.List;


public class RadixBranch {
    // Class that represents a valid condition path leading from a previous Node and stores the Condition(s)

    public ConditionPath path;
    public Node next;

    public RadixBranch(ConditionPath path) {
        this(path, new Node(true));
    }

    public RadixBranch(ConditionPath path, Node next) {
        this.path = path;
        this.next = next;
     }

     public List<Condition> conditions() {
        return path.conditions;
     }

    @Override
    public String toString() {
        return "RadixBranch[Conditions=" + path + "]";
    }
}
