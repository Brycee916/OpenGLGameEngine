

package csc133;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Main {
    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    static long window;
    static int WIN_WIDTH = 1800, WIN_HEIGHT = 1200;
    int WIN_POS_X = 30, WIN_POX_Y = 90;
    private static final int OGL_MATRIX_SIZE = 16;
    // call glCreateProgram() here - we have no gl-context here
    int shader_program;
    Matrix4f viewProjMatrix = new Matrix4f();
    FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
    int vpMatLocation = 0, renderColorLocation = 0;
    public static void main(String[] args) {
        //new csc133.slWindow().slWindow(WIN_WIDTH, WIN_HEIGHT);
        long myWindow = new slWindow().get(WIN_WIDTH, WIN_HEIGHT);
        new slWindow().render();
    } // public static void main(String[] args)
}
