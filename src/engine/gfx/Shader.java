package engine.gfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import engine.Game;

public class Shader {
	
	private int pid;
	private int vs;
	private int fs;
	
	public void setUniform(String name, int value){
		int location=GL20.glGetUniformLocation(pid, name);
		if(location!=-1){
			GL20.glUniform1i(location, value);
		}
	}
	
	public Shader(String vertexShader, String fragmentShader){
		pid=GL20.glCreateProgram();
		vs=loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fs=loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);
		GL20.glAttachShader(pid, vs);
		GL20.glAttachShader(pid, fs);
	}
	
	public void bindAttribute(int location, String name){
		GL20.glBindAttribLocation(pid, location, name);
	}
	
	public void validate(){
		GL20.glLinkProgram(pid);
		GL20.glValidateProgram(pid);
	}
	
	public void start(){
		GL20.glUseProgram(pid);
	}
	
	public void stop(){
		GL20.glUseProgram(0);
	}
	
	public void destroy(){
		stop();
		GL20.glDetachShader(pid, vs);
		GL20.glDetachShader(pid, fs);
		GL20.glDeleteShader(vs);
		GL20.glDeleteShader(fs);
		GL20.glDeleteProgram(pid);
	}
	
	private static int loadShader(String path, int type){
		StringBuilder src=new StringBuilder();
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(Game.class.getResourceAsStream(path)));
			String line;
			while((line=br.readLine())!=null){
				src.append(line).append("//\n");
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		int id=GL20.glCreateShader(type);
		GL20.glShaderSource(id, src);
		GL20.glCompileShader(id);
		if(GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS )==GL11.GL_FALSE){
			System.err.println("engine.gfx.Shader: ERROR: failed to compile shader ("+path+")");
			System.err.println(GL20.glGetShaderInfoLog(id, 500));
			System.exit(-1);
		}
		return id;
	}
}