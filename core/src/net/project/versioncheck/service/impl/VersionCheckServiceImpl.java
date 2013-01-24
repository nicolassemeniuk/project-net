/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
 */
package net.project.versioncheck.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.project.document.metric.DocumentMetricCollection;
import net.project.document.metric.DocumentMetrics;
import net.project.hibernate.service.ServiceFactory;
import net.project.resource.metric.ResourceMetricCollection;
import net.project.resource.metric.ResourceMetrics;
import net.project.security.User;
import net.project.space.metric.SpaceMetricCollection;
import net.project.space.metric.SpaceMetrics;
import net.project.util.DBVersion;
import net.project.versioncheck.service.IVersionCheckService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service(value="versionCheckService")
public class VersionCheckServiceImpl implements IVersionCheckService {

	private static final Logger log = Logger.getLogger(VersionCheckServiceImpl.class.getName());

	public String checkVersion(User user, String ipAddress) {
		String newVersion = null;
		try {
			if (!user.isApplicationAdministrator()) {
				if (log.isDebugEnabled()) {
					log.debug("Current user is not Application Administrator.");
				}
				return null;
			}	
			// check is the version check server available/online
			InetAddress addr = InetAddress.getByName("support.project.net");
			boolean isReachable = addr.isReachable(5000);
			if(!isReachable){
				if (log.isDebugEnabled()) {
					log.debug("Version check server is not reachable.");
				}
				return null;
			}
			// DBVersion should be changed with Hibernate based API
			DBVersion db = new DBVersion();
			db.load();
			if (log.isDebugEnabled()) {
				log.debug("Calling web service.");
			}
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			int numberOfUsers = 0;
			numberOfUsers = ServiceFactory.getInstance().getPnUserService().getUsersCount();
			// Web Service is disabled 
			//VersionControlProxy sampleVersionControlProxyid = new VersionControlProxy();
			//String result = sampleVersionControlProxyid.getVersion(ipAddress, db.getAppVersion(), numberOfUsers);
			String result = null;
			//if (result == null || "".equals(result)) {
				// backup strategy for sending/receiving information to the
				// version check server
				try {
					ResourceMetricCollection resourceMetricCollection = new ResourceMetricCollection();
					// Construct data
					StringBuffer data = new StringBuffer(URLEncoder.encode("ip", "UTF-8") + "=" + URLEncoder.encode(ipAddress, "UTF-8"));
					data.append("&" + URLEncoder.encode("users", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numberOfUsers), "UTF-8"));
					data.append("&" + URLEncoder.encode("currentVersion", "UTF-8") + "=" + URLEncoder.encode(db.getAppVersion(), "UTF-8"));
					
					data.append("&" + URLEncoder.encode("NEW_USERS_CURRENT_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue(ResourceMetrics.NEW_USERS_CURRENT_MONTH_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("ACTIVE_USER_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.ACTIVE_USER_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("NEW_USERS_THREE_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.NEW_USERS_THREE_MONTH_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("UNREGISTERED_USER_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.UNREGISTERED_USER_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("AVERAGE_USERS_PER_MONTH_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.AVERAGE_USERS_PER_MONTH_METRIC), "UTF-8"));
					//data.append("&" + URLEncoder.encode("TOTAL_USER_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.TOTAL_USER_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("NEW_USER_TREND_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.NEW_USER_TREND_METRIC), "UTF-8"));
					
					data.append("&" + URLEncoder.encode("USER_LOGIN_TODAY_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.USER_LOGIN_TODAY_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("AVERAGE_DAILY_USER_LOGINS_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.AVERAGE_DAILY_USER_LOGINS_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("TOTAL_LOGINS_TODAY_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.TOTAL_LOGINS_TODAY_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("USER_LOGIN_LAST_THIRTY_DAYS_METRIC", "UTF-8") + "=" + URLEncoder.encode(resourceMetricCollection.getMetricValue (ResourceMetrics.USER_LOGIN_LAST_THIRTY_DAYS_METRIC), "UTF-8"));
					
					SpaceMetricCollection spaceMetricCollection  = new SpaceMetricCollection();	
					data.append("&" + URLEncoder.encode("ACTIVE_PROJECT_COUNT_METRIC", "UTF-8") + "=" + URLEncoder.encode(spaceMetricCollection.getMetricValue (SpaceMetrics.ACTIVE_PROJECT_COUNT_METRIC), "UTF-8"));
					data.append("&" + URLEncoder.encode("ACTIVE_BUSINESS_COUNT_METRIC", "UTF-8") + "=" + URLEncoder.encode(spaceMetricCollection.getMetricValue (SpaceMetrics.ACTIVE_BUSINESS_COUNT_METRIC), "UTF-8"));
					
					DocumentMetricCollection documentMetricCollection = new DocumentMetricCollection();
					data.append("&" + URLEncoder.encode("SYSTEM_DOCUMENT_STORAGE_METRIC", "UTF-8") + "=" + URLEncoder.encode(documentMetricCollection.getMetricValue (DocumentMetrics.SYSTEM_DOCUMENT_STORAGE_METRIC), "UTF-8"));
					//System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
					//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
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
					wr.close();
					rd.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			//}
			//if (!db.getAppVersion().equals(result)) {
				newVersion = result;
			//}
			if (log.isDebugEnabled()) {
				log.debug("Is new version available: " + newVersion);
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
