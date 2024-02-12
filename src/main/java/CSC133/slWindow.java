
package csc133;

import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.system.MemoryUtil.*;

public class slWindow {
    private static int winWidth = 1800,
                      winHeight = 1200;
    private static long myWindow = 0;

    public slWindow(){

    }

    void slWindow(int win_width, int win_height) {
        System.out.println("Call to slWindow:: (width, height) == ("
                        + win_width + ", " + win_height +") received!");
    }

    public static long get(){
        if(myWindow == 0){//if window isnt open
            //create window
            myWindow = glfwCreateWindow(winWidth, winHeight, "CSC 133", NULL, NULL);
        }
        return myWindow;
    }

    public static long get(int width, int height){
        winWidth = width;
        winHeight = height;
        return get();
    }

}
