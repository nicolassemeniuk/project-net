package net.project.installer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import net.project.installer.util.SwingResourceManager;

public class DontInstallOracleScreen {

	private final JLabel oracleTargetLabel = new JLabel();
	private final JLabel databasePortLabel = new JLabel();
	private final JLabel passwordLabel = new JLabel();
	private final JLabel confirmPasswordLabel = new JLabel();
	private final JTextField databaseName = new JTextField();
	private final JTextField oraclePort = new JTextField();
	private final JPasswordField oracleSystemPasswordConfirmed = new JPasswordField();
	private final JPasswordField oracleSystemPassword = new JPasswordField();
	private final JButton testDatabaseConnectionButton = new JButton();
	private final JProgressBar progressBar = new JProgressBar();

	private final JPanel panel = new JPanel();
	private final JButton cancelButton = new JButton();
	private final JButton backButton = new JButton();
	private final JButton nextButton = new JButton();
	private final JLabel labelLogo = new JLabel();
	private final JTextPane welcomeToProjectnetTextPane = new JTextPane();
	private final JTextPane messageTextPane = new JTextPane();

	private static final int SCREEN_INDEX = 1;

	private JFrame jframe;

	public DontInstallOracleScreen() {
	}

	public DontInstallOracleScreen(JFrame jframe) {
		this.jframe = jframe;
		try {
			if (Main.panels.size() >= SCREEN_INDEX + 1) {
				JPanel p = Main.panels.get(SCREEN_INDEX);
				if (Main.DONT_INSTALL_ORACLE_SCREEN.equals(p.getName())) {
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
		this.jframe.setSize(new Dimension(800, 600));
		this.jframe.setIconImage(SwingResourceManager.getImage(Main.class, "/net/project/installer/images/favicon.ico"));
		this.jframe.setTitle("Welcome to Project.net Installer");

		this.jframe.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setName(Main.DONT_INSTALL_ORACLE_SCREEN);

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

		panel.add(oracleTargetLabel);
		oracleTargetLabel.setText("Database name");
		oracleTargetLabel.setBounds(67, 211, 136, 15);

		panel.add(databasePortLabel);
		databasePortLabel.setText("Default database port");
		databasePortLabel.setBounds(67, 268, 198, 15);

		panel.add(passwordLabel);
		passwordLabel.setText("SYSTEM user password");
		passwordLabel.setBounds(67, 325, 168, 15);

		panel.add(confirmPasswordLabel);
		confirmPasswordLabel.setText("Confirm password");
		confirmPasswordLabel.setBounds(67, 382, 168, 15);

		panel.add(databaseName);
		databaseName.setBounds(210, 209, 242, 19);

		panel.add(oraclePort);
		oraclePort.setBounds(211, 268, 90, 19);

		// JFileChooser fc = new JFileChooser(new
		// File(System.getProperty("user.dir")));
		// fc.setBounds(580, 209, 50, 19);
		// panel.add(fc);

		panel.add(oracleSystemPasswordConfirmed);
		oracleSystemPasswordConfirmed.setBounds(244, 378, 208, 19);

		panel.add(oracleSystemPassword);
		oracleSystemPassword.setBounds(243, 323, 209, 19);

		panel.add(testDatabaseConnectionButton);
		testDatabaseConnectionButton.addActionListener(new TestDatabaseConnectionButtonActionListener());
		testDatabaseConnectionButton.setText("Test database connection");
		testDatabaseConnectionButton.setBounds(296, 459, 208, 25);

		panel.add(progressBar);
		progressBar.setBounds(78, 425, 644, 14);

		// nextButton.setEnabled(false);
		if (Main.panels.size() >= SCREEN_INDEX + 1) {
			JPanel p = Main.panels.get(SCREEN_INDEX);
			if (Main.INSTALL_ORACLE_SCREEN.equals(p.getName())) {
				Main.panels.remove(SCREEN_INDEX);
				Main.panels.add(SCREEN_INDEX, panel);
			} else {
				Main.panels.add(panel);
			}
		} else {
			Main.panels.add(panel);
		}
	}

	private class TestDatabaseConnectionButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean validConnection = testDatabaseConnectionButtonActionPerformed(e);
			if (validConnection) {
				showDialog(jframe, "Database connection test successful!");
			} else {
				showDialog(jframe, "Database connection test unsuccessful!");
			}
		}
	}

	public void showDialog(JFrame f, String message) {
		final JDialog d = new JDialog(f, "Click OK", true);
		d.setSize(300, 150);
		
		messageTextPane.setBackground(UIManager.getColor("TabbedPane.tabAreaBackground"));
		//messageTextPane.setFont(new Font("Lucida Grande", Font.BOLD, 12));
		messageTextPane.setEditable(false);
		messageTextPane.setText(message);
		messageTextPane.setDisabledTextColor(new Color(170, 170, 170));
		messageTextPane.setBounds(d.getBounds());
		
		//JLabel l = new JLabel(message, JLabel.CENTER);
		d.getContentPane().setLayout(new BorderLayout());
		d.getContentPane().add(messageTextPane, BorderLayout.CENTER);
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

	protected boolean testDatabaseConnectionButtonActionPerformed(ActionEvent e) {
		Connection connection = null;
		boolean nextOk = nextButtonActionPerformed(e);
		if(!nextOk){
			return false;
		}
		try {
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);
			// Create a connection to the database
			String serverName = "localhost";
			String portNumber = WizardModel.getInstance().getOraclePort();
			String sid = WizardModel.getInstance().getDatabaseName();
			String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
			String username = "SYSTEM";
			String password = WizardModel.getInstance().getOracleSystemPassword();
			System.out.println("url:"+url);
			System.out.println("username:"+username);
			System.out.println("password:"+password);
			connection = DriverManager.getConnection(url, username, password);
			connection.close();
		} catch (ClassNotFoundException ee) {
			ee.printStackTrace();
			return false;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			showDialog(jframe, "Database connection could not be established. Check Oracle Listener process!");
			return false;
		}
		return true;
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

	protected boolean nextButtonActionPerformed(ActionEvent e) {
		// validate input
		try {
			String databaseSid = databaseName.getText();
			try {
				// oracle home validation
				if (databaseSid != null && !"".equals(databaseSid)) {
					WizardModel.getInstance().setDatabaseName(databaseSid);
				} else {
					showDialog(jframe, "Database name can not be empty!");
					return false;
				}
			} catch (Exception e2) {
				return false;
			}

			String oracleDatabasePort = oraclePort.getText();
			try {
				// oracle port validation
				Integer port = new Integer(oracleDatabasePort);
				WizardModel.getInstance().setOraclePort(port.toString());
			} catch (Exception e2) {
				showDialog(jframe, "Invalid Oracle port number!");
				return false;
			}

			// oracle system password validation
			String password = new String(oracleSystemPassword.getPassword());
			String passwordConfirmed = new String(oracleSystemPasswordConfirmed.getPassword());

			if ("".equals(password)) {
				showDialog(jframe, "Password field can not be empty!");
				return false;
			}

			if ("".equals(passwordConfirmed)) {
				showDialog(jframe, "Confirmed password field can not be empty!");
				return false;
			}

			if (!password.equals(passwordConfirmed)) {
				showDialog(jframe, "Password and confirmed password are not the same!");
				return false;
			}

			WizardModel.getInstance().setOracleSystemPassword(password);
			WizardModel.getInstance().setOracleSystemPasswordConfirmed(passwordConfirmed);

			panel.setVisible(false);
			if ("dontInstallTomcat".equals(WizardModel.getInstance().getTomcatAction())) {
				new DontInstallTomcatScreen(this.jframe);
			} else {
				new InstallTomcatScreen(this.jframe);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

	protected void backButtonActionPerformed(ActionEvent e) {
		panel.setVisible(false);
		JPanel p = Main.panels.get(SCREEN_INDEX - 1);
		p.setVisible(true);
		System.out.println("1. Main.panels:" + Main.panels.size());
		System.out.println("1. name:" + p.getName());
	}

}
