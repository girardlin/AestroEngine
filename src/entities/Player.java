package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import terrains.Terrain;

import java.util.List;

public class Player extends Entity
{
    private PlayerCamera camera;

    public Player(TexturedModel model, Vector3f position, Vector3f rotation, Vector3f scale)
    {
        super(model, position, rotation, scale);
        this.camera = new PlayerCamera(position);
    }

    public void UpdatePosition(float deltaTime, List<Terrain> terrains)
    {
        Terrain specifiedTerrain = terrains.get(0);
        for(Terrain terrain:terrains)
        {
            if(this.GetPosition().x > terrain.GetX() && this.GetPosition().x <= (terrain.GetX() + terrain.GetSize())
                    && this.GetPosition().z > terrain.GetZ() && this.GetPosition().z <= (terrain.GetZ() + terrain.GetSize()))
            {
                specifiedTerrain = terrain;
            }
        }
        float terrainHeight = specifiedTerrain.GetHeightOfTerrain(super.GetPosition().x, super.GetPosition().z);
        camera.UpdateData(deltaTime, terrainHeight);
        super.SetPosition(camera.GetPosition());
    }

    public PlayerCamera GetCamera()
    {
        return camera;
    }
}
