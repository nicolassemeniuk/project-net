package net.project.versioncheck;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class VersionCheckServiceTestManual {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(VersionCheckServiceTestManual.checkVersion());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String checkVersion() {
		String newVersion = null;
		try {

			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			int numberOfUsers = 0;
			numberOfUsers = 1000;
			// Web Service is disabled
			// VersionControlProxy sampleVersionControlProxyid = new
			// VersionControlProxy();
			// String result = sampleVersionControlProxyid.getVersion(ipAddress,
			// db.getAppVersion(), numberOfUsers);
			String result = null;
			if (result == null || "".equals(result)) {
				// backup strategy for sending/receiving information to the
				// version check server
				try {
					// Construct data
					StringBuffer data = new StringBuffer(URLEncoder.encode("ip", "UTF-8") + "=" + URLEncoder.encode(ipAddress, "UTF-8"));
					data.append("&" + URLEncoder.encode("users", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfUsers), "UTF-8"));
					data.append("&" + URLEncoder.encode("currentVersion", "UTF-8") + "=" + URLEncoder.encode("9.0.0", "UTF-8"));

					data.append("&" + URLEncoder.encode("NEW_USERS_CURRENT_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("ACTIVE_USER_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("NEW_USERS_THREE_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode("30", "UTF-8"));
					data.append("&" + URLEncoder.encode("UNREGISTERED_USER_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("AVERAGE_USERS_PER_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					// data.append("&" + URLEncoder.encode("TOTAL_USER_METRIC",
					// "UTF-8") + "=" +
					// URLEncoder.encode(resourceMetricCollection.getMetricValue
					// (ResourceMetrics.TOTAL_USER_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("NEW_USER_TREND_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

					data.append("&" + URLEncoder.encode("USER_LOGIN_TODAY_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("AVERAGE_DAILY_USER_LOGINS_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("TOTAL_LOGINS_TODAY_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("USER_LOGIN_LAST_THIRTY_DAYS_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

					data.append("&" + URLEncoder.encode("ACTIVE_PROJECT_COUNT_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
					data.append("&" + URLEncoder.encode("ACTIVE_BUSINESS_COUNT_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

					data.append("&" + URLEncoder.encode("SYSTEM_DOCUMENT_STORAGE_METRIC", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

					// System.setProperty("java.protocol.handler.pkgs",
					// "com.sun.net.ssl.internal.www.protocol");
					// Security.addProvider(new
					// com.sun.net.ssl.internal.ssl.Provider());
					// Send data
					
					URL url = new URL("https://support.project.net/versioncheck/VersionCheckServlet");
					URLConnection conn = url.openConnection();
					conn.setDoOutput(true);
					OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
					wr.write(data.toString());
					wr.flush();

					// Get the response
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line;
					while ((line = rd.readLine()) != null) {
						result = line;
					}
					newVersion = result;
					wr.close();
					rd.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// } catch (RemoteException re) {
			// if (log.isDebugEnabled()) {
			// log.debug(" Exception durring version check." + re.getMessage());
			// }
			// re.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newVersion;
	}

}
