package core.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import core.app.ProjectApplication;

public class PackageElement extends Element implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Element> children;
	
	public PackageElement() {
		
	}
	
	public PackageElement(String name, ProjectApplication appName, Element parent){
		super(name,appName,parent);
		this.children = new ArrayList<Element>();
	}
	
	public PackageElement(String name, Element parent){
		super(name,parent);
		this.children = new ArrayList<Element>();
	}
	public void addChild(Element child){
		this.children.add(child);
		
	}
	
	@Override
	/*
	 * Não precisam pertencer ao mesmo projeto para retornar true
	 * 
	 */
	public boolean equals(Object elem) {
		if(elem == null) return false;
		
		if(!(elem instanceof PackageElement)) return false;
		
		if(!this.getName().equals(((PackageElement)elem).getName())) return false;
		
		if(this.getParent() != null 				&& !this.getParent().equals(((PackageElement)elem).getParent())) return false;
		if(((PackageElement)elem).getParent() != null && !((PackageElement)elem).getParent().equals(this.getParent())) return false;
		
		return true;
	}
	
	public List<Element> getChildren() {
		return children;
	}
	
}
