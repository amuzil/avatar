package com.amuzil.omegasource.api.magus.skill.utils.data;

import com.amuzil.omegasource.api.magus.skill.FormPath;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillActive;
import com.amuzil.omegasource.bending.form.ActiveForm;

import java.util.LinkedList;
import java.util.List;

public class SkillPathBuilder extends PathBuilder {

    private List<ActiveForm> simpleForms;
    private List<ActiveForm> complexForms;
    public static SkillPathBuilder instance;

    public static SkillPathBuilder getInstance() {
        if (instance == null)
            instance = new SkillPathBuilder();
        instance.reset();
        return instance;
    }

    public SkillPathBuilder simpleForm(ActiveForm form) {
        this.simpleForms.add(form);
        return this;
    }

    public SkillPathBuilder complexForm(ActiveForm form) {
        this.complexForms.add(form);
        return this;
    }

    public FormPath build() {
        FormPath path = new FormPath(simpleForms, complexForms);
        this.reset();
        return path;
    }

    public void reset() {
        if (simpleForms == null)
            simpleForms = new LinkedList<>();
        if (complexForms == null)
            complexForms = new LinkedList<>();

        this.simpleForms.clear();
        this.complexForms.clear();
    }

}
