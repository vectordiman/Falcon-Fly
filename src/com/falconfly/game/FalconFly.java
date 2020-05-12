package com.falconfly.game;

import com.falconfly.engine.EngineWindow;
import com.falconfly.engine.GameItem;
import com.falconfly.engine.IGameLogic;
import com.falconfly.engine.graph.*;
import com.falconfly.engine.input.Keyboard;
import com.falconfly.engine.input.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class FalconFly implements IGameLogic {

	private Vector3f cameraInc;
	private Vector3f ambientLight;

	private Vector<GameItem> gameItems;
	private final Renderer renderer;
	private Camera camera;
	private PointLight pointLight;

	private Gameplay gameplay;

	private final float CAMERA_POS_STEP = 0.01f;
	private final float MOUSE_SENSITIVITY = 0.1f;

	private float step = 0.05f;
	private static float acceleration = 0.0001f;

	private int skip = 0;

	private static class OLine {

		private Vector<GameItem> line;

		public OLine(Mesh mesh, int count, float posX, float distance, float posY, float posZ) {
			line = new Vector<>();

			for (int i = 0; i < count; i++) {
				line.add(new GameItem(mesh));
			}

			for (GameItem obj : line) {
				obj.setScale(1f);
				obj.setPosition(posX, posY, posZ);
				posX += distance;
			}
		}
	}

	private static class OSurface {

		private Vector<OLine> surface;
		private final int LENGTH = 128;

		public OSurface(Mesh mesh, int count, float posX, float distanceX, float posY, float posZ, float distanceZ) {
			surface = new Vector<>();

			for (int i = 0; i < LENGTH; i++, posZ -= distanceZ) {
				surface.add(new OLine(mesh, count, posX, distanceX, posY, posZ));
			}
		}
	}

	public void regeneration(int pos, int quantity, float dPosZ, float place) {
		for (; pos < quantity; pos++) {
			while (gameItems.get(pos).getPosition().z >= -2) {
				float dif = gameItems.get(pos).getPosition().z + dPosZ;
				gameItems.get(pos).setPosition(
						gameItems.get(pos).getPosition().x,
						gameItems.get(pos).getPosition().y,
						place + dif);
			}
		}
	}

	public FalconFly() throws IOException {
		cameraInc = new Vector3f();
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		this.gameplay = new Gameplay();
		this.gameItems = new Vector<GameItem>();
	}

	@Override
	public void init() throws Exception {
		renderer.init();

		float reflectance = 1f;

		Mesh grassMesh = OBJLoader.loadMesh("models/grass");
		Texture grassTexture = new Texture("models/grass");
		Material grassMaterial = new Material(grassTexture, reflectance);
		grassMesh.setMaterial(grassMaterial);

		OSurface grassSurface = new OSurface(grassMesh, 4, -6, 3, -5, -3, 3);
		Vector<GameItem> allGrass = new Vector<>();
		for (int i = 0; i < grassSurface.LENGTH; i++) {
			allGrass.add(grassSurface.surface.get(i).line.get(0));
			allGrass.add(grassSurface.surface.get(i).line.get(1));
			allGrass.add(grassSurface.surface.get(i).line.get(2));
			allGrass.add(grassSurface.surface.get(i).line.get(3));
		}

		Mesh fenceMesh = OBJLoader.loadMesh("models/fence");
		Texture fenceTexture = new Texture("models/fence");
		Material fenceMaterial = new Material(fenceTexture, reflectance);
		fenceMesh.setMaterial(fenceMaterial);

		OSurface fenceSurface = new OSurface(fenceMesh, 2, -7,14, -5, -3, 2);
		Vector<GameItem> allFence = new Vector<>();
		for (int i = 0; i < fenceSurface.LENGTH; i++) {
			allGrass.add(fenceSurface.surface.get(i).line.get(0));
			allGrass.add(fenceSurface.surface.get(i).line.get(1));
		}

		Mesh humansRoadMesh = OBJLoader.loadMesh("models/humansRoad");
		Texture humansRoadTexture = new Texture("models/humansRoad");
		Material humansRoadMaterial = new Material(humansRoadTexture, reflectance);
		humansRoadMesh.setMaterial(humansRoadMaterial);

		OSurface humansRoadSurface = new OSurface(humansRoadMesh, 2, -10, 16, -5, -3, 4);
		Vector<GameItem> allHumansRoad = new Vector<>();
		for (int i = 0; i < humansRoadSurface.LENGTH; i++) {
			allGrass.add(humansRoadSurface.surface.get(i).line.get(0));
			allGrass.add(humansRoadSurface.surface.get(i).line.get(1));
		}

		Mesh house_6Mesh = OBJLoader.loadMesh("models/house_6");
		Texture house_6Texture = new Texture("models/house_6");
		Material house_6Material = new Material(house_6Texture, reflectance);
		house_6Mesh.setMaterial(house_6Material);

		OSurface house_6Surface = new OSurface(house_6Mesh, 1, 15, 16, -5, -3, 4);
		Vector<GameItem> house_6 = new Vector<>();
		for (int i = 0; i < 1; i++) {
			allGrass.add(house_6Surface.surface.get(i).line.get(0));
		}

		Mesh birdMesh  = OBJLoader.loadMesh("models/house_2");
		Texture birdTexture = new Texture("models/house_2");
		Material birdMaterial = new Material(birdTexture, reflectance);
		birdMesh.setMaterial(birdMaterial);
		GameItem bird = new GameItem(birdMesh);
		bird.setPosition(-2, -5, -5);

		gameItems.add(bird);
		for (GameItem obj : allGrass) {
			gameItems.add(obj);
		}
		for (GameItem obj : allFence) {
			gameItems.add(obj);
		}
		for (GameItem obj : allHumansRoad) {
			gameItems.add(obj);
		}
		for (GameItem obj : house_6) {
			obj.setRotation(0.5f, 0, 0.5f);
			gameItems.add(obj);
		}

		ambientLight = new Vector3f(0.2f, 0.2f, 0.2f);
		Vector3f lightColour = new Vector3f(1, 1, 1);
		Vector3f lightPosition = new Vector3f(-5, 20, 6);
		float lightIntensity = 750.0f;
		pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
		PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
		pointLight.setAttenuation(att);
	}

//	@Override
//	public void input(EngineWindow window) {
//		displyInc = 0;
//		displxInc = 0;
//		displzInc = 0;
//		scaleInc = 0;
//		if (Keyboard.keyPressed(GLFW_KEY_UP)) {
//			displyInc = 1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_DOWN)) {
//			displyInc = -1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_LEFT)) {
//			displxInc = -1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_RIGHT)) {
//			displxInc = 1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_A)) {
//			displzInc = -1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_Q)) {
//			displzInc = 1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_Z)) {
//			scaleInc = -1;
//		} else if (Keyboard.keyPressed(GLFW_KEY_X)) {
//			scaleInc = 1;
//		}
//
//		if (Keyboard.keyPressed(GLFW_KEY_ESCAPE)) {
//			glfwSetWindowShouldClose(window.id, true);
//		}
//	}

	@Override
	public void input(EngineWindow window, MouseInput mouseInput) {

		cameraInc.set(0, 0, 0);
		if (Keyboard.keyPressed(GLFW_KEY_W)) {
			cameraInc.z = -1;
		} else if (Keyboard.keyPressed(GLFW_KEY_S)) {
			cameraInc.z = 1;
		}
		if (Keyboard.keyPressed(GLFW_KEY_A) && this.skip == 0) {
			this.gameplay.goToLeft();
			this.skip = 5;
		} else if (Keyboard.keyPressed(GLFW_KEY_D) && this.skip == 0) {
			this.gameplay.goToRight();
			this.skip = 5;
		}
		if (Keyboard.keyPressed(GLFW_KEY_Z)) {
			cameraInc.y = -1;
		} else if (Keyboard.keyPressed(GLFW_KEY_X)) {
			cameraInc.y = 1;
		}

		float lightPos = pointLight.getPosition().z;
		if (Keyboard.keyPressed(GLFW_KEY_N)) {
			this.pointLight.getPosition().z = lightPos + 0.1f;
		} else if (Keyboard.keyPressed(GLFW_KEY_M)) {
			this.pointLight.getPosition().z = lightPos - 0.1f;
		}
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		// Update camera position

		if(this.gameplay.isCollisionWithEnemy()) {

		}
		camera.movePosition(0, 0, 0);
		//camera.movePosition(0.1f * cameraInc.x, 0.1f * cameraInc.y, 0.1f * cameraInc.z);

		boolean firstFlag = true;
		for (GameItem obj : gameItems) {
			if(firstFlag) {
				firstFlag = false;
				continue;
			}
			obj.setPosition(obj.getPosition().x, obj.getPosition().y, obj.getPosition().z + step);
		}

		regeneration(0 + 1, 128 * 4, 3, -384);
		regeneration(128 * 4 + 1, 128 * 6, 2, -256);
		regeneration(128 * 6 + 1, 128 * 8, 4, -512);
		if(gameplay.getPlayerPosition() == 1)
			gameItems.get(0).setPosition(-4, gameItems.get(0).getPosition().y, gameItems.get(0).getPosition().z);
		else if(gameplay.getPlayerPosition() == 2)
			gameItems.get(0).setPosition(-2, gameItems.get(0).getPosition().y, gameItems.get(0).getPosition().z);
		else
			gameItems.get(0).setPosition(0, gameItems.get(0).getPosition().y, gameItems.get(0).getPosition().z);
		step += acceleration;

		// Update camera based on mouse
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
		if(this.skip != 0)
			this.skip--;
		//this.gameplay.update();
	}

	@Override
	public void render(EngineWindow window) {
		renderer.render(window, gameItems, camera, ambientLight, pointLight);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		for (GameItem gameItem : gameItems) {
			gameItem.getMesh().cleanUp();
		}
	}

}