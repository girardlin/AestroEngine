package entities;

import org.joml.Vector3f;

public class Light
{
    private Vector3f position;
    private Vector3f color;
    private Vector3f attenuation = new Vector3f(1.0f, 0.0f, 0.0f);

    public Light(Vector3f position, Vector3f color)
    {
        this.position = position;
        this.color = color;
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation)
    {
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }

    public Vector3f GetPosition() { return position; }
    public Vector3f GetColor() { return color; }
    public Vector3f GetAttenuation() { return attenuation; }
    public void SetPosition(Vector3f position) { this.position = position; }
    public void SetColor(Vector3f color) { this.color = color; }
}
