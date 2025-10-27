package com.amuzil.omegasource.api.magus.tree;

import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.skill.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkillTreeNode {
    private HashMap<Form, SkillTreeNode> children = new HashMap<>();
    private Skill skill;

    public SkillTreeNode(Skill skill) {
        this.skill = skill;
    }

    public TreeResult ExecutePath(List<Form> forms) {
        if(skill != null) {
            return new TreeResult(TreeResult.ResultType.SKILL_FOUND, skill);
        }
        if(children.size() == 0) {
            return new TreeResult(TreeResult.ResultType.TERMINAL_NODE, null);
        }
        if(forms.size() > 0) {
            SkillTreeNode branch = children.get(forms.get(0));

            if(branch == null)
                return new TreeResult(TreeResult.ResultType.TERMINAL_NODE, null);

            return branch.ExecutePath(forms.subList(1, forms.size()));
        }
        return new TreeResult(TreeResult.ResultType.SKILL_NOT_FOUND, null);
    }

    public void addChild(List<Form> formPath, Skill skill) {
        if(formPath.isEmpty()) {
            this.skill = skill;
        } else {
            Form currentForm = formPath.get(0);
            List<Form> newList = formPath.size() > 1 ? formPath.subList(1, formPath.size()) : new ArrayList<>();
            // add a new node.
            SkillTreeNode branch = children.get(currentForm);
            if(branch != null) { // branch exists already, recurse.
                branch.addChild(newList, skill);
            } else { // create branch then recurse.
                SkillTreeNode newNode = new SkillTreeNode(null);
                newNode.addChild(newList, skill);
                children.put(currentForm, newNode);
            }
        }
    }
}
