package core.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import process.AuxCompare;
import core.app.ProjectApplication;
import core.nodes.ClassElement;
import core.nodes.Element;
import core.nodes.PackageElement;
import enums.KindVariabilityEnum;

public class NodeComposition {
	
	private Node node;
	/**
	 * faz a associação entre um item e os projetos que o possuem 
	 */
	private HashMap<Object,List<ProjectApplication>> itemsProjects;
	HashMap<List<ProjectApplication>, Float> percentageOfProjects;
		
	public NodeComposition(Node node){
		this.node=node;
		this.itemsProjects = new HashMap<Object, List<ProjectApplication>>();	
		percentageOfProjects = new HashMap<List<ProjectApplication>, Float>();
		
		run();
		
	}
	
	private void run(){
		
		//fazer um tratamento diferente para classes e paccotes
		
		//fazer o tratamento caso encontre uma classe vazia
		boolean emptyClasses = true;
		
		if(node.getContent().get(0) instanceof ClassElement){
			//a  chave será do tipo BodyDeclaration
			
			//olhar para os itens de classe do conteúdo
			for(Object o : node.getContent()){
				ClassElement content = (ClassElement)o;
				
				CompilationUnit unit2 = content.getAst();
				@SuppressWarnings("unchecked")
				List<AbstractTypeDeclaration> types2 = unit2.types();
				
				for(AbstractTypeDeclaration typeClass2 : types2){
					
					if(typeClass2.getNodeType() != ASTNode.TYPE_DECLARATION && typeClass2.getNodeType() != ASTNode.ENUM_DECLARATION){
						continue;
					}
					
					for(Object oBody : typeClass2.bodyDeclarations()){
						
						emptyClasses = false;
						BodyDeclaration bd = (BodyDeclaration) oBody;
						this.addProject(bd, content.getProject());
					}
				}
				
			}
			if(emptyClasses){
				
				for(Object content : this.node.getContent()){
					//vendo como é q faz com as classes vazias. tá gerando exceção. Caso de teste: \br\com\infowaypi\dolphin\validators\input\ValidacaoLancarDesconto.java
					this.addProject(node, ((Element)content).getProject());
				}
				
			}
			
		}else if(node.getContent().get(0) instanceof PackageElement){
			if(node.getContent().get(0).getName().equals("utils")){
				int a=0;
			}
			//a chave será do tipo Node
			
			emptyClasses=false;
			
			//olhar para o conteúdo dos filhos.
			for(Node child : node.getChildren()){
				for(Object o : child.getContent()){
					Element item = (Element)o;
					this.addProject(child, item.getProject());
				}
			}			
		}
		
		//Calcular as porcentagens
		// esse hashmap é quem vai dizer: projeto 1 e 2 tem tantos em comum, projetos 1,2 e outros tantos, e assim por diante
		HashMap<List<ProjectApplication>, Integer> itemsByProjectsSet = new HashMap<List<ProjectApplication>, Integer>();
		boolean found;
		
		if(this.node.getContent().get(0).getName().contains("config")){
			int a =0;
		}
		int totalitems=0;
		for(Object o : this.itemsProjects.keySet()){
			//this.projects.get(o);
			found=false;

			//este for apenas verifica se o conjunto de projetos armazenados nesta chave do atributo projetos já foi adicionado na variável itemsByProjectsSet
			// Caso já tenha sido, ele soma mais 1
			for(List<ProjectApplication> list : itemsByProjectsSet.keySet()){
				//nesse instante, cada elemento já está associado a seus respectivos projetos. Aqui somente é feita a contagem de quantos elementos são comuns a todos os
				//projetos, ou só de uma parte, ou é único
				
				if(this.compareProjectLists(this.itemsProjects.get(o),list)){
					int qtdItems = 1;
					if (o instanceof Node){
						qtdItems=((Node)o).getContent().size();}
					totalitems+=qtdItems;
					itemsByProjectsSet.put(list,itemsByProjectsSet.get(list)+qtdItems);
					found=true;
				}
			}
			//E caso ainda não tenha sido adicionado, aqui ele é inserido
			if(!found){
				int qtdItems = 1;
				if (o instanceof Node){
					qtdItems=((Node)o).getContent().size();}
				totalitems+=qtdItems;
				if (o instanceof Node){((Node)o).getContent().size();}
				itemsByProjectsSet.put(this.itemsProjects.get(o), qtdItems);
			}
		}
		
		
		for(List<ProjectApplication> list : itemsByProjectsSet.keySet()){
			percentageOfProjects.put(list,Float.valueOf(
					(float)itemsByProjectsSet.get(list)/(float)totalitems));
		}
		
	}

	private void addProject(BodyDeclaration bd, ProjectApplication project) {
		List<ProjectApplication> list = new ArrayList<ProjectApplication>();
		BodyDeclaration key = bd;
		
		for(Object o : this.itemsProjects.keySet()){
			if(o instanceof BodyDeclaration){
				BodyDeclaration bd2 = (BodyDeclaration) o;
				KindVariabilityEnum compareBD = new AuxCompare().compareBodyDeclaration(bd, bd2);
				if(compareBD != null && compareBD.compareTo(KindVariabilityEnum.EQUALS)==0){
					list = itemsProjects.get(bd2);
					key = bd2;
				}
			}
		}
		list.add(project);
		this.itemsProjects.put(key, list);
	}
	
	private void addProject(Node element, ProjectApplication pa){
		List<ProjectApplication> list;
		
		if(this.itemsProjects.containsKey(element)){
			list = itemsProjects.get(element);
		}
		else{
			list = new ArrayList<ProjectApplication>();
		}
		list.add(pa);
		this.itemsProjects.put(element, list);
	}
	
	private boolean compareProjectLists(List<ProjectApplication> l1, List<ProjectApplication> l2){
		
		if(l1.containsAll(l2) && l2.containsAll(l1)){
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		String returne="";
		returne = returne.concat(this.node.toString());
		returne = returne.concat("\n");
		for(List<ProjectApplication> percents : this.percentageOfProjects.keySet()){
			
			returne = returne.concat("\n(");
			for(ProjectApplication pa : percents){
				returne = returne.concat(pa.getName());
				returne = returne.concat(",");
			}
			returne = returne.substring(0, returne.length()-1);
			
			returne = returne.concat("): "+this.percentageOfProjects.get(percents));
		}
		
		return returne;
	}

	public HashMap<List<ProjectApplication>, Float> getPercentageOfProjects() {
		return percentageOfProjects;
	}
	
}
