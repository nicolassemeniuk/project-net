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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|    $Revision: 20398 $
|    $Date: 2010-02-11 08:33:06 -0300 (jue, 11 feb 2010) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;
import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

public class DomainListBean implements Serializable, INotificationDB {

    // TUT sole interface to JDBC
    private DBBean dbBean = new DBBean();
    private int notificationTypeCount = 0;


    /* -------------------------------  Constructors  ------------------------------- */
    // TUT no arg conmstructor mandatory
    public DomainListBean() {
        // do nothing
    }





    /* -------------------------------  Implementing domain listing methods  ------------------------------- */

    /**
     *  Returns an HTML option list populated with the available choices for  delivery types, as enumerated in the table  pn_notification_delivery_type<p>
     *  The types in this table delineate the delivery methods (email, pager, etc.) available to the user within the Notiifcation System. <p>
     *  The user will be notified via the method they select. <P>
     *
     *  @return an HTML OptionList enumerating the available choices for notiifcation delivery methods as enumerated in pn_notification_delivery_type
     */

    public String getDeliveryTypesOptionList() {
        // TUT qstr is queryString  convention
        String qstrGetDeliveryTypes = "select name, delivery_type_id from pn_notification_delivery_type";

        StringBuffer deliveryTypes = new StringBuffer();

        try {
            dbBean.executeQuery(qstrGetDeliveryTypes);

            // TUT dbBean has changed internal state after executeQuery.... the result of the query is held internally and one line of that result is  placed in  DBBean.result. getString is an sql thing that takes the name of the column and returns the value held in that sql-thing java.sql.resultSet
            while (dbBean.result.next())
                deliveryTypes.append("<option value=\"" + dbBean.result.getString("delivery_type_id") + "\">"
                        + PropertyProvider.get(dbBean.result.getString("name")) + "</option> \n");
        } catch (SQLException sqle) {
            // TUT log sql errors
        	Logger.getLogger(DomainListBean.class).debug("notification.DomainListBean.getDeliveryTypesOptionList threw an SQL exception: " + sqle);
        } // end catch
        finally {
            // TUT always release the bean.
            dbBean.release();
        }

        return deliveryTypes.toString();

    }


    /**
     *  Returns an HTML option list populated with the available choices for notiifcation delivery intervals, as enumerated in the table  pn_subscription_recurrence<p>
     *  The types in this table delineate the notification intervals (immediately, daily, weekly, monthly) available to the user within the Notiifcation System. <p>
     *  The user will be notified on the calendar interval they select. <P>
     *
     *  @return an HTML OptionList enumerating the available choices for notiifcation delivery intervals as enumerated in pn_subscription_recurrence
     */
    public String getDeliveryIntervalOptionList() {
        // TUT qstr is queryString  convention
        String qstrGetDeliveryIntervalOptions = "select name, recurrence_id from pn_subscription_recurrence";

        StringBuffer deliveryIntervals = new StringBuffer();

        try {
            dbBean.executeQuery(qstrGetDeliveryIntervalOptions);

            // TUT dbBean has changed internal state after executeQuery.... the result of the query is held internally and one line of that result is  placed in  DBBean.result. getString is an sql thing that takes the name of the column and returns the value held in that sql-thing java.sql.resultSet
            while (dbBean.result.next())
                deliveryIntervals.append("<option value=\"" + dbBean.result.getString("recurrence_id") + "\">"
                        + PropertyProvider.get(dbBean.result.getString("name")) + "</option> \n");
        } catch (SQLException sqle) {
            // TUT log sql errors
        	Logger.getLogger(DomainListBean.class).debug("notification.DomainListBean.getDeliveryIntervalOptionList threw an SQL exception: " + sqle);
        } // end catch
        finally {
            // TUT always release the bean.
            dbBean.release();
        }

        return deliveryIntervals.toString();

    }


    /**
     *  Returns an HTML option list populated with the available choices for notification types, given the <i>type</i> of object referenced by the <code>object_id</code> passed in as a parameter <p>
     *
     *  @return an HTML OptionList enumerating the available choices forthe available choices for notification types
     */

    public String getNotificationTypesOptionsList(String objectType, String isCreateType) {

        StringBuffer eventTypes = new StringBuffer();
        notificationTypeCount = 0;

        // Each of these PL/SQL calls limits the returned value so that only options which make sense for a given object type appear in the returned options list to the caller
        // So far, there are two cases or type sof objects - a file and a folder. As more types are added, more PL/SQL prepared statments will be added and another "if -then" pair will be added
        // to this try block
        String sql = "";
        try {
            if (objectType.equalsIgnoreCase(INotificationDB.FILE)){
                //dbBean.prepareCall(INotificationDB.PREPARED_CALL_GET_NOTIFICATION_TYPES_FOR_FILE);
            	sql = " SELECT n.notification_type_id, n.description "+
            		  "	FROM pn_event_type e, pn_notification_type n, pn_event_has_notification ehn" +
            		  " WHERE e.object_type = i_object_type   AND e.event_type_id = ehn.event_type_id " +
            		  " AND n.notification_type_id = ehn.notification_type_id  AND n.name != 'create' " +
            		  " AND n.name != 'create_container' AND n.name != 'remove_container'";
            } else {
               // dbBean.prepareCall(INotificationDB.PREPARED_CALL_GET_NOTIFICATION_TYPES);
               sql = "SELECT n.notification_type_id, n.description " +
               		 "FROM pn_event_type e, pn_notification_type n, pn_event_has_notification ehn " +
               		 "WHERE e.event_type_id = ehn.event_type_id AND n.notification_type_id = ehn.notification_type_id AND  n.record_status = 'A' ";
               if (objectType.equals("form")){
            	 sql =  sql.concat(" AND e.object_type in ( 'form' , 'form_data' ) ");
               }else{
            	 sql =  sql.concat(" AND e.object_type = '" + objectType + "' ");
               }
               if (isCreateType != null && isCreateType.equals("1")){
            	 sql = sql.concat(" AND IS_CREATE_TYPE = '1' ");  
               }
               
            }      
            dbBean.executeQuery(sql);
            
/*            dbBean.cstmt.setString(2, objectType);
            dbBean.cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            dbBean.executeCallable();
            ResultSet resultSet = (ResultSet) dbBean.cstmt.getObject(1);*/
            
            ResultSet resultSet  = dbBean.result;

            // TUT dbBean has changed internal state after executeQuery.... the result of the query is held internally and one line of that result is  placed in  DBBean.result. getString is an sql thing that takes the name of the column and returns the value held in that sql-thing java.sql.resultSet
            while (resultSet.next()) {
                eventTypes.append("<option value=\"" + resultSet.getString("notification_type_id") + "\">"
                        + PropertyProvider.get(resultSet.getString("description")) + "</option> \n");
                notificationTypeCount++;
            }

        } catch (SQLException sqle) {
            // TUT log sql errors
            System.out.println("notification.DomainListBean.getEventTypesOptionList threw an SQL exception: " + sqle);
        } // end catch
        finally {
            // TUT always release the bean.
            dbBean.release();
        }

        return eventTypes.toString();

    }


    /**
     *  Returns a List<String> populated with the available choices for notification types, 
     *  given the <i>type</i> of object referenced 
     *  by the <code>objectType</code> and <code>isCreateType</code> passed in as a parameter 
     *     
     */
    public String getNotificationTypesCheckList(String objectType, String isCreateType) {
    	return getNotificationTypesCheckList(objectType, isCreateType, null);
    }
    
    
    /**
     *  Returns a List<String> populated with the available choices for notification types, 
     *  given the <i>type</i> of object referenced 
     *  by the <code>objectType</code> and <code>isCreateType</code> and <code>moduleType</code>
     *  passed in as a parameter 
     *     
     */
    public String getNotificationTypesCheckList(String objectType, String isCreateType, String moduleType ) {
        StringBuffer eventTypes = new StringBuffer();
        notificationTypeCount = 0;

        String sql = "";
        try {
            if (objectType.equalsIgnoreCase(INotificationDB.FILE)){
            	sql = " SELECT n.notification_type_id, n.description "+
            		  "	FROM pn_event_type e, pn_notification_type n, pn_event_has_notification ehn" +
            		  " WHERE e.object_type = i_object_type   AND e.event_type_id = ehn.event_type_id " +
            		  " AND n.notification_type_id = ehn.notification_type_id  AND n.name != 'create' " +
            		  " AND n.name != 'create_container' AND n.name != 'remove_container'";
            } else {
               sql = "SELECT n.notification_type_id, n.description " +
               		 "FROM pn_event_type e, pn_notification_type n, pn_event_has_notification ehn " +
               		 "WHERE e.event_type_id = ehn.event_type_id AND n.notification_type_id = ehn.notification_type_id AND  n.record_status = 'A' ";
               if (objectType.equals(ObjectType.FORM)){
            	 sql =  sql.concat(" AND e.object_type in ( '"+ ObjectType.FORM +"' , '"+ ObjectType.FORM_DATA +"' ) ");
               }else{
            	 sql =  sql.concat(" AND e.object_type = '" + objectType + "' ");
               }
               if (isCreateType != null && isCreateType.equals("1")){
            	 sql = sql.concat(" AND IS_CREATE_TYPE = '1' ");  
               }
               
            }      
            dbBean.executeQuery(sql);
            ResultSet resultSet  = dbBean.result;

            while (resultSet.next()) {
            	String description = PropertyProvider.get(resultSet.getString("description"));
            	if((StringUtils.isNotEmpty(moduleType) && moduleType.equals("project") && description.contains("Project"))
            				|| (StringUtils.isNotEmpty(moduleType) && moduleType.equals("directory") && description.contains("Participant")) || StringUtils.isEmpty(moduleType)) {
        	    	eventTypes.append("<tr class='tableContent'><td nowrap>");
        	      	eventTypes.append("<input type=checkbox name='notificationType' id='notificationType' value=\"" + resultSet.getString("notification_type_id") + "\">" +
        	      			description + "\n");
        	      	eventTypes.append("</td> </tr>");
            	}
                notificationTypeCount++;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).error("DomainListBean.getNotificationTypesCheckList() failed.."+sqle.getMessage());
        } // end catch
        finally {
            dbBean.release();
        }
        return eventTypes.toString();
    }
    
	/**
	 * Method to get selected and unselected notification events
	 * 
	 * @param objectType
	 * @param notificationTypeId
	 * @return html string 
	 */
    public String getNotificationTypesSubscribeCheckList(String objectType, String []  notificationTypeId) {
        StringBuffer eventTypes = new StringBuffer();
        
        int index = 0;
        String notificationTypeIdL = "";
        String description = "";

        String sql = "";
        try {
          sql = "SELECT n.notification_type_id, n.description " +
		  		 "FROM pn_event_type e, pn_notification_type n, pn_event_has_notification ehn " +
		  		 "WHERE e.event_type_id = ehn.event_type_id AND n.notification_type_id = ehn.notification_type_id AND  n.record_status = 'A' " +
       	  		 " AND e.object_type = '" + objectType + "' order by n.notification_type_id ";

            dbBean.executeQuery(sql);
            ResultSet resultSet  = dbBean.result;
            while (resultSet.next()) {
            	eventTypes.append("<tr class='tableContent'>");
                eventTypes.append("<td>&nbsp;</td>");
                eventTypes.append("<td nowrap>");
                notificationTypeIdL = resultSet.getString("notification_type_id");
                description =  PropertyProvider.get(resultSet.getString("description"));

               	if( notificationTypeId[index]!=null && notificationTypeId[index].equals(notificationTypeIdL)) {
                		eventTypes.append("<input type=checkbox  checked name='notificationType' id='notificationType' value=\"" +  notificationTypeIdL + "\">" +
                				description + "\n");
                		index++;
                } else { 
        		eventTypes.append("<input type=checkbox name='notificationType' id='notificationType' value=\"" +  notificationTypeIdL + "\">" +
         				description + "\n");                	
                }	
               	eventTypes.append("</td>");
                eventTypes.append("</tr>");
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).error("DomainListBean.getNotificationTypesSubscribeCheckList() failed.."+sqle.getMessage());
        } 
        finally {
            dbBean.release();
        }
        return eventTypes.toString();
    }

    public String getNotificationTypeCount() {
        return String.valueOf(notificationTypeCount);
    }
}



