package toolBox;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.IntBuffer;

public class MousePicker
{
    private Vector3f currentRay;

    private Matrix4f invertedViewMatrix;
    private Matrix4f invertedProjectionMatrix;

    public MousePicker(Camera camera, Matrix4f projectionMatrix)
    {
        this.invertedViewMatrix = new Matrix4f(camera.GetViewMatrix()).invert();
        this.invertedProjectionMatrix = new Matrix4f(projectionMatrix).invert();
    }

    public void Update(Camera camera, int windowWidth, int windowHeight, float mouseX, float mouseY)
    {
        invertedViewMatrix = new Matrix4f(camera.GetViewMatrix()).invert();
        currentRay = CalculateMouseRay(windowWidth, windowHeight, mouseX, mouseY);
    }

    private Vector3f CalculateMouseRay(int windowWidth, int windowHeight, float mouseX, float mouseY)
    {
        //normalized device coordinates
        float x = (2.0f * mouseX) / windowWidth - 1.0f;
        float y = (2.0f * mouseY) / windowHeight - 1.0f;
        Vector2f normalizedCoords = new Vector2f(x, y);

        //clip coordinates
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);

        //eye coordinates
        Vector4f eyeCoords = new Vector4f(clipCoords).mul(invertedProjectionMatrix);
        eyeCoords = new Vector4f(eyeCoords.x, eyeCoords.y, -1.0f, 0.0f);

        //world coordinates
        Vector4f worldRay = new Vector4f(eyeCoords).mul(invertedViewMatrix);
        worldRay.normalize();

        //vec3f conversion
        return new Vector3f(worldRay.x, worldRay.y, worldRay.z);
    }

    public Vector3f GetCurrentRay()
    {
        return currentRay;
    }
}
