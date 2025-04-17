package com.amuzil.omegasource.api.magus.skill.utils.data;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.form.ActiveForm;

import java.util.LinkedList;
import java.util.List;

public class SkillPathBuilder extends PathBuilder {

    private List<ActiveForm> complexForms;
    private List<ActiveForm> simpleForms;
    public static SkillPathBuilder instance;

    public static SkillPathBuilder getInstance() {
        if (instance == null)
            instance = new SkillPathBuilder();
        instance.reset();
        return instance;
    }

    public SkillPathBuilder complex(ActiveForm form) {
        this.complexForms.add(form);
        return this;
    }

    public SkillPathBuilder simple(ActiveForm form) {
        this.simpleForms.add(form);
        return this;
    }

    public FormPath build() {
        // Need to copy the list
        LinkedList<ActiveForm> simple = simpleForms.isEmpty() ? new LinkedList<>() : new LinkedList<>(simpleForms);
        LinkedList<ActiveForm> complex = complexForms.isEmpty() ? new LinkedList<>() : new LinkedList<>(complexForms);
        FormPath path = new FormPath(simple, complex);
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

    public static boolean checkForms(List<ActiveForm> activeForms, List<ActiveForm> skillForms) {
        if (skillForms.size() != activeForms.size())
            return false;
        if (activeForms.isEmpty())
            return false;
        for (int i = 0; i < activeForms.size(); i++) {
            if (!activeForms.get(i).equals(skillForms.get(i)))
                return false;
        }
        return true;
    }

    public static boolean checkAllForms(FormPath first, FormPath second) {
        return checkForms(first.complex(), second.complex()) || checkForms(first.simple(), second.simple());
    }
}
