package out;

import java.util.List;

import core.app.ProductsFamily;
import core.app.ProjectApplication;
import core.nodes.Element;
import core.out.Node;
import core.out.NodeComposition;
import exceptions.NodeNotFoundException;

public class NodeCompositionShow {
	
	private String selectedNode;
	private ProductsFamily family;
	private Node node;

	public NodeCompositionShow(String selectedNode, ProductsFamily family) {
		super();
		this.selectedNode = selectedNode;
		this.family = family;
	}

	/**
	 * Deve retornar uma lista de objetos com atributos nomeados
	 *
	 * @param selectedNode
	 * @return
	 * @throws NodeNotFoundException 
	 */
	public String compositionOfNode() throws NodeNotFoundException {
		
		//recuperar o nó
		searchNode(selectedNode, family);
		
		return getNodeCompositionJson(node);
		
	}

	/**
	 *  Formato:
	 * {
	 *	name: 'Projeto1',
	 *	y: 4.55
	 * }, {
	 *  name: 'projeto2',
	 *  y: 10.9
	 * } ...
	 * @param node
	 * @return
	 */
	private String getNodeCompositionJson(Node node) {
		
		NodeComposition comp = node.getComposition();
		String returne = "";
		
		for(List<ProjectApplication> projectss : comp.getPercentageOfProjects().keySet()){
			
			returne = returne.concat(" { name: '");
			
			for(ProjectApplication pa : projectss){
				returne = returne.concat(" "+pa.getNameToDisplay()+",");
			}
			returne = returne.substring(0, returne.length()-1);
			returne = returne.concat("', y: ");
			
			returne = returne.concat(String.valueOf(comp.getPercentageOfProjects().get(projectss)));
			returne = returne.concat("},");
			
		}
		returne = returne.substring(0, returne.length()-1);
		
		return returne;
	}

	private void searchNode(String selectedNode, ProductsFamily family) throws NodeNotFoundException {
		
		//recuperar os nós da hierarquia
		String[] nodes = selectedNode.split(":");
		
		Node currentNode=null;// 
		
		for(int i=0;i<nodes.length;i++){
			if(currentNode == null){
				currentNode = family.getFinalTree();
				continue;
			}
			Node nextNode = null;
			String nodeName = nodes[i].split("/")[0];
			//percorrer os filhos do nó atual
			for(Node child : currentNode.getChildren()){
				//percorrer o conteúdo do filho
				for(Element e : child.getContent()){
					
					if(e.getName().equals(nodeName)){
						nextNode = child;
					}
				}
				if(nextNode != null){
					currentNode = nextNode;
					break;
				}
			}
			if(!currentNode.getChildren().isEmpty() && nextNode == null){
				throw new NodeNotFoundException(selectedNode, nodeName);
			}
		}
		this.node=currentNode;
		
	}

	public Node getNode() {
		return node;
	}

	
	
}
