package exceptions;

public class NodeNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nodePath;
	private String nodeName;
	
	public NodeNotFoundException(String nodePath, String nodeName) {
		super();
		this.nodePath = nodePath;
		this.nodeName = nodeName;
	}

	@Override
	public void printStackTrace() {
		System.out.println("Nó "+nodeName+ "não encontrado em: "+nodePath);
		super.printStackTrace();
	}
	
}
