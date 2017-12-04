package core.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import out.NodeJson;
import core.out.Node;

/**
 * Classe para representar todos produtos da família
 * @author Denise
 *
 */
public class ProductsFamily implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ProjectApplication> applications;
	/**
	 * armazena o nó raiz da árvore resultante da família
	 */
	private Node finalTree;
	
	public ProductsFamily(){
		this.applications = new ArrayList<ProjectApplication>();
	}
	
	public void addProduct(ProjectApplication app){
		this.applications.add(app);
	}
	
	/**
	 * Construir o objeto Json que nada mais é do que um array de todos os nós 
	 * @param idParent
	 * @return
	 */
	public JSONObject getJson(){
		
		//array data
		
		JSONObject returne = new JSONObject();
				
		JSONArray childrenJson = new JSONArray();
		
		NodeJson nodeJson = new NodeJson(this.getFinalTree());
		
		JSONArray jsonArrayChildren = nodeJson.getJson(null);
		for(int i=0; i<jsonArrayChildren.length();i++){
			childrenJson.put(jsonArrayChildren.get(i));
		}	
		returne.put("data", childrenJson);
	
		return returne;
	}
	
	public List<ProjectApplication> getApplications(){
		return this.applications
				;
	}

	public void setFinalTree(Node node) {
		this.finalTree=node;
		
	}

	public Node getFinalTree() {
		return finalTree;
	}

}
