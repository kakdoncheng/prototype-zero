package engine.gfx;

import org.lwjgl.opengl.GL11;

public class Texture {

	// GL target type & ID
	private int target, textureID;
	
	//img & texture dim
	private int width, height;
	private int texWidth, texHeight;
	
	// img to texture ratio
	private double widthRatio, heightRatio;
	
	public Texture(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
	}
	
	public int $imgHeight() {
		return height;
	}
	public int $imgWidth() {
		return width;
	}
	public double $height() {
		return heightRatio;
	}
	public double $width() {
		return widthRatio;
	}
	
	public void setHeight(int height) {
		this.height = height;
		setHeight();
	}
	public void setWidth(int width) {
		this.width = width;
		setWidth();
	}
	public void setTextureHeight(int texHeight) {
		this.texHeight = texHeight;
		setHeight();
	}
	public void setTextureWidth(int texWidth) {
		this.texWidth = texWidth;
		setWidth();
	}
	
	private void setHeight() {
		if (texHeight != 0) {
			heightRatio = ((double) height)/texHeight;
		}
	}
	private void setWidth() {
		if (texWidth != 0) {
			widthRatio = ((double) width)/texWidth;
		}
	}
	
	public void render(int cx, int cy, double rot, double scale) {
		render(cx, cy, rot, scale, 1, 1, 1, 1);
	}
	
	public void render(int cx, int cy, double rot, double scale, double r, double g, double b, double a) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// bind to the texture
		GL11.glBindTexture(target, textureID);
	
		// translate to the right location/rotation and prepare to draw
		// centered x,y, r deg rot
		GL11.glTranslatef(cx, cy, 0);	
		GL11.glRotated(rot, 0, 0, 1);
		GL11.glColor4d(r,g,b,a);
		
		// draw a quad textured to match the sprite
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(-width*scale/2, -height*scale/2, 0);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(width*scale/2, -height*scale/2, 0);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(width*scale/2, height*scale/2, 0);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(-width*scale/2, height*scale/2, 0);
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	public void destroy(){
		GL11.glDeleteTextures(textureID);
	}
}

