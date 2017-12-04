package core;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import core.nodes.Element;
import exceptions.InvalidArtifactException;


public class ASTNodeVariability extends Variability {

	private ASTNode artifact;
	/**
	 * Classe (e seu caminho) a qual pertence o artefato.
	 */
	private String classe;
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ASTNodeVariability)) return false;
		
		if(!this.classe.equals(((ASTNodeVariability)obj).classe)) return false;
		
		return ((ASTNodeVariability)obj).artifact.subtreeMatch(new ASTMatcher(), this.artifact);
	}
	@Override
	/**
	 * Retorna um objeto do tipo ASTNode
	 */
	public Object getArtifact() {
		return artifact;
	}

	@Override
	/**
	 * Recebe um objeto do tipo ASTNode
	 */
	public void setArtifact(Object artifact) {
		
		if(!(artifact instanceof ASTNode)){
			throw new InvalidArtifactException(artifact.getClass().getName(),this.artifact.getClass().getName());
		}
		
		this.artifact = (ASTNode)artifact;
	}

	public void setName(Element classe){
		if(this.artifact instanceof FieldDeclaration){
			this.setNameAttribute(classe);
		}
		else if(this.artifact instanceof MethodDeclaration){
			this.setNameMethod(classe);
		}
	}
	
	/**
	 * Atribuição do nome para um nó do tipo atributo
	 * @param classe
	 * @param method
	 */
	private void setNameMethod(Element classe) {
		MethodDeclaration meth = (MethodDeclaration)artifact;
		this.name = classe.getName().
				concat("_"+getType().getName()).
				concat("_"+meth.getReturnType2()).
				concat("_"+meth.getName().getIdentifier()).
				concat("_"+meth.parameters()).
				concat("_"+Integer.toString(meth.getModifiers()));
	}
	/**
	 * Atribuição do nome para um nó do tipo método
	 * @param classe
	 * @param attribute
	 */
	private void setNameAttribute(Element classe){
		
		this.name =  
				classe.getName().
				concat("_"+((FieldDeclaration)artifact).toString().replace(" ", "_"));
	}
	public String getClasse() {
		return classe;
	}
	public void setClasse(String classe) {
		this.classe = classe;
	}
}
