package net.project.installer.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.project.installer.util.SwingResourceManager;

public class PerformInstallationScreen extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -849870025146787020L;
	private final JPanel panel = new JPanel();
	private final JButton okButton = new JButton();

	private final JButton testDatabaseConnectionButton = new JButton();
	private final JLabel progresbarLabel = new JLabel();
	private final JLabel labelLogo = new JLabel();
	private final JProgressBar progressBar = new JProgressBar(0, 100);
	private final JTextPane welcomeToProjectnetTextPane = new JTextPane();

	private Task task;

	private JFrame jframe;

	public PerformInstallationScreen() {
	}

	public PerformInstallationScreen(JFrame jframe) {
		this.jframe = jframe;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		jframe.setSize(new Dimension(800, 600));
		jframe.setIconImage(SwingResourceManager.getImage(Main.class, "/net/project/installer/images/favicon.ico"));
		jframe.setTitle("Welcome to Project.net Installer");

		jframe.getContentPane().add(panel);
		panel.setLayout(null);
		panel.setName(Main.INSTALL_ORACLE_SCREEN);

		panel.add(okButton);
		okButton.addActionListener(new CancelButtonActionListener());
		okButton.setText("Cancel");
		okButton.setBounds(350, 514, 120, 29);

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

		panel.add(testDatabaseConnectionButton);
		testDatabaseConnectionButton.addActionListener(new TestDatabaseConnectionButtonActionListener());
		testDatabaseConnectionButton.setText("Test database connection");
		testDatabaseConnectionButton.setBounds(296, 440, 208, 25);
		testDatabaseConnectionButton.setEnabled(false);

		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		panel.add(progressBar);
		progressBar.setBounds(67, 479, 683, 14);

		panel.add(progresbarLabel);
		progresbarLabel.setBounds(67, 461, 385, 15);
		progresbarLabel.setVisible(true);

		progressBar.setIndeterminate(true);
		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.

		progressBar.setVisible(true);
		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();

	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
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

	protected boolean testDatabaseConnectionButtonActionPerformed(ActionEvent e) {
		Connection connection = null;
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

	protected void cancelButtonActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */

		private Thread t = new Thread() {

			@Override
			public void run() {
				try {
					if ("installOracle".equals(WizardModel.getInstance().getOracleAction())) {
						progresbarLabel.setText("Installing Oracle Express...");
						progresbarLabel.setVisible(true);
						System.out.println("Oracle installation started.");
						boolean oracleInstalled = HandleOracleInstallation.installOracle();
						System.out.println("Oracle installation ends:" + oracleInstalled);
						setProgress(99);
						sleep(1000);
					}
					
					sleepTime = 2000;
					setProgress(0);
					System.out.println("Tablespaces creation started.");
					progresbarLabel.setText("Creating tablespaces...");
					boolean tablespacesCreated = HandleOracleInstallation.createTablespaces();
					System.out.println("Tablespaces created:" + tablespacesCreated);
					setProgress(99);
					sleep(1000);
					sleepTime = 100;
					setProgress(0);
					System.out.println("Creating users.");
					progresbarLabel.setText("Creating users...");
					boolean usersCreated = HandleOracleInstallation.createUsers();
					System.out.println("Users created:" + usersCreated);
					setProgress(99);
					sleep(1000);
					sleepTime = 1500;
					setProgress(0);
					System.out.println("Importing users data.");
					progresbarLabel.setText("Creating users data...");
					boolean dataImported = HandleOracleInstallation.importData();
					System.out.println("Data imported:" + dataImported);

					setProgress(99);
					sleep(1000);
					sleepTime = 3000;
					setProgress(0);
					progresbarLabel.setText("Installing Tomcat and application...");
					HandleTomcatInstallation.startTomcatInstallation();
					progresbarLabel.setText("Installation complete.");
					progressBar.setVisible(false);
					okButton.setText("Ok");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		private int progress = 0;
		
		private int sleepTime = 11000;
		
		@Override
		public Void doInBackground() {
			Random random = new Random();
			
			// Initialize progress property.
			setProgress(0);
			// Sleep for at least one second to simulate "startup".
			try {
				Thread.sleep(1000 + random.nextInt(2000));
			} catch (InterruptedException ignore) {
			}

			t.start();

			while (progress < 100) {
				// Sleep for up to one second.
				try {
					Thread.sleep(random.nextInt(sleepTime));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Make random progress.
				progress = progress + 1;
				setProgress(Math.min(progress, 100));
			}
//			panel.setVisible(false);
//			if ("dontInstallTomcat".equals(WizardModel.getInstance().getTomcatAction())) {
//				new DontInstallTomcatScreen(jframe);
//			} else {
//				new InstallTomcatScreen(jframe);
//			}

			return null;
		}

		/*
		 * Executed in event dispatch thread
		 */
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			okButton.setEnabled(true);
		}

	}

}