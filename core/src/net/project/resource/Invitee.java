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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20642 $
|       $Date: 2010-03-30 10:57:51 -0300 (mar, 30 mar 2010) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.DefaultDirectory;
import net.project.base.directory.search.ISearchResult;
import net.project.security.User;
import net.project.space.Space;

import org.apache.commons.lang.StringUtils;

/**
 * An invitee to a space.
 */
public class Invitee implements Serializable{

    //
    // Static members
    //

    /**
     * Makes a collection of invitees from search results.
     * @param searchResults the collection of <code>ISearchResult</code>s
     * @return the collection of <code>Invitee</code>s
     * @throws ClassCastException if any elements are not of type
     * <code>ISearchResult</code>
     */
    public static Collection makeInvitees(Collection searchResults) {

        List inviteeList = new ArrayList();

        // Iterate over all search results creating an Invitee from
        // each one
        for (Iterator it = searchResults.iterator(); it.hasNext();) {
            ISearchResult nextResult = (ISearchResult)it.next();
            inviteeList.add(makeInvitee(nextResult));
        }

        return inviteeList;
    }

    /**
     * Creates an Invitee from a single search result.
     * @param searchResult the directory search result from which
     * to create the invitee
     * @return the Invitee
     */
    public static Invitee makeInvitee(ISearchResult searchResult) {
        Invitee invitee = new Invitee();
        invitee.setFirstName(searchResult.getFirstName());
        invitee.setLastName(searchResult.getLastName());
        invitee.setEmail(searchResult.getEmail());
        return invitee;
    }

    /**
     * Creates an Invitee from an instantiated (and loaded) user object
     * @param user and instantiated user
     * @return the Invitee
     */
    public static Invitee makeInvitee (User user) {
        Invitee invitee = new Invitee();
        invitee.setFirstName(user.getFirstName());
        invitee.setLastName(user.getLastName());
        invitee.setEmail(user.getEmail());
        invitee.setInvitedPerson(user);
        return invitee;
    }
    //
    // Instance members
    //

    /** The invitee's first name. */
    private String firstName = null;

    /** The invitee's last name. */
    private String lastName = null;

    /** The invitee's display name. */
    private String displayName = null;

    /** The invitee's email address. */
    private String email = null;

    /** the Invitee's ID */
    private String inviteeID = null;

    /** Indicates if the invitee has a corresponding person stub. */
    private boolean isInviteeRegistered = false;

    /** Indicates if the invitee is a registered user i.e. has entry in the pn_user table.*/
    private boolean isInviteeRegisteredUser = false;

    /**
     * The display name of the user who invited this user.  Currently available
     * in the new user report only.
     */
    private String invitorDisplayName; 

    /**
     * The date on which the user was originally invited.  Currently available
     * in the new user report only.
     */
    private Date invitedDate;

    /**
     * The date on which the user responded to the invitation.  Currently
     * available in the new user report only.
     */
    private Date responseDate;

    /**
     * Whether or not the user has responded to the invitiation.  Currently
     * available in the new user report only.
     */
    private boolean hasResponded;

    /**
     * The responsibilities of the invitee in the workspace.  Currently,
     * available only in the new user report.
     */
    private String responsibilities;

    /**
     * A person record for the user.  If this is already available, it will
     * prevent an expensive round trip to the database to load the user.  If it
     * isn't, we'd have to do the round trip anyhow.
     */
    private Person invitedPerson;
    
    private String invite="";
    
    private boolean disable;
    
    private String[] roles;
    
    private String title;
    
    private boolean online;
    
    private Space space;
    
    private String chargeCodeId;
    
    /**
     * Sets this invitee's first name.
     * This is used for notification purposes and is displayed in the
     * roster until they are registered.
     * @param firstName the first name of the invitee.
     * @see #getFirstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns this invitee's first name.
     * @return the first name
     * @see #setFirstName
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the invitee's ID.
     * @param inviteeID
     * @see #getID
     */
    public void setID(String inviteeID) {
        this.inviteeID = inviteeID;
    }

    /**
     * Returns the invitee's ID.
     * @return ID
     * @see #setID
     */
    public String getID() {
        return this.inviteeID;
    }

    /**
     * Sets this invitee's last name.
     * This is used for notification purposes and is displayed in the
     * roster until they are registered.
     * @param lastName the last name of the invitee.
     * @see #getLastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns this invitee's last name.
     * @return the last name
     * @see #setLastName
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets this invitee's display name.
     * Optional field; used instead of "Last name + first name"
     * when listing invitation information.
     * @param displayName the display name of the invitee.
     * @see #getDisplayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        setShortDisplayName(displayName);
    }
   
    /**
     * Returns this invitee's display name.
     * @return the display name or the concatenation of first and
     * last name if no displayname has been specified
     * @see #setDisplayName
     */
    public String getDisplayName() {
        String displayName = null;

        if (this.displayName == null) {
            displayName = new StringBuffer(getFirstName()).append(" ").append(getLastName()).toString();

        } else {
            displayName = this.displayName;
        }

        return displayName;
    }
    
    /**
     * Sets this invitee's email.
     * This is used for notification purposes.
     * @param email the email address of the invitee.
     * @see #getEmail
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns this invitee's email.
     * @return the email address
     * @see #setEmail
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the invitee already registered
     * @param isRegistered the flag
     * @see #isInviteeRegistered
     */
    public void setInviteeRegistered(boolean isRegistered) {
        this.isInviteeRegistered = isRegistered;
    }

    /**
     * Returns the invitee already registered.
     * @return the isRegistered the flag, true if the invitee
     * has corresponding person stub.
     * @see #setInviteeRegistered
     */
    public boolean isInviteeRegisteredPerson() {
        return this.isInviteeRegistered;
    }

    /**
     * Returns true if the invitee is a registered user.
     * @return the isRegisteredUser the flag, true if the invitee
     * has entry in the pn_user table
     */
    public boolean isInviteeRegisteredUser() {
        resolveUserIDForInvitee();
        return this.isInviteeRegisteredUser;
    }

    /**
     * Get the display name of the user which invited this user to the space.
     * This value is not available universally.  It is only available in the
     * new user report.
     *
     * @return a <code>String</code> value containing the display name of the
     * user that invited this user to the space.
     */
    public String getInvitorDisplayName() {
        return invitorDisplayName;
    }

    /**
     * Sets the display name of the user that invited this user to the space.
     * This value is not available universally.  It is only available in the
     * new user report.
     *
     * @param invitorDisplayName a <code>String</code> value containing the name
     * of the user that invited this user to the space.
     */
    public void setInvitorDisplayName(String invitorDisplayName) {
        this.invitorDisplayName = invitorDisplayName;
    }

    /**
     * Get the date on which this user was invited to the workspace.  This value
     * is not available universally.  It is only available in the new user
     * report.
     *
     * @return a <code>Date</code> value indicating the date on which this user
     * was originally invited to the workspace.
     */
    public Date getInvitedDate() {
        return invitedDate;
    }

    /**
     * Set the date on which this user was invited to the workspace.
     *
     * @param invitedDate a <code>Date</code> value indicating the date on which
     * this user was originally invited to the workspace.
     */
    public void setInvitedDate(Date invitedDate) {
        this.invitedDate = invitedDate;
    }

    /**
     * Get the date on which this user responded to the invitation request.
     * This value is currently only available in the new user report.
     *
     * @return a <code>Date</code> value indicating the date on which the user
     * responded to the invitation request.
     */
    public Date getResponseDate() {
        return responseDate;
    }

    /**
     * Set the date on which the user responded to the invitation request.
     *
     * @param responseDate a <code>Date</code> value indicating the date on
     * which the user responded to the invitation request.
     */
    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    /**
     * Has the user responded yet to the invitation request.  This value is only
     * available currently while doing the new user report.
     *
     * @return a <code>boolean</code> value indicating whether the user has
     * responded to the invitation request.
     */
    public boolean hasResponded() {
        return hasResponded;
    }

    /**
     * Indicate whether the user has responded to the invitation request.
     *
     * @param hasResponded a <code>boolean</code> value indicating whether the
     * user has responded to the invitation request.
     */
    public void setResponded(boolean hasResponded) {
        this.hasResponded = hasResponded;
    }

    /**
     * Get the responsibilities for the user in the workspace.  This value is
     * only available currently while doing the new user report.
     *
     * @return a <code>String</code> value indicating the responsibilities of
     * the user.
     */
    public String getResponsibilities() {
        return responsibilities;
    }

    /**
     * Set the responsibilities for the user in the workspace.
     *
     * @param responsibilities a <code>String</code> value containing the
     * responsibilities of the user.
     */
    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    /**
     * Get a <code>Person</code> object associated with the invitation.  This
     * will prevent loading the person again if we don't need to.
     *
     * @return a <code>Person</code> object that is associated with the person
     * being invited.
     */
    public Person getInvitedPerson() {
        return invitedPerson;
    }

    /**
     * Set a <code>Person</code> object which belongs to the user being invited.
     *
     * @param invitedPerson a <code>Person</code> who is associated with the
     * other user information provided and that is being invited.
     */
    public void setInvitedPerson(Person invitedPerson) {
        this.invitedPerson = invitedPerson;
    }

    /**
     * Two <code>Invitee</code>s are equal if they have the same
     * email addresss.  This is to avoid inviting the same person twice.
     * @return true if the specified object is an Invitee and
     * is equal to this one; false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invitee)) {
            return false;
        }

        final Invitee invitee = (Invitee) o;

        if (email != null ? !email.equals(invitee.email) : invitee.email != null) {
            return false;
        }

        return true;
    }

    /**
     * Hashcode based on email address hashCode, since that is
     * basis of equality check.
     * @return the hashCode
     */
    public int hashCode() {
        return (email != null ? email.hashCode() : 0);
    }

    /**
     * Returns the XMLDocument for this invitee.
     * <p>
     * Of the form: <code><pre>
     * &lt;Invitee&gt;
     *     &lt;FirstName&gt;...&lt;/FirstName&gt;
     *     &lt;LastName&gt;...&lt;/LastName&gt;
     *     &lt;DisplayName&gt;...&lt;/DisplayName&gt;
     *     &lt;EmailName&gt;...&lt;/EmailName&gt;
     * &lt;/Invitee&gt;
     * </pre></code>
     * </p>
     *
     * @return the XMLDocument
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {

        net.project.xml.document.XMLDocument xml = new net.project.xml.document.XMLDocument();

        try {

            xml.startElement("Invitee");
            xml.addElement("ID", getID());
            xml.addElement("FirstName", getFirstName());
            xml.addElement("LastName", getLastName());
            xml.addElement("DisplayName", getDisplayName());
            xml.addElement("Email", getEmail());
            xml.addElement("InvitorDisplayName", getInvitorDisplayName());
            xml.addElement("InvitedDate", getInvitedDate());
            xml.addElement("ResponseDate", getResponseDate());
            xml.addElement("HasResponded", String.valueOf(hasResponded()));
            xml.addElement("Responsibilities", getResponsibilities());
            xml.endElement();

        } catch (net.project.xml.document.XMLDocumentException e) {
            // Return empty xml document
        }

        return xml;
    }

    /**
     * Try to resolves the Person ID for on the basis of Invitee's email address.
     * If the ID exists i.e. there is a corresponding person stub then marks
     * the invitee as "Registered "
     */
    public void resolvePersonIDForInvitee() {

        this.inviteeID = DefaultDirectory.getPersonIDForEmail(this.email);

        if (this.inviteeID != null && !this.inviteeID.trim().equals("")) {
            this.isInviteeRegistered = true;
        } else {
            this.isInviteeRegistered = false;
        }

    }

    /**
     * Try to resolves the User ID for on the basis of Invitee's email address.
     * If the ID exists in the pn_user table then marks
     * the invitee as "Registered User"
     */
    public void resolveUserIDForInvitee() {

        this.inviteeID = DefaultDirectory.getUserIDForEmail(this.email);

        if (this.inviteeID != null && !this.inviteeID.trim().equals("")) {
            this.isInviteeRegisteredUser = true;
        } else {
            this.isInviteeRegisteredUser = false;
        }

    }

	/**
	 * @return the invite
	 */
	public String getInvite() {
		return invite;
	}

	/**
	 * @param invite the invite to set
	 */
	public void setInvite(String invite) {
		this.invite = invite;
	}

	/**
	 * @return the disable
	 */
	public boolean isDisable() {
		return disable;
	}
	
	/**
	 * @param disable the disable to set
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
	/**
	 * @return invitee first and last name.
	 */
	public String getSearchedDisplayName() {
		return getFirstName()+" "+getLastName();
	}

	/**
	 * @return the roles
	 */
	public String[] getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	/**
	 * @param space the space to set
	 */
	public void setSpace(Space space) {
		this.space = space;
	}
	/**
	 * 
	 * @param displayName
	 */
	public void setShortDisplayName(String displayName){
		this.displayName = displayName;
	}
	
	/**
	 * 
	 * @return short display 
	 */
	 public String getShortDisplayName(){
		if (StringUtils.isNotEmpty(displayName) && displayName.length() > 30) {
    		return this.displayName = displayName.substring(0, 30) + "..";
		} else {
			return displayName = this.displayName;
		}
	 }

	/**
	 * @return the chargeCodeId
	 */
	public String getChargeCodeId() {
		return chargeCodeId;
	}

	/**
	 * @param chargeCodeId the chargeCodeId to set
	 */
	public void setChargeCodeId(String chargeCodeId) {
		this.chargeCodeId = chargeCodeId;
	}
}
