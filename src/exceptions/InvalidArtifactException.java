package exceptions;

import core.Variability;

/**
 * Classe para representar um erro quando tentar adicionar um artefato de tipo incorreto em um objeto {@link Variability}
 * @author Denise
 *
 */
public class InvalidArtifactException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String received;
	private String expected;
	
	public InvalidArtifactException(String received, String expected) {
		this.received = received;
		this.expected = expected;
	}

	@Override
	public String getMessage() {
		
		return "Uso do tipo de artefato incorreto. "+ expected +" esperado, mas "+ received +" encontrado.";
	}

}
