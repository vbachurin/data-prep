package org.json;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class JSONObjectTest {

	private interface Function {

		public void setLine(String line);

		public void put(String key, Object value);

		public String get(String key);

		public String asString();
	}

	private static boolean DEBUG = false;

	private static final int NB_RUN = (DEBUG ? 1 : 10);

	private static List<String> lines;

	@BeforeClass
	public static void before() throws IOException, URISyntaxException {
		File file = new File(ClassLoader
				.getSystemResource("customers_10k.json").toURI());

		lines = Files.readLines(file, Charsets.UTF_8);
	}

	private void testCommon(String name, Function function) {
		System.out.println("===== " + name + " =====");

		List<Long> values = new ArrayList<>();

		for (int i = 0; i < NB_RUN; i++) {
			if (!DEBUG)
				Collections.shuffle(lines);
			long start = System.currentTimeMillis();
			for (String line : lines) {
				function.setLine(line);

				String city = function.get("city");
				function.put("country", "US");

				function.put("firstname", function.get("firstname")
						.toUpperCase());

				String allLine = function.asString();

				if (DEBUG) {
					System.out.println(allLine);
					System.out.println(city);
				}
				if (DEBUG)
					break;
			}
			values.add(System.currentTimeMillis() - start);
		}
		System.out.println(" ===> " + values + " in ms\n");
	}

	@Test
	public void testJSONORG() throws IOException {
		testCommon("Json.org", new Function() {

			JSONObject jsonObject;

			@Override
			public void setLine(String line) {
				jsonObject = new JSONObject(line);
			}

			@Override
			public void put(String key, Object value) {
				jsonObject.put(key, value);
			}

			@Override
			public String get(String key) {
				return jsonObject.getString(key);
			}

			@Override
			public String asString() {
				return jsonObject.toString();
			}
		});
	}

	@Test
	public void testJsonSimple() throws IOException {
		JSONParser parser = new JSONParser();

		testCommon("json simple", new Function() {

			org.json.simple.JSONObject jsonObject;

			@Override
			public void setLine(String line) {
				try {
					jsonObject = (org.json.simple.JSONObject) parser
							.parse(line);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void put(String key, Object value) {
				jsonObject.put(key, value);
			}

			@Override
			public String get(String key) {
				return (String) jsonObject.get(key);
			}

			@Override
			public String asString() {
				return jsonObject.toJSONString();
			}
		});
	}

	@Test
	public void testJacksonNode() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();

		testCommon("Jackson node", new Function() {

			ObjectNode oNode;

			@Override
			public void setLine(String line) {
				try {
					JsonNode jNode = mapper.readTree(line);
					oNode = JsonNodeFactory.instance.objectNode();
					oNode.putAll((ObjectNode) jNode);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void put(String key, Object value) {
				oNode.put(key, (String) value);
			}

			@Override
			public String get(String key) {
				return oNode.get(key).asText();
			}

			@Override
			public String asString() {
				return oNode.toString();
			}

		});
	}

	@Test
	public void testJacksonMap() throws IOException {
		final ObjectMapper mapper = new ObjectMapper();

		testCommon("Jackson map", new Function() {

			Map actualObj;

			@Override
			public void setLine(String line) {
				try {
					actualObj = mapper.readValue(line, Map.class);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void put(String key, Object value) {
				actualObj.put(key, value);
			}

			@Override
			public String get(String key) {
				return (String) actualObj.get(key);
			}

			@Override
			public String asString() {
				return actualObj.toString();
			}

		});
	}

	@Test
	public void testGson() throws IOException {
		testCommon("Gson", new Function() {

			Map data;

			@Override
			public void setLine(String line) {
				data = new Gson().fromJson(line, HashMap.class);
			}

			@Override
			public void put(String key, Object value) {
				data.put(key, value);
			}

			@Override
			public String get(String key) {
				return (String) data.get(key);
			}

			@Override
			public String asString() {
				return data.toString();
			}
		});
	}
}
