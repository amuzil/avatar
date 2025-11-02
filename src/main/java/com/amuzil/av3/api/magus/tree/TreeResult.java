package com.amuzil.av3.api.magus.tree;

import com.amuzil.av3.api.magus.skill.Skill;

public class TreeResult {
    public ResultType resultType;
    public Skill skill;

    public TreeResult(ResultType type, Skill skill) {
        this.resultType = type;
        this.skill = skill;
    }

    public enum ResultType {
        SKILL_FOUND,
        SKILL_FOUND_TERMINAL,
        SKILL_NOT_FOUND,
        TERMINAL_NODE
    }
}
