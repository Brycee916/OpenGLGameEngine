
package csc133;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.system.MemoryUtil.*;

public class slWindow {
    private static int winWidth,
                      winHeight;
    static long myWindow = 0;

    static GLFWErrorCallback errorCallback;
    static GLFWKeyCallback keyCallback;
    static GLFWFramebufferSizeCallback fbCallback;

    static int WIN_POS_X = 30;
    static int WIN_POX_Y = 90;
    private static final int OGL_MATRIX_SIZE = 16;
    // call glCreateProgram() here - we have no gl-context here
    static int shader_program;
    static Matrix4f viewProjMatrix = new Matrix4f();
    static FloatBuffer myFloatBuffer = BufferUtils.createFloatBuffer(OGL_MATRIX_SIZE);
    static int vpMatLocation = 0;
    static int renderColorLocation = 0;
    public slWindow(){

    }

    void slWindow(int win_width, int win_height) {
        System.out.println("Call to slWindow:: (width, height) == ("
                        + win_width + ", " + win_height +") received!");
    }

    public static long get(){
        if(myWindow == 0){//if window isnt open
            //create window
            render();
        }
        return myWindow;
    }

    public static long get(int width, int height){
        winWidth = width;
        winHeight = height;
        return get();
    }
    public static void render() {
        try {
            initGLFWindow();
            renderLoop();
            glfwDestroyWindow(myWindow);
            keyCallback.free();
            fbCallback.free();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    } // void render()
    private static void initGLFWindow() {
        final int SAMPLE_VALUE = 8;
        glfwSetErrorCallback(errorCallback =
                GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, SAMPLE_VALUE);   //enables multisazmpling 8xMSAA
        myWindow = glfwCreateWindow(winWidth, winHeight, "CSC 133", NULL, NULL);
        if (myWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");
        glfwSetKeyCallback(myWindow, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int
                    mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
            }
        });
        glfwSetFramebufferSizeCallback(myWindow, fbCallback = new
                GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if (w > 0 && h > 0) {
                            winWidth = w;
                            winHeight = h;
                        }
                    }
                });
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(myWindow, WIN_POS_X, WIN_POX_Y);
        glfwMakeContextCurrent(myWindow);
        int VSYNC_INTERVAL = 1;
        glfwSwapInterval(VSYNC_INTERVAL);
        glfwShowWindow(myWindow);
    } // private void initGLFWindow()
    static void renderLoop() {
        glfwPollEvents();   //render objects placed in queue
        initOpenGL();
        renderObjects();
        /* Process window messages in the main thread */
        while (!glfwWindowShouldClose(myWindow)) {
            glfwWaitEvents();
        }
    } // void renderLoop()
    static void initOpenGL() {
        final float RED_BACKGROUND_VALUE = 0.1f,
                GREEN_BACKGROUND_VALUE = 0.3f,
                BLUE_BACKGROUND_VALUE = 0.7f,
                ALPHA_BACKGROUND_VALUE = 1.0f;//0.0 is transparent & 1.0 is opaque
        String shaderSrcStr1 = "uniform mat4 viewProjMatrix;" +
                "void main(void) {" +
                " gl_Position = viewProjMatrix * gl_Vertex;" +
                "}";
        String shaderSrcStr2 = "uniform vec3 color;" +
                "void main(void) {" +
                " gl_FragColor = vec4(1.0f, 0.2f, 0.3f, 1.0f);" +
                "}";//color of the box
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glViewport(0, 0, winWidth, winHeight);
        glClearColor(RED_BACKGROUND_VALUE, GREEN_BACKGROUND_VALUE, BLUE_BACKGROUND_VALUE, ALPHA_BACKGROUND_VALUE);//set initial color of window
        shader_program = glCreateProgram();
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, shaderSrcStr1);
        glCompileShader(vs);
        glAttachShader(shader_program, vs);
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, shaderSrcStr2);
        glCompileShader(fs);
        glAttachShader(shader_program, fs);
        glLinkProgram(shader_program);
        glUseProgram(shader_program);
        vpMatLocation = glGetUniformLocation(shader_program, "viewProjMatrix");
        return;
    } // void initOpenGL()
    static void renderObjects() {
        final int VERTEX_DIMENSIONS = 2;   //number of dimensions
        float X_MAX_VIEW = 200,
            Y_MAX_VIEW = 200,
            Z_MAX_VIEW = 10;
        final float VECTOR_ZERO = 1.0f,
                VECTOR_ONE = 0.498f,
                VECTOR_TWO = 0.153f;
        final int MAX_ROWS = 7;
        final int MAX_COLS = 5;

        while (!glfwWindowShouldClose(myWindow)) {//while window should remain open
            glfwPollEvents();   //render objects placed in queue
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            int vbo = glGenBuffers();
            int ibo = glGenBuffers();
            //float[] vertices = {-20f, -20f, 20f, -20f, 20f, 20f, -20f, 20f};
            //int[] indices = {0, 1, 2, 0, 2, 3};

            float scaleFactor = 9f;
            float length = 10f * scaleFactor;
            float offset = 10f * scaleFactor;
            float padding = 5f * scaleFactor;
            float xmin = offset;
            float xmax = xmin + length;
            float ymax = winHeight - offset;
            float ymin = ymax - length;
            int vps = 4; //vertices per square
            int fpv = 2; //floats per vertices
            int indx = 0;
            float[] vertices = new float[MAX_ROWS * MAX_COLS * vps * fpv]; //7*5*4*2
            for(int row = 0; row < MAX_ROWS; row++){
                for(int col = 0; col < MAX_COLS; col++){
                    vertices[indx++] = xmin;
                    vertices[indx++] = ymin;
                    vertices[indx++] = xmax;
                    vertices[indx++] = ymin;
                    vertices[indx++] = xmax;
                    vertices[indx++] = ymax;
                    vertices[indx++] = xmin;
                    vertices[indx++] = ymax;
                    xmin = xmax + padding;
                    xmax = xmin + length;
                }
                xmin = offset;
                xmax = xmin + length;
                ymax = ymin - padding;
                ymin = ymax - length;
            }
            int ids = 6; //indices per square
            int[] indices = new int[MAX_ROWS * MAX_COLS * ids];
            int v_index = 0;
            int my_i = 0;
            while(my_i < indices.length){
                indices[my_i++] = v_index;
                indices[my_i++] = v_index + 1;
                indices[my_i++] = v_index + 2;
                indices[my_i++] = v_index;
                indices[my_i++] = v_index + 2;
                indices[my_i++] = v_index + 3;
                v_index += vps;
            }

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.
                    createFloatBuffer(vertices.length).
                    put(vertices).flip(), GL_STATIC_DRAW);
            glEnableClientState(GL_VERTEX_ARRAY);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, (IntBuffer) BufferUtils.
                    createIntBuffer(indices.length).
                    put(indices).flip(), GL_STATIC_DRAW);
            glVertexPointer(VERTEX_DIMENSIONS, GL_FLOAT, 0, 0L);
            viewProjMatrix.setOrtho(0.0f, (float)winWidth, 0.0f, (float)winHeight, 0.0f, Z_MAX_VIEW);
            //viewProjMatrix.setOrtho(0, 200, 0, 200, 0, Z_MAX_VIEW);
            glUniformMatrix4fv(vpMatLocation, false,
                    viewProjMatrix.get(myFloatBuffer));
            glUniform3f(renderColorLocation, VECTOR_ZERO, VECTOR_ONE, VECTOR_TWO);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            int VTD = 6; // need to process 6 Vertices To Draw 2 triangles
            glDrawElements(GL_TRIANGLES, VTD * MAX_ROWS * MAX_COLS, GL_UNSIGNED_INT, 0L);
            glfwSwapBuffers(myWindow);
        }
    } // renderObjects
}
