package dados;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import core.Variability;
import core.app.ProjectApplication;
import core.out.Node;

public class Dados {

	public static void calcular(List<Variability> variab, List<ASTNode> comunalidades, List<ProjectApplication> applications){
		int _0=0,_1=0,_2=0;
		int _01=0,_02=0,_12=0;
		int _012=0;
		int pack01=0, classe01=0,met01=0,atr01=0,modif01=0,ret01=0,corpoMet01=0;
		int pack02=0, classe02=0,met02=0,atr02=0,modif02=0,ret02=0,corpoMet02=0;
		int pack12=0, classe12=0,met12=0,atr12=0,modif12=0,ret12=0,corpoMet12=0;
		int pack0=0, classe0=0,met0=0,atr0=0,modif0=0,ret0=0,corpoMet0=0;
		int pack1=0, classe1=0,met1=0,atr1=0,modif1=0,ret1=0,corpoMet1=0;
		int pack2=0, classe2=0,met2=0,atr2=0,modif2=0,ret2=0,corpoMet2=0;
		
		int other=0;
		
		for(Variability v: variab){
			
			if(v.getApps().contains(applications.get(0))){
				if(v.getApps().contains(applications.get(1))){
					if(v.getApps().contains(applications.get(2))){
						_012++;						
					}
					else{
						_01++;
						if(v.getType()!=null)
						switch(v.getType()){
						case ATTRIBUTE:
							atr01++;
							break;
						case MODIFIER:
							modif01++;
							break;
						case METHOD:
							met01++;
							break;
						case PACKAGE:
							pack01++;
							break;
						case RETURN_TYPE:
							ret01++;
							break;
						case CLASS:
							classe01++;
							break;
						case BODY_OF_METHOD:
							corpoMet01++;
							break;
							default:
								other++;							
						}
						else{
							other++;
						}
					}
				}
				else if(v.getApps().contains(applications.get(2))){
					_02++;
					if(v.getType()!=null)
					switch(v.getType()){
					case ATTRIBUTE:
						atr02++;
						break;
					case MODIFIER:
						modif02++;
						break;
					case METHOD:
						met02++;
						break;
					case PACKAGE:
						pack02++;
						break;
					case RETURN_TYPE:
						ret02++;
						break;
					case CLASS:
						classe02++;
						break;
					case BODY_OF_METHOD:
						corpoMet01++;
						break;
						default:
							other++;							
					}
					else{
						other++;
					}
				}
				else{
					_0++;
					if(v.getType()!=null)
					switch(v.getType()){
					case ATTRIBUTE:
						atr0++;
						break;
					case MODIFIER:
						modif0++;
						break;
					case METHOD:
						met0++;
						break;
					case PACKAGE:
						pack0++;
						break;
					case RETURN_TYPE:
						ret0++;
						break;
					case CLASS:
						classe0++;
						break;
					case BODY_OF_METHOD:
						corpoMet0++;
						break;
						default:
							other++;							
					}
					else{
						other++;
					}
				}
			}
			else if(v.getApps().contains(applications.get(1))){
				if(v.getApps().contains(applications.get(2))){
					_12++;
					if(v.getType()!=null)
					switch(v.getType()){
					case ATTRIBUTE:
						atr12++;
						break;
					case MODIFIER:
						modif12++;
						break;
					case METHOD:
						met12++;
						break;
					case PACKAGE:
						pack12++;
						break;
					case RETURN_TYPE:
						ret12++;
						break;
					case CLASS:
						classe12++;
						break;
					case BODY_OF_METHOD:
						corpoMet12++;
						default:
							other++;							
					}
					else{
						other++;
					}
				}
				else{
					_1++;
					if(v.getType()!=null)
					switch(v.getType()){
					case ATTRIBUTE:
						atr1++;
						break;
					case MODIFIER:
						modif1++;
						break;
					case METHOD:
						met1++;
						break;
					case PACKAGE:
						pack1++;
						break;
					case RETURN_TYPE:
						ret1++;
						break;
					case CLASS:
						classe1++;
						break;
					case BODY_OF_METHOD:
						corpoMet1++;
						break;
						default:
							other++;							
					}
					else{
						other++;
					}
				}
			}
			else if(v.getApps().contains(applications.get(2))){
				_2++;
				if(v.getType()!=null)
				switch(v.getType()){
				case ATTRIBUTE:
					atr2++;
					break;
				case MODIFIER:
					modif2++;
					break;
				case METHOD:
					met2++;
					break;
				case PACKAGE:
					pack2++;
					break;
				case RETURN_TYPE:
					ret2++;
					break;
				case CLASS:
					classe2++;
					break;
				case BODY_OF_METHOD:
					corpoMet2++;
					break;
					default:
						other++;							
				}
				else{
					other++;
				}
			}			
			
		}
		
		int metodosComuns=0;
		int atributosComuns=0;
		
		for(ASTNode c : comunalidades){
			if(c.getNodeType() == ASTNode.FIELD_DECLARATION){
				atributosComuns++;
			}
			else if(c.getNodeType() == ASTNode.METHOD_DECLARATION){
				metodosComuns++;
			}
		}
		
		System.out.println("Métodos comuns: "+ metodosComuns+"\nAtributos COmuns: "+atributosComuns);
	
	}
	
	
	public static void debugarNos(List<Node> nodes){
		
		try{
		
			FileWriter arq = new FileWriter("D:\\Estudos\\Mestrado\\Projeto\\resultadoMain2.txt");
			PrintWriter gravarArq = new PrintWriter(arq);
			System.out.println("Iniciando processo de gravação...");
			System.out.println("Quantidade de nodes:"+nodes.size());
			
			int i=1;
			for(Node n: nodes){
				gravarArq.printf("%d .%n",i);
				gravarArq.printf(n.toString().concat("%nPais: %n"));
				gravarArq.printf("\t-->".concat(n.getParent().toString()));
				gravarArq.printf("%n<--%n");
				gravarArq.printf("Filhos: %n");
				gravarArq.printf("\t-->".concat(n.getChildren().toString()));
				gravarArq.printf("%n<--%n%n");
				i++;
			}
			arq.close();
			System.out.println("Gravação concluída");
		}
		catch(IOException e){
			System.out.println("Não foi possível gravar o arquivo");
			e.printStackTrace();
		}
	}
}
