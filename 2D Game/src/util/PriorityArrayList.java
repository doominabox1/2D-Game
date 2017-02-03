package util;

import java.util.ArrayList;

import def.Renderable;

public class PriorityArrayList extends ArrayList<Renderable>{
	private static final long serialVersionUID = 5640932718670823909L;
	public boolean addOrdered(Renderable r){
		System.out.println(r.getRenderPriority());
		for (int i = 0; i < this.size(); i++) {
			System.out.println(this.get(i).getRenderPriority() + " " + r.getRenderPriority());
			if(this.get(i).getRenderPriority() >= r.getRenderPriority()){
				this.add(i, r);
				return true;
			}
		}
		this.add(r);
		return true;
	}
}
