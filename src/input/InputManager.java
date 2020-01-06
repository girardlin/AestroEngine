package input;

import entities.Camera;
import org.lwjgl.BufferUtils;
import renderEngine.WindowManager;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class InputManager
{
    private final long window = WindowManager.GetWindow();
    //mouse variables
    private boolean firstMouseMovement = true;
    private DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
    private DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
    private double lastX = 0;
    private double lastY = 0;

    public InputManager()
    {   //disable cursor
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void ProcessInputs(Camera camera, float deltaTime)
    {
        //keyboard inputs
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) //forward
        {
            camera.ProcessKeyboardInputs("FORWARD", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) //backward
        {
            camera.ProcessKeyboardInputs("BACKWARD", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) //left strafe
        {
            camera.ProcessKeyboardInputs("STRAFE_LEFT", deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) //right strafe
        {
            camera.ProcessKeyboardInputs("STRAFE_RIGHT", deltaTime);
        }

        //mouse inputs
        glfwGetCursorPos(window, xpos, ypos);
        if(firstMouseMovement)
        {
            camera.ProcessMouseInputs(0.0f, 0.0f);
            firstMouseMovement = false;
        }
        else
        {
            camera.ProcessMouseInputs((float)(xpos.get(0) - lastX), (float)(ypos.get(0) - lastY));
        }
        lastX = xpos.get(0);
        lastY = ypos.get(0);
    }
}