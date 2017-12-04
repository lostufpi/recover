package control;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import process.Similarity;
import core.Variability;
import core.app.ProductsFamily;
import core.app.ProjectApplication;
import core.nodes.Element;
import extraction.ParseProject;

/**
 * Classe contendo as regras de negócio
 * @author Denise
 *
 */
public class Scheme implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductsFamily family;
	private List<ASTNode> communCodeBlock;
	private List<Element> communFile;
	private List<Variability> variab;
		
	public Scheme(){
		this.family = new ProductsFamily();
	}
	
	public void extractNodes(List<ProjectApplication> applications, String srcDir){

		this.prepare(applications);
		this.generateNodes(srcDir);
	}
	
	/**
	 * Método para a comunicação entre a fachada e classe de geração da árvore
	 * @param srcDir 
	 * @param limiarPackage 
	 * @return 
	 */
	public ProductsFamily unifyTree(double limiarPackage){
				
		Similarity process = new Similarity(family);
		process.setLimiarPackage(limiarPackage);
		process.run();
				
		return this.family;
	}
	
	/**
	 * Gerar os nós referentes às classes e pacotes
	 * @param applications
	 */
	private void generateNodes(String srcDir) {
		for(ProjectApplication pA : this.family.getApplications()){
			ParseProject.generatePackageElements(pA,srcDir);
			
		}
		
	}

	/**
	 * Método que irá criar os objetos de cada projeto
	 * @param applications
	 */
	private void prepare(List<ProjectApplication> applications){
				
		for(ProjectApplication p : applications){
			this.family.addProduct(p);
		}
	}

	public List<ASTNode> getCommunCodeBlock() {
		return communCodeBlock;
	}

	public List<Element> getCommunFile() {
		return communFile;
	}

	public List<Variability> getVariab() {
		return variab;
	}

}
