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
|   $Revision: 20493 $
|       $Date: 2010-03-01 12:22:30 -0300 (lun, 01 mar 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.notification;

import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.hibernate.model.PnDocBySpaceView;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.model.PnGroupHasPersonPK;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.security.group.Group;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupTypeID;
import net.project.security.group.PrincipalGroup;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.HTMLUtils;
import net.project.util.StringUtils;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * Wraps the <CODE>Subscription</CODE> object and provides methods for rendering
 * Stylesheets etc for the JSP Page
 */
public class SubscriptionBean extends Subscription {
    /**
     * The XML Formatter
     */
    private XMLFormatter xmlFormatter = new XMLFormatter();

    /** no-arg constructor required for a bean */
    public SubscriptionBean() {
    }

    /**
     * Gets the presentation version of this Subscription object.
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting HTML.
     *
     * @return presetation of   this Subscription object.
     */
    public String getSubscriptionPresentation() {
        try {
            //System.out.println(xmlFormatter.getPresentation( net.project.xml.IXMLTags.XML_VERSION_STRING  + getXMLBody() ));
            load();
        } catch (PersistenceException pe) {
        }

        return (xmlFormatter.getPresentation(net.project.xml.IXMLTags.XML_VERSION_STRING + getXMLBody()));       //clob
    }

    /**
     * Sets the stylesheet file name into the formatter member of this object.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        xmlFormatter.setStylesheet(styleSheetFileName);
    }

    /**
     * Returns a list of checkboxes enlisting the all the Space participants
     *
     * @param user the instance of <code>User</code>
     * @param spaceID The Space ID
     * @return String the HTML rendition of list checkboxes for all the space participants
     */
    public String getSpaceParticipantGroupCheckBoxList(User user, String spaceID) {
        StringBuffer participants = new StringBuffer();
        GroupCollection groupList = new GroupCollection();
        Iterator it = null;
        Group group = null;
        String display = null;
        boolean isOrderByGroupName = true;

        try {

			if (spaceID != null) {
                Space space = SpaceFactory.constructSpaceFromID(spaceID);
                groupList.setSpace(space);
            } else {
                groupList.setSpace(user.getCurrentSpace());
            }

            groupList.loadAll(isOrderByGroupName);
            groupList.updateWithOwningSpace();
            it = groupList.iterator();
            while (it.hasNext()) {
                group = (Group) it.next();

                display = group.getName();
                participants.append("<tr class='tableContent'>");
                participants.append("<td nowrap>");

				if (group.isPrincipal() && ((PrincipalGroup) group).getOwnerID().equals(user.getID())){
                    participants.append("<input type=checkbox checked=checked name='teamMembers' value=\"" + group.getID() + "\">" +
                    		HTMLUtils.escape(display) + "\n");
                } else {
                    participants.append("<input type=checkbox name='teamMembers' value=\"" + group.getID() + "\">" +
                    		HTMLUtils.escape(display) + "\n");
                }
                participants.append("</td>");
                participants.append("</tr>");
            }
        } catch (PersistenceException pe) {
        	Logger.getLogger(SubscriptionBean.class).debug("SubscriptionBean.getSpaceParticipantGroupCheckBoxList() threw a PersistenceException: " + pe);
        } catch (GroupException ge) {
            // No groups
        	Logger.getLogger(SubscriptionBean.class).debug("SubscriptionBean.getSpaceParticipantGroupCheckBoxList() threw a GroupException: " + ge);
        }
        return participants.toString();
    }

    

    /**
     * Returns all the external subscribers  as an "in" clause suitable for a select
     * statement.  For example:<code><pre>
     *      'abc@xyz.com', 'xyz@abc.com'</code></pre>
     *
     * @return The String representation for that
     */
    public String getExternalSubscribersInCSV() {
        Iterator itr = this.subscribers.iterator();
        String externalSubscribers = null;
        while (itr.hasNext()) {
            Subscriber subscriber = (Subscriber) itr.next();
            if (subscriber.isExternalSubscriber()) {

                // For the time -being for this release
                Iterator itrAddresses = subscriber.addressCollection.iterator();
                while (itrAddresses.hasNext()) {
                    Address address = (Address) itrAddresses.next();
                    if (externalSubscribers != null) {
                        externalSubscribers = externalSubscribers + address.getAddress() + " ,";
                    } else {
                        externalSubscribers = address.getAddress() + ",";
                    }
                }
            }
        }
        if (externalSubscribers != null) {
            int lastIndex = externalSubscribers.lastIndexOf(',');
            return externalSubscribers.substring(0, lastIndex);
        } else {
            return null; // If nothing return Null
        }
    }
    
    /**
     * Method to get object icon url
     * @param objectId
     * @return image url
     */
    public String getObjectIconUrl(Integer objectId){
      	PnObjectType objectType = ServiceFactory.getInstance().getPnObjectTypeService().getObjectTypeByObjectId(
				objectId);

      	if(objectType == null){
      		return null;      		
      	}
      	
      	if(objectType.getObjectType().equals(ObjectType.TASK)){
    		return SessionManager.getJSPRootURL() + "/images/activitylog/task.gif";
      	} else if(objectType.getObjectType().equals(ObjectType.CONTAINER)){
    		return SessionManager.getJSPRootURL() + "/images/folder.gif";
      	} else if(objectType.getObjectType().equals(ObjectType.DOCUMENT)){
          	try {
          		PnDocBySpaceView pnDocBySpaceView = ServiceFactory.getInstance().getPnDocBySpaceViewService()
						.getPnDocBySpaceView(objectId);
            		return SessionManager.getJSPRootURL() + pnDocBySpaceView.getAppIconUrl();
  
          	} catch (Exception pnetEx) {
            	Logger.getLogger(SubscriptionBean.class).debug("SubscriptionBean.getObjectIconUrl() threw Exception: " + pnetEx.getMessage());
          	}
      	} else if(objectType.getObjectType().equals(ObjectType.PROJECT)){
    		return SessionManager.getJSPRootURL() + "/images/notify/project_icon.png";
      	} else if(objectType.getObjectType().equals(ObjectType.BLOG)){
    		return SessionManager.getJSPRootURL() + "/images/notify/blog_icon.png";
      	} else if(objectType.getObjectType().equals(ObjectType.WIKI)){
    		return SessionManager.getJSPRootURL() + "/images/notify/wiki_icon.png";
      	} else if(objectType.getObjectType().equals(ObjectType.DISCUSSION_GROUP)){
    		return SessionManager.getJSPRootURL() + "/images/notify/discussion_icon.png";
      	} else if(objectType.getObjectType().equals(ObjectType.FORM) || objectType.getObjectType().equals(ObjectType.FORM_DATA)){
    		return SessionManager.getJSPRootURL() + "/images/notify/form_icon.png";
      	} else if(objectType.getObjectType().equals(ObjectType.NEWS)){
    		return SessionManager.getJSPRootURL() + "/images/notify/news_icon.png";
      	}
      	return null;
      }
}
