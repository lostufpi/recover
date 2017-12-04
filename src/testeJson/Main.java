package testeJson;

import com.google.gson.GsonBuilder;

public class Main {

	public static void main(String[] args) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.enableComplexMapKeySerialization();

		gsonBuilder.serializeNulls();
		gsonRegisterTypeAdapters( gsonBuilder );

		String json = new GsonBuilder().create().toJson( new InfoMunicipios("PI", "Teresina"));
		System.out.println(json);
	}
	public static void gsonRegisterTypeAdapters( GsonBuilder gsonBuilder ) {
		gsonBuilder.registerTypeAdapter( InfoMunicipios.class , new MunicipiosPorEstadoSerializer());
	}
}

