package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Attributes;
import engine.State;
import engine.entities.Hardpoint;
import engine.entities.Hitbox;
import engine.gfx.Camera;
import engine.gfx.StringGraphics;
import engine.gfx.TextureLoader;
import engine.utils.FileExplorer;

public class LoadingState extends State{
	
	public static final String LOAD_PATH=".\\";
	public static final char COMMENT_DELIMITER='#';

	private PrototypeGame game;
	
	private boolean skip;
	private boolean prompt;
	private boolean dat;
	private boolean txr;
	private boolean bgs;
	
	private int index;
	private ArrayList<String> paths;
	private ArrayList<String> text;
	
	public LoadingState(PrototypeGame game){
		this.game=game;
	}
	
	public void reset() {
		skip=false;
		prompt=true;
		dat=false;
		txr=false;
		bgs=false;
		index=0;
		paths=null;
		text=new ArrayList<String>();
	}
	
	// helper parser methods
	private void parseWeapon(ArrayList<String[]> data){
		Attributes a=new Attributes();
		String label="???";
		String datatype="attributes";
		HashSet<String> datatypes=new HashSet<String>();
		datatypes.add("attributes");
		datatypes.add("sounds");
		datatypes.add("particles");
		
		for(String[] values:data){
			if(datatypes.contains(values[0])){
				datatype=values[0];
			}
			if(values.length<2){
				continue;
			}
			switch(datatype){
				case "sounds":
					break;
				case "particles":
					break;
				default:
					try{
						double v=Double.parseDouble(values[1]);
						a.setValue(values[0], v);
					}catch(NumberFormatException nfe){
						a.setMetadata(values[0], values[1]);
					}
					break;
			}
		}
		
		if(a.$metadata("label")!=null){
			label=a.$metadata("label");
		}else{
			label=a.$metadata("weapon");
			a.setMetadata("label", label);
		}
		
		// register weapon
		WeaponBlueprint.registerBlueprint(label, a);
	}
	private void parseOutfit(ArrayList<String[]> data){
		Attributes a=new Attributes();
		String label="???";
		for(String[] values:data){
			try{
				double v=Double.parseDouble(values[1]);
				a.setValue(values[0], v);
			}catch(NumberFormatException nfe){
				a.setMetadata(values[0], values[1]);
			}
		}
		if(a.$metadata("label")!=null){
			label=a.$metadata("label");
		}else{
			label=a.$metadata("outfit");
			a.setMetadata("label", label);
		}
		
		// debug print
		/*
		for(String s:a.toStringList()){
			System.out.println(s);
		}
		System.out.println();
		//*/
		
		// create outfit
		Outfit.registerOutfit(label, a);
	}
	private void parseBlueprint(ArrayList<String[]> data){
		String model="???";
		Attributes a=new Attributes();
		HashMap<String, Integer> outfits=new HashMap<String, Integer>();
		HashMap<String, Integer> cargo=new HashMap<String, Integer>();
		ArrayList<Hardpoint> engineHardpoints=new ArrayList<Hardpoint>();
		ArrayList<Hardpoint> weaponHardpoints=new ArrayList<Hardpoint>();
		ArrayList<Hitbox> hitboxes=new ArrayList<Hitbox>();
		// currently unused
		ArrayList<String> weapons=new ArrayList<String>();
		
		String datatype="attributes";
		HashSet<String> datatypes=new HashSet<String>();
		datatypes.add("attributes");
		datatypes.add("outfits");
		datatypes.add("cargo");
		datatypes.add("hitbox");
		datatypes.add("weapons");
		datatypes.add("turrets");
		datatypes.add("engines");
		
		// get model name from blueprint line
		if(data.get(0).length>1){
			model=data.get(0)[1];
			a.setMetadata("model", model);
		}
		
		// parse core data
		for(String[] values:data){
			if(datatypes.contains(values[0])){
				datatype=values[0];
			}
			if(values.length<2){
				continue;
			}
			switch(datatype){
				case "hitbox":
					try{
						double hx=Double.parseDouble(values[0]);
						double hy=Double.parseDouble(values[1]);
						double hr=16;
						if(values.length>2){
							hr=Double.parseDouble(values[2]);
						}
						hitboxes.add(new Hitbox(null, hx, hy, hr));
					}catch(NumberFormatException nfe){
						// handle exception
					}
					break;
				case "outfits":
					try{
						int i=Integer.parseInt(values[1]);
						outfits.put(values[0], i);
					}catch(NumberFormatException nfe){
						// handle exception
					}
					break;
				case "cargo":
					try{
						int i=Integer.parseInt(values[1]);
						cargo.put(values[0], i);
					}catch(NumberFormatException nfe){
						// handle exception
					}
					break;
				case "weapons":
					try{
						double wx=Double.parseDouble(values[0]);
						double wy=Double.parseDouble(values[1]);
						double wt=0;
						if(values.length>2){
							wt=Double.parseDouble(values[2]);
						}
						weaponHardpoints.add(new Hardpoint(wx, wy, wt));
						
						// weapon assignments currently unused,
						// this is a placeholder
						if(values.length>3){
							weapons.add(values[3]);
						}else{
							weapons.add(null);
						}
					}catch(NumberFormatException nfe){
						// handle exception
					}
					break;
				// currently no support for turrets
				case "turrets":
					break;
				case "engines":
					try{
						double ex=Double.parseDouble(values[0]);
						double ey=Double.parseDouble(values[1]);
						engineHardpoints.add(new Hardpoint(ex, ey, 0));
					}catch(NumberFormatException nfe){
						// handle exception
					}
					break;
				default:
					try{
						double v=Double.parseDouble(values[1]);
						a.setValue(values[0], v);
					}catch(NumberFormatException nfe){
						a.setMetadata(values[0], values[1]);
					}
					break;
			}
		}
		
		// override model name found in blueprint line
		// if expressly stated in attributes
		if(a.$metadata("model")!=null){
			model=a.$metadata("model");
		}
		
		// create blueprint
		SpacecraftBlueprint.registerBlueprint(model, a, outfits, cargo, hitboxes, engineHardpoints, weaponHardpoints);
	}

	public void update(double dt) {
		// in game loop, update happens first
		// skipping first tick will allow render to happen first
		///*
		if(skip){
			skip=false;
			return;
		}
		//*/
		
		// break loop once everything is loaded
		// as loading stars is the final step, only check bgs flag
		if(bgs){
			if(prompt){
				text.add("Ready.");
				prompt=false;
			}
			// on left/right mouse click or enter pressed
			if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
				///*
				//game.setFPSLimit(30);
				//game.renderFPS(true);
				game.renderBackgroundStars(true);
				State.setCurrentState(1);
				State.$currentState().reset();
				//*/
			}
			
		}else{
			// uncap for quick loading
			//game.setFPSLimit(9000);
		}
		
		// load data from text files
		if(!dat){
			
			// set path if null
			if(paths!=null){
				// break if no more files found
				if(paths.size()==index){
					
					dat=true;
					index=0;
					paths=null;
					text.add(SpacecraftBlueprint.registeredBlueprints()+" spacecraft blueprints loaded.");
					text.add(WeaponBlueprint.registeredBlueprints()+" weapon blueprints loaded.");
					text.add(Outfit.registeredOutfits()+" outfits loaded.");
					
					// load textures next
					skip=true;
					return;
				}
				
				// parse texture name and path
				String path=paths.get(index);
				String[] labels=path.split("[.\\\\]"); // what the fuck is regex
				
				// only load txt files for now
				// parse files line by line, building models slowly
				if(labels.length>0 && labels[labels.length-1].equals("txt")){
					// raw lines
					ArrayList<String> lines=FileExplorer.loadTextFromFile(path.replace("\\", "\\\\"));
					// selected datatype
					String datatype="end";
					ArrayList<String[]> data=new ArrayList<String[]>();
					// valid datatypes
					HashSet<String> datatypes=new HashSet<String>();
					datatypes.add("finalize");
					datatypes.add("blueprint");
					datatypes.add("outfit");
					datatypes.add("weapon");
					
					// clean up each line and parse
					for(String line:lines){
						// stop loading from this text file
						if(datatype.equals("finalize")){
							break;
						}
						
						// trim line
						line=line.trim();
						// skip empty lines
						// windows uses carriage return paired with new line
						// i.e. "\r\n"
						if(line.isEmpty() || line.charAt(0)=='\r' || line.charAt(0)=='\n'){
							continue;
						}
						// skip comment lines
						if(line.charAt(0)==COMMENT_DELIMITER){
							continue;
						}
						
						// split line into parts
						String[] values=line.split("[,:;]");
						// trim each part
						for(int i=0;i<values.length;i++){
							values[i]=values[i].trim();
						}
						
						// check current line for datatype
						// only the blueprint datatype are currently supported
						// if valid datatype is specified, select correct parser, load with current data, reset data
						if(datatypes.contains(values[0])){
							switch(datatype){
								case "blueprint":
									parseBlueprint(data);
									break;
								case "outfit":
									parseOutfit(data);
									break;
								case "weapon":
									parseWeapon(data);
									break;
								default:
									break;
							}
							datatype=values[0];
							data=new ArrayList<String[]>();
						}
						
						// add line to data
						data.add(values);
					}
				}
				index++;
				
			}else{
				paths=FileExplorer.$filesFromDirectory(LOAD_PATH+"data", true);
			}
			
			// update loading screen text
			if(paths!=null){
				if(index<1){
					text.add("Loading data "+index+"/"+paths.size());
				}else{
					text.set(text.size()-1, "Loading data "+index+"/"+paths.size());
				}
			}
			
		// if textures havent loaded yet,
		// attempt to load textures
		}else if(!txr){ 
			
			// set path if null
			if(paths!=null){
				// break if no more files found
				if(paths.size()==index){
					
					txr=true;
					index=0;
					paths=null;
					text.add(TextureLoader.$size()+" textures loaded.");
					
					// load stars next
					skip=true;
					text.add("Creating stardust.");
					return;
				}
				
				// parse texture name and path
				String path=paths.get(index);
				String[] labels=path.split("[.\\\\]"); // what the fuck is regex
				
				// only load pngs for now
				if(labels.length>0 && labels[labels.length-1].equals("png")){
					TextureLoader.loadTexture(labels[labels.length-2], TextureLoader.loadImageFromFile(path.replace("\\", "\\\\")));
				}
				index++;
				
			}else{
				paths=FileExplorer.$filesFromDirectory(LOAD_PATH+"txr", true);
			}
			
			// update loading screen text
			if(paths!=null){
				if(index<1){
					text.add("Loading textures "+index+"/"+paths.size());
				}else{
					text.set(text.size()-1, "Loading textures "+index+"/"+paths.size());
				}
			}
		
		// load background stars
		}else if(!bgs){
			game.reloadBackgroundStars();
			bgs=true;
		}
	}

	public void render(Camera c) {
		
		// color
		double red=1;
		double green=1;
		double blue=1;
		
		// render text
		for(int i=0; i<text.size(); i++){
			StringGraphics.drawUnifont(text.get(i), -game.$displayWidth()/2, (game.$displayHeight()/2)-8-(14*(text.size()-i)), red, green, blue, 0.5, 1);
		}
		
		// render loading bar
		int size=3;
		int bit=-1;
		int progress=game.$displayWidth()/2;
		
		if(paths!=null){
			bit=game.$displayWidth()/paths.size();
			progress=(bit*index)-(game.$displayWidth()/2);
		}
		
		// use triangles, not quads
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_TRIANGLES);
		
		GL11.glColor4d(red, green, blue, 0.5);
		
		// bottom left corner
		GL11.glVertex2d(-game.$displayWidth()/2, game.$displayHeight()/2);
		GL11.glVertex2d(progress, game.$displayHeight()/2);
		GL11.glVertex2d(-game.$displayWidth()/2, game.$displayHeight()/2-size);
		
		// top right corner
		GL11.glVertex2d(progress, game.$displayHeight()/2);
		GL11.glVertex2d(progress, game.$displayHeight()/2-size);
		GL11.glVertex2d(-game.$displayWidth()/2, game.$displayHeight()/2-size);
		
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
