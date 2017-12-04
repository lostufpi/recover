package out;


import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

import core.app.ProjectApplication;
import core.nodes.Element;
import core.nodes.PackageElement;
import core.out.Node;

/**
 * Classe responsavel por recuperar todo o Json dos projetos
 * @author Denise
 *
 */
public class NodeJson {
	
	private Node node;

	public NodeJson(Node node){
		this.node = node;
	}

	public JSONArray getJson(String idParent){

		Element element = this.node.getContent().get(0);
		boolean pakage = element instanceof PackageElement;
		
		//pegar os projetos que aos quais pertence o nó
		
		JSONObject jsonObject = new JSONObject();
		JSONArray returne = new JSONArray();
		
		String diffParents = "";
		if(this.node.hasDifferentParents()) diffParents="*";
		
		//id = idParent + nome atual
		
		String id = this.idNode(idParent);
		jsonObject.put("id", id);
		
		if(id.contains("imposto")){
			System.out.println(id);
		}
		
		if(idParent==null){
			jsonObject.put("parent", "#");
		}else{
			jsonObject.put("parent", idParent);
		}
		
		jsonObject.put("text", element.getName()+" "+this.node.projectsOfTheNode() + diffParents);
		jsonObject.put("type", pakage ? "pacote" : "classe");
		
		returne.put(jsonObject);
		
		if(pakage){
			for(Node child : this.node.getChildren()){
				NodeJson childJson = new NodeJson(child);
				JSONArray childrenJson = childJson.getJson(id);
				for(int i=0; i<childrenJson.length();i++){
					returne.put(childrenJson.get(i));
				}
			}
		
		}
		return returne;
	}
	/**
	 * ID do nó: hierarquia separada por ':' e projetos separados '/'
	 * @param idParent
	 * @return
	 */
	private String idNode(String idParent){
		String id=null;
		Element element = this.node.getContent().get(0);
		
			try{
		if(idParent == null){
			id = element.getName();
		}
		else{
				id = idParent.concat(":"+element.getName());
		}
		
		for(ProjectApplication pa : this.node.getProjects()){
			id = id.concat("/"+pa.getNameToDisplay());
		}
			}
			catch(OutOfMemoryError e){
				e.printStackTrace();
			}
		
		return id;
	}
	//private String uniqueId(String pretended)
}
