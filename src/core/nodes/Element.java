package core.nodes;

import core.app.ProjectApplication;
import core.out.Node;
import exceptions.ExistingNodeExcetption;

/**
 * Representa um pacote ou uma classe de uma aplicação
 * @author Denise
 *
 */
public abstract class Element {

	private String name;
	private Element parent;
	private ProjectApplication project;
	private Node node;
	
	public String getName() {
		return name;
	}
	
	public Element(String name, ProjectApplication appName, Element parent){
		this.name = name;
		this.parent = parent;
		this.project = appName;
		this.node=null;
	}

	public Element(String name, Element parent){
		this.name = name;
		this.parent = parent;
		this.project = parent.getProject();
	}

	public Element() {
		
	}

	public Element getParent() {
		return parent;
	}

	public ProjectApplication getProject() {
		return project;
	}
	
	@Override
	public String toString() {
		
		if(this.getParent() != null){
			return this.getParent().toString().concat("."+this.name);
		}
		
		return this.name;
	}
	
	public String toStringWithProject(){
		String returne="";
		
		if(this.getParent() != null){
			returne= this.getParent().toStringWithProject().replace(">","."+this.name);
		}
		else{
			returne = "<"+this.project.getName().concat("/"+this.name);
		}
		
		returne = returne.concat(">");
		
		return returne;
		
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		if(this.node == null){
			this.node = node;
		}
		else{
			throw new ExistingNodeExcetption(this);
		}
	}

	/**
	 * para o caso de reconstruir a arvore
	 */
	public void resetNode() {
		this.node=null;
		
	}
}
