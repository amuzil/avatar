package com.amuzil.av3.api.magus.condition;

import com.amuzil.av3.Avatar;

import java.util.List;


public abstract class Condition {

    public enum Result {
        SUCCESS,
        FAILURE,
        NONE
    }

    protected static final Runnable NO_OPERATION = () -> Avatar.LOGGER.debug("Result: No Operation");

    protected String name;
    protected boolean active = false;
    protected Runnable onSuccess;
    protected Runnable onFailure;

    public void registerRunnables(String name, Runnable onSuccess, Runnable onFailure) {
        this.name = name;
        //RadixTree.getLogger().info("Registering results");
//        if (this instanceof KeyPressCondition && ((KeyPressCondition) this).getKey() == 0)
//            Thread.dumpStack();
        //RadixTree.getLogger().info("Result: success");
        this.onSuccess = onSuccess;
        //            RadixTree.getLogger().info("Result: failure: " + getClass());
        //            Thread.dumpStack();
        this.onFailure = onFailure;
//        registerEntry();
    }

    public void registerRunnables() {}

    // Every Condition needs to call this in their constructor
    public void registerEntry() {
        ConditionRegistry.register(this);
    }

    public void unregister() {
        Avatar.LOGGER.debug("Unregistering condition:{}", getClass());
    }

    protected Runnable runOn(Result result) {
        return switch (result) {
            case SUCCESS -> onSuccess;
            case FAILURE -> onFailure;
            case NONE -> NO_OPERATION;
        };
    }

    public String name() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Change this for custom conditions/conditions you want registered in your own mod
    public String modID() {
        return Avatar.MOD_ID;
    }

    public void reset() {}

    public boolean getActiveStatus() {
        return active;
    }

    public static boolean startsWith(List<Condition> conditions, List<Condition> subConditions) {
        try {
            return conditions.subList(0, subConditions.size()).equals(subConditions);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[ " + name + " ]";
    }

    public Runnable onSuccess() {
        return this.onSuccess;
    }

    public Runnable onFailure() {
        return this.onFailure;
    }
}
