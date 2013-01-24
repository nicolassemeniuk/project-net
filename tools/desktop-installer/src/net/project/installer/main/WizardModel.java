package net.project.installer.main;

public class WizardModel {

	private WizardModel() {
	}

	private static WizardModel wizardModel;

	private String oracleAction;

	private String tomcatAction;

	private String oracleTargetLocation;

	private String oraclePort;

	private String oracleSystemPassword;

	private String oracleSystemPasswordConfirmed;

	private String databaseName = "XE";

	private boolean databaseConnectionSuccessfull;

	private boolean oracleLicenseAccepted = false;

	private String tomcatHomeLocation = "/Users/ljubisapunosevac/Programs/apache-tomcat-5.5.28";

	private String mailServerHostName;

	private String mailServerUsername;

	private String mailServerPassword;

	private String mailServerConfirmedPassword;

	public static WizardModel getInstance() {
		try {
			if (wizardModel == null) {
				wizardModel = new WizardModel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wizardModel;
	}

	public boolean isOracleLicenseAccepted() {
		return oracleLicenseAccepted;
	}

	public void setOracleLicenseAccepted(boolean oracleLicenseAccepted) {
		this.oracleLicenseAccepted = oracleLicenseAccepted;
	}

	public String getTomcatHomeLocation() {
		return tomcatHomeLocation;
	}

	public void setTomcatHomeLocation(String tomcatHomeLocation) {
		this.tomcatHomeLocation = tomcatHomeLocation;
	}

	public String getMailServerHostName() {
		return mailServerHostName;
	}

	public void setMailServerHostName(String mailServerHostName) {
		this.mailServerHostName = mailServerHostName;
	}

	public String getMailServerUsername() {
		return mailServerUsername;
	}

	public void setMailServerUsername(String mailServerUsername) {
		this.mailServerUsername = mailServerUsername;
	}

	public String getMailServerPassword() {
		return mailServerPassword;
	}

	public void setMailServerPassword(String mailServerPassword) {
		this.mailServerPassword = mailServerPassword;
	}

	public String getMailServerConfirmedPassword() {
		return mailServerConfirmedPassword;
	}

	public void setMailServerConfirmedPassword(String mailServerConfirmedPassword) {
		this.mailServerConfirmedPassword = mailServerConfirmedPassword;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public boolean isDatabaseConnectionSuccessfull() {
		return databaseConnectionSuccessfull;
	}

	public void setDatabaseConnectionSuccessfull(boolean databaseConnectionSuccessfull) {
		this.databaseConnectionSuccessfull = databaseConnectionSuccessfull;
	}

	public String getOracleTargetLocation() {
		return oracleTargetLocation;
	}

	public void setOracleTargetLocation(String oracleTargetLocation) {
		this.oracleTargetLocation = oracleTargetLocation;
	}

	public String getOraclePort() {
		return oraclePort;
	}

	public void setOraclePort(String oraclePort) {
		this.oraclePort = oraclePort;
	}

	public String getOracleSystemPassword() {
		return oracleSystemPassword;
	}

	public void setOracleSystemPassword(String oracleSystemPassword) {
		this.oracleSystemPassword = oracleSystemPassword;
	}

	public String getOracleSystemPasswordConfirmed() {
		return oracleSystemPasswordConfirmed;
	}

	public void setOracleSystemPasswordConfirmed(String oracleSystemPasswordConfirmed) {
		this.oracleSystemPasswordConfirmed = oracleSystemPasswordConfirmed;
	}

	public String getOracleAction() {
		return oracleAction;
	}

	public void setOracleAction(String oracleAction) {
		this.oracleAction = oracleAction;
	}

	public String getTomcatAction() {
		return tomcatAction;
	}

	public void setTomcatAction(String tomcatAction) {
		this.tomcatAction = tomcatAction;
	}

	public boolean canGoOnSecondScreen() {
		return (oracleAction != null && tomcatAction != null);
	}

}
