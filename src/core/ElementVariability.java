package core;

import core.nodes.Element;

public class ElementVariability extends Variability {

	private Element artifact;

	@Override
	public boolean equals(Object obj) {
		
		//TODO falta verificar se as variabilidades pertencem a classes "equivalentes"
		
		if(!(obj instanceof ElementVariability)) return false;
		
		if(!((ElementVariability)obj).getName().equals(this.artifact.getName())) return false;
		
		return ((ElementVariability)obj).artifact.equals(this.artifact);
	}

	@Override
	/**
	 * Retorna um objeto do tipo Element
	 */
	public Object getArtifact() {
		return artifact;
	}

	@Override
	/**
	 * Recebe um objeto do tipo Element
	 */
	public void setArtifact(Object artifact) {
		if(!(artifact instanceof Element)) return;
		this.artifact = (Element)artifact;
	}

	public void setName(String name){
		this.name = name;
	}
	
}
