package input;

import entities.Player;
import entities.PlayerCamera;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import renderEngine.MasterRenderer;
import renderEngine.WindowManager;
import toolBox.MousePicker;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

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

    private IntBuffer windowWidth = BufferUtils.createIntBuffer(1);
    private IntBuffer windowHeight = BufferUtils.createIntBuffer(1);

    private MousePicker mousePicker;
    private PlayerCamera camera;
    private MasterRenderer renderer;

    public InputManager(PlayerCamera camera, MasterRenderer renderer)
    {   //disable cursor
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        this.camera = camera;       //camera reference
        this.renderer = renderer;   //renderer reference
        this.mousePicker = new MousePicker(camera, renderer.GetProjectionMatrix());
    }

    public void ProcessInputs(float deltaTime)
    {
        glfwGetWindowSize(window, windowWidth, windowHeight);

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
            //camera for now is first person, so mouse position for ray casting will be center of screen for now
            mousePicker.Update(camera, windowWidth.get(0), windowHeight.get(0), (float)windowWidth.get(0) / 2, (float)windowHeight.get(0) / 2);

            firstMouseMovement = false;
        }
        else
        {
            camera.ProcessMouseInputs((float)(xpos.get(0) - lastX), (float)(ypos.get(0) - lastY));

            mousePicker.Update(camera, windowWidth.get(0), windowHeight.get(0), (float)windowWidth.get(0) / 2, (float)windowHeight.get(0) / 2);
        }
        lastX = xpos.get(0);
        lastY = ypos.get(0);
    }

    public Vector3f GetCurrentRay()
    {
        return mousePicker.GetCurrentRay();
    }
}