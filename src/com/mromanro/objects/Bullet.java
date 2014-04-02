package com.mromanro.objects;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import android.os.Parcel;
import android.os.Parcelable;

import com.mromanro.data.VertexArray;
import com.mromanro.programs.ColorShaderProgram;
import com.mromanro.util.Geometry.Point;

public class Bullet implements Parcelable{
	
	private static final int POSITION_COMPONENT_COUNT = 3;
	
	private final VertexArray vertexArray;
	
	public final static int damageDealt = 1;
	
	public Point location;
	public int fired = 0;
	
	private static final float[] VERTEX_DATA = {
		//x, y, z
		//Triangle 1
		-0.015f, 0.03f, 0f,
		0.015f, -0.03f, 0f,
		-0.015f, -0.03f, 0f,
		
		//Triangle 2
		-0.015f, 0.03f, 0f,
		0.015f, 0.03f, 0f,
		0.015f, -0.03f, 0f
	};
	
	public Bullet(){
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public Bullet(Parcel in){
		this.fired = in.readInt();
		float x = in.readFloat();
		float y = in.readFloat();
		float z = in.readFloat();
		this.location = new Point(x, y, z);
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void bindData(ColorShaderProgram colorProgram){
		vertexArray.setVertexAttribPointer(0, 
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT,
				0);
	}
	
	public void draw(){
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(fired);
		dest.writeFloat(location.x);
		dest.writeFloat(location.y);
		dest.writeFloat(location.z);
	}
	
	public static final Parcelable.Creator<Bullet> CREATOR = new Parcelable.Creator<Bullet>() {

		@Override
		public Bullet createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Bullet(source);
		}

		@Override
		public Bullet[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Bullet[size];
		}
	};
}
