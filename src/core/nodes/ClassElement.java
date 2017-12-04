package core.nodes;

import java.io.Serializable;

import org.eclipse.jdt.core.dom.CompilationUnit;

public class ClassElement extends Element implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompilationUnit ast;
	
	public ClassElement(String name, Element parent){
		super(name,parent);
	}
	
	public void setAst(CompilationUnit ast){
		this.ast = ast;
	}

	public CompilationUnit getAst() {
		return ast;
	}

	@Override
	/**
	 * Retorna true se os dois elementos tiverem o mesmo nome e os mesmos pais. Os demais atributos não são checados. Não precisam pertencer ao mesmo projeto
	 */
	public boolean equals(Object elem) {
		
		if(elem == null) return false;
		
		if(!(elem instanceof ClassElement)) return false;
		
		if(!this.getName().equals(((ClassElement)elem).getName())) return false;
		
		if(this.getParent() != null 				&& !this.getParent().equals(((ClassElement)elem).getParent())) return false;
		if(((ClassElement)elem).getParent() != null && !((ClassElement)elem).getParent().equals(this.getParent())) return false;
		
		return true;
	}
	
}
