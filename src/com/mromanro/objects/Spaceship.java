package com.mromanro.objects;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import android.os.Parcel;
import android.os.Parcelable;

import com.mromanro.data.VertexArray;
import com.mromanro.evader.Constants;
import com.mromanro.programs.TextureShaderProgram;
import com.mromanro.util.Geometry.Circle;
import com.mromanro.util.Geometry.Point;



public class Spaceship{
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
			TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	public Circle collisionCircle;
	public Point location;
	
	private static final float[] VERTEX_DATA = {
		//Triangle 1
		// X, Y, S, T
		-.1f, -.1f, 0f, 1f,
		.1f, .1f, 1f, 0f,
		-.1f, .1f, 0f, 0f,
		
		//Triangle 2
		-.1f, -.1f, 0f, 1f,
		.1f, -.1f, 1f, 1f, 
		.1f, .1f, 1f, 0f	
		
	};
	
	private final VertexArray vertexArray;
	
	public Spaceship(){
		vertexArray = new VertexArray(VERTEX_DATA);
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
	
	public void draw(){
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

}
