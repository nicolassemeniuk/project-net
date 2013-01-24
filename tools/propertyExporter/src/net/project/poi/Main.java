package net.project.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream("bin/file.properties"));
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Property file name and location are not specified !");
				System.exit(0);
			} catch (IOException e) {
				System.out.println("Wrong property file name or location !");
				System.exit(0);
			}

			if ("true".equals(properties.getProperty("is.export"))) {
				Export e = new Export();
				System.out.println("Please have a look into log file. You can find log file on this location:");
				System.out.println(properties.getProperty("output.filename.location") + File.separator + "export.log");
				if("true".equals(properties.getProperty("is.export.full"))) {
					e.exportProperties(properties);
				} else {
					e.exportPropertiesTranslators(properties);
				}
			} else { 
				Import i = new Import();
				System.out.println("Please have a look into log file. You can find log file on this location:");
				System.out.println(properties.getProperty("input.filename.location") + File.separator + "import.log");
				i.importProperties(properties);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
