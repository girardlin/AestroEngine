package shaders;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public abstract class ShaderProgram
{
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private int attributeCount;

    private FloatBuffer matrix4fBuffer = BufferUtils.createFloatBuffer(16);
    private FloatBuffer matrix3fBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String vertexFile, String fragmentFile)
    {
        attributeCount = 0;
        vertexShaderID = LoadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = LoadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        BindAttribute();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
    }

    private static int LoadShader(String file, int type)
    {
        //attempt to read in file contents and append to string builder
        StringBuilder shaderSource = new StringBuilder();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null)
            {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        }
        catch (IOException e)
        {
            System.err.println("Could not read shader file.");
            e.printStackTrace();
            System.exit(-1);
        }

        //shader creation and compilation
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }
        return shaderID;
    }

    protected abstract void BindAttribute();
    protected void BindAttribute(int attribute, String variableName)
    {
        GL20.glBindAttribLocation(programID, attribute, variableName);
        attributeCount++;
    }

    public void Bind() { GL20.glUseProgram(programID); }
    public void Unbind() { GL20.glUseProgram(0); }

    public int GetAttributeCount()
    {
        return attributeCount;
    }

    public void CleanUp()
    {
        Unbind();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    //uniform lookup / set (with caching)
    private Map<String, Integer> uniformLocationCache = new HashMap<String, Integer>();

    private int GetUniformLocation(String name)
    {
        if (uniformLocationCache.containsKey(name))
            return uniformLocationCache.get(name);

        int location = GL20.glGetUniformLocation(programID, name);
        if (location == -1) { System.err.println("Uniform " + name + " doesn't exist."); }
        uniformLocationCache.put(name, location);

        return location;
    }

    public void SetUniform1b(String name, boolean value) { GL20.glUniform1f(GetUniformLocation(name), (value) ? 1 : 0); }
    public void SetUniform1i(String name, int value) { GL20.glUniform1i(GetUniformLocation(name), value); }
    public void SetUniform1f(String name, float value)
    {
        GL20.glUniform1f(GetUniformLocation(name), value);
    }
    public void SetUniform3f(String name, float v1, float v2, float v3) { GL20.glUniform3f(GetUniformLocation(name), v1, v2, v3); }
    public void SetUniform3f(String name, Vector3f vector) { GL20.glUniform3f(GetUniformLocation(name), vector.x, vector.y, vector.z); }
    public void SetUniform4f(String name, float v1, float v2, float v3, float v4) { GL20.glUniform4f(GetUniformLocation(name), v1, v2, v3, v4); }
    public void SetUniform4f(String name, Vector4f vector) { GL20.glUniform4f(GetUniformLocation(name), vector.w, vector.x, vector.y, vector.z); }
    public void SetUniformMat3f(String name, Matrix3f matrix)
    {
        matrix.get(matrix3fBuffer);
        GL20.glUniformMatrix3fv(GetUniformLocation(name), false, matrix3fBuffer);
    }
    public void SetUniformMat4f(String name, Matrix4f matrix)
    {
        matrix.get(matrix4fBuffer);
        GL20.glUniformMatrix4fv(GetUniformLocation(name), false, matrix4fBuffer);
    }
}