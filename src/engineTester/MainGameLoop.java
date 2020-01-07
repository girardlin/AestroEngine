package engineTester;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import entities.Entity;
import entities.Light;
import entities.Player;
import input.InputManager;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.joml.Vector3f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop
{
	public static void main(String[] args)
	{
		final long MAIN_WINDOW = WindowManager.Create();
		InputManager inputManager = new InputManager();
		Loader loader = new Loader();
		Light sun = new Light(new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(1.0f, 0.95f, 0.95f));
		MasterRenderer renderer = new MasterRenderer();
		Random random = new Random();

		//Terrain Instantiations
		List<Terrain> terrains = new ArrayList<Terrain>();

		TerrainTexture backTexture = new TerrainTexture(loader.LoadTexture("grass2.png"));
		TerrainTexture rTexture = new TerrainTexture(loader.LoadTexture("mud.png"));
		TerrainTexture gTexture = new TerrainTexture(loader.LoadTexture("grassFlowers.png"));
		TerrainTexture bTexture = new TerrainTexture(loader.LoadTexture("path.png"));

		TerrainTexturePack terrainTexturePack = new TerrainTexturePack(backTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.LoadTexture("blendMap.png"));

		Terrain terrain1 = new Terrain(0, -1, loader, terrainTexturePack, blendMap, "heightmap.png");
		Terrain terrain2 = new Terrain(-1, -1, loader, terrainTexturePack, blendMap, "heightmap.png");

		terrains.add(terrain1);
		terrains.add(terrain2);

		//Entity instantiations
		List<Entity> entities = new ArrayList<Entity>();

		RawModel treeModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("tree.obj"));
		ModelTexture treeTexture = new ModelTexture(loader.LoadTexture("tree.png"));
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeTexture);

		RawModel grassModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("grassModel.obj"));
		ModelTexture grassTexture = new ModelTexture(loader.LoadTexture("grassTexture.png"));
		grassTexture.SetTransparencyBool(true);
		grassTexture.SetUsingFakeLighting(true);
		TexturedModel grassTexturedModel = new TexturedModel(grassModel, grassTexture);

		RawModel flowerModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("grassModel.obj"));
		ModelTexture flowerTexture = new ModelTexture(loader.LoadTexture("flower.png"));
		flowerTexture.SetTransparencyBool(true);
		flowerTexture.SetUsingFakeLighting(true);
		TexturedModel flowerTexturedModel = new TexturedModel(flowerModel, flowerTexture);

		RawModel fernModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("fern.obj"));
		ModelTexture fernTexture = new ModelTexture(loader.LoadTexture("fern.png"));
		fernTexture.SetTransparencyBool(true);
		TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernTexture);

		Player player = new Player(flowerTexturedModel, new Vector3f(0.0f, 0.5f, -20.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));

		for(int i = 0; i < 1000; i++)
		{
			entities.add(new Entity(treeTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), 1.0f));
		}

		for(int i = 0; i < 600; i++)
		{
			entities.add(new Entity(grassTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), 0.2f));
			entities.add(new Entity(fernTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), 0.1f));
		}

		for(int i = 0; i < 300; i++)
		{
			entities.add(new Entity(flowerTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), 0.2f));
		}

		//MAIN GAME LOOP
		while(!glfwWindowShouldClose(MAIN_WINDOW))
		{
			renderer.FrameBegin();
			inputManager.ProcessInputs(player.GetCamera(), renderer.GetDeltaTime(), renderer);
			player.UpdatePosition(renderer.GetDeltaTime(), terrains);

			for(Entity entity:entities){
				renderer.ProcessEntity(entity);
			}

			for(Terrain terrain:terrains)
			{
				renderer.ProcessTerrain(terrain);
			}

			//float sinTime = (float) Math.sin(glfwGetTime());
			//float cosTime = (float) Math.cos(glfwGetTime());
			//sun.SetPosition(new Vector3f(2000.0f * sinTime, 2000.0f * cosTime, 2000.0f));

			renderer.Render(sun, player.GetCamera());

			WindowManager.FrameEnd();
		}
		//CLEANUP
		renderer.CleanUp();
		loader.CleanUp();
		WindowManager.Close();
	}
}
