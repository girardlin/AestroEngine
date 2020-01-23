package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import skybox.SkyboxRenderer;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer
{
    //sky color
    private static final float RED = 0.5444f;
    private static final float GREEN = 0.62f;
    private static final float BLUE = 0.69f;
    private static final Vector3f SKY_COLOR = new Vector3f(RED, GREEN, BLUE);

    //projection matrix creation variables
    private static float fovy = 90.0f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000.0f;
    Matrix4f projectionMatrix = new Matrix4f();

    //specific renderers
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private SkyboxRenderer skyboxRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    public MasterRenderer(Loader loader)
    {
        //culling optimization and z-depth testing
        GL.createCapabilities();
        EnableCulling();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        //projection matrix calculation / creation
        projectionMatrix.setPerspective((float)Math.toRadians(fovy), WindowManager.GetScreenAspectRatio(), NEAR_PLANE, FAR_PLANE);

        entityRenderer = new EntityRenderer(projectionMatrix);
        terrainRenderer = new TerrainRenderer(projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, GetProjectionMatrix());
    }

    public void RenderScene(List<Terrain> terrains, List<Entity> entities, List<Light> lights, Player player, Vector4f clipPlane)
    {
        FrameBegin();

        //terrain and entity list processing
        for(Terrain terrain:terrains)
        {
            ProcessTerrain(terrain);
        }
        for(Entity entity:entities)
        {
            ProcessEntity(entity);
        }

        Render(lights, player.GetCamera(), clipPlane);
    }

    public void FrameBegin()
    {
        //wireframe
        //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        //set projection matrix
        projectionMatrix.setPerspective((float)Math.toRadians(fovy), WindowManager.GetScreenAspectRatio(), NEAR_PLANE, FAR_PLANE);
    }

    public void Render(List<Light> lights, Camera camera, Vector4f clipPlane)
    {
        //render all processed entities
        entityRenderer.Render(entities, lights, camera, SKY_COLOR, clipPlane);

        //render all terrain
        terrainRenderer.Render(terrains, lights, camera, SKY_COLOR, clipPlane);

        //render skybox
        skyboxRenderer.Render(camera, SKY_COLOR, WindowManager.GetDeltaTime());

        entities.clear();
        terrains.clear();
    }

    public void ProcessEntity(Entity entity)
    {
        //get list of entities from entity hash map using entity parameter's model as key
        TexturedModel entityModel = entity.GetModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null)
        {
            batch.add(entity);
        }
        else
        {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void ProcessTerrain(Terrain terrain)
    {
        terrains.add(terrain);
    }

    public static void EnableCulling()
    {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void DisableCulling()
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void IncreaseFOVY(float amount)
    {
        fovy += amount * 32.5f;
        if (fovy > 102.0f) { fovy = 102.0f; }
    }

    public void DecreaseFOVY(float amount)
    {
        fovy -= amount * 60.0f;
        if (fovy < 90.0f) { fovy = 90.0f; }
    }

    public Matrix4f GetProjectionMatrix()
    {
        return projectionMatrix;
    }

    public void CleanUp()
    {
        entityRenderer.CleanUp();
        terrainRenderer.CleanUp();
        skyboxRenderer.CleanUp();
    }
}
