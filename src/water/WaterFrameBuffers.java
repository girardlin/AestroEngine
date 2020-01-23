package water;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class WaterFrameBuffers
{
    private IntBuffer windowWidth;
    private IntBuffer windowHeight;

    protected static int REFLECTION_WIDTH;
    private static int REFLECTION_HEIGHT;

    protected static int REFRACTION_WIDTH;
    private static int REFRACTION_HEIGHT;

    private int reflectionFrameBuffer;
    private int reflectionTexture;
    private int reflectionDepthBuffer;

    private int refractionFrameBuffer;
    private int refractionTexture;
    private int refractionDepthTexture;

    public WaterFrameBuffers()
    {
        //retrieve window size
        windowWidth = BufferUtils.createIntBuffer(1);
        windowHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(GLFW.glfwGetCurrentContext(), windowWidth, windowHeight);

        REFLECTION_WIDTH = windowWidth.get(0) / 2;
        REFLECTION_HEIGHT = windowHeight.get(0) / 2;

        REFRACTION_WIDTH = windowWidth.get(0);
        REFRACTION_HEIGHT = windowHeight.get(0);

        InitializeReflectionFrameBuffer();
        InitializeRefractionFrameBuffer();
    }

    private void InitializeReflectionFrameBuffer()
    {
        reflectionFrameBuffer = CreateFrameBuffer();
        reflectionTexture = CreateColorTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
        reflectionDepthBuffer = CreateDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
        {
            System.err.println("Reflection Frame Buffer was not properly initialized.");
        }

        UnbindFrameBuffer();
    }

    private void InitializeRefractionFrameBuffer()
    {
        refractionFrameBuffer = CreateFrameBuffer();
        refractionTexture = CreateColorTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
        refractionDepthTexture = CreateDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
        {
            System.err.println("Reflection Frame Buffer was not properly initialized.");
        }

        UnbindFrameBuffer();
    }

    private int CreateFrameBuffer()
    {
        int frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        return frameBuffer;
    }

    private int CreateColorTextureAttachment(int width, int height)
    {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);

        return texture;
    }

    private int CreateDepthTextureAttachment(int width, int height)
    {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);

        return texture;
    }

    private int CreateDepthBufferAttachment(int width, int height)
    {
        int depthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
        return depthBuffer;
    }

    private void BindFrameBuffer(int frameBuffer, int width, int height)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, width, height);
    }

    public void BindReflectionFrameBuffer()
    {
        BindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
    }

    public void BindRefractionFrameBuffer()
    {
        BindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
    }

    public void UnbindFrameBuffer()
    {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, windowWidth.get(0), windowHeight.get(0));
    }

    public int GetReflectionTexture()
    {
        return reflectionTexture;
    }

    public int GetRefractionTexture()
    {
        return refractionTexture;
    }

    public int GetRefractionDepthTexture()
    {
        return refractionDepthTexture;
    }

    public void CleanUp()
    {
        GL30.glDeleteFramebuffers(reflectionFrameBuffer);
        GL11.glDeleteTextures(reflectionTexture);
        GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
        GL30.glDeleteFramebuffers(refractionFrameBuffer);
        GL11.glDeleteTextures(refractionTexture);
        GL11.glDeleteTextures(refractionDepthTexture);
    }
}
