package net.project.docvault;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(args[0]));
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Property file name and location are not specified !");
				System.exit(0);
			} catch (IOException e) {
				System.out.println("Wrong property file name or location !");
				System.exit(0);
			}

			Report r = new Report();
			r.getStatisticReport(properties, Report.ReportType.PROJECTS);
			r.getStatisticReport(properties, Report.ReportType.BUSINESS);
			r.getStatisticReport(properties, Report.ReportType.USERS);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
