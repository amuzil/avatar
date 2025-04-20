package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.bending.element.fire.Firebending;

import java.util.HashMap;


public class Elements {
    public static final HashMap<String, Element> ALL_FOUR = new HashMap<>();

    public static final Element AIR = new Element(Element.Art.AIR);
    public static final Element WATER = new Element(Element.Art.WATER);
    public static final Element EARTH = new Element(Element.Art.EARTH);
    public static final Element FIRE = new Element(Element.Art.FIRE);

    public static Element get(String name) {
        return ALL_FOUR.get(name);
    }

    public static Element get(Element.Art art) {
        return ALL_FOUR.get(art.toString());
    }

    public static void init() {
        Firebending.init();
    }
}
