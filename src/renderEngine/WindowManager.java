package renderEngine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WindowManager
{	
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 600;
	
	private static long window;
	
	public static long Create()
	{
		//initial setup, error callback creation
		System.out.println("AestroEngine - LWJGL v." + Version.getVersion());
		GLFWErrorCallback.createPrint(System.err).set(); 
		
		//glfw initialization
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		//window creation
		window = glfwCreateWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "AestroEngine - LWJGL v." + Version.getVersion(), NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		return window;
	}
	
	public static void FrameEnd() //update function
	{
		glfwSwapBuffers(window);
		glfwPollEvents();
	}
	
	public static void Close()
	{
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	public static long GetWindow() { return window; }
	public static int GetScreenWidth() { return SCREEN_WIDTH; }
	public static int GetScreenHeight() { return SCREEN_HEIGHT; }
	public static float GetScreenAspectRatio() { return (float)(SCREEN_WIDTH / SCREEN_HEIGHT); }
}