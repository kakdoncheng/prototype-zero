package engine;

import java.util.ArrayList;
import java.util.HashMap;

public class Attributes {
	
	private HashMap<String, String> metadata;
	private HashMap<String, Double> values;
	
	protected HashMap<String, Double> $values(){
		return values;
	}
	
	public void setMetadata(String key, String label){
		metadata.put(key, label);
	}
	public String $metadata(String key){
		return metadata.getOrDefault(key, null);
	}
	
	public void setValue(String key, double value){
		values.put(key, value);
	}
	public void changeValue(String key, double delta){
		values.put(key, $value(key)+delta);
	}
	public boolean is(String key){
		return values.getOrDefault(key, 0.0d)>0;
	}
	public double $value(String key){
		return values.getOrDefault(key, 0.0d);
	}
	
	
	public void addAttributes(Attributes modifier){
		for(String key:modifier.$values().keySet()){
			if(values.containsKey(key)){
				changeValue(key, modifier.$value(key));
			}else{
				setValue(key, modifier.$value(key));
			}
		}
	}
	public void subtractAttributes(Attributes modifier){
		for(String key:modifier.$values().keySet()){
			if(values.containsKey(key)){
				changeValue(key, -modifier.$value(key));
			}else{
				setValue(key, -modifier.$value(key));
			}
		}
	}
	
	public ArrayList<String> toStringList(){
		ArrayList<String> list=new ArrayList<String>();
		for(HashMap.Entry<String, String> entry : metadata.entrySet()){
			list.add(entry.getKey()+": "+entry.getValue());
		}
		for(HashMap.Entry<String, Double> entry : values.entrySet()){
			list.add(entry.getKey()+": "+entry.getValue());
		}
		return list;
	}
	
	public Attributes(){
		metadata=new HashMap<String, String>();
		values=new HashMap<String, Double>();
	}
	
	public Attributes clone(){
		Attributes a=new Attributes();
		for(HashMap.Entry<String, String> entry : metadata.entrySet()){
			a.setMetadata(entry.getKey(), entry.getValue());
		}
		for(HashMap.Entry<String, Double> entry : values.entrySet()){
			a.setValue(entry.getKey(), entry.getValue());
		}
		return a;
	}
}
