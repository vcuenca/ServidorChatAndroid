package main.java;

import com.google.gson.Gson;

public class JsonUtilities {

	public static String objetcToJson(Mensaje mensaje) {
		Gson gson = new Gson();
		return gson.toJson(mensaje);
	}
}
