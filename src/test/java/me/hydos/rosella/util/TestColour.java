package me.hydos.rosella.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestColour {

    @Test
    void testAsFloats() {
        Colour c = new Colour(1.0f, 1.0f, 1.0f, 1.0f);
        assertEquals(1.0f, c.rAsFloat(), 1e-6f);
        assertEquals(1.0f, c.gAsFloat(), 1e-6f);
        assertEquals(1.0f, c.bAsFloat(), 1e-6f);

        c = new Colour(0.0f, 0.0f, 0.0f, 0.0f);
        assertEquals(0.0f, c.rAsFloat(), 1e-6f);
        assertEquals(0.0f, c.gAsFloat(), 1e-6f);
        assertEquals(0.0f, c.bAsFloat(), 1e-6f);

        c = new Colour(0.5f, 0.5f, 0.5f, 0.5f);
        assertEquals(0.5f, c.rAsFloat(), 1e-2f);
        assertEquals(0.5f, c.gAsFloat(), 1e-2f);
        assertEquals(0.5f, c.bAsFloat(), 1e-2f);
    }
}
