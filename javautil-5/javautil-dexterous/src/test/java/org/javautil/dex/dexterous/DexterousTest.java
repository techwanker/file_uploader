package org.javautil.dex.dexterous;

import org.junit.Test;

public class DexterousTest {

	@Test
	public void test() throws Exception {
		String args = "-config=src/test/resources/DataSources.xml -inputFiles=src/test/resources/justexit.dex";
		Dexterous.main(args.split(" "));
	}

	@Test
	public void test2() throws Exception {
		String args = "-config=src/test/resources/DataSources.xml -inputFiles=src/test/resources/select.dex";
		Dexterous.main(args.split(" "));
	}

	public static void main(String args[]) throws Exception {
		DexterousTest test = new DexterousTest();
		test.test2();
	}
}
