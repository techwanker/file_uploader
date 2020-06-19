package org.javautil.cli;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

public class JavautilTest {

	@Test
	public void test() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		BasicConfigurator.configure();
		// TODO use argument splitter
		String[] args = "filelister src/test/resources".split(" ");
		org.javautil.cli.Javautil.main(args);
	}
	
	@Test
	public void test2() throws Exception {
		BasicConfigurator.configure();
		// TODO use argument splitter
		String[] args = "src/test/resources".split(" ");
		org.javautil.cli.FileLister.main(args);
	}
	

}
