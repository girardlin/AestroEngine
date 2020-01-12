package engineTester;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiElement;
import guis.GuiRenderer;
import guis.GuiTexture;
import input.InputManager;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.joml.Vector2f;
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
		MasterRenderer renderer = new MasterRenderer(loader);
		Random random = new Random();

		//Lights Instantiations
		List<Light> lights = new ArrayList<Light>();

		Light sun = new Light(new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.8f, 0.8f, 0.8f));
		lights.add(sun);

		//Terrain Instantiations
		List<Terrain> terrains = new ArrayList<Terrain>();

		TerrainTexture backTexture = new TerrainTexture(loader.LoadTexture("grass.png"));
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

		RawModel treeModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("pine.obj"));
		ModelTexture treeTexture = new ModelTexture(loader.LoadTexture("pine.png"));
		treeTexture.SetTransparencyBool(true);
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeTexture);

		RawModel fernModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("fern.obj"));
		ModelTexture fernTexture = new ModelTexture(loader.LoadTexture("fern.png"));
		fernTexture.SetTransparencyBool(true);
		fernTexture.SetNumberOfRows(2);
		TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernTexture);

		RawModel lampModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("lamp.obj"));
		ModelTexture lampTexture = new ModelTexture(loader.LoadTexture("lamp.png"));
		TexturedModel lampTexturedModel = new TexturedModel(lampModel, lampTexture);

		//gui instantiations
		/*
		List<GuiElement> guis = new ArrayList<GuiElement>();
		for(int i = 0; i < 16; i++)
		{
			GuiElement gui = new GuiElement(new GuiTexture(loader.LoadTexture("caveman.png")), i + 1, new Vector2f(0.9f, 0.9f), new Vector2f(0.10f, 0.10f));
			gui.GetGuiTexture().SetNumberOfRows(4);
			guis.add(gui);
		}
		*/

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		//player instantiation
		Player player = new Player(treeTexturedModel, new Vector3f(0.0f, 0.5f, -20.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));

		//tree rendering
		for(int i = 0; i < 1200; i++)
		{
			Entity entity = new Entity(treeTexturedModel, new Vector3f(random.nextFloat() * 160 - 80,0,random.nextFloat() * -80), 0.6f * (random.nextFloat() / 4 + 1));
			for(Terrain terrain:terrains)
			{
				if(terrain.InsideThisTerrain(entity.GetPosition().x, entity.GetPosition().z))
				{
					entity.SetPosition(new Vector3f(entity.GetPosition().x, terrain.GetHeightOfTerrain(entity.GetPosition().x, entity.GetPosition().z) - 0.5f, entity.GetPosition().z));
				}
			}
			entities.add(entity);
		}

		//fern rendering
		for(int i = 0; i < 9600; i++)
		{
			Entity entity = new Entity(fernTexturedModel, random.nextInt(4), new Vector3f(random.nextFloat() * 160 - 80,0,random.nextFloat() * -80), 0.1f);
			for(Terrain terrain:terrains)
			{
				if(terrain.InsideThisTerrain(entity.GetPosition().x, entity.GetPosition().z))
				{
					entity.SetPosition(new Vector3f(entity.GetPosition().x, terrain.GetHeightOfTerrain(entity.GetPosition().x, entity.GetPosition().z) - 0.1f, entity.GetPosition().z));
				}
			}
			entities.add(entity);
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

			renderer.Render(lights, player.GetCamera());
			WindowManager.FrameEnd();
		}
		//CLEANUP
		guiRenderer.CleanUp();
		renderer.CleanUp();
		loader.CleanUp();
		WindowManager.Close();
	}
}
