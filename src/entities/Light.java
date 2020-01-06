package entities;

import org.joml.Vector3f;

public class Light
{
    private Vector3f position;
    private Vector3f color;

    public Light(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
    }

    public Vector3f GetPosition() { return position; }
    public Vector3f GetColor() { return color; }
    public void SetPosition(Vector3f position) { this.position = position; }
    public void SetColor(Vector3f color) { this.color = color; }
}
