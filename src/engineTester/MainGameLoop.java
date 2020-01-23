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
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterTile;

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

		Light sun = new Light(new Vector3f(200.0f), new Vector3f(0.9f));
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

		//water instantiation
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(-40.0f, -40.0f, -0.5f));
		waters.add(new WaterTile(40.0f, -40.0f, -0.5f));

		WaterFrameBuffers fbos = new WaterFrameBuffers();

		//player instantiation
		Player player = new Player(treeTexturedModel, new Vector3f(0.0f, 0.5f, -20.0f), new Vector3f(0.0f), new Vector3f(1.0f));

		//gui element instantiations
		//GuiTexture healthbar = new GuiTexture(loader.LoadTexture("healthbar.png"));
		//GuiElement guiElement = new GuiElement(healthbar, new Vector2f( 0.8f, 0.95f), new Vector2f(0.2f, 0.05f));

		List<GuiElement> guis = new ArrayList<GuiElement>();
		//guis.add(guiElement);

		//Main Managers
		MasterRenderer renderer = new MasterRenderer(loader);
		WaterRenderer waterRenderer = new WaterRenderer(loader, renderer.GetProjectionMatrix(), fbos);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		InputManager inputManager = new InputManager(player.GetCamera(), renderer);

		/* --------------------------------------------------------------------------------------------------------------- */

		//MAIN GAME LOOP
		while(!glfwWindowShouldClose(MAIN_WINDOW))
		{
			inputManager.ProcessInputs(WindowManager.GetDeltaTime());
			player.UpdatePosition(WindowManager.GetDeltaTime(), terrains);

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			//render scene to reflection framebuffer and set clip plane to above water height, do reflection calculations to give illusion
			fbos.BindReflectionFrameBuffer();
			float distance = 2 * (player.GetCamera().GetPosition().y - waters.get(0).GetHeight());
			player.GetCamera().ChangePositionY(-distance);
			player.GetCamera().InvertPitch();
			renderer.RenderScene(terrains, entities, lights, player, new Vector4f(0,1,0, -waters.get(0).GetHeight()));
			player.GetCamera().InvertPitch();
			player.GetCamera().ChangePositionY(distance);

			//render scene to refraction framebuffer and set clip plane to above water height
			fbos.BindRefractionFrameBuffer();
			renderer.RenderScene(terrains, entities, lights, player, new Vector4f(0,-1,0, waters.get(0).GetHeight()));

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.UnbindFrameBuffer();
			renderer.RenderScene(terrains, entities, lights, player, new Vector4f(0,-1,0,10000));
			waterRenderer.Render(waters, player.GetCamera());
			guiRenderer.Render(guis);

			WindowManager.FrameEnd();
		}
		//CLEANUP
		guiRenderer.CleanUp();
		waterRenderer.CleanUp();
		fbos.CleanUp();
		renderer.CleanUp();
		loader.CleanUp();
		WindowManager.Close();
	}
}
