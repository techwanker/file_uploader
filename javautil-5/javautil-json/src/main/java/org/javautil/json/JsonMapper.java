package org.javautil.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonMapper {

	private final ObjectMapper mapper = new ObjectMapper();

	public Object ObjectToJson(String json, Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz is null");
		}
		if (json == null) {
			throw new IllegalArgumentException("json is null");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
		Object returnValue = null;
		try {
			returnValue = mapper.readValue(bais, clazz);
		} catch (JsonParseException e) {
			throw new IllegalArgumentException(e);
		} catch (JsonMappingException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return returnValue;
	}
}
