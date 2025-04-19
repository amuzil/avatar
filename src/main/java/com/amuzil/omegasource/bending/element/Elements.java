package com.amuzil.omegasource.bending.element;

import com.amuzil.omegasource.bending.element.fire.Firebending;

import java.util.ArrayList;
import java.util.List;


public class Elements {
    public static final List<Element> LIST = new ArrayList<>();

    public static final Element AIR = new Element(Element.Art.AIR);
    public static final Element WATER = new Element(Element.Art.WATER);
    public static final Element EARTH = new Element(Element.Art.EARTH);
    public static final Element FIRE = new Element(Element.Art.FIRE);

    public static Element fromName(String name) {
        return LIST.stream().filter(element -> element.name().equals(name)).findFirst().get();
    }

    public static Element fromArt(Element.Art art) {
        return LIST.stream().filter(element -> element.type().equals(art)).findFirst().get();
    }

    public static void init() {
        Firebending.init();
    }
}
