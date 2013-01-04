package net.project.installer.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import net.project.installer.util.SwingResourceManager;

public class WelcomeScreen {

	private ButtonGroup buttonGroupTomcat = new ButtonGroup();
	private ButtonGroup buttonGroupOracleLicense = new ButtonGroup();
	private ButtonGroup buttonGroupOracle = new ButtonGroup();
	private final JPanel panel = new JPanel();
	private final JButton cancelButton = new JButton();
	private final JButton nextButton = new JButton();
	private final JRadioButton radioButtonInstallOracle = new JRadioButton();
	private final JRadioButton radioButtonInstallSchema = new JRadioButton();
	private final JRadioButton radioButtonInstallTomcat = new JRadioButton();
	private final JRadioButton radioButtonInstallApplication = new JRadioButton();
	private final JRadioButton radioButtonAcceptOracleLicense = new JRadioButton();
	private final JRadioButton radioButtonDontAcceptOracleLicense = new JRadioButton();
	private final JLabel labelLogo = new JLabel();
	private final JTextPane welcomeToProjectnetTextPane = new JTextPane();
	private final JLabel tomcatLabel = new JLabel();
	private final JLabel oracleConfigurationLabel = new JLabel();

	private JFrame jframe;

	public WelcomeScreen() {
	}

	public WelcomeScreen(Main jframe) {
		this.jframe = jframe;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JPanel getPanel() {
		return panel;
	}

	private void jbInit() throws Exception {
		jframe.setSize(new Dimension(800, 600));
		jframe.setIconImage(SwingResourceManager.getImage(Main.class, "/net/project/installer/images/favicon.ico"));
		jframe.setTitle("Welcome to Project.net Installer");

		jframe.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setName(Main.WELCOME_SCREEN);

		panel.add(cancelButton);
		cancelButton.addActionListener(new CancelButtonActionListener());
		cancelButton.setText("Cancel");
		cancelButton.setBounds(265, 514, 120, 29);

		panel.add(nextButton);
		nextButton.addActionListener(new NextButtonActionListener());
		nextButton.setText("Next>>");
		nextButton.setBounds(444, 514, 120, 29);

		panel.add(radioButtonInstallOracle);
		radioButtonInstallOracle.setActionCommand("installOracle");
		radioButtonInstallOracle.setName("installOracle");
		radioButtonInstallOracle.setText("Install Oracle Express database");
		radioButtonInstallOracle.setBounds(67, 247, 233, 23);

		panel.add(radioButtonInstallSchema);
		radioButtonInstallSchema.setActionCommand("dontInstallOracle");
		radioButtonInstallSchema.setName("dontInstallOracle");
		radioButtonInstallSchema.setText("No, I already have a database. Install Project.net into existing database.");
		radioButtonInstallSchema.setBounds(67, 297, 552, 23);

		buttonGroupOracle.add(radioButtonInstallOracle);
		buttonGroupOracle.add(radioButtonInstallSchema);

		panel.add(radioButtonInstallTomcat);
		radioButtonInstallTomcat.setActionCommand("installTomcat");
		radioButtonInstallTomcat.setName("installTomcat");
		radioButtonInstallTomcat.setText("Install Tomcat 6.0");
		radioButtonInstallTomcat.setBounds(67, 397, 187, 23);

		panel.add(radioButtonInstallApplication);
		radioButtonInstallApplication.setActionCommand("dontInstallTomcat");
		radioButtonInstallApplication.setName("dontInstallTomcat");
		radioButtonInstallApplication.setText("No, I already have a Tomcat. Deploy application into existing Tomcat instance.");
		radioButtonInstallApplication.setBounds(67, 447, 554, 23);

		buttonGroupTomcat.add(radioButtonInstallTomcat);
		buttonGroupTomcat.add(radioButtonInstallApplication);

		panel.add(labelLogo);
		// labelLogo.setIcon(SwingResourceManager.getIcon(Main.class,
		// "/net/project/installer/images/background-all.gif"));
		labelLogo.setIcon(SwingResourceManager.getIcon(Main.class, "/net/project/installer/images/logo_pnet.png"));
		labelLogo.setBounds(67, 10, 129, 76);

		panel.add(welcomeToProjectnetTextPane);
		welcomeToProjectnetTextPane.setBackground(UIManager.getColor("TabbedPane.tabAreaBackground"));
		welcomeToProjectnetTextPane.setFont(new Font("Lucida Grande", Font.BOLD, 12));
		welcomeToProjectnetTextPane.setEditable(false);
		welcomeToProjectnetTextPane
				.setText("Welcome to Project.net new installer. This is a great way to easy install and configure everything you need for Project.net application. In this wizard you can select and configure all necessary parts required for application proper work. Please select below one of the Oracle related configuration and one for Tomcat. Depending on the choices you select wizard will help you configure all required parts.");
		welcomeToProjectnetTextPane.setDisabledTextColor(new Color(170, 170, 170));
		welcomeToProjectnetTextPane.setBounds(67, 92, 683, 99);

		panel.add(tomcatLabel);
		tomcatLabel.setText("Tomcat configuration");
		tomcatLabel.setBounds(67, 370, 168, 15);

		panel.add(oracleConfigurationLabel);
		oracleConfigurationLabel.setText("Oracle configuration");
		oracleConfigurationLabel.setBounds(67, 226, 162, 15);

		ActionListener a = new OracleActionListener();
		radioButtonInstallOracle.addActionListener(a);
		radioButtonInstallSchema.addActionListener(a);

		ActionListener b = new TomcatActionListener();
		radioButtonInstallTomcat.addActionListener(b);
		radioButtonInstallApplication.addActionListener(b);

		Main.panels.add(panel);

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

	protected void cancelButtonActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	protected void nextButtonActionPerformed(ActionEvent e) {
		if (WizardModel.getInstance().canGoOnSecondScreen()) {
			if(!WizardModel.getInstance().isOracleLicenseAccepted() && "installOracle".equals(WizardModel.getInstance().getOracleAction())){
				showDialog();
				return;
			}
			panel.setVisible(false);
			showConfigPanel();
		}
	}

	private void showConfigPanel() {
		if ("dontInstallOracle".equals(WizardModel.getInstance().getOracleAction())) {
			new DontInstallOracleScreen(this.jframe);
		} else {
			new InstallOracleScreen(this.jframe);
		}

	}
	
	class OracleActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String choice = buttonGroupOracle.getSelection().getActionCommand();
			System.out.println("ACTION Choice Selected: " + choice);
			WizardModel.getInstance().setOracleAction(choice);
		}
	}

	class OracleLicenseActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String choice = buttonGroupOracleLicense.getSelection().getActionCommand();
			System.out.println("ACTION Choice Selected: " + choice);
			if("accpetLicense".equals(choice)){
				WizardModel.getInstance().setOracleLicenseAccepted(true);
			}else{
				WizardModel.getInstance().setOracleLicenseAccepted(false);
			}
		}
	}

	class TomcatActionListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			String choice = buttonGroupTomcat.getSelection().getActionCommand();
			System.out.println("ACTION Choice Selected: " + choice);
			WizardModel.getInstance().setTomcatAction(choice);
		}
	}

	public void showDialog() {
		try {
			FileInputStream isPnet = new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "OracleLicense.txt"));
			InputStreamReader isrPnet = new InputStreamReader(isPnet);
			BufferedReader brPnet = new BufferedReader(isrPnet);
			String linePnet;
			StringBuffer sb = new StringBuffer();
			while ((linePnet = brPnet.readLine()) != null) {
				sb.append(linePnet).append("\n");
			}
			final JDialog d = new JDialog(jframe, "Oracle Express License", true);
			d.setSize(jframe.getWidth(), jframe.getHeight());
			

			d.setSize(new Dimension(800, 600));
			d.setIconImage(SwingResourceManager.getImage(Main.class, "/net/project/installer/images/favicon.ico"));

			JPanel p = new JPanel();
			d.getContentPane().add(p);
			p.setLayout(null);
			p.setName(Main.WELCOME_SCREEN);
			
			JTextPane oracleLicense = new JTextPane();
			oracleLicense.setBounds(20, 20, 760, 422);
			oracleLicense.setText(sb.toString());
			oracleLicense.setAutoscrolls(true);
			oracleLicense.setEditable(false);
			oracleLicense.setPreferredSize(new Dimension(740, 402));
			JButton b = new JButton("OK");
			b.setBounds(338, 538, 123, 30);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					d.setVisible(false);
					d.dispose();
				}
			});
			
			radioButtonAcceptOracleLicense.setActionCommand("accpetLicense");
			radioButtonAcceptOracleLicense.setBounds(20, 497, 305, 30);
			radioButtonAcceptOracleLicense.setName("accpetLicense");
			radioButtonAcceptOracleLicense.setText("I accept the terms in the license agreement");

			radioButtonDontAcceptOracleLicense.setActionCommand("dontAccpetLicense");
			radioButtonDontAcceptOracleLicense.setBounds(20, 461, 415, 30);
			radioButtonDontAcceptOracleLicense.setName("dontAccpetLicense");
			radioButtonDontAcceptOracleLicense.setText("I do not accept the terms in the license agreement");
			radioButtonDontAcceptOracleLicense.setSelected(true);
			
			buttonGroupOracleLicense.add(radioButtonAcceptOracleLicense);
			buttonGroupOracleLicense.add(radioButtonDontAcceptOracleLicense);
			
			
			p.add(radioButtonAcceptOracleLicense);
			p.add(radioButtonDontAcceptOracleLicense);
			p.add(b);
			p.add(oracleLicense);
			
			ActionListener a = new OracleLicenseActionListener();
			radioButtonAcceptOracleLicense.addActionListener(a);
			radioButtonDontAcceptOracleLicense.addActionListener(a);
			
			d.add(p);
			d.setLocationRelativeTo(jframe);
			d.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
