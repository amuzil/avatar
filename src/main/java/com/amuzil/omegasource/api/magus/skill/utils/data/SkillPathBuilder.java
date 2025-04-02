package com.amuzil.omegasource.api.magus.skill.utils.data;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.form.ActiveForm;

import java.util.LinkedList;
import java.util.List;

public class SkillPathBuilder extends PathBuilder {

    private List<ActiveForm> activeForms;
    public static SkillPathBuilder instance;

    public static SkillPathBuilder getInstance() {
        if (instance == null)
            instance = new SkillPathBuilder();
        instance.reset();
        return instance;
    }

    public SkillPathBuilder addForm(ActiveForm form) {
        this.activeForms.add(form);
        return this;
    }

    public FormPath build() {
        FormPath path = new FormPath(new LinkedList<>(activeForms));
        this.reset();
        return path;
    }

    public void reset() {
        if (activeForms == null)
            activeForms = new LinkedList<>();

        this.activeForms.clear();
    }


    public static boolean checkForms(List<ActiveForm> formsFirst, List<ActiveForm> formsSecond) {
        if (formsSecond.size() != formsFirst.size())
            return false;
        if (formsFirst.isEmpty())
            return false;
        for (int i = 0; i < formsFirst.size(); i++) {
            if (!formsFirst.get(i).equals(formsSecond.get(i)))
                return false;
        }
        return true;
    }
}
