package com.dartusha;

public class Apple extends Fruit {
    final float weight=1.0f;
    final String name="Яблоко";

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public String getName() {
        return name;
    }
}
