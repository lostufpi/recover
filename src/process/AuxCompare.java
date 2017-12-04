package process;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import core.ASTNodeVariability;
import core.app.ProjectApplication;
import core.nodes.ClassElement;
import core.nodes.Element;
import enums.KindVariabilityEnum;

public class AuxCompare {

	/**
	 * Compara o código de uma mesma classe em todas as aplicações
	 * @param classe Classe a ser comparada
	 * @param level Nìvel em que a classe se encontra
	 * @param compProjs 
	 */
	@SuppressWarnings("unchecked")
	void compareClasses(ClassElement classe, int level, CompareProjects compProjs){

		//checar se ela é igual em todos os projetos

		//coletar todas as classes
		List<ClassElement> classes = new ArrayList<ClassElement>();
		for(ProjectApplication pa : compProjs.getFamily().getApplications()){

			List<Element> elemsInLevel = pa.getElementsByLevel().get(level);
			classes.add(
					(ClassElement) elemsInLevel.get((elemsInLevel.indexOf(classe))));
		}
		//comparar
		boolean iguais = true;
		for(int i=0;i<classes.size();i++){
			ClassElement c1 = classes.get(i);
			CompilationUnit unit1 = c1.getAst();

			boolean achouDiferente = false;

			for(int j=i+1; j<classes.size();j++){

				ClassElement c2 = classes.get(j);
				CompilationUnit unit2 = c2.getAst();

				if(!unit1.subtreeMatch(new ASTMatcher(), unit2)){
					achouDiferente=true;
					break;
				}
			}
			if(achouDiferente){
				iguais=false;
				break;
			}
		}

		if(iguais) return;

		//comparar os elementos

		//pegar a primeira classe
		for(int i=0;i<classes.size();i++){
			ClassElement c1 = classes.get(i);
			CompilationUnit unit1 = c1.getAst();
			List<AbstractTypeDeclaration> types1 = unit1.types();

			for (AbstractTypeDeclaration typeClass1 : types1) {
				//Returns an integer value identifying the type of this concrete AST node
				if (typeClass1.getNodeType() == ASTNode.TYPE_DECLARATION) {//A type declaration is the union of a class declaration and an interface declaration
					// Class def found

					//Returns the live ordered list of body declarations of this type declaration. Atributos e métodos nesse caso 
					List<BodyDeclaration> bodies1 = typeClass1.bodyDeclarations();

					//pegar o elemento a ser comparado
					for(BodyDeclaration bd1 : bodies1){
						//para cada método/atributo de bodies1, vamos percorrer as demais classes, afim de saber se o mesmo é comum ou variante

						if(this.communASTNContains(bd1, (TypeDeclaration)typeClass1, compProjs)){
							continue;
						}

						//usado para verificação
						ASTNodeVariability v = new ASTNodeVariability();
						v.setArtifact(bd1);
						v.setClasse(c1.toString());
						if(compProjs.variabContains(v,classe)){
							compProjs.addAppVariab(v, c1.getProject(),c1);//v.addApp(classe.getProject());
							continue;
						}

						//de inicio, assumo que é comum, caso não for encontrado nalguma classe, seto como falso
						boolean common = true;

						//pegar a segunda classe
						for(int j=0; j<classes.size();j++){

							ClassElement c2 = classes.get(j);

							if(c2.getProject().equals(c1.getProject())){
								continue;
							}

							CompilationUnit unit2 = c2.getAst();
							
							//procurar classe equivalente
							if(!c2.equals(c1)) continue;

							List<AbstractTypeDeclaration> types2 = unit2.types();
							for (AbstractTypeDeclaration typeClass2 : types2) {
								//Returns an integer value identifying the type of this concrete AST node
								if (typeClass1.getNodeType() == ASTNode.TYPE_DECLARATION) {//A type declaration is the union of a class declaration and an interface declaration
									// Class def found

									//Returns the live ordered list of body declarations of this type declaration. Atributos e métodos nesse caso 
									List<BodyDeclaration> bodies2 = typeClass2.bodyDeclarations();

									/**
									 * informa se o bd1 achou um igual ou uma variante em bodies2
									 */
									boolean foundEqual = false;
									boolean foundVariant = false;
									//pegar o elemento a ser comparado
									for(BodyDeclaration bd2 : bodies2){

										//comparar bd1 com bd2
										KindVariabilityEnum k = compareBodyDeclaration(bd1,bd2);
										
										if(k==null) continue;
										
										switch(k){
										case EQUALS:
											foundEqual=true;
											break;
										case MODIFIER:
											compProjs.createVariabilityASTNode(classe, c1.getProject(), KindVariabilityEnum.MODIFIER,bd1);
											compProjs.createVariabilityASTNode(classe, c2.getProject(), KindVariabilityEnum.MODIFIER,bd2);
											foundVariant=true;
											break;
										case RETURN_TYPE:
											compProjs.createVariabilityASTNode(classe, c1.getProject(), KindVariabilityEnum.RETURN_TYPE,bd1);
											compProjs.createVariabilityASTNode(classe, c2.getProject(), KindVariabilityEnum.RETURN_TYPE,bd2);
											foundVariant=true;
											break;
										case BODY_OF_METHOD:
											//nesse caso criar uma comunalidade para o método e uma variabilidade para o corpo
											compProjs.createVariabilityASTNode(classe, c1.getProject(), KindVariabilityEnum.BODY_OF_METHOD, bd1);
											compProjs.createVariabilityASTNode(classe, c2.getProject(), KindVariabilityEnum.BODY_OF_METHOD, bd2);
											//para dizer que encontrou o mesmo método, apesar de seu corposer diferente
											foundEqual=true;
											break;
										default:
											break;
										}
										
										break;
									}

									if(!foundEqual && !foundVariant){
										compProjs.createVariabilityASTNode(classe,c1.getProject(),null,bd1);
									}
									if(!foundEqual){
										//TODO se o tipo de variabilidade for o corpo do método, então então o método deverá ser considerado comum. Para isso dar certo, a
										//busca terá que afinar a granularidade
										common = false;
									}
								}
							}
							if(!common){
								break;
							}
						}
						if(common){
							compProjs.addCommunalityASTN(bd1);
						}
					}
				}
			}



		}

	}
	
	private boolean communASTNContains(ASTNode node, TypeDeclaration classe, CompareProjects compProjs){
		for(ASTNode n : compProjs.getCommunalitiesASTN()){
			
			if(node.subtreeMatch(new ASTMatcher(), n)){			
				//Se isso acontecer, devo comparar se eles pertecem à mesma classe, e mesmos pacotes
				
				ASTNode typeD;
				typeD = n.getParent();
				while(typeD.getParent() != null){
					
					if (typeD instanceof TypeDeclaration){
						TypeDeclaration typeD2 = (TypeDeclaration) typeD;
						//if(!typeD2.isInterface()){
							
							//compara se os nomes das classes são iguais
							if( typeD2.getName().getIdentifier().
							equals(classe.getName().getIdentifier())){
								
								//o pai é um CompilationUnit
								//comparar a declaração de pacotes dos dois
								String typeD2Name = ((CompilationUnit)typeD2.getParent()).getPackage().getName().getFullyQualifiedName();
								String typeDName = ((CompilationUnit)typeD.getParent()).getPackage().getName().getFullyQualifiedName();
								
								if(typeDName.equals(typeD2Name)){
									return true;
								}
							/*}
							else{
								return false;
							}*/
						}						
					}
					typeD = typeD.getParent();
				}
			}					
		}
		return false;
			
	}
	
	public KindVariabilityEnum compareBodyDeclaration(BodyDeclaration bd1, BodyDeclaration bd2) {
		
		if(bd1.getNodeType() == ASTNode.FIELD_DECLARATION){
			if(bd2.getNodeType() != ASTNode.FIELD_DECLARATION){
				return null;
			}
			
			FieldDeclaration field1 = (FieldDeclaration) bd1;
			FieldDeclaration field2 = (FieldDeclaration) bd2;
			
			if(field1.subtreeMatch(new ASTMatcher(), field2)){
				return KindVariabilityEnum.EQUALS;
			}
			for(Object varDec1 : field1.fragments()){
				VariableDeclarationFragment var1 = (VariableDeclarationFragment) varDec1;
				
				for(Object varDec2 : field2.fragments()){
					VariableDeclarationFragment var2 = (VariableDeclarationFragment) varDec1;
					
					//TODO comparar um a um
				}
			}
			
			//TODO  Futuro: afinar a granularidade da comparação dos atributos. VariableDeclarationFragments
			
		}
		
		if(bd1.getNodeType() == ASTNode.METHOD_DECLARATION){
			
			if(bd2.getNodeType() != ASTNode.METHOD_DECLARATION){
				return null;
			}
			KindVariabilityEnum l = this.equalMethods(bd1,bd2); 
			
			return l;
			
		}
		
		if(bd1.getNodeType()== ASTNode.TYPE_DECLARATION){
			if(bd2.getNodeType() != ASTNode.TYPE_DECLARATION){
				return null;
			}
			if(bd1.subtreeMatch(new ASTMatcher(), bd2)){
				return KindVariabilityEnum.EQUALS;
			}
		}
		
		if(bd1.getNodeType()==ASTNode.INITIALIZER){
			if(bd2.getNodeType()!=ASTNode.INITIALIZER){
				return null;
			}
			if(((Initializer)bd1).getBody()!=null && ((Initializer)bd1).getBody().statements().isEmpty()){
				if(((Initializer)bd2).getBody()!=null && ((Initializer)bd2).getBody().statements().isEmpty()){
					return KindVariabilityEnum.EQUALS;
				}
			}
		}
		
		return null;
		
	}

	@SuppressWarnings("unchecked")
	/**
	 * Compara dois métodos
	 * @param bd1
	 * @param bd2
	 * @return 
	 */
	private KindVariabilityEnum equalMethods(BodyDeclaration bd1, BodyDeclaration bd2){
				
		MethodDeclaration method1 = (MethodDeclaration) bd1;
		MethodDeclaration method2 = (MethodDeclaration) bd2;

		//nomes diferentes
		if(!method1.getName().getIdentifier().equals(method2.getName().getIdentifier())){
			return null;
		}
		
		if(method1.subtreeMatch(new ASTMatcher(), method2) ||
				emptyBodies(method1, method2)){
			return KindVariabilityEnum.EQUALS;
		}
			
		//verificar se um e somente um é construtor
		if(method1.isConstructor() ^ method2.isConstructor()){
			return null;
		}
		
		//comparar parâmetros
		if(!(this.sameParameters(method1, method2))){
			//métodos de assinatura diferente, não podemos compará-los
			return null;
			//continue
		}
		
		//TODO ver como vai ficar essa comparação e criação de variabilidades
		//comparar o retorno
		if(!method1.isConstructor()){
			if(!method1.getReturnType2().subtreeMatch(new ASTMatcher(), method2.getReturnType2())){
				
				//se o retorno é diferente, então trata-se de uma variabilidade				
				return KindVariabilityEnum.RETURN_TYPE;
			}
		}
		
		//comparar modificadores
		if (method1.getModifiers() != method2.getModifiers()){
			
			return KindVariabilityEnum.MODIFIER;
		}
		
		//compara lançamentos de exceções
		if(!this.sameExceptions(method1, method1)){
			return KindVariabilityEnum.THROWN_EXCEPTION;
		}
		
		//até agora sei que as assinaturas dos métodos são iguais, mas eles não são iguais
		//comparar
		
		// comparar demais elementos do método
		//Algoritmo:
		//chamar o Body
		//pegar a list statments do Body, uma lista de  Statement
		//verificar que tipo de statement é cada um
		
		//se tem a mesma assinatura, mas não são exatamente iguais, enão a diferença está no corpo do método
		
		//quando um dos métodos tem um suppress warning e o outro não, mesmo sendo iguais, a comparação direta retorna falso. Já que as demais comparações deram ok, verificamos o corpo agora
		if ((method1.getBody() != null && method2.getBody() != null) && 
				method1.getBody().subtreeMatch(new ASTMatcher(), method2.getBody())){
			return KindVariabilityEnum.EQUALS;
		}
		
		return KindVariabilityEnum.BODY_OF_METHOD;
		
	}

	private boolean emptyBodies(MethodDeclaration method1,
			MethodDeclaration method2) {
		boolean empty=false;
		
		if((method1.getBody() != null && method2.getBody() != null) &&
				(method1.getBody().statements().isEmpty() && method2.getBody().statements().isEmpty())){
			empty=true;
		}
		return empty;
	}
	
	private boolean sameParameters(MethodDeclaration m1, MethodDeclaration m2){
		
		List<Object> parameters1 = m1.parameters();
		List<Object> parameters2 = m2.parameters();
		
		int size = parameters1.size();
		
		if(size != parameters2.size())
			return false;
		
		for(int i=0;i<size;i++){
			SingleVariableDeclaration parameter1 = (SingleVariableDeclaration) parameters1.get(i);
			SingleVariableDeclaration parameter2 = (SingleVariableDeclaration) parameters2.get(i);
			if(!parameter1.subtreeMatch(new ASTMatcher(), parameter2)){
				return false;
			}
		}
		
		return true;
	}
	
	private boolean sameExceptions(MethodDeclaration m1, MethodDeclaration m2){
		
		List<Object> excep = m1.thrownExceptions();
		List<Object> excep2 = m2.thrownExceptions();
		
		int size = excep.size();
		
		if(size != excep2.size())
			return false;
		
		for(int i=0;i<size;i++){
			SimpleName parameter1 = (SimpleName) excep.get(i);
			SimpleName parameter2 = (SimpleName) excep2.get(i);
			if(!parameter1.subtreeMatch(new ASTMatcher(), parameter2)){
				return false;
			}
		}
		
		return true;
	}
}