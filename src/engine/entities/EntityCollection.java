package engine.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import engine.Vector;
import engine.gfx.Camera;

public class EntityCollection<E extends Entity> {
	
	private int renderDistance;
	private ArrayList<E> entities;
	private ArrayList<E> queue;
	private ArrayList<E> removed;
	
	private Comparator<E> updateSorter;
	public void setSortOrder(Comparator<E> sorter){
		updateSorter=sorter;
	}
	public Comparator<E> $yAxisSorter(){
		return new Comparator<E>(){
			public int compare(E a, E b) {
				if(!(a!=null&&b!=null))
					return 0;
				if(a.$y()<b.$y())
					return -1;
				if(a.$y()>b.$y())
					return 1;
				return 0;
			}
		};
	}
	
	public EntityCollection() {
		renderDistance=0;
		setSortOrder(null);
		entities=new ArrayList<E>();
		queue=new ArrayList<E>();
		removed=new ArrayList<E>();
	}
	
	public void update(double dt) {
		if(queue.size()>0){
			for(E e:queue){
				if(e!=null)
					entities.add(e);
			}
			queue.clear();
		}
		if(removed.size()>0){
			removed.clear();
		}
		if(updateSorter!=null){
			entities.sort(updateSorter);
		}
		Iterator<E> ie=entities.iterator();
		while(ie.hasNext()){
			E e=ie.next();
			e.update(dt);
			
			//attempt to die first, then remove if still inactive
			if(!e.isActive()){
				e.onDeath();
			}
			if(!e.isActive()){
				ie.remove();
				removed.add(e);
			}
		}
	}
	
	public void render(Camera c) {
		for(E e:entities){
			if(Vector.distanceFromTo(e.$x(), e.$y(), c.$dx(), c.$dy())<=renderDistance){
				e.render(c);
			}
		}
	}
	
	
	public void addEntity(E e) {
		queue.add(e);
	}
	public void clear(){
		while(entities.size()>0){
			entities.remove(0);
		}
		while(queue.size()>0){
			queue.remove(0);
		}
	}
	public void setRenderDistance(int d){
		this.renderDistance=d;
	}
	public double $renderDistance(){
		return this.renderDistance;
	}
	public int $size(){
		return entities.size();
	}
	public int $sizeOf(Class<?> entityType){
		int amt=0;
		for(E e:entities){
			if(e.getClass().getTypeName().equals(entityType.getTypeName())){
				amt++;
			}
		}
		for(E e:queue){
			if(e.getClass().getTypeName().equals(entityType.getTypeName())){
				amt++;
			}
		}
		return amt;
	}
	public ArrayList<E> $entities(){
		return entities;
	}
	public ArrayList<E> $lastRemovedEntities(){
		return removed;
	}

}
