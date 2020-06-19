package org.javautil.cli;

import org.javautil.cli.FileLister;
import org.junit.Test;

public class FileListerTest {

	@Test
	public void test() throws Exception {
		FileLister.main("-directory src/test/resources/org/javautil/FileListerData".split(" "));
	}
	
	@Test
	public void testPictures() throws Exception {
		FileLister.main("-directory /common/home/jjs/Pictures -outputFile /common/scratch/common-home-jjs-pictures.csv -sourceName viao -noDigest".split(" ") );
	}
}
