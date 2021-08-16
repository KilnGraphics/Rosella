package me.hydos.rosella.test_utils;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Very basic camera to view scenes from different angles
 */
public class NoclipCamera {

    private static final Vector3f EMPTY = new Vector3f();
    private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
    private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
    private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
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

    public boolean hideCursor;

    public void setup(long pWindow) {
        GLFW.glfwSetKeyCallback(pWindow, (window1, key, scancode, keyAction, mods) -> {
            boolean movement = keyAction == GLFW.GLFW_PRESS || keyAction == GLFW.GLFW_REPEAT;
            switch (key) {
                case GLFW.GLFW_KEY_W -> forwardMotion = movement;
                case GLFW.GLFW_KEY_S -> backMotion = movement;
                case GLFW.GLFW_KEY_A -> leftMotion = movement;
                case GLFW.GLFW_KEY_D -> rightMotion = movement;
                case GLFW.GLFW_KEY_SPACE -> upMotion = movement;
                case GLFW.GLFW_KEY_LEFT_SHIFT -> downMotion = movement;

                case GLFW.GLFW_KEY_LEFT -> rotateLeftMotion = movement;
                case GLFW.GLFW_KEY_RIGHT -> rotateRightMotion = movement;

                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (keyAction == GLFW.GLFW_PRESS) hideCursor = !hideCursor;
                }
            }
        });
    }

    public void update() {
        double lastUpdateTime = GLFW.glfwGetTime();
        this.deltaTime = (float) (lastUpdateTime - this.lastUpdateTime);
        this.lastUpdateTime = lastUpdateTime;
        updateMovement();

        viewMatrix.identity();
        viewMatrix.rotate(rotation.x, X_AXIS);
        viewMatrix.rotate(rotation.y, Y_AXIS);
        viewMatrix.translate(position.x, position.y, position.z);
    }

    /**
     * Its so ugly it gets its own method to be separate from the good code
     */
    private void updateMovement() {
        float deltaMovement = 50.0f * deltaTime;
        float deltaRotation = (float) Math.toRadians(50.0f * deltaTime);

        if (rotateLeftMotion) {
            rotation.add(0, -deltaRotation, 0);
            rotation.y %= 360.0f;
        }
        if (rotateRightMotion) {
            rotation.add(0, deltaRotation, 0);
            rotation.y %= 360.0f;
        }

        Vector3f motionVector = new Vector3f();

        if (forwardMotion) {
            motionVector.add(0, 0f, 1f);
        }
        if (backMotion) {
            motionVector.add(0f, 0f, -1f);
        }
        if (leftMotion) {
            motionVector.add(1f, 0f, 0f);
        }
        if (rightMotion) {
            motionVector.add(-1f, 0f, 0f);
        }

        if (!motionVector.equals(EMPTY)) {
            motionVector.normalize(deltaMovement);
        }

        motionVector.rotateX(-rotation.x);
        motionVector.rotateY(-rotation.y);
        motionVector.rotateZ(-rotation.z);

        // we normalize before we process y movement on purpose. it feels really unnatural otherwise.
        // TODO: figure out why the proj matrix requires these to be reversed
        if (upMotion) {
            motionVector.add(0, -deltaMovement, 0);
        }
        if (downMotion) {
            motionVector.add(0, deltaMovement, 0f);
        }

        position.add(motionVector);
    }


}
