package engineTester;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import entities.Camera;
import entities.Entity;
import entities.Light;
import input.InputManager;
import models.RawModel;
import models.TexturedModel;
import org.joml.Vector3f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;

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

		RawModel treeModel = OBJLoader.LoadObjModel("tree.obj", loader);
		ModelTexture treeTexture = new ModelTexture(loader.LoadTexture("tree.png"));
		TexturedModel treeTexturedModel = new TexturedModel(treeModel, treeTexture);

		RawModel grassModel = OBJLoader.LoadObjModel("grassModel.obj", loader);
		ModelTexture grassTexture = new ModelTexture(loader.LoadTexture("grassTexture.png"));
		TexturedModel grassTexturedModel = new TexturedModel(grassModel, grassTexture);

		RawModel fernModel = OBJLoader.LoadObjModel("fern.obj", loader);
		ModelTexture fernTexture = new ModelTexture(loader.LoadTexture("fern.png"));
		TexturedModel fernTexturedModel = new TexturedModel(fernModel, fernTexture);

		Light light = new Light(new Vector3f(20000.0f, 20000.0f, 20000.0f), new Vector3f(1.0f, 0.95f, 0.95f));

		Terrain terrain1 = new Terrain(0, 0, loader, new ModelTexture(loader.LoadTexture("grass.png")));
		Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.LoadTexture("grass.png")));
		Terrain terrain3 = new Terrain(-1, 0, loader, new ModelTexture(loader.LoadTexture("grass.png")));
		Terrain terrain4 = new Terrain(-1, -1, loader, new ModelTexture(loader.LoadTexture("grass.png")));

		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();

		for(int i = 0; i < 1000; i++)
		{
			entities.add(new Entity(treeTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f)));
		}

		for(int i = 0; i < 600; i++)
		{
			Entity temp = new Entity(grassTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
			temp.SetScale(new Vector3f(0.2f, 0.2f, 0.2f));
			entities.add(temp);
		}

		for(int i = 0; i < 600; i++)
		{
			Entity temp = new Entity(fernTexturedModel, new Vector3f(random.nextFloat() * 80 - 40,0,random.nextFloat() * -80), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f));
			temp.SetScale(new Vector3f(0.1f, 0.1f, 0.1f));
			entities.add(temp);
		}


		while(!glfwWindowShouldClose(MAIN_WINDOW))
		{
			renderer.FrameBegin();
			inputManager.ProcessInputs(camera, renderer.GetDeltaTime());

			//float sinTime = 100.0f * (float) Math.sin(GLFW.glfwGetTime()/4);
			//float cosTime = 100.0f * (float) Math.cos(GLFW.glfwGetTime()/4);

			//light.SetPosition(new Vector3f(sinTime, cosTime, 0.0f));
			for(Entity entity:entities){
				renderer.ProcessEntity(entity);
			}

			renderer.ProcessTerrain(terrain1);
			renderer.ProcessTerrain(terrain2);
			renderer.ProcessTerrain(terrain3);
			renderer.ProcessTerrain(terrain4);

			renderer.Render(light, camera);

			WindowManager.FrameEnd();
		}
		renderer.CleanUp();
		loader.CleanUp();
		WindowManager.Close();
	}
}
