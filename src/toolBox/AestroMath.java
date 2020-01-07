package toolBox;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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

    /*
    public static Matrix4f CreateThinsViewMatrix(float pitch, float yaw, Vector3f position)
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

    public static float BarryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos)
    {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }
}
