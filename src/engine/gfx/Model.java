package engine.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class Model {
	
	private int drawc;
	private int vid;
	private int tid;
	private int iid;
	
	private IntBuffer createIntBuffer(int[] a){
		IntBuffer buffer=BufferUtils.createIntBuffer(a.length);
		buffer.put(a);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer createFloatBuffer(float[] a){
		FloatBuffer buffer=BufferUtils.createFloatBuffer(a.length);
		buffer.put(a);
		buffer.flip();
		return buffer;
	}
	
	public Model(float[] v, float[] t, int[] i){
		drawc=i.length;
		
		vid=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vid);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(v), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		tid=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tid);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(t), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		iid=GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iid);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, createIntBuffer(i), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void render(){
		//GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		//GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vid);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		//GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tid);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		//GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iid);
		GL11.glDrawElements(GL11.GL_TRIANGLES, drawc, GL11.GL_UNSIGNED_INT, 0);
		
		//GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, drawc);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		//GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		//GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}
}
