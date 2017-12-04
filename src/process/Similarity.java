package process;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import core.app.ProductsFamily;
import core.app.ProjectApplication;
import core.nodes.ClassElement;
import core.nodes.Element;
import core.nodes.PackageElement;
import core.out.Node;
import core.out.NodeComposition;
import enums.KindVariabilityEnum;

public class Similarity {

	private List<ProjectApplication> projects;
	private double limiar;
	private double limiarPackage;
	private AuxCompare comparator;
	private List<Node> nodes;
	private ProductsFamily family;
	
	public Similarity(ProductsFamily family){
		this.family = family;
		this.projects = new ArrayList<ProjectApplication>();
		this.projects.addAll(family.getApplications());
		this.comparator = new AuxCompare();
		this.limiar = 0.65;  
		this.nodes = new ArrayList<Node>();
	}
	
	public void addProject(ProjectApplication pa){
		this.projects.add(pa);
	}
	
	public void run(){
		//zerar os nós
		reset();
		calculateClasses();
		//("Organizar pacotes")
		arrangePackages();
		nodesToAloneElements();
		//("localizar pais")
		pointParents();
		nodesComposition();
	}

	/**
	 * Comparar as classes com base em seus atributos e métodos
	 */
	@SuppressWarnings("unchecked")
	public void calculateClasses(){
		
		List<ClassElement> classesPA1 = new ArrayList<ClassElement>();
		List<ClassElement> classesPA2 = new ArrayList<ClassElement>();
				
		//FOR DE FORA (projetos)
		for(ProjectApplication pa1: this.projects){
			
			//FOR DE FORA (classes)
			classesPA1 = extractClassesFromProjApp(pa1);
			
			//for de dentro (projetos)
			for(ProjectApplication pa2: this.projects){
			
				//evitar comparações repetidas
				if(this.projects.indexOf(pa1) >= this.projects.indexOf(pa2)){
					continue;
				}
			
				classesPA2 = extractClassesFromProjApp(pa2);
				for(ClassElement class1 : classesPA1){
					
					CompilationUnit unit1 = class1.getAst();
					List<AbstractTypeDeclaration> types1 = unit1.types();
					
					List<BodyDeclaration> bodies1 = new ArrayList<BodyDeclaration>(); 
				
					//FOR DE FORA (os tipos declarados dentro do arquivo)
					for (AbstractTypeDeclaration typeClass1 : types1) {
						//Returns an integer value identifying the type of this concrete AST node
						if (typeClass1.getNodeType() != ASTNode.TYPE_DECLARATION) {
							continue;
							//A type declaration is the union of a class declaration and an interface declaration
						}
						// Class def found
		
						//Returns the live ordered list of body declarations of this type declaration. Atributos e métodos nesse caso 
						bodies1.addAll(typeClass1.bodyDeclarations());
					}
						
					//for de dentro (classes)
					for(ClassElement class2 : classesPA2){
						CompilationUnit unit2 = class2.getAst();
						List<AbstractTypeDeclaration> types2 = unit2.types();
						
						List<BodyDeclaration> bodies2 = new ArrayList<BodyDeclaration>(); 
						
						//for de dentro (os tipos declarados dentro do arquivo)
						for(AbstractTypeDeclaration typeClass2 : types2){
							
							if(typeClass2.getNodeType() != ASTNode.TYPE_DECLARATION){
								continue;
							}
							//TODO Adiconar tratamento para enum
							// if(typeClass2.getNodeType() != ASTNode.ENUM_DECLARATION)
							
							// vai pegar todos os bodies de todos os typeDeclarations que tiverem dentro do arquivo
							bodies2.addAll(typeClass2.bodyDeclarations());							
						}
						
						double rate = compareBodiesLists(bodies1,bodies2);
						
						if((rate > this.limiar)||
								(Double.isNaN(rate) && class1.toString().equals(class2.toString()))){

							joinElements(class1, class2);							
						}
					}								
				}
			}	
		}
	}

	private Node joinElements(Element element1, Element element2) {
		
		
		/*
		 * Antes de juntar os nós vou ter que verificar se o element1 não já foi juntado com algum filho de element2 e vice-versa
		 * Esse é o caso de qdo em um projeto o desenvolvedor resolve refatorar as classes criando um novo pacote dentro de outro
		 * agrupando as classes que tem algo em comum, assim o pacote do outro projeto acaba por ser similar ao pai e ao filho ao 
		 * mesmo tempo.
		 */
		
		Node nodeClass1 = node(element1);
		//verificar se no conteúdo de nodeClass1 existe algum filho de element2
		if(nodeClass1 != null && nodeClass1.isDescendentOf(element2)){
			return null;
		}
		
		Node nodeClass2 = node(element2);
		//verificar se no conteúdo de nodeClass2 existe algum filho (na árvore do projeto) de element1
		if(nodeClass2 != null && nodeClass2.isDescendentOf(element1)){
			return null;
		}
		
		Node finale=null;
		
		//Se qualquer um deles for diferente de nulo, ou seja, se node já foi instanciado, ou se já existe node para a classe 1 ou para a classe 2
		if((nodeClass1 != null) || (nodeClass2 != null)){
			
			if( nodeClass1 != null){
				addElementInNode(element2,nodeClass1);
				finale = nodeClass1;
			}
			else{
				addElementInNode(element1,nodeClass2);
				finale = nodeClass2;
			}
		}
		else{							
			finale = newNode(element1,element2);
		}
				
		return finale;
	}
	
	/**
	 * cria nodes para as classes que ficaram sozinhas 
	 * @param classesPA1 
	 */
/*	private void nodesToAloneClasses(List<ClassElement> classes) {
		
		for(ClassElement c: classes){
			if(this.node(c)==null){
				this.newNode(c);
			}
		}
	}*/

	private void addElementInNode(Element element, Node node){
		//verificar se o node já contem a classe
		if(node.contains(element)){
			return;
		}
		
		node.addContent(element);
		element.setNode(node);
	}
	
	private Node newNode(Element... element){
		
		Node node = new Node(element);
		this.nodes.add(node);
		
		for(int i=0;i<element.length;i++){
			element[i].setNode(node);
		}
		
		return node;
	}
	
	/**
	 * Retorna o objeto Node que contém o elemento passado como parâmetro. Caso, não exista, retorna null
	 * @param element
	 * @return
	 */
	private Node node(Element element) {
		
		if(element == null) return null;
		
		for(Node n : this.nodes){
			for(Object o :n.getContent()){
				if(!(o instanceof Element)){
					continue;
				}
			
				if(element.equals(o) && element.getProject().equals(((Element)o).getProject())){
					return n;
				}			
			}
		}
		
		return null;
	}

	private double compareBodiesLists(List<BodyDeclaration> bodies1,
			List<BodyDeclaration> bodies2) {
				
		double common = 0;
		int bodiesEquals =0;
		boolean found;
		for(BodyDeclaration bd1: bodies1){
			
			 found=false;
			
			for(BodyDeclaration bd2: bodies2){
				KindVariabilityEnum variab = comparator.compareBodyDeclaration(bd1, bd2);
				
				if(variab == null){
					continue;
				}
				
				switch(variab){
				
				case MODIFIER:
				case RETURN_TYPE:
				case BODY_OF_METHOD:
				case THROWN_EXCEPTION:
					common+=0.5;
					found=true;
					break;
				case EQUALS:
					common++;
					found=true;
					bodiesEquals++;
					break;
				default:
					System.out.println("lascou");
				}
				if(found){
					break;
				}
			}
		}
		double total = bodies1.size()+bodies2.size() - bodiesEquals;
		return (common/total);
		
	}

	private List<ClassElement> extractClassesFromProjApp(ProjectApplication pa){
		List<ClassElement> classes = new ArrayList<ClassElement>();
		
		pa.generateLevels();
		
		for(List<Element> nivel : pa.getElementsByLevel()){
			for(Element e : nivel){
				if(e instanceof ClassElement){
					classes.add((ClassElement) e);
				}
			}
		}
		
		return classes;
	}

	public List<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Estruturar a árvore a partir do último nível
	 */
	private void arrangePackages(){

		//procurar o último nível
		int lastLevel=0;
		
		for(ProjectApplication pa : this.projects){
			List<List<Element>> elementsByLevel = pa.getElementsByLevel();
			int size = elementsByLevel.size();
			if(size > (lastLevel+1)){
				lastLevel = size-1;
			}
		}
		
		arrangeByLevel(lastLevel);
		
	}

	private void arrangeByLevel(int lastLevel) {
		
		for(int level = lastLevel;level>=0;level--){
			
			for(ProjectApplication pa : this.projects){
				try{			
					
					//verificar se o projeto possui esse nivel
					if((pa.getElementsByLevel().size()-1) < level) continue;
						
					for(Element e : pa.getElementsByLevel().get(level)){
						Node node = e.getNode();
						if(node != null){
							
							verifyParent(node);
						}
					
					}
				}catch(java.lang.IndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
			
		}
		
	}

	/**
	 * Método para verificar se os pais de elementos agrupados podem ser agrupados também
	 * @param node nó que contem os elementos que já estão agrupados
	 */
	private void verifyParent(Node node) {
		
		List<Element> elements = node.getContent();
		
		//comparar os pais par a par
		//pegar o primeiro pai
		for(int i =0;i<elements.size();i++){
			Object o = elements.get(i);
			
			Element e1 = (Element) o;
			Element parent1 = e1.getParent();
			if(parent1==null)continue;
			
			//pegar o segundo pai
			for(int j=0;j<elements.size();j++){
			
				//evitar comparações repetidas
				if(j <= i){
					continue;
				}
				Object ob = elements.get(j);
				
				Element e2 = (Element) ob;
				Element parent2 = e2.getParent();
				if(parent2==null)continue;
				
				Node nodeParent = node(parent1);
				if(nodeParent!=null && nodeParent.contains(parent2)){
					continue;
				}
				
				if(samePackages((PackageElement)parent1, (PackageElement)parent2)){
					
					joinElements(parent1, parent2);
				}
			}
		}
		
	}

	private boolean samePackages(PackageElement element1, PackageElement element2) {
		
		//obter a lista de filhos
		List<Element> children1 = element1.getChildren();
		List<Element> children2 = element2.getChildren();
		//qtd de filhos "like"
		int like = 0;
		
		//comparar os filhos
		for(Element e1 : children1){
			
			for(Element e2 : children2){
				
				//verificar se são do mesmo tipo
				if(	(e1 instanceof ClassElement && e2 instanceof ClassElement) ||
					(e1 instanceof PackageElement && e2 instanceof PackageElement)){
					
					//verificar se pertencem ao mesmo node
					if(e1.getNode() != null && e2.getNode() != null && e1.getNode().equals(e2.getNode())){
						like+=2;
					}
				}
				else{
					continue;
				}
			}
		}
		int total = children1.size()+children2.size();
		if( ((float)like/total) >= this.limiarPackage){
			return true;
		}
		return false;
		
	}

	private void nodesToAloneElements() {
		
		for(ProjectApplication pa : this.projects){
			List<List<Element>> elementsByLevel = pa.getElementsByLevel();
			for(List<Element> list : elementsByLevel){
				for(Element e : list){
					if(e.getNode()==null){
						this.newNode(e);
					}
				}
			}
		}
		
	}
	/**
	 * Diz quem é pai de quem
	 */
	private void pointParents() {
		
		List<Node> parents;
			
		List<Node> toRemove = new ArrayList<Node>();
		
		for(Node node :  this.nodes){
			
			//armazena os pais 
			parents = new ArrayList<Node>();
			
			Node parent=null;
			boolean different=false;
			for(Object o : node.getContent()){
				
				Node parent_ = node(((Element)o).getParent());
				
				if(parent_ != null) parents.add(parent_);
				if(parent == null){
					parent = parent_;
				}
				else if(!parent_.equals(parent)){
					different = true;
				}
			}
			//			parents será vazio se se tratar do elemento root
			if(parents.isEmpty()){
				
				//gambiarra!! deve haver apenas um elemento root. Se já tiver um setado, então vou mesclar os dois
				Node root = this.family.getFinalTree();
				
				if(root == null){				
					this.family.setFinalTree(node);
				}
				else{
					for(Node o : node.getChildren()){
						root.addChild(o);
					}
					for(Node o : node.getParent()){
						root.addParent(o);
					}
					for(Element o : node.getContent()){
						root.addContent(o);
					}
					toRemove.add(node);
				}
				continue;
			}
			
			if(!different){
				//os pais apontam para o mesmo objeto, então basta fazer a operação para um deles
				parents.get(0).addChild(node);
				node.addParent(parents.get(0));
			}
			else if(different){				
				//o nó cujo conteúdo possuir mais de um pai terá mais de um pai
				//na árvore esse nó deverá ser exibido duas vezes, mas com algum destaque para que o usuário consiga identificar que esta duplicado
				node.setDifferentParents(true);
				for(Node nodeParent : parents){
					node.addParent(nodeParent);
					nodeParent.addChild(node);
				}
				
			}
		}
		for(Node n : toRemove){
			this.nodes.remove(n);
		}	
	}

	/**
	 * Método que verificará a proporção de cada projeto em um nó
	 */
	private void nodesComposition() {
		
		for(Node node : this.nodes){
			
			node.setComposition(new NodeComposition(node));			
			
		}
		
	}

	/**
	 * Resetar os nós e o elemento raiz
	 */
	private void reset() {
		
		for(ProjectApplication pa: this.projects){
			
			pa.generateLevels();
			
			for(List<Element> nivel : pa.getElementsByLevel()){
				for(Element e : nivel){
					e.resetNode();
				}
			}
		}
		
		this.family.setFinalTree(null);
	}

	public double getLimiarPackage() {
		return limiarPackage;
	}

	public void setLimiarPackage(double limiarPackage) {
		this.limiarPackage = limiarPackage;
	}
	
}
