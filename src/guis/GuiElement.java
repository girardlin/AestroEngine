package guis;

import org.joml.Vector2f;

public class GuiElement
{
    private Vector2f position;
    private Vector2f scale;

    private int textureIndex = 0;

    private GuiTexture guiTexture;

    public GuiElement(GuiTexture guiTexture, Vector2f position, Vector2f scale)
    {
        this.guiTexture = guiTexture;
        this.position = position;
        this.scale = scale;
    }

    public GuiElement(GuiTexture guiTexture, int index, Vector2f position, Vector2f scale)
    {
        this.guiTexture = guiTexture;
        this.textureIndex = index;
        this.position = position;
        this.scale = scale;
    }

    public float GetTextureXOffset()
    {
        int row = textureIndex % guiTexture.GetNumberOfRows();
        return (float) row / (float) guiTexture.GetNumberOfRows();
    }

    public float GetTextureYOffset()
    {
        int column = textureIndex / guiTexture.GetNumberOfRows();
        return (float) column / (float) guiTexture.GetNumberOfRows();
    }

    public Vector2f GetPosition() { return position; }
    public Vector2f GetScale() { return scale; }
    public GuiTexture GetGuiTexture() { return guiTexture; }
}
