package util;

import interfaces.Renderable;

import java.util.Comparator;

public class RenderableComparator implements Comparator<Renderable>{
	@Override
	public int compare(Renderable one, Renderable two) {
		return one.getRenderPriority() - two.getRenderPriority();
	}

}
