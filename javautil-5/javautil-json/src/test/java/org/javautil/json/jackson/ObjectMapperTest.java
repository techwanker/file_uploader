package org.javautil.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.javautil.json.jackson.User.Gender;
import org.junit.Test;

// http://wiki.fasterxml.com/JacksonInFiveMinutes

public class ObjectMapperTest {
	private final Logger logger = Logger.getLogger(getClass());
	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void mapToBeanTest() throws JsonParseException,
			JsonMappingException, IOException {
		User user = mapToBean();
		assertEquals(Gender.MALE, user.getGender());
		assertEquals("Joe", user.getName().getFirst());
		assertFalse(user.isVerified());
	}

	// should compare mapToBean
	/**
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public User mapToBean() throws JsonParseException, JsonMappingException,
			IOException {
		User user = mapper.readValue(new File("src/test/resources/user.json"),
				User.class);
		return user;
	}

	public String beanToJson(User user) throws JsonGenerationException,
			JsonMappingException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		mapper.writeValue(writer, user);
		String retval = baos.toString();
		return retval;
	}

	@Test
	// TODO not a test
	public void mapThousand() throws JsonParseException, JsonMappingException,
			IOException {
		int iterations = 1000;
		long start = System.nanoTime();
		for (int i = 0; i < iterations; i++) {
			mapOne();
		}
		long end = System.nanoTime();
		long nanos = end - start;
		double nanosPer = ((double) nanos) / iterations;
		double millisPer = nanosPer / 1e6;
		logger.info("elapsed nanos " + (end - start));
		logger.info("millis per " + millisPer);
	}

	public void mapOne() throws JsonParseException, JsonMappingException,
			IOException {
		mapper.readValue(new File("src/test/resources/user.json"), User.class);
	}
}
