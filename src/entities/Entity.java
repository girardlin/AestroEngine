package entities;

import models.TexturedModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolBox.AestroMath;

public class Entity
{
    private TexturedModel model;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    boolean scaledEvenly = true;

    public Entity(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale)
    {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        CheckScale();
    }

    public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale)
    {
        this(model, position, rotation, new Vector3f(scale, scale, scale));
    }

    public Entity(TexturedModel model, Vector3f position, float scale)
    {
        this(model, position, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(scale, scale, scale));
    }

    public void ChangePosition(float dx, float dy, float dz)
    {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void ChangePosition(Vector3f dVector)
    {
        this.position.x += dVector.x;
        this.position.y += dVector.y;
        this.position.z += dVector.z;
    }

    public void ChangeRotation(float dx, float dy, float dz) //in degrees
    {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }

    public void ChangeRotation(Vector3f dVector) //in degrees
    {
        this.rotation.x += dVector.x;
        this.rotation.y += dVector.y;
        this.rotation.z += dVector.z;
    }

    private void CheckScale()
    {
        if(scale.x != scale.y || scale.x != scale.z)
        {
            scaledEvenly = false;
        }
    }

    public TexturedModel GetModel() { return model; }
    public Vector3f GetPosition() { return position; }
    public Vector3f GetRotation() { return rotation; }
    public Vector3f GetScale() { return scale; }
    public boolean IsScaledEvenly() { return scaledEvenly; }
    public void SetModel(TexturedModel model) { this.model = model; }
    public void SetPosition(Vector3f position) { this.position = position; }
    public void SetRotation(Vector3f rotation) { this.rotation = rotation; }
    public void SetScale(Vector3f scale)
    {
        this.scale = scale;
        CheckScale();
    }

    public Matrix4f GetModelMatrix()
    {
        return AestroMath.CreateModelMatrix(position, rotation, scale);
    }
}
