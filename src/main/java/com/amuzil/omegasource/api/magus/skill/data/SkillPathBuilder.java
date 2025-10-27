package com.amuzil.omegasource.api.magus.skill.data;

import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.form.ActiveForm;

import java.util.LinkedList;
import java.util.List;

public class SkillPathBuilder {

    private List<Form> forms;
    public static SkillPathBuilder instance;

    public static SkillPathBuilder getInstance() {
        if (instance == null)
            instance = new SkillPathBuilder();
        instance.reset();
        return instance;
    }

    public SkillPathBuilder add(Form form) {
        this.forms.add(form);
        return this;
    }

    public List<Form> build() {
        // Need to copy the list
        LinkedList<Form> list = forms == null || forms.isEmpty() ? new LinkedList<>() : new LinkedList<>(forms);
        this.reset();
        return list;
    }

    public void reset() {
        if (forms == null)
            forms = new LinkedList<>();

        this.forms.clear();
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
        return checkForms(first.complex(), second.complex());
    }
}
