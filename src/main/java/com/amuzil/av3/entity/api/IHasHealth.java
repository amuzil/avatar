package com.amuzil.av3.entity.api;

// Used primarily for constructs, shields, e.t.c. Living mobs with health are a separate category.
public interface IHasHealth {

        /**
        * Gets the current health of this entity. Should be non-negative.
        */
        float health();

        /**
        * Sets the current health of this entity. Should be non-negative.
        */
        void health(float health);

        /**
        * Gets the maximum health of this entity. Should be positive.
        */
        float maxHealth();

        /**
         * Sets the maximum health of this entity. Should be positive.
         */
        void maxHealth(float maxHealth);

        void hurt(float damage);

        void heal(float amount);

        boolean noHealth();
}
