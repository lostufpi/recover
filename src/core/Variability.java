package core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import core.app.ProjectApplication;
import core.nodes.Element;
import enums.KindVariabilityEnum;

/**
 * Classe para representar uma variabilidade entre produtos de uma mesma família
 * @author Denise
 *
 */
public abstract class Variability {
	
	protected String name;
	private List<ProjectApplication> apps;
	private KindVariabilityEnum type;
	public Variability(){
		this.apps = new ArrayList<ProjectApplication>();
	}
	
	public String getName() {
		return name;
	}
	public List<ProjectApplication> getApps() {
		return apps;
	}
	public void addApp(ProjectApplication pA){
		this.apps.add(pA);
	}
	
	public abstract Object getArtifact();
	public abstract void setArtifact(Object artifact);

	public void setType(KindVariabilityEnum type) {
		this.type = type;
	}

	public KindVariabilityEnum getType() {
		return type;
	}
	
	@Override
	public String toString() {
		String text = "";
		
		text = text.concat("Name: "+name);
		text = text.concat("\nType: "+this.type);
		text = text.concat("\nApplications: \n[");
		for(ProjectApplication pa : this.apps){
			text = text.concat(pa.getName()+"\n");
		}
		text = text.substring(0, text.length());
		text = text.concat("]");
		
		return text;
	}

}
