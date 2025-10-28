package com.amuzil.omegasource.api.magus.skill.data;

import com.amuzil.omegasource.api.magus.form.Form;

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
}
