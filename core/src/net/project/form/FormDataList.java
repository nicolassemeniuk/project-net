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
|     $RCSfile$
|    $Revision: 19989 $
|        $Date: 2009-09-17 12:46:21 -0300 (jue, 17 sep 2009) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.resource.Roster;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.xml.XMLFormatter;

/**
 * FormDataList represents a collection of {@link FormData} objects.
 * It provides functionality for manipulating those FormData objects.
 * @since emu
 * @author Tim
 */
public class FormDataList 
        extends java.util.ArrayList 
        implements java.io.Serializable, net.project.persistence.IXMLPersistence {

    private String masterTableName = null ;
    private List<FormFilterConstraint> formFilterConstraintList =  new ArrayList<FormFilterConstraint>();
    private XMLFormatter xml = new XMLFormatter();

    /**
     * Creates an empty list
     */
    public FormDataList() {
        super();

    }

    /**
     * Creates a list with specified initial capacity
     * @see java.util.ArrayList#ArrayList(int)
     */
    public FormDataList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Adds a FormData object to this list.
     * @param o the FormData object
     * @throws IllegalArgumentException if specified object is not of type {@link FormData}
     * @see java.util.ArrayList#add(Object)
     */
    public boolean add(Object o) {
        if ( !(o instanceof FormData) ) {
            throw new IllegalArgumentException("Attempt to add non-FormData object to FormDataList failed");
        }
        return super.add(o);
    }

    /**
     * Adds a FormData object to this list at specified index.
     * @param element the FormData object
     * @throws IllegalArgumentException if specified object is not of type {@link FormData}
     * @see java.util.ArrayList#add(int, Object)
     */
    public void add(int index, Object element) {
        if ( !(element instanceof FormData) ) {
            throw new IllegalArgumentException("Attempt to add non-FormData object to FormDataList failed");
        }
        super.add(index, element);
    }

    /**
     * Sets the Master Table Name for the FormDataList
     * 
     * @param masterTableName
     *               the name of the MasterTable in which the Form Data has been stored
     */
    public void setMasterTableName( String masterTableName ) {
        this.masterTableName = masterTableName ;
    }

    /**
     * Sets filter Constraints for the List
     * 
     * @param formFilterConstraint
     *               <code>FormFilterConstraint</code> istance
     */
    public void addFilterConstraint( FormFilterConstraint formFilterConstraint) {
        this.formFilterConstraintList.add(formFilterConstraint);
    }

    /**
      * Converts the object to XML representation.
      * This method returns the object as XML text.
      * @return XML representation of the object including XML version tag
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
            getXMLBody();
    }

    /**
     * Converts the object to XML node representation without the xml version tag.
     * This method returns the object as XML text.  For example:
     * <pre>
     * &lt;FormDataList&gt;
     *     &lt;FormData&gt; ... &lt;/FormData&gt;
     *     &lt;FormData&gt; ... &lt;/FormData&gt;
     * &lt;/FormDataList&gt;</pre>
     * @return XML node representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        FormData formData = null;

        xml.append("<FormDataList>");

        Iterator it = this.iterator();
        while (it.hasNext()) {
            formData = (FormData) it.next();
            xml.append(formData.getXMLBody());
        }
       
        xml.append("</FormDataList>");

        return xml.toString();
    }

    /**
     * Loads the constrainted or filtered meta data for the <code>FormData</code> ( rows of the Form)
     * from the database
     * 
     * @param formFilterConstraint
     *               <code>FormFilterConstraint</code> instance
     * @exception PersistenceException
     *                   is thrown if anything goes wrong
     */
/*    public void loadConstrainted(FormFilterConstraint formFilterConstraint) 
        throws PersistenceException {

        this.formFilterConstraint = formFilterConstraint;
        load();
    }*/
    
    /**
     * Loads the meta data for the <code>FormData</code> ( rows of the Form)
     * from the database
     * 
     * @exception PersistenceException is thrown if anything goes wrong 
     */
    public void load() throws PersistenceException {

        DBBean db = new DBBean();     
        //The call to SessionManager is a bit hackish.  I would like to get
        //the current roster.  Normally I would have passed this in, but I
        //am trying to be gentle because this is part of a patch.
        DisplayNameCache cache = new DisplayNameCache(SessionManager.getUser().getCurrentSpace().getRoster());

        try {
            if(this.masterTableName == null ) 
                throw new PersistenceException(" FormDataList cannot be loaded");

            StringBuffer query = new StringBuffer();
    
            query.append("select mt.object_id , mt.version_id ,mt.previous_version_id , mt.is_current , ");
            query.append("  mt.seq_num , mt.create_person_id, mt.creator_email, mt.modify_person_id , mt.date_modified , ");
            query.append(" mt.date_created , mt.crc , pon.name as workspace_name from pn_class_instance pc , pn_object_name pon , " );
            query.append(this.masterTableName+" mt ");
            query.append(" where ");
            query.append("pc.space_id = pon.object_id and ");
            query.append("mt.object_id = pc.class_instance_id and pc.record_status ='A' ");
    
            for (FormFilterConstraint formFilterConstraint : formFilterConstraintList ){
	            if (formFilterConstraint != null && formFilterConstraint.getParametersMap().size() > 0 ) {
	    
	                Iterator itr = formFilterConstraint.getParametersMap().entrySet().iterator();
	
			// This makes an important assumption that in case of forms with selection list
			// the multi_data_seq always starts from index of zero 
	
	                query.append(" and mt.is_current = 1 and mt.multi_data_seq = 0 ");
	
	                for (int i = 0 ; i < formFilterConstraint.getParametersMap().size() ; i++ ) {
	
	                    Map.Entry me = (Map.Entry) itr.next();
	                    query.append(" and ");
	                    //query.append("mt."+me.getKey());
	                    query.append(me.getKey());
	                    query.append(formFilterConstraint.getOperator());
	                    query.append(me.getValue());
	                }
	
	            }
            }
            
           db.setQuery(query.toString());
           db.executeQuery();

           while (db.result.next()) {
               
               FormData formData = new FormData();
               formData.m_data_object_id = db.result.getString("object_id");
               formData.m_version_id = db.result.getString("version_id");
               formData.m_previous_version_id = db.result.getString("previous_version_id");
               formData.m_sequence_num = Conversion.toInt(db.result.getString("seq_num"));
               formData.m_create_person_id = db.result.getString("create_person_id");
               formData.m_creator_email = db.result.getString("creator_email");
               formData.m_create_person_display_name = cache.getDisplayName(formData.m_create_person_id);
               formData.m_modify_person_id = db.result.getString("modify_person_id");
               formData.m_modify_person_display_name = cache.getDisplayName(formData.m_modify_person_id);
               formData.m_date_created = db.result.getTimestamp("date_created");
               formData.m_date_modified = db.result.getTimestamp("date_modified");
               formData.m_crc = db.result.getTimestamp("crc");
               formData.m_form_data_space_name = db.result.getString("workspace_name");
               this.add(formData);
           }

       } catch (SQLException sqle ){
           throw new PersistenceException(sqle.getMessage(), sqle);
       } finally{
           db.release();
       }

   }

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet (String styleSheetFileName) {
        xml.setStylesheet(styleSheetFileName);
    }

    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation () {
        return( xml.getPresentation( getXML()) );
    } 

}

class DisplayNameCache {
    Roster roster;
    HashMap displayNameCache = new HashMap();

    public DisplayNameCache(Roster r) { roster = r; }

    String getDisplayName(String id) {
        String displayName = (String)displayNameCache.get(id);
        if (displayName == null) {
            displayName = roster.getAnyPerson(id).getDisplayName();
        }
        return displayName;
    }
}
