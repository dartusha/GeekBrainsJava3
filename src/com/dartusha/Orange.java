package com.dartusha;

public class Orange extends Fruit {
    final float weight=1.5f;
    final String name="Апельсин";

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public String getName() {
        return name;
    }
}
