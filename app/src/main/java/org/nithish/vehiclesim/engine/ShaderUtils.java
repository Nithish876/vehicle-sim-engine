package org.nithish.vehiclesim.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class ShaderUtils {

    public static String loadShaderFromFile(String path) {
        try {
            return new String(
                Files.readAllBytes(Paths.get(path)),
                StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int compileShader(String shaderCode, int shaderType) {
        int shaderID = glCreateShader(shaderType);
        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println("Error compiling shader");
            System.out.println(glGetShaderInfoLog(shaderID));
        }

        return shaderID;
    }
}
