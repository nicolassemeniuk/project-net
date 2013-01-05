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
|   $RCSfile$
|   $Revision: 19103 $
|   $Date: 2009-04-19 06:37:34 -0300 (dom, 19 abr 2009) $
|   $Author: vivana $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.xml.XMLUtils;

/**
 * An entry on the form menu. Provides basic properties of a form type.
 */
public class FormMenuEntry implements IXMLPersistence, IHTMLOption {
    /**
     * ID of the Form
     */
    protected String id = null;
    /**
     * The type of the Form
     */
    protected String type = null;
    /**
     * The Name of the Form
     */
    protected String name = null;
    /**
     * The description of the Form
     */
    protected String description = null;
    /**
     * The abbreviation for the form 
     */
    protected String abbreviation = null;
    /**
     * The record status in the database
     */
    protected String recordStatus = null;
    /**
     * The name of the Master Table name
     */
    protected String masterDataTableName = null;
    /**
     * The no. of records that are active in the table
     */
    protected String activeCount = null;
    /**
     * The ID for the Owner Space
     */
    protected String ownerSpaceID = null;
    
    /**
     * The id of the Current Form Space
     */
    protected String currentSpaceID = null;
    /**
     * The space to which the form belongs
     */
    protected Space space = null;
    /**
     * External support status of the form
     */    
    protected String supportExternalAccess = null;
    /**
     * Exteranal ID of the form if exist
     */
    protected String externalId = null;
    
    /**
     * has work flow for the form
     */
    protected String hasWorkflows = null;

    
    protected boolean owner;
    
    protected boolean visible;
    
    // All lists for form represented by form menu entry

    private ArrayList formLists = new ArrayList();
    /**
     * The collection of the Form Data item belonging to the Form
     */
    private FormDataList formDataList = new FormDataList();
    /**
     * The filter constraint for the form 
     */
    private List<FormFilterConstraint> formFilterConstraintList = new ArrayList<FormFilterConstraint>() ;

    /**
     * Returns the ID for the Form
     * 
     * @return <code>String</code> the ID for the Form
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the type for the Form
     * 
     * @return The type for the form
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns the Form Name
     * 
     * @return the Form Name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * The description for the Form
     * 
     * @return Form Description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the owner space ID 
     * 
     * @return the owner space ID
     */
    public String getOwnerSpaceID() {
        return this.ownerSpaceID;
    }    

    
    /**
	 * @return the supportExternalAccess
	 */
	public String getSupportExternalAccess() {
		return supportExternalAccess;
	}

	/**
	 * @param supportExternalAccess the supportExternalAccess to set
	 */
	public void setSupportExternalAccess(String supportExternalAccess) {
		this.supportExternalAccess = supportExternalAccess;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the externalURL
	 */
	public String getExternalURL() {
		return SessionManager.getAppURL()+ "/eaf?extid=" + externalId+"&extSid="+currentSpaceID;
	}	
	
	/**
     * Returns the publication status 
     * 
     * @return 
     */
    public String getPublicationStatus() {
    	if (isOwner()){
	        if (this.recordStatus.equals("A"))
	            return PropertyProvider.get("prm.form.designer.status.active.name");
	        else if (this.recordStatus.equals("P"))
	            return PropertyProvider.get("prm.form.designer.status.pending.name");
	        else
	            return PropertyProvider.get("prm.form.designer.status.unknown.name");
    	}else{
    		if (isVisible()){
    			return PropertyProvider.get("prm.form.designer.status.active.name");
    		}else{
    			return PropertyProvider.get("prm.form.designer.status.pending.name");
    		}
    	}
    }

   /**
    * Specifies filter for Form Name
    * @param formFilterConstraint FormNameFilter
    */
    public void addFilterConstraint(FormFilterConstraint formFilterConstraint ) {
        this.formFilterConstraintList.add(formFilterConstraint) ;
    }

     /**
      * Return the lists for this form menu entry
      * @return collection of formLists
      */
    public ArrayList getFormLists() {
        return this.formLists;
    }

     /**
      * Add a form list to this form menu entry's form lists collection
      * @param formList the FormList to add
      */
    public void addFormList(FormList formList) {
        this.formLists.add(formList);
    }

    /**
     * Converts the FormMenu to XML representation without the xml header tag
     * This method returns the FormMenu as XML text without the xml header tag..
     * 
     * @return XML representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        xml.append("<FormMenuEntry>\n");
        xml.append(getXMLAttributes());
        xml.append("</FormMenuEntry>\n");
        return xml.toString();
    }

    /**
     * Returns this FormMenu's attributes as XML.
     * This does not include the <code>&lt;FormMenuEntry&gt;...&lt;/FormMenuEntry&gt;</code>
     * tags.
     * @return this FormMenu's attributes as XML
     */
    protected String getXMLAttributes() {
        Iterator it = null;
        FormList formList = null;
        StringBuffer xml = new StringBuffer();

        xml.append("<id>" + XMLUtils.escape(id) + "</id>\n");
        xml.append("<type>" + XMLUtils.escape(type) + "</type>\n");
        xml.append("<name>" + XMLUtils.escape(name) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(description) + "</description>\n");
        xml.append("<abbreviation>" + XMLUtils.escape(abbreviation) + "</abbreviation>\n");
        xml.append("<recordStatus>" + XMLUtils.escape(recordStatus) + "</recordStatus>\n");
        xml.append("<publicationStatus>" + getPublicationStatus() + "</publicationStatus>\n");
        xml.append("<activeCount>" + XMLUtils.escape(activeCount) + "</activeCount>\n");
        xml.append("<has_workflows>" + XMLUtils.escape(hasWorkflows) + "</has_workflows>\n");
        xml.append("<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>\n");
        xml.append("<support_external_access>" + XMLUtils.escape(supportExternalAccess) + "</support_external_access>\n");
        xml.append("<external_url>" + XMLUtils.escape(getExternalURL()) + "</external_url>\n");
        xml.append("<owner>" + owner + "</owner>\n");
        xml.append("<ownerId>" + XMLUtils.escape(ownerSpaceID) + "</ownerId>\n");
        
        // Now add in the formList XML if present
        if (formLists.size() > 0) {
            it = formLists.iterator();
            while (it.hasNext()) {
                formList = (FormList) it.next();
                xml.append("<FormList>\n");
                xml.append("<id>" + XMLUtils.escape(formList.getID()) + "</id>\n");
                xml.append("<is_default>" + XMLUtils.escape("" + formList.m_is_default) + "</is_default>\n");
                xml.append("<name>" + XMLUtils.escape(formList.m_list_name) + "</name>\n");
                xml.append("<description>" + XMLUtils.escape(formList.m_list_description) + "</description>\n");
                xml.append("<field_count>" + XMLUtils.escape("" + formList.m_field_cnt) + "</field_count>\n");
                xml.append("</FormList>\n");
            }
        }
            
        if (this.formDataList.size() > 0) {
            xml.append(formDataList.getXMLBody());
        }

        if(space !=  null)
            xml.append(space.getXMLProperties());

        return xml.toString();
    }

    /**
     * Converts the FormMenu to XML representation.
     * This method returns the FormMenu as XML text.
     * 
     * @return XML representation of the FormMenu
     */
    public String getXML(){
        return( IXMLPersistence.XML_VERSION + getXMLBody() );
    }

    /**
     * Loads the Form Data item for the Form
     * 
     * @throws PersistenceException if anything goes wrong
     */
    public void loadFormDataList() throws PersistenceException  {

        this.formDataList.setMasterTableName(this.masterDataTableName);

        if(this.formFilterConstraintList == null || this.formFilterConstraintList.size() == 0) 
            this.formDataList.load();
        else {          	
            //this.formDataList.loadConstrainted(formFilterConstraintList.get(0));
        	for(FormFilterConstraint formFilterConstraint : formFilterConstraintList){
        		this.formDataList.addFilterConstraint(formFilterConstraint);
        	}
        	this.formDataList.load();
        }

    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return getName();
    }

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getCurrentSpaceID() {
		return currentSpaceID;
	}

	public void setCurrentSpaceID(String currentSpaceID) {
		this.currentSpaceID = currentSpaceID;
	}

    
    
}



