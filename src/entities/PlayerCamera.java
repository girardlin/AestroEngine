package entities;

import org.joml.Vector3f;
import terrains.Terrain;

public class PlayerCamera extends Camera
{
    private final boolean FREE_CAMERA = false;

    private static final float CAMERA_MOVE_SPEED = 1.0f;
    private static final float CAMERA_SENSITIVITY = 0.04f;
    private static float GRAVITY = -5.0f;
    private static final float JUMP_POWER = 1.65f;

    private static final float CAMERA_HEIGHT = 0.5f;

    private float upwardSpeed = 0;
    private boolean isInAir = false;

    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    public PlayerCamera(Vector3f position)
    {
        this.position = position;
    }
    public PlayerCamera() { }

    public void ProcessKeyboardInputs(String input, float deltaTime)
    {
        Vector3f modifiedCameraFront;
        if (FREE_CAMERA)
        {
            modifiedCameraFront = new Vector3f(front);
        }
        else
        {
            modifiedCameraFront = new Vector3f(front.x, 0.0f, front.z);
        }

        float speed = CAMERA_MOVE_SPEED * deltaTime;
        //keyboard inputs
        if (input.equals("SPRINT_FORWARD"))
        {
            position.add(new Vector3f(modifiedCameraFront).normalize().mul(speed * 1.55f));
        }
        if (input.equals("FORWARD"))
        {
            position.add(new Vector3f(modifiedCameraFront).normalize().mul(speed));
        }
        if (input.equals("BACKWARD"))
        {
            position.sub(new Vector3f(modifiedCameraFront).normalize().mul(speed));
        }
        if (input.equals("STRAFE_LEFT"))
        {
            position.sub(new Vector3f(modifiedCameraFront).cross(up).normalize().mul(speed));
        }
        if (input.equals("STRAFE_RIGHT"))
        {
            position.add(new Vector3f(modifiedCameraFront).cross(up).normalize().mul(speed));
        }
        if (input.equals("JUMP") && !isInAir)
        {
            Jump();
            isInAir = true;
        }
    }

    public void ProcessMouseInputs(float offsetX, float offsetY)
    {
        //mouse inputs
        this.offsetX = offsetX * CAMERA_SENSITIVITY;
        this.offsetY = offsetY * CAMERA_SENSITIVITY;

        yaw += this.offsetX;
        pitch -= this.offsetY;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) (Math.sin(Math.toRadians(pitch)));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.normalize();
    }

    public void UpdateData(float deltaTime, float terrainHeight)
    {
        if(FREE_CAMERA)
        {
            GRAVITY = 0.0f;
            isInAir = true;
        }

        upwardSpeed += GRAVITY * deltaTime;
        position.y += upwardSpeed * deltaTime;

        if (position.y < terrainHeight + CAMERA_HEIGHT)
        {
            position.y = terrainHeight + CAMERA_HEIGHT;
            isInAir = false;
        }
    }

    private void Jump()
    {
        if(!FREE_CAMERA)
        {
            this.upwardSpeed = JUMP_POWER;
        }
    }
}
