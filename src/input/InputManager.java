package input;

import entities.PlayerCamera;
import org.lwjgl.BufferUtils;
import renderEngine.MasterRenderer;
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

    public void ProcessInputs(PlayerCamera camera, float deltaTime, MasterRenderer renderer)
    {
        //keyboard inputs
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        {
            glfwSetWindowShouldClose(window, true);
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) //forward
        {
            if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) //if holding shift too
            {
                camera.ProcessKeyboardInputs("SPRINT_FORWARD", deltaTime);
                renderer.IncreaseFOVY(deltaTime);
            }
            else
            {
                camera.ProcessKeyboardInputs("FORWARD", deltaTime);
                renderer.DecreaseFOVY(deltaTime);
            }
        }
        else { renderer.DecreaseFOVY(deltaTime); } //fov reset
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
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) //right strafe
        {
            camera.ProcessKeyboardInputs("JUMP", deltaTime);
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