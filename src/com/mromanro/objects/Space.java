package com.mromanro.objects;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;

import com.mromanro.data.VertexArray;
import com.mromanro.evader.Constants;
import com.mromanro.programs.TextureShaderProgram;
import com.mromanro.util.Geometry.Point;

public class Space {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT +
			TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;
	
	public Point location;
	
	private static float[] VERTEX_DATA = {
		//X, Y, S, T
		//Triangle 1
		-1f, -1f, 0f, 1f,
		-1f, 1f, 0f, 0f,
		1f, -1f, 1f, 1f,
		
		//Triangle 2
		-1f, 1f, 0f, 0f,
		1f, 1f, 1f, 0f, 
		1f, -1f, 1f, 1f
	};
	
	private final VertexArray vertexArray;
	
	public Space(Point location){
		this.location = location;
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
