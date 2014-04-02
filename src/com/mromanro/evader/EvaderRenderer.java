package com.mromanro.evader;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.widget.TextView;

import com.mromanro.objects.Asteroid;
import com.mromanro.objects.Bullet;
import com.mromanro.objects.Space;
import com.mromanro.objects.Spaceship;
import com.mromanro.programs.ColorShaderProgram;
import com.mromanro.programs.TextureShaderProgram;
import com.mromanro.util.Geometry;
import com.mromanro.util.Geometry.Point;
import com.mromanro.util.Geometry.Vector;
import com.mromanro.util.TextureHelper;

public class EvaderRenderer implements Renderer {
	private final Context context;
	
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];

	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;

	private int spaceshipTexture;
	private int asteroidTexture;
	private int spaceTexture;

	private Spaceship spaceShip;
	private Space space, space2;
	private Asteroid[] asteroids = new Asteroid[10];
	private Bullet[] bullets = new Bullet[5];
	//private ArrayList<Asteroid> smallAsteroids = new ArrayList<Asteroid>(20);
	
	private float leftBound = -.8f;
	private float rightBound = .8f;
	
	private Point spaceShipPosition;
	private Vector spaceDownVector;
	private Vector spaceshipVector;
	private Vector bulletVector;
	public int score = 0;
	
	private EvaderActivity eActivity;
	
	public EvaderRenderer(Context context){
		this.context = context;
	}
	
	public EvaderRenderer(Context context, EvaderActivity eActivity){
		this.context = context;
		this.eActivity = eActivity;

	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		space = new Space(new Point(0f, 0f, 0f));
		space2 = new Space(new Point(0f, 2f, 0f));
		spaceShip = new Spaceship();
		
		spaceshipVector = new Vector(0, 0, 0);
		bulletVector = new Vector(0, 0.08f, 0);
		spaceDownVector = new Vector(0f, -0.01f, 0f);
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		asteroidTexture = TextureHelper.loadTexture(context, R.drawable.asteroid_text);
		spaceshipTexture = TextureHelper.loadTexture(context, R.drawable.space);
		spaceTexture = TextureHelper.loadTexture(context, R.drawable.stars_background);
		
		spaceShipPosition = new Point(0f, -0.8f, 0f);
		
		for(int i = 0; i < 10; i++){
			asteroids[i] = new Asteroid(3);
			if(i < 5)
				bullets[i] = new Bullet();
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		score++;
		
		eActivity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				TextView v = (TextView)eActivity.findViewById(R.id.scoreView);
				v.setText("Score: " + score);
			}
			
		});

		if(score % 1000 == 0){
			Constants.STATIC_HEALTH++;
		}
		
		if((spaceShipPosition.x - 0.2f < leftBound && spaceshipVector.x < 0f) || (spaceShipPosition.x + 0.2f > rightBound && spaceshipVector.x > 0f)){
			spaceshipVector = new Vector(0f, 0f, 0f);
		}
			
		spaceShipPosition = spaceShipPosition.translate(spaceshipVector);
		
		
		space.location = space.location.translate(spaceDownVector);
		space2.location = space2.location.translate(spaceDownVector);
		
		if(space.location.y <= -2f)
			space.location = new Point(0f, 2f, 0f);
		if(space2.location.y <= -2f)
			space2.location = new Point(0f, 2f, 0f);
		
		glClear(GL_COLOR_BUFFER_BIT);
		
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
		
		//draw background
		positionObjectInScene(space.location.x, space.location.y, space.location.z);
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, spaceTexture);
		space.bindData(textureProgram);
		space.draw();
		
		//draw second background
		positionObjectInScene(space2.location.x, space2.location.y, space2.location.z);
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, spaceTexture);
		space2.bindData(textureProgram);
		space2.draw();

		
		//draw spaceship
		positionObjectInScene(spaceShipPosition.x, spaceShipPosition.y, spaceShipPosition.z);
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, spaceshipTexture);
		spaceShip.bindData(textureProgram);
		spaceShip.draw();
				
		//draw asteroids
		for(Asteroid s : asteroids){
			if(s.health > 0){
				s.location = s.location.translate(s.downVector);
				positionObjectInScene(s.location.x, s.location.y, s.location.z);
				textureProgram.useProgram();
				textureProgram.setUniforms(modelViewProjectionMatrix, asteroidTexture);
				s.bindData(textureProgram);
				s.draw();
				if(s.location.y <= -1.2f)
					s.newRandomPoint(0.25f, 0f);
			}
		}
		

		for(Bullet bullet : bullets){
			if(bullet.fired == 1){
				bullet.location = bullet.location.translate(bulletVector);
				positionObjectInScene(bullet.location.x, bullet.location.y, bullet.location.z);
				colorProgram.useProgram();
				colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 1f, 0f);
				bullet.bindData(colorProgram);
				bullet.draw();
				
				if(bullet.location.y >= 1.2f)
					bullet.fired = 0;
			}
		}
		
		checkBulletCollision();
		
		checkAsteroidCollision();
	}
	
	private boolean checkBulletCollision(){
		for(Bullet bullet : bullets){
			if(bullet.fired == 1){
				for(Asteroid asteroid: asteroids){
						float distance = Geometry.vectorBetween(bullet.location, asteroid.location).length();
						if(distance < (.03f + asteroid.radius)){
						asteroid.takeDmg(Bullet.damageDealt);
						bullet.fired = 0;
						if(asteroid.health == 0){
							asteroid.newRandomPoint(0.25f, 0f);
							score += 50;
						}
					}
				}
				/*if(!smallAsteroids.isEmpty() && bullet.fired){
					for(Asteroid smallAst : smallAsteroids){
						
					}
				}*/
			}
		}
		return false;
	}
	
	private boolean checkAsteroidCollision(){
		for(Asteroid asteroid : asteroids){
			float distance = Geometry.vectorBetween(spaceShipPosition, asteroid.location).length();
			
			if (distance < (.05f + asteroid.radius)){
				eActivity.setAnswerResult(score);
				eActivity.finish();
			}
		}
		return false;
	}
	
	private void positionObjectInScene(float x, float y, float z){
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, x, y, z);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		//perspectiveM(projectionMatrix,0 , 45, (float) width / (float) height,1f, 10f);
		float aspectRatio = (float)width / (float)height;
		frustumM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 3, 7);
		setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1f, 0f);
	}
	
	public void handleMove(float x){
		spaceshipVector = new Vector(x, 0, 0);
	}
	
	public void handleTouch(){
		for(Bullet bullet : bullets){
			if(bullet.fired == 0){
				bullet.fired = 1;
				bullet.location = new Point(spaceShipPosition.x, spaceShipPosition.y, spaceShipPosition.z);
				break;
			}
		}
	}
}
