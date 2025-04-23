package org.nithish.vehiclesim.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class GameLoop {

    private long window;
    private CubeRenderer cubeRenderer;
    private long lastTime;
    private int frames;
    private double fps;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Set up GLFW error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // Create window
        window = glfwCreateWindow(800, 600, "Vehicle Sim - 3D Cube", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // Center window
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Initialize CubeRenderer
        cubeRenderer = new CubeRenderer();
        cubeRenderer.init();

        // Enable V-Sync
        glfwSwapInterval(1);
        glfwShowWindow(window);

        // Initialize FPS counter
        lastTime = System.nanoTime();
        frames = 0;
        fps = 0;
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            // Clear buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Update FPS
            long currentTime = System.nanoTime();
            frames++;
            if (currentTime - lastTime >= 1_000_000_000) {
                fps = frames;
                System.out.println("FPS: " + fps);
                frames = 0;
                lastTime = currentTime;
            }

            // Render cube
            cubeRenderer.render();

            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        cubeRenderer.cleanup();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void main(String[] args) {
        new GameLoop().run();
    }
}
