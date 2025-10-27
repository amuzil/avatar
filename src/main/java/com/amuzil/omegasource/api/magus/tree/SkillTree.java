package com.amuzil.omegasource.api.magus.tree;

import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.capability.Bender;

import java.util.HashMap;
import java.util.List;

public class SkillTree {
    private static HashMap<Element, SkillTreeNode> branches = new HashMap<>(); // TODO: discuss selection type refactor

    public static TreeResult ExecutePath(Bender bender, List<Form> formPath) {
        return branches.get(bender.getElement()).ExecutePath(formPath);
    }

    public static void RegisterSkill(Element element, /* BendingSelection.Target targetType, */List<Form> formPath, Skill skill) {
        SkillTreeNode root = branches.get(element);

        if(root == null) {
            root = new SkillTreeNode(null);
            branches.put(element, root);
        }

        root.addChild(formPath, skill);
    }

    public static void clear() {
        branches = new HashMap<>();
    }
}
