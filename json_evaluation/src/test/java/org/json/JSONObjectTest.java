package org.json;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class JSONObjectTest {

	private class Result {

		String allLine;
		String city;

		public Result(String allLine, String city) {
			super();
			this.allLine = allLine;
			this.city = city;
		}

	}

	@FunctionalInterface
	private interface Function {

		public Result process(String line);
	}

	private static boolean DEBUG = false;

	private static List<String> lines;

	@BeforeClass
	public static void before() throws IOException {
		File file = new File("/home/stef/talend/test_files/customers_10k.json");

		lines = Files.readLines(file, Charsets.UTF_8);
	}

	private void testCommon(String name, Function function) {
		System.out.println(name);
		long start = System.currentTimeMillis();
		for (String line : lines) {
			Result result = function.process(line);

			if (DEBUG) {
				System.out.println(result.allLine);
				System.out.println(result.city);
			}
			if (DEBUG)
				break;
		}
		System.out.println(" ===> " + (System.currentTimeMillis() - start)
				+ " ms");
	}

	@Test
	public void testJSONORG() throws IOException {
		testCommon("Json.org", new Function() {

			public Result process(String line) {
				JSONObject jsonObject = new JSONObject(line);
				String city = jsonObject.getString("city");
				String allLine = jsonObject.toString();
				return new Result(allLine, city);
			}
		});
	}

	@Test
	public void testJacksonNode() throws IOException {
		testCommon("Jackson node", new Function() {

			final ObjectMapper mapper = new ObjectMapper();

			public Result process(String line) {
				try {
					JsonNode actualObj = mapper.readTree(line);
					String city = actualObj.get("city").asText();
					String allLine = actualObj.toString();
					return new Result(allLine, city);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		});
	}

	@Test
	public void testJacksonMap() throws IOException {
		System.out.println("Jackson map");
		long start = System.currentTimeMillis();

		final ObjectMapper mapper = new ObjectMapper();

		for (String line : lines) {
			Map actualObj = mapper.readValue(line, Map.class);
			String value = (String) actualObj.get("city");
			String string = actualObj.toString();

			if (DEBUG) {
				System.out.println(value);
				System.out.println(string);
			}
			if (DEBUG)
				break;
		}
		System.out.println(" ===> " + (System.currentTimeMillis() - start)
				+ " ms");
	}

	@Test
	public void testGson() throws IOException {
		System.out.println("Gson");
		long start = System.currentTimeMillis();

		for (String line : lines) {
			Map data = new Gson().fromJson(line, HashMap.class);
			String value = (String) data.get("city");
			String string = data.toString();

			if (DEBUG) {
				System.out.println(value);
				System.out.println(string);
			}
			if (DEBUG)
				break;
		}
		System.out.println(" ===> " + (System.currentTimeMillis() - start)
				+ " ms");
	}
}
