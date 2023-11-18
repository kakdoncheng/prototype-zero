package engine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileExplorer {
	
	public static ArrayList<String> $filesFromDirectory(String path, boolean recursive){
		ArrayList<String> dir=new ArrayList<String>();
		ArrayList<String> files=new ArrayList<String>();
		dir.add(path);
		while(dir.size()>0){
			File cdir = new File(dir.remove(0));
			File[] list=cdir.listFiles();
			for(File f:list){
				if(f.isFile()){
					files.add(f.getPath());
				}else if(recursive && f.isDirectory()){
					dir.add(f.getPath());
				}
			}
		}
		return files;
	}
	
	public static ArrayList<String> loadTextFromFile(String path){
		ArrayList<String> lines=new ArrayList<String>();
		FileReader file;
		BufferedReader buffer;
		String line;
		try {
			file=new FileReader(path);
			buffer=new BufferedReader(file);
			while((line=buffer.readLine())!=null){
				lines.add(line);
			}
			buffer.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
}
