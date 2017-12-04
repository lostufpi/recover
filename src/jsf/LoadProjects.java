package jsf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import out.NodeCompositionShow;
import util.FilesOperations;
import control.Scheme;
import core.app.ProductsFamily;
import core.app.ProjectApplication;
import core.nodes.Element;
import exceptions.NodeNotFoundException;

@ManagedBean
@ApplicationScoped
public class LoadProjects implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<UploadedFile> projects;
	private List<File> projectFiles;
	
	private String src="/src/main/java";
	private double limiarPackage = 0.6;
	
	private ProductsFamily data;
	private List<ProjectApplication> apps;
	private Scheme scheme;
	
	private long idSrcProjects;
	private String selectedNode;
	
	private String jsonHighcharts="";
	private String titleHighcharts="";
	
	private String sessionDir;
	private String sessionDirUnzip;
	
	public LoadProjects() {
		this.projects = new ArrayList<UploadedFile>();
		this.idSrcProjects = System.currentTimeMillis();
		this.projectFiles = new ArrayList<File>();
		this.apps = new ArrayList<ProjectApplication>();
		this.scheme = new Scheme();
	}

	/**
	 * O método é chamado uma vez para cada arquivo enviado, em threads diferentes, que podem rodar em paralelo
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event){
		UploadedFile file = event.getFile();
		this.projects.add(file);
		try{
			//gambiarra. se não tiver isso, o programa quebra qdo for tentar executar o getInputstream() em uploadedFileToFile()
			file.getInputstream();
		} catch(IOException e){
			e.printStackTrace();
		}
		System.out.println(file.getFileName());	
	}
	
	/**
	 * Usado para exibir na tela os projetos carregados
	 * @return
	 */
	public String getNames(){
		
		if(projects.size()==0) return "";
		
		String message="Os projetos abaixo foram carregados com sucesso:";
		for(UploadedFile f : projects){
			message = " "+ message.concat("<br/>*"+f.getFileName());
		}
		
		return message;
	}
	
	/**
	 * Terceiro passo
	 * @return A página a ser carregada após o botão ser pressionado
	 */
	public String generateTree(){
		
		scheme.extractNodes(this.apps, src);
		this.data = scheme.unifyTree(this.limiarPackage);
		
		//Ao final do fluxo, devo executar a limpeza dos arquivos
		cleanFiles();
		
		return "results";
	}
		
	/**
	 * Terceiro passo - Atualização
	 * @return A página a ser carregada após a alteração do limiar
	 */
	public String regenerateTree(){
		
		this.data = scheme.unifyTree(this.limiarPackage);
				
		return "results";
	}
	
	/**
	 * Segundo passo
	 * @return
	 */
	public String loadProjects(){

		List<File> files = uploadedFileToFile();
		sessionDirUnzip = "D:/recoverFiles/unzip/"+idSrcProjects;
		//comando necessário para não bugar a aplicação, caso o usuário inicie o processo novamente da página inicial
		FilesOperations.clean(sessionDirUnzip + "/" );
		this.projectFiles = new ArrayList<File>();
		
		for(File f : files){
			FilesOperations.unzip(sessionDirUnzip + "/" + files.indexOf(f) + "/", f);
			this.projectFiles.add(new File(sessionDirUnzip + "/" + files.indexOf(f) + "/").listFiles()[0]);
		}

				
		this.prepareApps(projectFiles);
		return "editProjects";
	}
	
	private void prepareApps(List<File> applications){
		this.apps = new ArrayList<ProjectApplication>(); 
		
		for(File p : applications){
			this.apps.add(new ProjectApplication(p));
		}
	}

	private List<File> uploadedFileToFile() {
		List<File> files = new ArrayList<File>();
		
		try{
			
			for(UploadedFile uf : this.projects){
				
				sessionDir = "D:/recoverFiles/"+idSrcProjects;
				String dir = sessionDir + "/" + projects.indexOf(uf) + "/";
				FilesOperations.createDir(dir);
				
				File file = new File(dir+uf.getFileName());
				file.createNewFile();
				InputStream inputstream = uf.getInputstream();
				Path path = file.toPath();
				Files.copy(inputstream, path, StandardCopyOption.REPLACE_EXISTING);
				files.add(file);
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		return files;
	}
	
	public String getJsonJstree(){
		String json = data.getJson().toString();
		//remover as chaves que circundam o objeto
		json = json.substring(1, json.length()-1);
			
			//https://developer.mozilla.org/pt-BR/docs/AJAX/Getting_Started
		return json;
				
	}
	
	public String getJsonHighcharts(){
		return this.jsonHighcharts;
	}
	
	public void createJsonhighcharts(){
		
		NodeCompositionShow nodeShow = new NodeCompositionShow(this.selectedNode,this.data);
		
		try{
		
			String composition = this.jsonHighcharts = nodeShow.compositionOfNode();
			
			this.jsonHighcharts="[{"+
					"name: 'Participação',"+
					"colorByPoint: true,"+
					"data: ["+composition+"]"+
				"}]";
			this.titleHighcharts = makeChartTitle(nodeShow);
		}
		catch(NodeNotFoundException e){
			e.printStackTrace();
		}
		
		System.out.println(this.jsonHighcharts);
	}

	private String makeChartTitle(NodeCompositionShow nodeShow) {
		List<Element> list = nodeShow.getNode().getContent();
		List<String> stringList = new ArrayList<String>();
		
		for(Element e : list){
			boolean contains = false;
			for(String s : stringList){
				if(e.getName().equals(s)){
					contains = true;
					break;
				}
			}
			if(!contains){
				stringList.add(e.getName());
			}
		}
		
		return stringList.toString();
	}
	
	public void setSrc(String src){
		this.src = src;
	}

	public String getSrc() {
		return src;
	}
	
	private void cleanFiles(){

		FilesOperations.delete(this.sessionDir);
		FilesOperations.delete(this.sessionDirUnzip);
	}
	
	public void reset(){
		FacesContext facesContext = FacesContext.getCurrentInstance();  
		HttpSession session = (HttpSession) facesContext .getExternalContext().getSession(false);  
		session.invalidate();
	}

	public List<ProjectApplication> getApps() {
		return apps;
	}

	public String getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(String selectedNode) {
		this.selectedNode = selectedNode;
	}

	public String getTitleHighcharts() {
		return titleHighcharts;
	}

	public void setTitleHighcharts(String titleHighcharts) {
		this.titleHighcharts = titleHighcharts;
	}

	public double getLimiarPackage() {
		return limiarPackage;
	}

	public void setLimiarPackage(double limiarPackage) {
		this.limiarPackage = limiarPackage;
	}
	
	public String oi(){
		return "oi";
	}
	
}
