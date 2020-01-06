package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer
{
    //delta time variables
    private static float deltaTime = 0.0f;
    private static float lastFrame = 0.0f;
    private static float currentFrame;

    //projection matrix creation variables
    private static final float FOVY = 90.0f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000.0f;

    private StaticShader entityShader = new StaticShader();
    private EntityRenderer entityRenderer = new EntityRenderer(entityShader, GetProjectionMatrix());

    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer = new TerrainRenderer(terrainShader, GetProjectionMatrix());

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    public MasterRenderer()
    {
        //culling optimization enable
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void FrameBegin()
    {
        GL11.glClearColor(0.55f, 0.85f, 1.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        currentFrame = (float) GLFW.glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;
    }

    public void Render(Light sun, Camera camera)
    {
        //render all processed entities
        entityShader.Bind();
        entityRenderer.Render(entities, camera, sun);
        entityShader.Unbind();

        //render all terrain
        terrainShader.Bind();
        terrainRenderer.Render(terrains, camera, sun);
        terrainShader.Unbind();

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

    public Matrix4f GetProjectionMatrix()
    {
       return new Matrix4f().setPerspective((float)Math.toRadians(FOVY), WindowManager.GetScreenAspectRatio(), NEAR_PLANE, FAR_PLANE);
    }

    public float GetDeltaTime()
    {
        if (deltaTime > 0.1f)
        {
            System.out.println(">0.1s Lag Recorded");
        }
        return deltaTime;
    }

    public void CleanUp()
    {
        entityShader.CleanUp();
        terrainShader.CleanUp();
    }
}
