package net.project.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "statistic")
public class Statistic implements Serializable {
	
	
	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -2929116242092351447L;

	/** 
	 * default constructor 
	 */
	public Statistic() {
	}
    
	public Statistic(String address, Date time, String version, Integer numberOfUsers, Integer averageUsersPerMonth, 
			Integer userLoginToday, Integer userLoginLastMonth, Integer totalLoginsToday, String systemDocumentStorage, 
			Integer unregisteredUser, Integer activeBusinessCount, String newUserTrend,  
			Integer newUsersCurrentMonth,  Integer newUsersThreeMonth, Double averageDailyUserLogin, Integer activeProjectCount, 
			Integer activeUser, Boolean marked) {
		this.userIpAddress = address;
		this.versionCheckTime = time;
		this.currentVersion = version;
		this.numberOfUsers = numberOfUsers;
		this.averageUsersPerMonth = averageUsersPerMonth;
		this.userLoginToday = userLoginToday;
		this.userLoginLastMonth = userLoginLastMonth;
		this.totalLoginsToday = totalLoginsToday;
		this.systemDocumentStorage = systemDocumentStorage;	
		this.unregisteredUser = unregisteredUser;		
		this.activeBusinessCount = activeBusinessCount;
		this.newUserTrend = newUserTrend;
		this.newUsersCurrentMonth = newUsersCurrentMonth;
		this.newUsersThreeMonth = newUsersThreeMonth;
		this.averageDailyUserLogin = averageDailyUserLogin;
		this.activeProjectCount = activeProjectCount;
		this.activeUser = activeUser;
		this.marked = marked;
	}
	
	public Statistic(String address, Date time, String version, Integer numberOfUsers, Integer averageUsersPerMonth, 
			Integer userLoginToday, Integer userLoginLastMonth, Integer totalLoginsToday, String systemDocumentStorage, 
			Integer unregisteredUser, Integer activeBusinessCount, String newUserTrend,  
			Integer newUsersCurrentMonth,  Integer newUsersThreeMonth, Double averageDailyUserLogin, Integer activeProjectCount, 
			Integer activeUser, Boolean marked, String latitude, String longitude, String area, String city, String country, String organization) {
		this.userIpAddress = address;
		this.versionCheckTime = time;
		this.currentVersion = version;
		this.numberOfUsers = numberOfUsers;
		this.averageUsersPerMonth = averageUsersPerMonth;
		this.userLoginToday = userLoginToday;
		this.userLoginLastMonth = userLoginLastMonth;
		this.totalLoginsToday = totalLoginsToday;
		this.systemDocumentStorage = systemDocumentStorage;	
		this.unregisteredUser = unregisteredUser;		
		this.activeBusinessCount = activeBusinessCount;
		this.newUserTrend = newUserTrend;
		this.newUsersCurrentMonth = newUsersCurrentMonth;
		this.newUsersThreeMonth = newUsersThreeMonth;
		this.averageDailyUserLogin = averageDailyUserLogin;
		this.activeProjectCount = activeProjectCount;
		this.activeUser = activeUser;
		this.marked = marked;
		this.latitude = latitude;
		this.longitude = longitude;
		this.area = area;
		this.city = city;
		this.country = country;
		this.organization = organization;
	}



	public Statistic(String ip, Date time, String currentVersion, Integer numberOfUsers){
		this.userIpAddress = ip;
		this.versionCheckTime = time;
		this.currentVersion = currentVersion;
		this.numberOfUsers = numberOfUsers;
	}
	
	public Statistic(String ip){
		this.userIpAddress = ip;
	}
	
	private String userIpAddress;
	
	private Date versionCheckTime;
	
	private String currentVersion;
	
	private Integer numberOfUsers;
	
	private Integer averageUsersPerMonth;
	
	private Integer userLoginToday;
	
	private Integer userLoginLastMonth;
	
	private Integer totalLoginsToday;
	
	private String systemDocumentStorage;
	
	private Integer unregisteredUser;
	
	private Integer activeBusinessCount;
	
	private String newUserTrend;
	
	private Integer newUsersCurrentMonth;
	
	private Integer newUsersThreeMonth;
	
	private Double averageDailyUserLogin;
	
	private Integer activeProjectCount;
	
	private Integer activeUser;
	
	private Boolean marked;
	
	private String latitude;
	private String longitude;
	private String area;
	private String city;
	private String country;
	private String organization;
	
	@Column(name = "LATITUDE")
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@Column(name = "LONGITUDE")
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Column(name = "AREA")
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Column(name = "CITY")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "COUNTRY")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "ORGANIZATION")
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Column(name = "MARKED")
	public Boolean getMarked() {
		return marked;
	}

	public void setMarked(Boolean marked) {
		this.marked = marked;
	}

	/**
	 * @return Returns the activeBusinessCount.
	 */
	@Column(name = "ACTIVE_BUSINESS_COUNT")
	public Integer getActiveBusinessCount() {
		return activeBusinessCount;
	}

	/**
	 * @param activeBusinessCount The activeBusinessCount to set.
	 */
	public void setActiveBusinessCount(Integer activeBusinessCount) {
		this.activeBusinessCount = activeBusinessCount;
	}

	/**
	 * @return Returns the activeProjectCount.
	 */
	@Column(name = "ACTIVE_PROJECT_COUNT")
	public Integer getActiveProjectCount() {
		return activeProjectCount;
	}

	/**
	 * @param activeProjectCount The activeProjectCount to set.
	 */
	public void setActiveProjectCount(Integer activeProjectCount) {
		this.activeProjectCount = activeProjectCount;
	}

	/**
	 * @return Returns the activeUser.
	 */
	@Column(name = "ACTIVE_USER")
	public Integer getActiveUser() {
		return activeUser;
	}

	/**
	 * @param activeUser The activeUser to set.
	 */
	public void setActiveUser(Integer activeUser) {
		this.activeUser = activeUser;
	}

	/**
	 * @return Returns the averageDailyUserLogin.
	 */
	@Column(name = "AVERAGE_DAILY_USER_LOGINS")
	public Double getAverageDailyUserLogin() {
		return averageDailyUserLogin;
	}

	/**
	 * @param averageDailyUserLogin The averageDailyUserLogin to set.
	 */
	public void setAverageDailyUserLogin(Double averageDailyUserLogin) {
		this.averageDailyUserLogin = averageDailyUserLogin;
	}

	/**
	 * @return Returns the averageUsersPerMonth.
	 */
	@Column(name = "AVERAGE_USERS_PER_MONTH", length = 4)
	public Integer getAverageUsersPerMonth() {
		return averageUsersPerMonth;
	}

	/**
	 * @param averageUsersPerMonth The averageUsersPerMonth to set.
	 */
	public void setAverageUsersPerMonth(Integer averageUsersPerMonth) {
		this.averageUsersPerMonth = averageUsersPerMonth;
	}

	/**
	 * @return Returns the newUsersCurrentMonth.
	 */
	@Column(name = "NEW_USERS_CURRENT_MONTH")
	public Integer getNewUsersCurrentMonth() {
		return newUsersCurrentMonth;
	}

	/**
	 * @param newUsersCurrentMonth The newUsersCurrentMonth to set.
	 */
	public void setNewUsersCurrentMonth(Integer newUsersCurrentMonth) {
		this.newUsersCurrentMonth = newUsersCurrentMonth;
	}

	/**
	 * @return Returns the newUsersThreeMonth.
	 */
	@Column(name = "NEW_USERS_THREE_MONTH")
	public Integer getNewUsersThreeMonth() {
		return newUsersThreeMonth;
	}

	/**
	 * @param newUsersThreeMonth The newUsersThreeMonth to set.
	 */
	public void setNewUsersThreeMonth(Integer newUsersThreeMonth) {
		this.newUsersThreeMonth = newUsersThreeMonth;
	}

	/**
	 * @return Returns the newUserTrend.
	 */
	@Column(name = "NEW_USER_TREND", length=10)
	public String getNewUserTrend() {
		return newUserTrend;
	}

	/**
	 * @param newUserTrend The newUserTrend to set.
	 */
	public void setNewUserTrend(String newUserTrend) {
		this.newUserTrend = newUserTrend;
	}

	/**
	 * @return Returns the systemDocumentStorage.
	 */
	@Column(name = "SYSTEM_DOCUMENT_STORAGE", length=25)
	public String getSystemDocumentStorage() {
		return systemDocumentStorage;
	}

	/**
	 * @param systemDocumentStorage The systemDocumentStorage to set.
	 */
	public void setSystemDocumentStorage(String systemDocumentStorage) {
		this.systemDocumentStorage = systemDocumentStorage;
	}

	/**
	 * @return Returns the totalLoginsToday.
	 */
	@Column(name = "TOTAL_LOGINS_TODAY")
	public Integer getTotalLoginsToday() {
		return totalLoginsToday;
	}

	/**
	 * @param totalLoginsToday The totalLoginsToday to set.
	 */
	public void setTotalLoginsToday(Integer totalLoginsToday) {
		this.totalLoginsToday = totalLoginsToday;
	}

	/**
	 * @return Returns the unregisteredUser.
	 */
	@Column(name = "UNREGISTERED_USER")
	public Integer getUnregisteredUser() {
		return unregisteredUser;
	}

	/**
	 * @param unregisteredUser The unregisteredUser to set.
	 */
	public void setUnregisteredUser(Integer unregisteredUser) {
		this.unregisteredUser = unregisteredUser;
	}

	/**
	 * @return Returns the userLoginLastMonth.
	 */
	@Column(name = "USER_LOGIN_LAST_THIRTY_DAYS")
	public Integer getUserLoginLastMonth() {
		return userLoginLastMonth;
	}

	/**
	 * @param userLoginLastMonth The userLoginLastMonth to set.
	 */
	public void setUserLoginLastMonth(Integer userLoginLastMonth) {
		this.userLoginLastMonth = userLoginLastMonth;
	}

	/**
	 * @return Returns the userLoginToday.
	 */
	@Column(name = "USER_LOGIN_TODAY")
	public Integer getUserLoginToday() {
		return userLoginToday;
	}

	/**
	 * @param userLoginToday The userLoginToday to set.
	 */
	public void setUserLoginToday(Integer userLoginToday) {
		this.userLoginToday = userLoginToday;
	}

	@Column(name = "NUMBER_OF_USERS", nullable = false, length = 10)
	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	@Column(name = "CLIENT_VERSION", nullable = false, length = 10)
	public String getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	@Id
	@Column(name = "CLIENT_ADDRESS", unique = true, nullable = false, length = 25)
	public String getUserIpAddress() {
		return userIpAddress;
	}

	public void setUserIpAddress(String userIpAddress) {
		this.userIpAddress = userIpAddress;
	}

	@Column(name = "REQUEST_TIME", nullable = false)
	public Date getVersionCheckTime() {
		return versionCheckTime;
	}

	public void setVersionCheckTime(Date versionCheckTime) {
		this.versionCheckTime = versionCheckTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("userIpAddress:"+userIpAddress+"\n");
		sb.append("versionCheckTime:"+versionCheckTime+"\n");
		sb.append("currentVersion:"+currentVersion+"\n");
		sb.append("numberOfUsers:"+numberOfUsers+"\n");
		sb.append("averageUsersPerMonth:"+averageUsersPerMonth+"\n");
		sb.append("userLoginToday:"+userLoginToday+"\n");
		sb.append("userLoginLastMonth:"+userLoginLastMonth+"\n");
		sb.append("totalLoginsToday:"+totalLoginsToday+"\n");
		sb.append("systemDocumentStorage:"+systemDocumentStorage+"\n");
		sb.append("unregisteredUser:"+unregisteredUser+"\n");
		sb.append("activeBusinessCount:"+activeBusinessCount+"\n");
		sb.append("newUserTrend:"+newUserTrend+"\n");
		sb.append("newUsersCurrentMonth:"+newUsersCurrentMonth+"\n");
		sb.append("newUsersThreeMonth:"+newUsersThreeMonth+"\n");
		sb.append("averageDailyUserLogin:"+averageDailyUserLogin+"\n");
		sb.append("activeProjectCount:"+activeProjectCount+"\n");
		sb.append("activeUser:"+activeUser+"\n");
		sb.append("marked:"+marked+"\n");
		sb.append("latitude:"+latitude+"\n");
		sb.append("longitude:"+longitude+"\n");
		sb.append("area:"+area+"\n");
		sb.append("city:"+city+"\n");
		sb.append("country:"+country+"\n");
		sb.append("organization:"+organization+"\n");
		return sb.toString();
	}
	
	
	
}
