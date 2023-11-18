package engine.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class StringGraphics {
	
	private static char nul='?';
	private static String alphabet=" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~Ï€Ð°Ð±Ð²Ð³Ð´ÐµÑ‘Ð¶Ð·Ð¸Ð¹ÐºÐ»Ð¼Ð½Ð¾Ð¿Ñ€Ñ�Ñ‚ÑƒÑ„Ñ…Ñ†Ñ‡ÑˆÑ‰ÑŠÑ‹ÑŒÑ�ÑŽÑ�Ð�Ð‘Ð’Ð“Ð”Ð•Ð�Ð–Ð—Ð˜Ð™ÐšÐ›ÐœÐ�ÐžÐŸÐ Ð¡Ð¢Ð£Ð¤Ð¥Ð¦Ð§Ð¨Ð©ÐªÐ«Ð¬Ð­Ð®Ð¯";
	private static HashMap<Character, Texture> lucida, unifont;
	
	public static void registerFont(String path){
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Font uniFont = null;
		try {
			uniFont = Font.createFont(Font.TRUETYPE_FONT,is);
		} catch (FontFormatException | IOException e) {
			System.out.print("WARNING: Exception loading font "+path+".\n");
			e.printStackTrace();
			return;
		}
		Font f = uniFont.deriveFont(24f);
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(f);
	}
	
	private static void loadCharTexture(char c){
		BufferedImage img=TextureLoader.newTransparentImage(16, 16);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Lucida Console", Font.BOLD, 16));
		g.setColor(Color.WHITE);
		g.drawString(c+"", 0, 14);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		lucida.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	private static void loadCharTextureU(char c){
		BufferedImage img=TextureLoader.newTransparentImage(16, 16);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 16));
		g.setColor(Color.WHITE);
		g.drawString(c+"", 0, 14);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		unifont.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	//new Font("Lucida Console", Font.BOLD, 16);
	// !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
	public static void init(){
		registerFont("./fonts/unifont-14.0.04.ttf");
		lucida=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTexture(c);
		}
		unifont=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTextureU(c);
		}
	}
	public static void destroy(){
		for(HashMap.Entry<Character, Texture> pair: lucida.entrySet()){
			lucida.get(pair.getKey()).destroy();
		}
		for(HashMap.Entry<Character, Texture> pair: unifont.entrySet()){
			unifont.get(pair.getKey()).destroy();
		}
	}
	
	public static void draw(String s, int x, int y){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			lucida.getOrDefault(s.charAt(i), lucida.get(nul)).render(x+8+(int)dx, y+8, 0, 1);
			dx+=(11);
		}
	}
	
	public static void drawUnifont(String s, int x, int y){
		drawUnifont(s, x, y, 1, 1, 1, 1, 1);
	}
	public static void drawUnifont(String s, int x, int y, double r, double g, double b, double a, double scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			unifont.getOrDefault(s.charAt(i), unifont.get(nul)).render(x+8+(int)dx, y+8, 0, scale, r, g, b, a);
			dx+=(9*scale);
		}
	}

}
