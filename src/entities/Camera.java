package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolBox.AestroMath;

public class Camera
{
    protected Vector3f position = new Vector3f(0.0f, 0.5f, -20.0f);
    protected Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    protected Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    protected float pitch = 0.0f;
    protected float yaw = -90.0f;
    protected float roll;

    public Camera(Vector3f position)
    {
        this.position = position;
    }
    public Camera() { }

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
