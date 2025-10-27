package com.amuzil.omegasource.api.magus.tree;

import com.amuzil.omegasource.api.magus.skill.Skill;

import java.util.Optional;

public class TreeResult {
    public ResultType resultType;
    public Skill skill;

    public TreeResult(ResultType type, Skill skill) {
        this.resultType = type;
        this.skill = skill;
    }

    public enum ResultType {
        SKILL_FOUND,
        SKILL_NOT_FOUND,
        TERMINAL_NODE
    }
}
