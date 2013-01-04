package net.project;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.project.test.util.TestProperties;

public class AllTests {

	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite("Test for net.project.*");
		try {
			Set<String> testCasesToExecute = searchDirectory();
			for (String className : testCasesToExecute) {
				Class classToRun = Class.forName(className);
				System.out.println(className + " added.");
				suite.addTestSuite(classToRun);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return suite;
	}

	private static Set<String> searchDirectory() {
		TestProperties properties = TestProperties.getInstance();
		String sep = File.separator;
		String schedulePath = sep + "net" + sep + "project";
		Set existingInTestDirectory = new HashSet();
		searchDirectory(new File(properties.getProperty("sourceFilePath") + schedulePath), "net.project",
				existingInTestDirectory);
		return existingInTestDirectory;
	}

	private static void searchDirectory(File directory, String prefix, Set<String> existingInTestDirectory) {

		if (!directory.exists())
			System.out.println(directory.getAbsolutePath() + " does not exists!");

		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getName();

			if (file.isDirectory() && !fileName.equals(".svn")) {
				searchDirectory(file, prefix + "." + fileName, existingInTestDirectory);
			} else if (file.isFile() && fileName.endsWith("Test.java")) {
				String className = fileName.substring(0, fileName.lastIndexOf(".java"));
				// sjmittal: this is a temporary excultion, would be removed later on
				if ("CSTaskEndpointCalculationTest".equals(className))
					continue;
				existingInTestDirectory.add(prefix + "." + className);
			}
		}
	}

}
