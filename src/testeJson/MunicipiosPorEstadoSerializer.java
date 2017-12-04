package testeJson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MunicipiosPorEstadoSerializer implements JsonSerializer<InfoMunicipios> {

	@Override
	public JsonElement serialize(InfoMunicipios arg0, Type arg1, JsonSerializationContext arg2) {
		return new Gson().toJsonTree(arg0);
	}

}