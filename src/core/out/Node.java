package core.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.app.ProjectApplication;
import core.nodes.Element;

public class Node {

	/**
	 * Itens que vai agrupar
	 */
	private List<Element> contents;
	private List<Node> parents;
	private List<Node> children;
	private NodeComposition composition;
	private boolean differentParents;
	
	public Node(Element... e){
		this.parents = new ArrayList<Node>();
		this.children = new ArrayList<Node>();
		//adicionar os conteudos
		this.contents = new ArrayList<Element>(Arrays.asList(e));
	}

	public void addContent(Element o){
		
		if(o.toString().contains("br.com.infowaypi.dolphin.financeiro")){
			System.out.println(o.toString());
		}
		
		this.contents.add(o);
	}
	
	public List<Element> getContent() {
		return contents;
	}

	public List<Node> getParent() {
		return parents;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	public void addParent(Node parent){
		
		if(this.parents.contains(parent)) return;		
		
		this.parents.add(parent);
	}
	
	public void addChild(Node child){
		
		if(this.children.contains(child)) return;
		
		this.children.add(child);
	}
	
	public boolean contains(Element element){
		for(Object o : this.contents){
			if(o instanceof Element){
				Element c = (Element)o;
				if(c.equals(element) && c.getProject().equals(element.getProject())){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		
		String returne = "{";
		
		for(Object c : this.contents){
			Element element = (Element) c;
			
			returne = returne.concat(element.toStringWithProject()).concat("\n");
			
		}
		//retirar o último \n
		returne = returne.substring(0, returne.length()-1);
		
		return returne+"}";
	}
	
	public List<ProjectApplication> getProjects(){
		
		List<ProjectApplication> returne = new ArrayList<ProjectApplication>();
		
		for(Object c : this.contents){
			Element element = (Element)c;
			returne.add(element.getProject());
		}
		return returne;
	}

	public NodeComposition getComposition() {
		return composition;
	}

	public void setComposition(NodeComposition composition) {
		this.composition = composition;
	}
	

	/**
	 * Retorna uma string formada pelo os nomes dos projetos aos quais pertencem o conteúdo do nó
	 * @return
	 */
	public List<String> projectsOfTheNode(){
		List<String> list = new ArrayList<String>();
		for(Element e : this.contents){
			list.add(e.getProject().getNameToDisplay());
		}
		return list;
	}

	public boolean hasDifferentParents() {
		return differentParents;
	}

	public void setDifferentParents(boolean differentParents) {
		this.differentParents = differentParents;
	}
	
	/**
	 * Verifica se o objeto this é descendente de um nó
	 * @param e possível descendência
	 * @return
	 */
	public boolean isDescendentOf(Element ancestral){
				
		//estava fazendo errrado
		// na verdade tenho é que verificar se no conteúdo deste nó existe descendente, considerando a árvore do projeto, do elemento ancestral
		
		//percorrer os elementos do pai
		for(Node parent: this.parents){
			for(Element e : parent.contents){
				if(e.equals(ancestral) && e.getProject().equals(ancestral.getProject())){
					return true;
				}
			}
		}
		
		//recursão
		for(Node parent: this.parents){
			if( parent.isDescendentOf(ancestral)){
				return true;
			}
		}
		
		return false;
	}
}
