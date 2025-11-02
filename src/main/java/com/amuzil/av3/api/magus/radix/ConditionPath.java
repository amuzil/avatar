package com.amuzil.av3.api.magus.radix;

import com.amuzil.av3.api.magus.condition.Condition;

import java.util.LinkedList;
import java.util.List;


public class ConditionPath {

    public List<Condition> conditions;
    private boolean isDirty;

    public ConditionPath() {
        conditions = new LinkedList<>();
    }

    public ConditionPath(List<Condition> activatedConditions) {
        conditions = new LinkedList<>(activatedConditions);
    }

    public void addStep(Condition activatedCondition) {
        if (activatedCondition != null && conditions != null) {
            conditions.add(activatedCondition);
        }
    }

    @Override
    public String toString() {
        return conditions.toString();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Condition cond : conditions) {
            hash += cond.hashCode();
        }
        // Hashing involves size of the list, and then an arbitrarily large prime number; ex: 29.
        hash = hash % (conditions.size() * 29);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        // This needs to be overridden to *ignore* modifiers. It needs to only check that each condition in each path matches
        // (using custom defined hashcodes/equals method for each).
        if (!(obj instanceof ConditionPath)) return false;

        return hashCode() == obj.hashCode() && conditions.size() == ((ConditionPath) obj).conditions.size();
    }
}
