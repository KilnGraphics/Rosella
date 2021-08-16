package me.hydos.rosella.test_utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Very basic camera to view scenes from different angles
 */
public class NoclipCamera {

    public static final boolean PREFER_RAW_INPUT = true;
    public static float moveSpeed = 50.0f;
    public static float mouseSensitivity = .2f;

    private static final float FULL_ROTATION = (float) (Math.PI * 2.0);
    private static final Vector3f ZERO = new Vector3f();

    private double lastUpdateTime = 0;
    public float deltaTime = 0;

    public Matrix4f viewMatrix = new Matrix4f();
    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public Vector3f posMotion = new Vector3f();

    private boolean hideCursor;

    public void setup(long pWindow) {
        GLFW.glfwSetKeyCallback(pWindow, (pWindow_, key, scancode, keyAction, mods) -> {
            float motionModifier = 0.0f;
            if (keyAction == GLFW.GLFW_PRESS) {
                motionModifier = 1.0f;
            } else if (keyAction == GLFW.GLFW_RELEASE) {
                motionModifier = -1.0f;
            }

            switch (key) {
                case GLFW.GLFW_KEY_W -> posMotion.z += motionModifier;
                case GLFW.GLFW_KEY_S -> posMotion.z -= motionModifier;
                case GLFW.GLFW_KEY_A -> posMotion.x += motionModifier;
                case GLFW.GLFW_KEY_D -> posMotion.x -= motionModifier;
                // TODO: figure out why these need to be reversed (is it the proj matrix?)
                case GLFW.GLFW_KEY_SPACE -> posMotion.y -= motionModifier;
                case GLFW.GLFW_KEY_LEFT_SHIFT -> posMotion.y += motionModifier;

                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (keyAction == GLFW.GLFW_PRESS) {
                        hideCursor = !hideCursor;
                        if (hideCursor) {
                            grabMouse(pWindow_);
                        } else {
                            releaseMouse(pWindow_);
                        }
                    }
                }
            }
        });

        if (PREFER_RAW_INPUT) {
            GLFW.glfwSetInputMode(pWindow, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.glfwRawMouseMotionSupported() ? 1 : 0);
        }

        GLFW.glfwSetCursorPosCallback(pWindow, (pWindow_, xPos, yPos) -> {
            rotation.x = (float) (yPos / (1000.0f / mouseSensitivity) * FULL_ROTATION);
            rotation.y = (float) (xPos / (1000.0f / mouseSensitivity) * FULL_ROTATION);
            clampRotation();
        });
    }

    private void grabMouse(long pWindow) {
        GLFW.glfwSetInputMode(pWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    private void releaseMouse(long pWindow) {
        GLFW.glfwSetInputMode(pWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    private void clampRotation() {
        rotation.x %= FULL_ROTATION;
        rotation.y %= FULL_ROTATION;
        rotation.z %= FULL_ROTATION;
    }

    public void updateMatrix() {
        updateTime();
        updateMovement();

        viewMatrix.identity();
        viewMatrix.rotateXYZ(rotation);
        viewMatrix.translate(position.x, position.y, position.z);
    }

    public void updateTime() {
        double lastUpdateTime = GLFW.glfwGetTime();
        this.deltaTime = (float) (lastUpdateTime - this.lastUpdateTime);
        this.lastUpdateTime = lastUpdateTime;
    }

    private void updateMovement() {
        if (!posMotion.equals(ZERO)) {
            Vector3f modifiedMotion = new Vector3f();

            posMotion.normalize(moveSpeed * deltaTime, modifiedMotion);

            modifiedMotion.rotateX(-rotation.x);
            modifiedMotion.rotateY(-rotation.y);
            modifiedMotion.rotateZ(-rotation.z);

            position.add(modifiedMotion);
        }
    }

}
