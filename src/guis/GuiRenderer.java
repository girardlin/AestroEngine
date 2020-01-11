package guis;

import models.RawModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import toolBox.AestroMath;

import java.util.List;

public class GuiRenderer
{
    private final RawModel quad;
    private GuiShader shader;

    public GuiRenderer(Loader loader)
    {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.LoadToVAO(positions);
        shader = new GuiShader();
    }

    public void Render(List<GuiElement> guis)
    {
        shader.Bind();
        GL30.glBindVertexArray(quad.GetVaoID());
        GL20.glEnableVertexAttribArray(0);

        //alpha blending enabled for ui rendering, and depth test disabled
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for(GuiElement gui:guis)
        {
            PrepareGuiElement(gui);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.GetVertexCount());
        }

        //turn off blending and re-enable depth testing
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.Unbind();
    }

    public void Render(GuiElement gui)
    {
        shader.Bind();
        GL30.glBindVertexArray(quad.GetVaoID());
        GL20.glEnableVertexAttribArray(0);

        //alpha blending enabled for ui rendering, and depth test disabled
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        PrepareGuiElement(gui);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.GetVertexCount());

        //turn off blending and re-enable depth testing
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.Unbind();
    }

    private void PrepareGuiElement(GuiElement gui)
    {
        //bind gui element's texture ID and set uniforms
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.GetGuiTexture().GetTextureID());

        Matrix4f matrix = AestroMath.CreateModelMatrix(gui.GetPosition(), gui.GetScale());
        shader.SetUniformMat4f("u_ModelMatrix", matrix);
        shader.SetUniform1f("u_NumberOfRows", gui.GetGuiTexture().GetNumberOfRows());
        shader.SetUniform2f("u_Offset", gui.GetTextureXOffset(), gui.GetTextureYOffset());
    }

    public void CleanUp()
    {
        shader.CleanUp();
    }
}
