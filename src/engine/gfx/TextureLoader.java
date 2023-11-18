package engine.gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import engine.Game;

public class TextureLoader {

	private static HashMap<String, Texture> textures;
	private static ColorModel glAlphaColorModel;
	private static ColorModel glColorModel;
	
	public static int $size(){
		if(textures!=null){
			return textures.size();
		}
		return -1;
	}
	
	public static void init() {
		textures = new HashMap<String, Texture>();
		glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
											new int[] {8,8,8,8},
											true,
											false,
											ComponentColorModel.TRANSLUCENT,
											DataBuffer.TYPE_BYTE);
		glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
											new int[] {8,8,8,0},
											false,
											false,
											ComponentColorModel.OPAQUE,
											DataBuffer.TYPE_BYTE);
	}
	
	//private static IntBuffer createIntBuffer(int size) {
	//	ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
	//	temp.order(ByteOrder.nativeOrder());
	//	return temp.asIntBuffer();
	//}
	private static int newTextureID() { 
		 IntBuffer tmp = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer(); 
		 GL11.glGenTextures(tmp); 
		 return tmp.get(0);
	} 
	private static int $2n(int i) {
		int n=2;
		while(n<i)n*=2;
		return n;
	} 
	
	//buffered img methods
	public static BufferedImage loadImage(String path) {
		try {
			BufferedImage image = ImageIO.read(Game.class.getResource(path));
			return image;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	public static BufferedImage loadImageFromFile(String path) {
		File input;
		try {
			input=new File(path);
			return ImageIO.read(input);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	public static BufferedImage newTransparentImage(int width, int height){
		Color cnul=new Color(0.0f, 0.0f, 0.0f, 0.0f);
		BufferedImage img=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for(int x=0; x<img.getWidth(); x++){
			for(int y=0; y<img.getHeight(); y++){
				img.setRGB(x, y, cnul.getRGB());
			}
		}
		return img;
	}
	public static BufferedImage invertImage(BufferedImage img) {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                if(col.getAlpha()!=0){
                	col = new Color(255-col.getRed(),255-col.getGreen(),255-col.getBlue());
                }
                img.setRGB(x, y, col.getRGB());
            }
        }
        return img;
	}
	public static BufferedImage makeImageTranslucent(BufferedImage source, double alpha) {
	    BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), java.awt.Transparency.TRANSLUCENT);
	    Graphics2D g = target.createGraphics();
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
	    g.drawImage(source, null, 0, 0);
	    g.dispose();
	    return target;
	}
	
	private static ByteBuffer convertImageData(BufferedImage img, Texture txr) { 
		ByteBuffer imageBuffer = null; 
		WritableRaster raster;
		BufferedImage txrImg;
		
		// find the closest power of 2 for the width and height
		// of the produced texture
		int txrWidth=$2n(img.getWidth()), txrHeight=$2n(img.getHeight());
		//while (txrWidth < img.getWidth()) {
		//	txrWidth *= 2;
		//}
		//while (txrHeight < img.getHeight()) {
		//	txrHeight *= 2;
		//}
		txr.setTextureHeight(txrHeight);
		txr.setTextureWidth(txrWidth);
		
		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (img.getColorModel().hasAlpha()) {
			raster=Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,txrWidth,txrHeight,4,null);
			txrImg=new BufferedImage(glAlphaColorModel,raster,false,new Hashtable<Object, Object>());
		} else {
			raster=Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,txrWidth,txrHeight,3,null);
			txrImg=new BufferedImage(glColorModel,raster,false,new Hashtable<Object, Object>());
		}
			
		// copy the source image into the produced image
		Graphics g=txrImg.getGraphics();
		g.setColor(new Color(0f,0f,0f,0f));
		g.fillRect(0,0,txrWidth,txrHeight);
		g.drawImage(img,0,0,null);
		
		// build a byte buffer from the temporary image 
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) txrImg.getRaster().getDataBuffer()).getData(); 

		imageBuffer = ByteBuffer.allocateDirect(data.length); 
		imageBuffer.order(ByteOrder.nativeOrder()); 
		imageBuffer.put(data, 0, data.length); 
		imageBuffer.flip();
		
		return imageBuffer; 
	} 
	
	
	// loading and getting textures
	public static void loadTexture(String id, BufferedImage img){ 
		
		// create the texture ID for this texture 
		int txrID = newTextureID(); 
		Texture txr = new Texture(GL11.GL_TEXTURE_2D, txrID); 
		txr.setWidth(img.getWidth());
		txr.setHeight(img.getHeight());
		
		// bind this texture 
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, txrID); 
 
		// convert that image into a byte buffer of texture data 
		ByteBuffer textureBuffer = convertImageData(img,txr); 
		
		//if using GL11.GL_TEXTURE_2D, which we always will
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
 
		// produce a texture from the byte buffer
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, $2n(img.getWidth()), $2n(img.getHeight()),
				0, img.getColorModel().hasAlpha()?GL11.GL_RGBA:GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureBuffer); 
		
		textures.put(id, txr); 
	} 
	
	public static Texture $texture(String id){
		return textures.get(id);
	}
	
	public static void removeTexture(String id){
		textures.remove(id);
	}
	
	public static void destroy(){
		for(HashMap.Entry<String, Texture> pair: textures.entrySet()){
			textures.get(pair.getKey()).destroy();
		}
		textures.clear();
	}
}

