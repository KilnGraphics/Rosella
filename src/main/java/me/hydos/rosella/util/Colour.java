package me.hydos.rosella.util;

public record Colour(int r, int g, int b, int a) {

    public Colour(float r, float g, float b, float a) {
        this((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
    }

    public float rAsFloat() {
        return r / 255F;
    }

    public float gAsFloat() {
        return g / 255F;
    }

    public float bAsFloat() {
        return b / 255F;
    }
}
