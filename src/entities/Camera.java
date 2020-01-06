package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolBox.AestroMath;

public class Camera
{
    private Vector3f position = new Vector3f(0.0f, 1.0f, 3.0f);
    private Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    private float pitch;
    private float yaw = -90.0f;
    private float roll;

    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    private float cameraSpeed;
    private float cameraSensitivity = 0.05f;

    public Camera(Vector3f position)
    {
        this.position = position;
    }
    public Camera() { }

    public void ProcessKeyboardInputs(String movement, float deltaTime)
    {
        cameraSpeed = 2.0f * deltaTime;
        //keyboard inputs
        if (movement.equals("FORWARD"))
        {
            position.add(new Vector3f(front).normalize().mul(cameraSpeed));
        }
        if (movement.equals("BACKWARD"))
        {
            position.sub(new Vector3f(front).normalize().mul(cameraSpeed));
        }
        if (movement.equals("STRAFE_LEFT"))
        {
            position.sub(new Vector3f(front).cross(up).normalize().mul(cameraSpeed));
        }
        if (movement.equals("STRAFE_RIGHT"))
        {
            position.add(new Vector3f(front).cross(up).normalize().mul(cameraSpeed));
        }
    }

    public void ProcessMouseInputs(float offsetX, float offsetY)
    {
        //mouse inputs
        this.offsetX = offsetX * cameraSensitivity;
        this.offsetY = offsetY * cameraSensitivity;

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

    public Vector3f GetPosition() { return position; }
    public float GetPitch() { return pitch; }
    public float GetYaw() { return yaw; }
    public float GetRoll() { return roll; }

    public void SetPosition(Vector3f position)
    {
        this.position = position;
    }

    public Matrix4f GetViewMatrix()
    {
        return new Matrix4f().lookAt(position, new Vector3f(front).add(position), up);
    }
}
