// package org.nithish.vehiclesim.engine;

// import static org.lwjgl.opengl.GL11.*;
// import static org.lwjgl.opengl.GL30.*;

// import java.nio.FloatBuffer;
// import org.joml.Matrix4f;
// import org.joml.Vector3f;
// import org.lwjgl.opengl.GL;
// import org.lwjgl.system.MemoryStack;

// public class CubeRenderer {

//     private int vao, vbo;
//     private int shaderProgram;
//     private float[] vertices = {
//         -0.5f,
//         -0.5f,
//         -0.5f, // front face
//         0.5f,
//         -0.5f,
//         -0.5f,
//         0.5f,
//         0.5f,
//         -0.5f,
//         -0.5f,
//         0.5f,
//         -0.5f,
//         -0.5f,
//         -0.5f,
//         0.5f, // back face
//         0.5f,
//         -0.5f,
//         0.5f,
//         0.5f,
//         0.5f,
//         0.5f,
//         -0.5f,
//         0.5f,
//         0.5f,
//     };

//     public void init() {
//         loadShaders();
//         createCube();
//     }

//     public void render() {
//         glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//         glUseProgram(shaderProgram);

//         // Create transformations using JOML
//         Matrix4f projection = new Matrix4f()
//             .perspective((float) Math.toRadians(70), 800f / 600f, 0.1f, 100.0f);
//         Matrix4f view = new Matrix4f()
//             .lookAt(
//                 new Vector3f(0, 0, 3), // camera position
//                 new Vector3f(0, 0, 0), // look at
//                 new Vector3f(0, 1, 0) // up vector
//             );
//         Matrix4f model = new Matrix4f().rotateY((float) Math.toRadians(45)); // Rotate the cube
//         Matrix4f mvp = new Matrix4f();
//         projection.mul(view, mvp).mul(model);

//         // Upload MVP matrix to shader
//         int mvpLoc = glGetUniformLocation(shaderProgram, "uMVP");
//         try (MemoryStack stack = MemoryStack.stackPush()) {
//             FloatBuffer fb = stack.mallocFloat(16);
//             mvp.get(fb);
//             glUniformMatrix4fv(mvpLoc, false, fb);
//         }

//         glBindVertexArray(vao);
//         glDrawArrays(GL_QUADS, 0, 24); // 6 faces * 4 vertices each
//         glBindVertexArray(0);

//         glUseProgram(0);
//     }

//     private void createCube() {
//         vao = glGenVertexArrays();
//         vbo = glGenBuffers();

//         glBindVertexArray(vao);

//         glBindBuffer(GL_ARRAY_BUFFER, vbo);
//         glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

//         glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
//         glEnableVertexAttribArray(0);

//         glBindBuffer(GL_ARRAY_BUFFER, 0);
//         glBindVertexArray(0);
//     }

//     private void loadShaders() {
//         String vertexShaderCode = ShaderUtils.loadShaderFromFile(
//             "assets/cube.vert"
//         );
//         String fragmentShaderCode = ShaderUtils.loadShaderFromFile(
//             "assets/cube.frag"
//         );

//         int vertexShader = ShaderUtils.compileShader(
//             vertexShaderCode,
//             GL_VERTEX_SHADER
//         );
//         int fragmentShader = ShaderUtils.compileShader(
//             fragmentShaderCode,
//             GL_FRAGMENT_SHADER
//         );

//         shaderProgram = glCreateProgram();
//         glAttachShader(shaderProgram, vertexShader);
//         glAttachShader(shaderProgram, fragmentShader);
//         glLinkProgram(shaderProgram);
//         glValidateProgram(shaderProgram);

//         glDeleteShader(vertexShader);
//         glDeleteShader(fragmentShader);
//     }
// }
package org.nithish.vehiclesim.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

public class CubeRenderer {

    private int vao, vbo, shaderProgram;
    private int modelLoc, viewLoc, projLoc;
    private Matrix4f model, view, projection;

    public void init() {
        // Define cube vertices (positions only, for simplicity)
        float[] vertices = {
            // Front face
            -0.5f,
            -0.5f,
            0.5f, // Bottom-left
            0.5f,
            -0.5f,
            0.5f, // Bottom-right
            0.5f,
            0.5f,
            0.5f, // Top-right
            -0.5f,
            0.5f,
            0.5f, // Top-left
            // Back face
            -0.5f,
            -0.5f,
            -0.5f, // Bottom-left
            0.5f,
            -0.5f,
            -0.5f, // Bottom-right
            0.5f,
            0.5f,
            -0.5f, // Top-right
            -0.5f,
            0.5f,
            -0.5f, // Top-left
        };

        // Define indices for cube faces
        int[] indices = {
            // Front
            0,
            1,
            2,
            2,
            3,
            0,
            // Back
            4,
            5,
            6,
            6,
            7,
            4,
            // Left
            0,
            3,
            7,
            7,
            4,
            0,
            // Right
            1,
            5,
            6,
            6,
            2,
            1,
            // Top
            3,
            2,
            6,
            6,
            7,
            3,
            // Bottom
            0,
            4,
            5,
            5,
            1,
            0,
        };

        // Create VAO and VBO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuffer = stack.mallocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        }

        // Create EBO (Element Buffer Object)
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer indexBuffer = stack.mallocFloat(indices.length);
            for (int i : indices) {
                indexBuffer.put(i);
            }
            indexBuffer.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        }

        // Specify vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);

        // Create shaders
        String vertexShaderSource =
            "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "void main() {\n" +
            "    gl_Position = projection * view * model * vec4(aPos, 1.0);\n" +
            "}\n";

        String fragmentShaderSource =
            "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "void main() {\n" +
            "    FragColor = vec4(1.0, 0.5, 0.2, 1.0);\n" +
            "}\n";

        // Compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompileError(vertexShader);

        // Compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompileError(fragmentShader);

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkProgramLinkError(shaderProgram);

        // Delete shaders
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        // Get uniform locations
        modelLoc = glGetUniformLocation(shaderProgram, "model");
        viewLoc = glGetUniformLocation(shaderProgram, "view");
        projLoc = glGetUniformLocation(shaderProgram, "projection");

        // Initialize matrices
        model = new Matrix4f();
        view = new Matrix4f();
        projection = new Matrix4f();

        // Set up projection matrix (perspective)
        projection.setPerspective(
            (float) Math.toRadians(45.0f),
            800.0f / 600.0f,
            0.1f,
            100.0f
        );

        // Set up view matrix (camera looking at origin)
        view.lookAt(
            new org.joml.Vector3f(0.0f, 0.0f, 3.0f), // Camera position
            new org.joml.Vector3f(0.0f, 0.0f, 0.0f), // Look at origin
            new org.joml.Vector3f(0.0f, 1.0f, 0.0f) // Up vector
        );
    }

    public void render() {
        // Rotate cube
        model.identity();
        model.rotate((float) glfwGetTime() * 0.5f, 0.0f, 1.0f, 0.0f); // Rotate around Y-axis
        model.rotate((float) glfwGetTime() * 0.3f, 1.0f, 0.0f, 0.0f); // Rotate around X-axis

        // Use shader program
        glUseProgram(shaderProgram);

        // Update uniforms
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer modelBuffer = stack.mallocFloat(16);
            model.get(modelBuffer);
            glUniformMatrix4fv(modelLoc, false, modelBuffer);

            FloatBuffer viewBuffer = stack.mallocFloat(16);
            view.get(viewBuffer);
            glUniformMatrix4fv(viewLoc, false, viewBuffer);

            FloatBuffer projBuffer = stack.mallocFloat(16);
            projection.get(projBuffer);
            glUniformMatrix4fv(projLoc, false, projBuffer);
        }

        // Draw cube
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram);
    }

    private void checkShaderCompileError(int shader) {
        int success = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shader);
            throw new RuntimeException("Shader compilation error: " + infoLog);
        }
    }

    private void checkProgramLinkError(int program) {
        int success = glGetProgrami(program, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(program);
            throw new RuntimeException("Program linking error: " + infoLog);
        }
    }
}
