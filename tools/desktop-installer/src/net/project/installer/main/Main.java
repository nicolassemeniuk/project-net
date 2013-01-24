package net.project.installer.main;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JFrame {

	private static final long serialVersionUID = -1000977526342350716L;

	public static String WELCOME_SCREEN = "welcomeScreen";

	public static String INSTALL_ORACLE_SCREEN = "installOracleScreen";

	public static String DONT_INSTALL_ORACLE_SCREEN = "dontInstallOracleScreen";

	public static String INSTALL_TOMCAT_SCREEN = "installTomcatScreen";

	public static String DONT_INSTALL_TOMCAT_SCREEN = "dontInstallTomcatScreen";

	public static List<JPanel> panels = new ArrayList<JPanel>();

	 public static void main(String[] args) {
		 new Main().setVisible(true);
	}

	public Main() {
		try {
			new WelcomeScreen(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	

}
