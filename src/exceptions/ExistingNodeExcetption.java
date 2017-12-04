package exceptions;

import core.nodes.Element;

public class ExistingNodeExcetption extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Element element;
	
	public ExistingNodeExcetption(Element element) {
		super();
		this.element = element;
	}
	
	@Override
	public void printStackTrace() {
		
		System.out.println("O elemento "+ element + " já está no node "+ element.getNode().toString());
		
		super.printStackTrace();
	}
	
}
