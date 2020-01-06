package toolBox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class AestroMath
{
    public static Matrix4f CreateModelMatrix(Vector3f translation, Vector3f rotation, Vector3f scale)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation);
        matrix.rotate((float) Math.toRadians(rotation.x), 1.0f, 0.0f, 0.0f);
        matrix.rotate((float) Math.toRadians(rotation.y), 0.0f, 1.0f, 0.0f);
        matrix.rotate((float) Math.toRadians(rotation.z), 0.0f, 0.0f, 1.0f);
        matrix.scale(new Vector3f(scale.x, scale.y, scale.z));
        return matrix;
    }

    /* No longer in use
    public static Matrix4f CreateViewMatrix(float pitch, float yaw, Vector3f position)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(pitch), 1.0f, 0.0f, 0.0f);
        matrix.rotate((float) Math.toRadians(yaw), 0.0f, 1.0f, 0.0f);
        Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
        matrix.translate(negativeCameraPos);
        return matrix;
    }
    */
}
