package net.project.installer.main;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DontInstallTomcatScreen {

	private JFrame jframe;
	
	private static final int SCREEN_INDEX = 2;

	public DontInstallTomcatScreen() {
	}

	public DontInstallTomcatScreen(JFrame jframe) {
		this.jframe = jframe;
		try {
			if (Main.panels.size() >= SCREEN_INDEX + 1) {
				JPanel p = Main.panels.get(SCREEN_INDEX);
				if (Main.INSTALL_TOMCAT_SCREEN.equals(p.getName())) {
					p.setVisible(true);
					return;
				}
			}
			//jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
