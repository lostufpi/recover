package testeJson;

public class InfoMunicipios {

	private String UF;
	private String cidade;

	public InfoMunicipios(String uF, String cidade) {
		super();
		UF = uF;
		this.cidade = cidade;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

}
