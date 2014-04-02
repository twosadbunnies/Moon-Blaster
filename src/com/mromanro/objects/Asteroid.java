package com.mromanro.objects;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.FloatMath;

import com.mromanro.data.VertexArray;
import com.mromanro.evader.Constants;
import com.mromanro.programs.TextureShaderProgram;
import com.mromanro.util.Geometry.Point;
import com.mromanro.util.Geometry.Vector;

public class Asteroid implements Parcelable{
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
			TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	
	public int health;
	
	public Point location = new Point(0f, 1f, 0f);
	public Vector downVector;
	public float radius = 0.2f;
	
	private static float[] VERTEX_DATA = {
		//Triangle 1
		// X, Y, S, T
		-.2f, -.2f, 0f, 1f,
		.2f, .2f, 1f, 0f,
		-.2f, .2f, 0f, 0f,
		
		/*//Triangle 2
		-.2f, -.2f, 0f, 1f,
		.2f, -.2f, 1f, 1f, 
		.2f, .2f, 1f, 0f*/		
	};
	
	private final VertexArray vertexArray;
	
	public Asteroid(int health){
		Constants.STATIC_HEALTH = health;
		this.health = Constants.STATIC_HEALTH;
		
		newRandomPoint(0.25f, 0);
		appendCircle();
		//GeneratedData generatedData = ObjectBuilder.createCircle(new Circle(new Point(0f, 0f, 0f), 0.2f), 32);
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public Asteroid(Parcel in){
		this.health = in.readInt();
		float x = in.readFloat();
		float y = in.readFloat();
		float z = in.readFloat();
		float vecx = in.readFloat();
		float vecy = in.readFloat();
		float vecz = in.readFloat();
		this.location = new Point(x, y, z);
		this.downVector = new Vector(vecx, vecy, vecz);
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void appendCircle(){
		int offset = 0;
		VERTEX_DATA = new float[34 * 4];
		VERTEX_DATA[offset++] = 0f;
		VERTEX_DATA[offset++] = 0f;
		VERTEX_DATA[offset++] = .5f;
		VERTEX_DATA[offset++] = .5f;
		
		for(int i = 0; i <= 32; i++){
			float angleInRadians = ((float) i/ (float) 32) * ((float) Math.PI * 2f);
			
			VERTEX_DATA[offset++] = 0f + 0.2f * FloatMath.cos(angleInRadians);
			VERTEX_DATA[offset++] = 0f + 0.2f * FloatMath.sin(angleInRadians);
			VERTEX_DATA[offset++] = (0f + 0.5f * FloatMath.cos(angleInRadians)) + 0.5f;
			VERTEX_DATA[offset++] = (0f + 0.5f * FloatMath.sin(angleInRadians)) + 0.5f;
		}
	}
	
	public void newRandomPoint(float offsetX, float offsetY){
		float position = (float)Math.random();
		double bool = Math.random();
		if(bool > 0.49)
			position = -position;
		if(position < 0)
			this.location = new Point(position + offsetX, ((float)Math.random() * 5) + 1.2f, 0f); 
		else
			this.location = new Point(position - offsetX, ((float)Math.random() * 5) + 1.2f, 0f);
		downVector = new Vector(0f, -(float)((Math.random() + 0.1) * 0.01) * 2.5f, 0f);
		this.health = Constants.STATIC_HEALTH;
	}
	
	public void bindData(TextureShaderProgram textureProgram){
		vertexArray.setVertexAttribPointer(0,
										textureProgram.getPositionAttributeLocation(),
										POSITION_COMPONENT_COUNT,
										STRIDE);
		
		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, 
										   textureProgram.getTextureCoordinatesAttributeLocation(),
										   TEXTURE_COORDINATES_COMPONENT_COUNT,
										   STRIDE);
	}
	
	public void takeDmg(int dmg){
		health = health - dmg;
	}
	
	public void draw(){
		glDrawArrays(GL_TRIANGLE_FAN, 0, 34);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(health);
		dest.writeFloat(location.x);
		dest.writeFloat(location.y);
		dest.writeFloat(location.z);
		dest.writeFloat(downVector.x);
		dest.writeFloat(downVector.y);
		dest.writeFloat(downVector.z);
	}
	
	public static final Parcelable.Creator<Asteroid> CREATOR = new Parcelable.Creator<Asteroid>() {

		@Override
		public Asteroid createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Asteroid(source);
		}

		@Override
		public Asteroid[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Asteroid[size];
		}
	};
}
