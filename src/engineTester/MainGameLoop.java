package engineTester;

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
		//RNG
		Random random = new Random();

		//Main Window Creation and Loader
		final long MAIN_WINDOW = WindowManager.Create();
		Loader loader = new Loader();

		//Lights Instantiations
		List<Light> lights = new ArrayList<Light>();

		Light sun = new Light(new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.8f, 0.8f, 0.8f));
		lights.add(sun);

		//Terrain Texture Loading
		TerrainTexture backTexture = new TerrainTexture(loader.LoadTexture("grass.png"));
		TerrainTexture rTexture = new TerrainTexture(loader.LoadTexture("mud.png"));
		TerrainTexture gTexture = new TerrainTexture(loader.LoadTexture("grassFlowers.png"));
		TerrainTexture bTexture = new TerrainTexture(loader.LoadTexture("path.png"));

		TerrainTexturePack terrainTexturePack = new TerrainTexturePack(backTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.LoadTexture("blendMap.png"));

		//Terrain Instantiations
		List<Terrain> terrains = new ArrayList<Terrain>();

		Terrain terrain1 = new Terrain(0, -1, loader, terrainTexturePack, blendMap, "heightmap.png");
		Terrain terrain2 = new Terrain(-1, -1, loader, terrainTexturePack, blendMap, "heightmap.png");

		terrains.add(terrain1);
		terrains.add(terrain2);

		//Textured Model Loading
		RawModel treeModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("pine.obj"));
		ModelTexture treeTexture = new ModelTexture(loader.LoadTexture("pine.png"));
		treeTexture.SetTransparencyBool(true);
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeTexture);

		RawModel fernModel = loader.LoadToVAO(OBJFileLoader.loadOBJ("fern.obj"));
		ModelTexture fernTexture = new ModelTexture(loader.LoadTexture("fern.png"));
		fernTexture.SetTransparencyBool(true);
		fernTexture.SetNumberOfRows(2);
		TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernTexture);

		//Entity instantiations
		List<Entity> entities = new ArrayList<Entity>();

		//tree entities
		for(int i = 0; i < 1200; i++)
		{
			Entity entity = new Entity(treeTexturedModel, new Vector3f(random.nextFloat() * 160 - 80,0,random.nextFloat() * -80), 0.55f * (random.nextFloat() / 4 + 1));
			for(Terrain terrain:terrains)
			{
				if(terrain.InsideThisTerrain(entity.GetPosition().x, entity.GetPosition().z))
				{
					entity.SetPosition(new Vector3f(entity.GetPosition().x, terrain.GetHeightOfTerrain(entity.GetPosition().x, entity.GetPosition().z) - 0.5f, entity.GetPosition().z));
				}
			}
			entities.add(entity);
		}

		//fern entities
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

		//player instantiation
		Player player = new Player(treeTexturedModel, new Vector3f(0.0f, 0.5f, -20.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));

		//gui instantiations
		//none for now

		//Main Managers
		MasterRenderer renderer = new MasterRenderer(loader);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		InputManager inputManager = new InputManager(player.GetCamera(), renderer);

		/* --------------------------------------------------------------------------------------------------------------- */

		//MAIN GAME LOOP
		while(!glfwWindowShouldClose(MAIN_WINDOW))
		{
			renderer.FrameBegin();

			inputManager.ProcessInputs(renderer.GetDeltaTime());
			player.UpdatePosition(renderer.GetDeltaTime(), terrains);

			//terrain and entity list processing
			for(Terrain terrain:terrains)
			{
				renderer.ProcessTerrain(terrain);
			}
			for(Entity entity:entities)
			{
				renderer.ProcessEntity(entity);
			}

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
