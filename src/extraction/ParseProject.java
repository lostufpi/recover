package extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import core.app.ProjectApplication;
import core.nodes.ClassElement;
import core.nodes.Element;
import core.nodes.PackageElement;

/**
 * Classe que ir� gerar os n�s dos pacotes e a AST das classes do projeto
 * @author Denise
 *
 */
public class ParseProject {
	
	public static void generatePackageElements(ProjectApplication proj, String srcDir1){
		
		PackageElement root = new PackageElement("rootPackage",proj,null);
		proj.setRootElement(root);
		
		//estudar como fazer caso o usu�rio queira voltar e alterar alguma coisa. Problema: os arquivos s�o apagados logo que o fluxo termina, ent�o quando volta, o programa n�o acha mais os arquivos, ent�o d� nullPointer
		
		String srcDir = proj.getFileApplication().getAbsolutePath().concat("//"+srcDir1);
				
		File code = new File(srcDir);
		
		for(File dir : code.listFiles()){
			if(!dir.isDirectory()){
				ClassElement classElement = createClassElement(dir,root);
				if(classElement != null){
					root.addChild(classElement);
				}
				continue;
			}
			PackageElement pE = new PackageElement(dir.getName(), proj,root);
			addChildren(pE,dir);
			root.addChild(pE);
			
		}
		
	}
	
	private static ClassElement createClassElement(File path,Element parent){
		if(path.getName().endsWith(".java")){
			ClassElement child = new ClassElement(path.getName(),parent);
			generateAst(path,child);
			return child;
		}
		return null;
	}

	private static Element addChildren(PackageElement pack, File path) {
		
		for(File dir : path.listFiles()){
			if(!dir.isDirectory()){
				ClassElement classElement = createClassElement(dir,pack);
				if(classElement != null){
					pack.addChild(classElement);
				}
				continue;
			}
			PackageElement pE = new PackageElement(dir.getName(),pack);
			addChildren(pE,dir);
			pack.addChild(pE);
			
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private static void generateAst(File classe, ClassElement cE) {
		
		try{
		
			FileInputStream stream = new FileInputStream(classe);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader br = new BufferedReader(reader);
			String linha = br.readLine();
			String texto ="";
			while(linha != null){
				texto = texto.concat(linha+"\n");
				linha = br.readLine();
			}
			br.close();
			
			Map options = JavaCore.getOptions();
			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
			
			ASTParser parser = ASTParser.newParser(AST.JLS3);// avisa ao analisador para fazer a an�lise segundo a especifica��o do Java 3� edi��o

			parser.setCompilerOptions(options);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);//fala para o parser esperar um ICompilationUnit como entrada. ICompilationUnit � um ponteiro para um arquivo Java
			parser.setSource(texto.toCharArray()); 
			CompilationUnit unit = (CompilationUnit)parser.createAST(null);
		    unit.recordModifications();
		    
		    cE.setAst(unit);
		    
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
