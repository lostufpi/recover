package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FilesOperations {
	
	public static void createDir(String dir){

		String[] folders = dir.split("/");
		
		String folder = "";
		for(int i=0;i<folders.length;i++){
			
			folder = folder.concat(folders[i]);
			
			if(!new File(folder).exists()){
				
				new File(folder).mkdir();
			}
			folder = folder.concat("/");
		}
		
	}

	
	private static final void copyInputStream(InputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int len;
		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param src Local onde vai ser descompactado o arquivo
	 * @param file Arquivo zipado
	 */
	public static final void unzip(String src, File file) {
		Enumeration<ZipEntry> entries;
		ZipFile zipFile;

		try {
			zipFile = new ZipFile(file, Charset.forName("CP866"));
			entries = (Enumeration<ZipEntry>) zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();
				File file2 = new File(src+entry.getName());
				if(entry.isDirectory()) {
					
					file2.mkdir();
					continue;
				}
				
				if( !file2.getParentFile().exists() ) {
					file2.getParentFile().mkdirs();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(src+entry.getName());
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(fileOutputStream));
			}
			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Erro ao descompactar:" + ioe.getMessage());
			return;
		}
	}

	/**
	 * Exclui um arquivo ou diretório
	 * @param path Caminho da pasta a ser excluída 
	 */
	public static void delete(String path) {
		
		File toDelete = new File(path);
		
		if(toDelete.isDirectory()){
			clean(path);
		}
		//sendo um arquivo posso excluir direto, sendo um diretório já excluí todo o seu conteúdo
		toDelete.delete();
	}
	
	/**
	 * Esvazia o conteúdo de uma pasta
	 * @param path pasta a ser esvaziada
	 */
	public static void clean(String path){
		File toDelete = new File(path);
		
		if(toDelete.isDirectory()){
			String[] sub = toDelete.list();
			//excluir todos os subdiretórios
			for(int i=0;i<sub.length;i++){
				delete(path+"\\"+sub[i]);
			}
		}
	}
}
