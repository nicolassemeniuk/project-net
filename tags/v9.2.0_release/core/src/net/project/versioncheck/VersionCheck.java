package net.project.versioncheck;

import net.project.base.property.PropertyProvider;

public class VersionCheck {

	private boolean versionCheckEnabled = PropertyProvider.getBoolean("prm.versioncheck.isenabled");
	
	private boolean checkAgain = false;
	
	private boolean newVersionAvailable = false;
	
	private String  newVersion = "";

	private Long lastTimeCheck = System.currentTimeMillis();
	
	private Long checkAfterThisMuchHours = Long.valueOf(PropertyProvider.get("prm.versioncheck.check.period.in.hours")) * 60 * 60;

	public boolean isVersionCheckEnabled() {
		return versionCheckEnabled;
	}

	public void setVersionCheckEnabled(boolean versionCheckEnabled) {
		this.versionCheckEnabled = versionCheckEnabled;
	}

	public Long getLastTimeCheck() {
		return lastTimeCheck;
	}

	public void setLastTimeCheck(Long lastTimeCheck) {
		this.lastTimeCheck = lastTimeCheck;
	}

	public Long getCheckAfterThisMuchHours() {
		return checkAfterThisMuchHours;
	}

	public void setCheckAfterThisMuchHours(Long checkAfterThisMuchHours) {
		this.checkAfterThisMuchHours = checkAfterThisMuchHours;
	}
	
	public boolean isCheckAgain() {
		try {
			if (System.currentTimeMillis() - checkAfterThisMuchHours < lastTimeCheck){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setCheckAgain(boolean checkAgain) {
		this.checkAgain = checkAgain;
	}
	
	public boolean isNewVersionAvailable() {
		return newVersionAvailable;
	}

	public void setNewVersionAvailable(boolean newVersionAvailable) {
		this.newVersionAvailable = newVersionAvailable;
	}
	
	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
	
}
