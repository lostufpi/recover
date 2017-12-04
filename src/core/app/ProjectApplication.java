package core.app;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import core.nodes.Element;
import core.nodes.PackageElement;

/**
 * Classe para representar um integrante da família de produtos
 * @author Denise
 *
 */
public class ProjectApplication  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File fileApplication;
	/**
	 * Nó inicial da árvore
	 */
	private Element rootElement;
	private List<List<Element>> elementsByLevel;
	private String name;
	private String nameToDisplay;
	
	public ProjectApplication(){
		this.elementsByLevel = new ArrayList<List<Element>>();
	}

	public ProjectApplication(File project){
		this.fileApplication = project;
		this.elementsByLevel = new ArrayList<List<Element>>();
		this.name = project.getAbsolutePath();
		if(getNameToDisplay()==null) setNameToDisplay(project.getName());
	}
	
	public void setRootElement(Element element){
		this.rootElement = element;
	}
	
	public void generateLevels(){
		
		//verificar se já foi gerado
		if(this.elementsByLevel.size()>0){
			return;
		}
		
		int level=0;
		
		elementsByLevel.add(new ArrayList<Element>());
		elementsByLevel.get(level).add(rootElement);
		level++;
		
		addNextLevel(rootElement,level);
		
		
	}
	/**
	 * 
	 * @param elem
	 * @param level próximo nível a ser adicionado
	 */
	private void addNextLevel(Element elem,int level) {
		if (elem instanceof PackageElement){
			for(Element e: ((PackageElement) elem).getChildren()){
				if(elementsByLevel.size()<=level){
					elementsByLevel.add(new ArrayList<Element>());
				}
				elementsByLevel.get(level).add(e);
				addNextLevel(e, level+1);
				
			}
		}
		
	}

	public List<List<Element>> getElementsByLevel() {
		return elementsByLevel;
	}
	
	public File getFileApplication() {
		return fileApplication;
	}
	
	public String getFileApplicationName(){
		return fileApplication.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof ProjectApplication)) return false;
		
		return this.fileApplication.equals(((ProjectApplication)obj).fileApplication);
	}

	public String getName() {
		return name;
	}
	
	public String getNameToDisplay(){
		return this.nameToDisplay;
	}

	public void setNameToDisplay(String nameToDisplay) {
		this.nameToDisplay = nameToDisplay;
	}
}
