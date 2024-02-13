

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
    //static int WIN_WIDTH = 1800, WIN_HEIGHT = 1200;
    static int WIN_WIDTH = 900, WIN_HEIGHT = 900;

    // call glCreateProgram() here - we have no gl-context here
    int shader_program;
    Matrix4f viewProjMatrix = new Matrix4f();
    int vpMatLocation = 0, renderColorLocation = 0;
    public static void main(String[] args) {
        //new csc133.slWindow().slWindow(WIN_WIDTH, WIN_HEIGHT);
        //new Main.render();
        long myWindow = new slWindow().get(WIN_WIDTH, WIN_HEIGHT);

    } // public static void main(String[] args)
}
