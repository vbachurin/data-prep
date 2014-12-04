package org.talend.recipeProcessor;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PureJavaTest {

	private long test(File source, RecipeProcessor rp) throws IOException {
		long start = System.currentTimeMillis();
		rp.upperCase(source, "firstname");
		long end = System.currentTimeMillis();
		return (end - start);
	}

	private void test(File source) throws IOException {
		// long duration1 = test(source, new PureJava());
		// long duration2 = test(source, new Spark());
		// System.out.println("PureJava - " + duration1 + " ms for file "
		// + source.getName());
		// System.out.println("Spark - " + duration2 + " ms for file "
		// + source.getName());
	}

	@Test
	public void smallFile() throws IOException {
		test(new File("/home/stef/talend/test_files/customers.csv"));
	}

	@Test
	public void largeFile() throws IOException {
		test(new File("/home/stef/talend/test_files/customers_large.csv"));
	}

	@Test
	public void veryLargeFile() throws IOException {
		test(new File("/home/stef/talend/test_files/customers_very_large.csv"));
	}

	// @Test
	// public void extraLargeFile() throws IOException {
	// File source = new File(
	// "/home/stef/talend/test_files/customers_extra_large.csv");
	// File target = new File("/tmp/out4.csv");
	// RecipeProcessor rp = new PureJava();
	// rp.upperCase(source, target, "firstname");
	// }
}
