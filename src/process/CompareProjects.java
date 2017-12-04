package process;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Initializer;

import core.ASTNodeVariability;
import core.ElementVariability;
import core.Variability;
import core.app.ProductsFamily;
import core.app.ProjectApplication;
import core.nodes.ClassElement;
import core.nodes.Element;
import core.nodes.PackageElement;
import enums.KindVariabilityEnum;

/**
 * Classe para comparar projetos de uma mesma família
 * @author Denise
 *
 */
public class CompareProjects {
	
	private List<Variability> variabilities;
	private List<Element> communalitiesElem;
	private List<ASTNode> communalitiesASTN;
	private ProductsFamily family;
	private AuxCompare compare;
		
	public CompareProjects(ProductsFamily fam){
		this.variabilities = new ArrayList<Variability>();
		this.communalitiesElem = new ArrayList<Element>();
		this.communalitiesASTN = new ArrayList<ASTNode>();
		this.family = fam;
		this.compare = new AuxCompare();
	}

	public void execute(){
		
		for(ProjectApplication pa : this.family.getApplications()){
			pa.generateLevels();
		}
		
		int level=0;
		boolean levelExists =true;
		int countExecution;
		
		//busca por níveis
		while(levelExists){
			levelExists=false;
			countExecution=0;
			for(ProjectApplication pa1 : family.getApplications()){
				if(pa1.getElementsByLevel().size()<=level){
					continue;
				}
				else{
					levelExists=true;
				}
				List<Element> elemLevel = pa1.getElementsByLevel().get(level);
				
				for(Element e : elemLevel){
					
					//usado para verificação
					ElementVariability v = new ElementVariability();
					v.setArtifact(e);
					
					if(communalitiesElem.contains(e)){
						continue;
					}
					if(variabContains(v,e)){

						this.addAppVariab(v, pa1,e);//v.addApp(pa1);
						continue;
					}
					//se já estou na segunda rodada e o elemento em questão não é comum, e não está em variabilities, então ele pode ser considerado uma nova variabilidade
					if(countExecution==1){
						v.setName(e.getName());
						v.addApp(pa1);
						if(v.getArtifact() instanceof PackageElement){
							v.setType(KindVariabilityEnum.PACKAGE);
						}
						else{
							v.setType(KindVariabilityEnum.CLASS);
						}
						variabilities.add(v);
						continue;
					}
					
					boolean common = true;
					//comparar o elemento atual (e) com todos os outros elementos de mesmo nível das outras aplicações
					for(ProjectApplication pa2 : family.getApplications()){
						if(!pa2.getElementsByLevel().get(level).contains(e)){
							//encontrou variabilidade
							common=false;
							break;
						}
					}
					if(common){
						communalitiesElem.add(e);
						
						//se encontrou a mesma classe em todas as aplicações, é necessário ainda compará-la internamente
						if(e instanceof ClassElement){
							compare.compareClasses((ClassElement)e,level, this);
						}
					}
					else{
						v.setName(e.getName());
						v.addApp(pa1);
						if(v.getArtifact() instanceof PackageElement){
							v.setType(KindVariabilityEnum.PACKAGE);
						}
						else{
							v.setType(KindVariabilityEnum.CLASS);
						}
						this.addVariability(v);
					}
					
				}
				countExecution++;
			}
			level++;
		}
	}
		
	
	/**
	 * 
	 * @param bd1
	 * @param pa1 
	 * @param bd2
	 * @param pa2 
	 * @param classe 
	 * @param pa 
	 * @return Retorna:
	 * returne[0]:  true se os BodyDeclaration são iguais
	 * return[1]: se foi detectada uma variabilidade. Nesse caso ela já terá sido criada
	 */
	

	/**
	 * 
	 * @param v Variabilidade contendo o artefato
	 * @param classe Classe onde ocorre a variabilidade
	 * @param app Aplicação onde ocorre a variabilidade
	 */
	void createVariabilityASTNode(ClassElement classe, ProjectApplication app, KindVariabilityEnum type, ASTNode artifact) {
		
		ASTNodeVariability v = new ASTNodeVariability();
		
		//ignorar os initializers vazios
		if(artifact.getNodeType() == ASTNode.INITIALIZER){
			if(!((Initializer)artifact).getBody().toString().contains(";")){
				return;
			}
		}
		
		if(type==null){
		
			if(artifact.getNodeType() == ASTNode.FIELD_DECLARATION){
				type = KindVariabilityEnum.ATTRIBUTE;
			}
			else if(artifact.getNodeType() == ASTNode.METHOD_DECLARATION){
				type = KindVariabilityEnum.METHOD;
			}
		}

		v.setType(type);
		v.setArtifact(artifact);
		v.setClasse(classe.toString());
		
		v.setName(classe);
		v.addApp(app);
		if(this.variabContains(v, classe)){
			this.addAppVariab(v, app, classe);
		}
		else{
			this.addVariability(v);
		}
		
	}

	
	/**
	 * Verifica se o atributo variabilities já contem o elemento, ou algum de seus ancestrais 
	 * @param v O elemento a ser verificado
	 * @param classeOrPackage 
	 * @return verdadeiro caso contenha
	 */
	boolean variabContains(Variability v, Element classeOrPackage) {
			
		if(this.variabilities.contains(v)) return true;
		
		if(v instanceof ElementVariability){
			
			Element eParent;
			eParent = ((Element)v.getArtifact()).getParent();
			if(eParent == null) return false; //não encontrou
			ElementVariability aux = new ElementVariability();
			aux.setArtifact(eParent);
			aux.setName(eParent.getName());
			return variabContains(aux,classeOrPackage);
		}
		else 
			if(v instanceof ASTNodeVariability){
			
				ASTNode astParent;
				astParent = ((ASTNode)v.getArtifact()).getParent();
				if(astParent != null){
					ASTNodeVariability aux = new ASTNodeVariability();
					aux.setArtifact(astParent);
					aux.setClasse(classeOrPackage.toString());
					return variabContains(aux,classeOrPackage);
				}
				else{
					Variability aux = new ElementVariability();
					aux.setArtifact(classeOrPackage);
					return variabContains(aux, classeOrPackage);
				}
			
		}
		
		return false;
	}
	
	
	/**
	 * Adiciona uma aplicação a uma variabilidade detectada previamente
	 * @param e
	 * @param pa
	 * @param classe Classe a qual pertence a variabilidade
	 */
	void addAppVariab(Variability e, ProjectApplication pa, Element classe){
		
		Variability v = this.getVariability(e, pa, classe);//variabilities.get(variabilities.indexOf(e));
		
		if(!v.getApps().contains(pa)){
			v.addApp(pa);
		}
		
	}

	/**
	 * Método para retornar a variabilidade contida no atributo variabilities correspondente à variabilidade passada por parâmetro. Se o atributo não possuir diretamente
	 * a variabilidade, mas possuir um ancestral desta, esse será retornado
	 * @param v Variabilidade a ser buscada
	 * @param pa Projeto que contem a variabilidade
	 * @return A variabilidade passada por parâmetro, caso ela pertença ao atributo variabilities, ou um ancestral dela, caso ele pertença ao atributo variabilities 
	 */
	private Variability getVariability(Variability v,ProjectApplication pa, Element classOrPackage){
		
		if(this.variabilities.contains(v)) return variabilities.get(variabilities.indexOf(v));
		
		if(v instanceof ElementVariability){
			
			Element eParent;
			eParent = ((Element)v.getArtifact()).getParent();
			if(eParent == null) return null; //não encontrou
			ElementVariability aux = new ElementVariability();
			aux.setArtifact(eParent);
			aux.setName(eParent.getName());
			return getVariability(aux,pa,classOrPackage);
		}
		else 
			if(v instanceof ASTNodeVariability){
			
				ASTNode astParent;
				astParent = ((ASTNode)v.getArtifact()).getParent();
				if(astParent != null){
					ASTNodeVariability aux = new ASTNodeVariability();
					aux.setArtifact(astParent);
					aux.setClasse(classOrPackage.toString());
					return getVariability(aux,pa,classOrPackage);
				}
				else{
					Variability aux = new ElementVariability();
					aux.setArtifact(classOrPackage);
					return getVariability(aux, pa, classOrPackage);
				}
			
		}
		
		return null;
		
	}
	
	private void addVariability(Variability v){
				
		this.variabilities.add(v);
	}
	
	void addCommunalityASTN(ASTNode node){
		this.communalitiesASTN.add(node);
	}
	
	public List<Variability> getVariabilities() {
		return variabilities;
	}

	public List<Element> getCommunalitiesElem() {
		return communalitiesElem;
	}

	public List<ASTNode> getCommunalitiesASTN() {
		return communalitiesASTN;
	}

	public ProductsFamily getFamily() {
		return family;
	}

	public void setFamily(ProductsFamily family) {
		this.family = family;
	}
}
