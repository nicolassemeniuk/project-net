package net.project.installer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import net.project.installer.util.SwingResourceManager;

public class InstallTomcatScreen {
	

	private final JPanel panel = new JPanel();
	private final JButton cancelButton = new JButton();
	private final JButton backButton = new JButton();
	private final JButton nextButton = new JButton();
	private final JLabel labelLogo = new JLabel();
	private final JTextPane welcomeToProjectnetTextPane = new JTextPane();
	
	private final JLabel passwordLabel = new JLabel();
	private final JLabel confirmPasswordLabel = new JLabel();
	
	private final JLabel tomcatTargetLabel = new JLabel();
	private final JLabel mailServHostLabel = new JLabel();
	private final JTextField tomcatLocation = new JTextField();
	private final JTextField mailServerHost = new JTextField();
	private final JPasswordField mailServerConfigrmPassword = new JPasswordField();
	private final JPasswordField mailServerPassword = new JPasswordField();
	private final JLabel mailServUsernameLabel = new JLabel();
	private final JTextField mailServerUsername = new JTextField();

	private static final int SCREEN_INDEX = 2;

	private JFrame jframe;

	public InstallTomcatScreen() {
	}

	public InstallTomcatScreen(JFrame jframe) {
		this.jframe = jframe;
		try {
			if (Main.panels.size() >= SCREEN_INDEX + 1) {
				JPanel p = Main.panels.get(SCREEN_INDEX);
				if (Main.INSTALL_TOMCAT_SCREEN.equals(p.getName())) {
					p.setVisible(true);
					return;
				}
			}
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		jframe.setSize(new Dimension(800, 600));
		jframe.setIconImage(SwingResourceManager.getImage(Main.class, "/net/project/installer/images/favicon.ico"));
		jframe.setTitle("Welcome to Project.net Installer");

		panel.setLayout(null);

		jframe.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setName(Main.INSTALL_TOMCAT_SCREEN);

		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonActionListener());
		cancelButton.setText("Cancel");
		cancelButton.setBounds(350, 514, 120, 29);

		panel.add(nextButton);
		nextButton.addActionListener(new NextButtonActionListener());
		nextButton.setText("Next>>");
		nextButton.setBounds(500, 514, 120, 29);

		panel.add(backButton);
		backButton.addActionListener(new BackButtonActionListener());
		backButton.setText("<<Back");
		backButton.setBounds(200, 514, 120, 29);

		panel.add(labelLogo);
		// labelLogo.setIcon(SwingResourceManager.getIcon(Main.class,
		// "/net/project/installer/images/background-all.gif"));
		labelLogo.setIcon(SwingResourceManager.getIcon(Main.class, "/net/project/installer/images/logo_pnet.png"));
		labelLogo.setBounds(67, 10, 129, 76);
		//
		panel.add(welcomeToProjectnetTextPane);
		welcomeToProjectnetTextPane.setBackground(UIManager.getColor("TabbedPane.tabAreaBackground"));
		welcomeToProjectnetTextPane.setFont(new Font("Lucida Grande", Font.BOLD, 12));
		welcomeToProjectnetTextPane.setEditable(false);
		welcomeToProjectnetTextPane
				.setText("Welcome to Project.net new installer. This is a great way to easy install and configure everything you need for Project.net application. In this wizard you can select and configure all necessary parts required for application proper work. Please select below one of the Oracle related configuration and one for Tomcat. Depending on the choices you select wizard will help you configure all required parts.");
		welcomeToProjectnetTextPane.setDisabledTextColor(new Color(170, 170, 170));
		welcomeToProjectnetTextPane.setBounds(67, 92, 683, 99);
		
		panel.add(tomcatTargetLabel);
		tomcatTargetLabel.setText("Tomcat home location");
		tomcatTargetLabel.setBounds(67, 211, 168, 15);
		
		panel.add(mailServHostLabel);
		mailServHostLabel.setText("Mail server host");
		mailServHostLabel.setBounds(67, 268, 144, 15);
		
		panel.add(passwordLabel);
		passwordLabel.setText("Mail server password");
		passwordLabel.setBounds(67, 383, 168, 15);
		
		panel.add(confirmPasswordLabel);
		confirmPasswordLabel.setText("Confirm password");
		confirmPasswordLabel.setBounds(67, 440, 168, 15);
		
		panel.add(tomcatLocation);
		tomcatLocation.setBounds(244, 209, 242, 19);
		
		panel.add(mailServerHost);
		mailServerHost.setBounds(244, 266, 90, 19);
		
		panel.add(mailServerConfigrmPassword);
		mailServerConfigrmPassword.setBounds(244, 436, 208, 19);
		
		panel.add(mailServerPassword);
		mailServerPassword.setBounds(243, 381, 209, 19);
		
		panel.add(mailServUsernameLabel);
		mailServUsernameLabel.setText("Mail server username");
		mailServUsernameLabel.setBounds(67, 328, 144, 15);
		
		panel.add(mailServerUsername);
		mailServerUsername.setBounds(244, 326, 208, 19);


		// nextButton.setEnabled(false);
		if (Main.panels.size() >= SCREEN_INDEX + 1) {
			JPanel p = Main.panels.get(SCREEN_INDEX);
			if (Main.DONT_INSTALL_TOMCAT_SCREEN.equals(p.getName())) {
				Main.panels.remove(SCREEN_INDEX);
				Main.panels.add(SCREEN_INDEX, panel);
			} else {
				Main.panels.add(panel);
			}
		} else {
			Main.panels.add(panel);
		}
	}

	public void showDialog(JFrame f, String message) {
		final JDialog d = new JDialog(f, "Click OK", true);
		d.setSize(200, 150);
		JLabel l = new JLabel(message, JLabel.CENTER);
		d.getContentPane().setLayout(new BorderLayout());
		d.getContentPane().add(l, BorderLayout.CENTER);
		JButton b = new JButton("OK");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				d.setVisible(false);
				d.dispose();
			}
		});
		JPanel p = new JPanel(); // Flow layout will center button.
		p.add(b);
		d.getContentPane().add(p, BorderLayout.SOUTH);
		d.setLocationRelativeTo(f);
		d.setVisible(true);
	}
	
	
	private class CancelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			cancelButtonActionPerformed(e);
		}
	}

	private class NextButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			nextButtonActionPerformed(e);
		}
	}

	private class BackButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			backButtonActionPerformed(e);
		}
	}

	protected void cancelButtonActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	protected void nextButtonActionPerformed(ActionEvent e) {
		// validate input
		try {
			String tomcatHomeLocation = tomcatLocation.getText();
			try {
				// tomcat home validation
				if(!new File(tomcatHomeLocation).isDirectory()){
					throw new Exception("Invalid folder location.");
				}else{
					WizardModel.getInstance().setTomcatHomeLocation(tomcatHomeLocation);
				}
			} catch (Exception e2) {
				showDialog(jframe, "Invalid Tomcat home folder!");
				return;
			}
			
			String mailHost = mailServerHost.getText();
			try {
				// mail server host validation
				WizardModel.getInstance().setMailServerHostName(mailHost);
			} catch (Exception e2) {
				return;
			}
			
			// mail server username validation
			String mailServerUsrname = new String(mailServerUsername.getText());
			
			WizardModel.getInstance().setMailServerUsername(mailServerUsrname);
			
			// mail server password validation
			String password = new String(mailServerPassword.getPassword());
			String passwordConfirmed = new String(mailServerConfigrmPassword.getPassword());
			
//			if("".equals(password)){
//				showDialog(jframe, "Password field can not be empty!");
//				return;
//			}
//			
//			if("".equals(passwordConfirmed)){
//				showDialog(jframe, "Confirmed password field can not be empty!");
//				return;
//			}
			
			if(!password.equals(passwordConfirmed)){
				showDialog(jframe, "Password and confirmed password are not the same!");
				return;
			}
			
			WizardModel.getInstance().setMailServerPassword(password);
			WizardModel.getInstance().setMailServerConfirmedPassword(passwordConfirmed);

			//HandleTomcatInstallation.updateExistingTomcatInstallation();
			panel.setVisible(false);
			new PerformInstallationScreen(jframe);
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	protected void backButtonActionPerformed(ActionEvent e) {
		panel.setVisible(false);
		JPanel p = Main.panels.get(SCREEN_INDEX - 1);
		p.setVisible(true);
		System.out.println("1. Main.panels:"+Main.panels.size());
		System.out.println("1. name:"+p.getName());
	}


}
