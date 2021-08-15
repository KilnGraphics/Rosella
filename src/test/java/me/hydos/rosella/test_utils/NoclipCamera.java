package me.hydos.rosella.test_utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Very basic camera to view scenes from different angles
 */
public class NoclipCamera {

    private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
    private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
    private double lastUpdateTime = 0;
    public float deltaTime = 0;
    public Matrix4f viewMatrix = new Matrix4f();
    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();

    public boolean forwardMotion;
    public boolean backMotion;
    public boolean upMotion;
    public boolean downMotion;
    public boolean leftMotion;
    public boolean rightMotion;
    public boolean rotateLeftMotion;
    public boolean rotateRightMotion;

    public void setup(long pWindow) {
        GLFW.glfwSetKeyCallback(pWindow, (window1, key, scancode, keyAction, mods) -> {
            boolean movement = keyAction == 1 || keyAction == 2;
            switch (key) {
                case GLFW.GLFW_KEY_W -> forwardMotion = movement;
                case GLFW.GLFW_KEY_S -> backMotion = movement;
                case GLFW.GLFW_KEY_A -> leftMotion = movement;
                case GLFW.GLFW_KEY_D -> rightMotion = movement;

                case GLFW.GLFW_KEY_LEFT -> rotateLeftMotion = movement;
                case GLFW.GLFW_KEY_RIGHT -> rotateRightMotion = movement;
                case GLFW.GLFW_KEY_UP -> upMotion = movement;
                case GLFW.GLFW_KEY_DOWN -> downMotion = movement;
            }
        });
    }

    public void update() {
        double lastUpdateTime = GLFW.glfwGetTime();
        this.deltaTime = (float) (lastUpdateTime - this.lastUpdateTime);
        this.lastUpdateTime = lastUpdateTime;
        updateMovement();

        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), X_AXIS);
        viewMatrix.rotate((float) Math.toRadians(rotation.y), Y_AXIS);
        viewMatrix.translate(position.x, position.y, position.z);
    }

    /**
     * Its so ugly it gets its own method to be separate from the good code
     */
    private void updateMovement() {
        float deltaMovement = 50 * deltaTime;
        float deltaRotation = 20 * deltaTime;

        if (forwardMotion) {
            position.add(0, 0f, -deltaMovement);
        }
        if (backMotion) {
            position.add(0f, 0f, deltaMovement);
        }
        if (leftMotion) {
            position.add(-deltaMovement, 0f, 0f);
        }
        if (rightMotion) {
            position.add(deltaMovement, 0f, 0f);
        }
        if (rotateLeftMotion) {
            rotation.add(0, deltaRotation, 0);
        }
        if (rotateRightMotion) {
            rotation.add(0, -deltaRotation, 0);
        }
        if (upMotion) {
            position.add(0, -deltaMovement, 0);
        }
        if (downMotion) {
            position.add(0, deltaMovement, 0f);
        }
    }
}
