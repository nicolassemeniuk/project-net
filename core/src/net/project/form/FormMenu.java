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

 package net.project.form;


import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.project.base.ExceptionList;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.Conversion;
import net.project.util.StringUtils;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * A menu of available form types for one or more spaces.
 * The menu may include forms which are owned by other spaces but seen
 * by the current space.
 */
public class FormMenu implements IXMLPersistence {
    /** the space context for the form menu */
    private Space space = null;

    /** the user context for the form menu */
    private net.project.security.User user = null;

    /** determines whether forms with record_status pending (P) are displayed */
    private boolean m_showPending = false;

    /** determines whether system forms are displayed (e.g. Contacts form) */
    private boolean m_showSystemClasses = false;

    /** List of FormMenuEntry on this menu */
    public ArrayList m_formTypes = null;

    /** db access bean  */
    private final DBBean db;
    private boolean m_isLoaded = false;

    /** Contains XML formatting information and utilities specific to this object **/
    private final XMLFormatter m_formatter;

    /**
     * Indicates whether to load forms owned by space only.  The alternative
     * is to load all forms that the current space has access to, some of which
     * are owned by other spaces.
     */
    private boolean isLoadOwnedFormsOnly = false;
    
    
    /**
     * Indicates whether to load forms visible by space only(some of which are owned by other spaces).
     * The alternative  is to load all forms that the current space has access to some of which is not currently
     * visible in space (this case is only possible if space does not own the form)
     */
    private boolean loadVisibleFormsOnly = true;    
    
    private final HashMap spaceMap = new HashMap();

    // Right now  , will be use to filter on the basis of form name
    private String formNameFilter = null;

    private String spaceTypeFilter = null;

    private boolean isClassTypeNameRequired = true;

    private boolean isFormDataListRequired = false;

    private FormFilterConstraint formFilterConstraint = null;

    /**
     * Activates all forms (including system forms) in the specified space.
     * @param spaceID the id of the space
     * @param user the user activating the forms
     * @throws PersistenceException if there is a problem loading or activating
     * the forms in the space
     * @throws ExceptionList when one or more errors occured while trying to
     * activate some forms.
     */
    public static void activeAllForSpace(String spaceID, net.project.security.User user)
        throws PersistenceException, ExceptionList {
    	
    	activeAllForSpace(spaceID, user, true);

    }

    /**
     * Activates all active (no pending forms will be activated) (including system forms) in the specified space.
     * @param spaceID the id of the space
     * @param user the user activating the forms
     * @throws PersistenceException if there is a problem loading or activating
     * the forms in the space
     * @throws ExceptionList when one or more errors occured while trying to
     * activate some forms.
     */
    public static void activateAllActiveForSpace(String spaceID, net.project.security.User user)
        throws PersistenceException, ExceptionList {
    	
    	activeAllForSpace(spaceID, user, false);

    }
    
    /**
     * Activates all forms (including system forms) in the specified space.
     * @param spaceID the id of the space
     * @param user the user activating the forms
     * @param activatePending do you want to activate existing pending forms?
     * @throws PersistenceException if there is a problem loading or activating
     * the forms in the space
     * @throws ExceptionList when one or more errors occured while trying to
     * activate some forms.
     */
    public static void activeAllForSpace(String spaceID, net.project.security.User user, boolean activatePending)
        throws PersistenceException, ExceptionList {

        try {
            FormMenu formMenu = new FormMenu();
            formMenu.setSpaceID(spaceID);
            formMenu.setUser(user);
            formMenu.setDisplayPending(activatePending);
            formMenu.setDisplaySystemForms(true);
            formMenu.load();
            formMenu.activateAll();

        } catch (FormException fe) {
            // No forms defined.  Quietly do nothing
        }

    }    
    
    /**
     * Creates a new, empty FormMenu
     */
    public FormMenu() {
        db = new DBBean();
        m_formatter = new XMLFormatter();
        m_formTypes = new ArrayList();
    }

    /**
     * Sets the Space context for the form menu to the specified space.
     * @param space the current space context
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Sets the Space context for the form menu to the space for the specified
     * space ID.
     * <b>Note:</b> Currently this defaults to constructing a ProjectSpace
     * @param spaceID the id of the current space context
     */
    public void setSpaceID(String spaceID) {
        setSpace(new ProjectSpace(spaceID));
    }

    /**
     * Set the User context for the form menu.
     * @param user the current User context.
     */
    public void setUser(net.project.security.User user) {
        this.user = user;
    }

    /**
     * Sets whether pending forms are displayed.
     * @param value true means forms in Pending status will be included in
     * this FormMenu; false means only forms in Active status will be included.
     */
    public void setDisplayPending(boolean value) {
        m_showPending = value;
    }

    /**
     * Indicates whether pending forms are displayed.
     * @return true if pending forms are included in this FormMenu;
     * false otherwise
     */
    public boolean getDisplayPending() {
        return m_showPending;
    }

    /**
     * Sets whether system forms are displayed.
     * A system form is a form which may be accessed through a tool other
     * than the Forms tool, for example the "Contacts" form.
     * @param value true means system forms will be included in this FormMenu;
     * false means only non-system forms will be included.
     */
    public void setDisplaySystemForms(boolean value) {
        m_showSystemClasses = value;
    }

    /**
     * Indicates whether system forms are displayed.
     * @return true if system forms are included in this FormMenu;
     * false otherwise
     */
    public boolean getDisplaySystemForms() {
        return m_showSystemClasses;
    }

    /**
     * Specifies whether to load forms owned by space only.
     * @param isLoadOwnedFormsOnly true means only those forms owned
     * by the current space will be loaded; false means all forms that
     * the current space can see will be loaded
     */
    public void setLoadOwnedFormsOnly(boolean isLoadOwnedFormsOnly) {
        this.isLoadOwnedFormsOnly = isLoadOwnedFormsOnly;
    }

    /**
     * Specifies filter for Space Type
     * @param spaceTypeFilter a <code>String</code> value containing the
     * SpaceTypeFilter
     */
    public void setSpaceTypeFilter(String spaceTypeFilter) {
        this.spaceTypeFilter = spaceTypeFilter;
    }

    /**
     * Specifies filter for Form Name
     *
     * @param formNameFilter a <code>String</code>
     */
    public void setFormNameFilter(String formNameFilter) {
        this.formNameFilter = formNameFilter;
    }

    /**
     * Specifies filter for Form Name
     * @param formFilterConstraint a <code>String</code> value
     */
    public void setFilterConstraint(FormFilterConstraint formFilterConstraint) {
        this.formFilterConstraint = formFilterConstraint;
    }

    public void setFormDataListRequired(boolean isRequired) {
        this.isFormDataListRequired = isRequired;
    }

    /**
     * Indicates whether only forms owned by the current space context are
     * included in this FormMenu.
     * @return true if only owned forms are included; false if all forms
     * that the current space context has access to are included
     */
    private boolean isLoadOwnedFormsOnly() {
        return this.isLoadOwnedFormsOnly;
    }


    public boolean isLoadVisibleFormsOnly() {
		return loadVisibleFormsOnly;
	}

	public void setLoadVisibleFormsOnly(boolean loadVisibleFormsOnly) {
		this.loadVisibleFormsOnly = loadVisibleFormsOnly;
	}

	/**
     * Return the entries of this FormMenu.
     * Each entry is of type {@link FormMenuEntry}
     * @return the collection of entries
     */
    public ArrayList getEntries() {
        return m_formTypes;
    }

    /**
     * Returns the number of entries in this form menu.
     * @return the number of entries or 0 if this form menu is not loaded
     */
    public int size() {

        if (m_formTypes != null)
            return m_formTypes.size();
        else
            return 0;
    }

    /**
     * Clear the items in this formMenu.
     */
    public void clear() {

        m_formTypes.clear();
        m_isLoaded = false;
        this.spaceMap.clear();
    }

    /**
     * Get an option list for an HTML select tag containing all forms in this menu.
     * @deprecated as of Version 7.4.  Please use
     * <code>HTMLOptionList.makeOptionList(FormMenu.getEntries())</code> instead.
     * @return the HTML option list
     */
    public String getFormsOptionList() {

        StringBuffer sb = new StringBuffer();
        FormMenuEntry menuEntry;

        if (m_formTypes == null)
            return "";

        for (int row = 0; row < m_formTypes.size(); row++) {

            menuEntry = (FormMenuEntry)m_formTypes.get(row);
            sb.append("<option value=\"" + menuEntry.id + "\">");
            sb.append(menuEntry.name + "</option>\n");

        }
        return sb.toString();
    }

    /**
     * Converts the FormMenu to XML representation without the xml header tag
     * This method returns the FormMenu as XML text without the xml header tag..
     * @return XML representation
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();

        xml.append("<FormMenu>\n");

        if (space != null) {
            xml.append("<space_id>" + this.space.getID() + "</space_id>\n");
            xml.append("<jsp_root_url>" + XMLUtils.escape(SessionManager.getJSPRootURL()) + "</jsp_root_url>\n");
        }

        if ((m_formTypes != null) && (m_formTypes.size() > 0)) {
            for (Iterator it = m_formTypes.iterator(); it.hasNext();) {
                FormMenuEntry fme = (FormMenuEntry)it.next();
                xml.append(fme.getXMLBody());
            }
        }

        xml.append("</FormMenu>\n");

        return xml.toString();
    }

    /**
     * Converts the FormMenu to XML representation.
     * This method returns the FormMenu as XML text.
     * @return XML representation of the FormMenu
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Indicates whether this form menu has been loaded from persistence.
     * @return true if this form menu is loaded; false otherwise
     */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    /**
     * Load entries in the form menu.
     * @throws FormException if no forms have been defined for this space
     * @throws PersistenceException if there is a problem loading the form menu
     */
    public void load() throws FormException, PersistenceException {

        String query = null;

        // clear the old data from the formMenu.
        clear();

        if ((this.space == null) || (this.space.getID() == null))
            throw new NullPointerException("Space or Space.m_space_id is null");

        // Construct the query for loading forms
        query = "select " +
        	"	c.class_id, " +
        	"	ct.class_type_name, " +
        	"	c.class_name ," +
        	"	c.class_desc, " +
            "	c.class_abbreviation, " +
            "	c.master_table_name, " +
            //"	ci.active_count as active_count, " +
            "	aa.active_count  as  active_count, " +
            "	c.record_status, " +
            "	c.owner_space_id, " +
            " 	c.supports_external_access, " +
            "	c.external_class_id, " +
            "	(select  " +
            "		count(envelope_id) " +
            " 	from " +
            "		pn_envelope_has_object " +
            "	where " +
            "		object_id = c.class_id ) as workflow," +
            " 	shc.is_owner, " +
            "	shc.visible, " +
            "	sfv.child_ids as child_ids, " +
            "	c.shared "+
            "from " +
            "	pn_space_has_class shc, " +
            "	pn_class_type ct, " +
            "	pn_class c , " +
            "	pn_class_inst_active_cnt_view ci, " +
            "	pn_shared_forms_visiblity sfv, " +
            "   (select " +
            "		decode(ac.active_count, null, 0, ac.active_count) as active_count, " +
            "		c.class_id as  class_id " +
            "	from " +
            "		pn_class c, " + 
            "		(select " +
            "			ci.class_id, " + 
            "			count(*) as active_count, " +
            "		 	ci.space_id " +
            "		from " +
            "			pn_class_instance ci " +
            "		where " +
            "			ci.record_status = 'A' " +
            "		group by " +
            "			ci.class_id,ci.space_id) ac " + 
            "	where " +
            "		ac.class_id(+) = c.class_id and " +
            "		ac.space_id = " + this.space.getID()  + ")  aa " +
            "where " +
            "	shc.space_id =" + this.space.getID() + "  ";

        // Modify the query to specify loading forms where current space owns
        // the form if boolean is set
/*        if (isLoadOwnedFormsOnly()) {
            query += "and shc.is_owner = " + Conversion.booleanToInt(true) + " ";
        }*/
        if (isLoadVisibleFormsOnly()) {
            query += "and shc.visible = " + Conversion.booleanToInt(true) + " ";
        }
        
        
        query += " 	and c.class_id = ci.class_id " +
        		"	and c.class_id = shc.class_id " +
        		"	and ct.class_type_id = c.class_type_id " +
        		"	and ct.class_type_name = 'form' " +
        		"	and shc.space_id = sfv.space_id(+) " +
        		"	and shc.class_id = sfv.class_id(+) " +
        		"	and c.class_id = aa.class_id(+) ";

        if (m_showPending)
            query += " and c.record_status in ('A', 'P') ";
        else
            query += " and c.record_status = 'A' ";

        if (!m_showSystemClasses)
            query += " and c.is_system_class=0 ";

        if (formNameFilter != null)
            query += " and UPPER(c.class_name) like UPPER('" + formNameFilter + "')";

        query += " order by c.class_name asc";
      
        load(query);

    }

    /**
     * Load the lists for each FormMenuEntry.  Note - the lists simply
     * consist of the name and description - they are lightweight.
     * @throws PersistenceException if there is a problem loading the lists
     */
    public void loadLists() throws PersistenceException {

        HashMap formClasses = null;
        Iterator it = null;
        FormList list = null;
        FormMenuEntry entry = null;
        String query;
        String currentClassID = null;
        String classID = null;

        // First build hash of menu entry IDs to menu entries so we can retrieve
        // by classID later
        formClasses = new HashMap();
        it = m_formTypes.iterator();
        while (it.hasNext()) {
            entry = (FormMenuEntry)it.next();
            formClasses.put(entry.getID(), entry);
        }


        // Selects lists for each classID in space
        query = "select shcl.class_id, cl.list_id, cl.list_name, cl.list_desc, cl.field_cnt, shcl.is_default, cl.crc " +
            "from pn_space_has_class_list shcl, pn_class c, pn_class_type ct, pn_class_list cl " +
            "where shcl.space_id = " + this.space.getID() + " " +
            "and c.class_id = shcl.class_id " +
            "and c.record_status = 'A' " +
            "and ct.class_type_id = c.class_type_id " +
            "and ct.class_type_name = 'form' " +
            "and cl.class_id = c.class_id " +
            "and cl.list_id = shcl.list_id " +
            "and cl.record_status='A' " +
            "order by shcl.class_id asc, cl.list_id asc ";

        try {
            db.setQuery(query);
            db.executeQuery();

            currentClassID = "";

            while (db.result.next()) {
                classID = db.result.getString("class_id");
                // Grab entry for class id if this is a new one
                if (!classID.equals(currentClassID)) {
                    entry = (FormMenuEntry)formClasses.get(classID);
                    currentClassID = classID;
                }

                list = new FormList();
                list.setID(db.result.getString("list_id"));
                list.m_list_name = db.result.getString("list_name");
                list.m_list_description = db.result.getString("list_desc");
                list.m_field_cnt = db.result.getInt("field_cnt");
                list.m_is_default = Conversion.toBool(db.result.getString("is_default"));
                list.m_crc = db.result.getTimestamp("crc");

                entry.addFormList(list);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FormMenu.class).error("FormMenu.loadLists failed " + sqle);
            throw new PersistenceException("Failed to load form lists for form menu", sqle);

        } finally {
            db.release();
        }
    }


    /**
     * Loads this FormMenu with menus available to the current space that are
     * also accessible by the specified space.
     * Note that all forms available to the current space context are loaded,
     * not just those that are owned by it.   Those entries which are also
     * accessible by specified <code>accessingSpaceID</code> are of type
     * {@link VisibleFormMenuEntry}.  Those entries which are not accessible
     * are of type {@link FormMenuEntry}.
     * @param accessingSpaceID the id of the space accessing the menu entries
     * available to the current space context.
     * @throws PersistenceException if there is a problem loading
     */
    void loadAccessibleToSpaceID(String accessingSpaceID) throws PersistenceException {

        StringBuffer query = new StringBuffer();
        FormMenuEntry menuEntry = null;
        String providingSpaceID = this.space.getID();
        int index = 0;
        boolean isAccessible = false;

        // Build query to load form entries available to current space context
        // with an "is_accessible" flag set if the space with accessingSpaceID
        // can see the space
        query.append("select c.class_id, ct.class_type_name, c.class_name, c.class_desc, ");
        query.append("c.class_abbreviation, c.master_table_name, ci.active_count as active_count, c.record_status, ");
        query.append("decode(access_space.class_id, null, 0, 1) as is_accessible ");
        query.append("from pn_space_has_class shc, pn_class_type ct, pn_class c, pn_class_inst_active_cnt_view ci ");
        query.append("pn_space_has_class access_space ");
        query.append("where shc.space_id = ? ");
        query.append(" and c.class_id = ci.class_id ");
        query.append("and c.class_id = shc.class_id and ct.class_type_id = c.class_type_id and ct.class_type_name = 'form' ");
        query.append("and access_space.class_id(+) = shc.class_id and access_space.space_id(+) = ?  ");
        if (m_showPending) {
            query.append(" and c.record_status in ('A', 'P') ");
        } else {
            query.append(" and c.record_status = 'A' ");
        }
        if (!m_showSystemClasses) {
            query.append(" and c.is_system_class = 0 ");
        }
        query.append("order by c.class_name asc ");

        // End of query

        // Clear out existing entries
        m_formTypes.clear();

        try {
            db.prepareStatement(query.toString());

            db.pstmt.setString(++index, providingSpaceID);
            db.pstmt.setString(++index, accessingSpaceID);
            db.executePrepared();

            while (db.result.next()) {
                isAccessible = (db.result.getInt("is_accessible") == 1);

                // Create new menu entry of appropriate type
                if (isAccessible) {
                    menuEntry = new VisibleFormMenuEntry();

                } else {
                    menuEntry = new FormMenuEntry();

                }
                menuEntry.id = db.result.getString("class_id");
                menuEntry.type = db.result.getString("class_type_name");
                menuEntry.name = db.result.getString("class_name");
                menuEntry.description = db.result.getString("class_desc");
                menuEntry.abbreviation = db.result.getString("class_abbreviation");
                menuEntry.masterDataTableName = db.result.getString("master_table_name");
                menuEntry.activeCount = db.result.getString("active_count");
                menuEntry.recordStatus = db.result.getString("record_status");
                m_formTypes.add(menuEntry);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(FormMenu.class).error("FormMenu.loadAccessibleToSpaceID failed " + sqle);
            throw new PersistenceException("Failed to load entries for form menu.", sqle);


        } finally {
            db.release();

        }

    }

    /**
     * Activates (publishes) all the forms on the menu.  This method will always
     * complete, even when it finds a form that does not work correctly.
     *
     * @throws PersistenceException when the database cannot be connected to, or
     * some other unexpected bug.
     * @throws ExceptionList when one or more errors occured while trying to
     * activate some forms.
     */
    public void activateAll() throws PersistenceException, ExceptionList {

        FormDesigner form = null;
        String class_id = null;

        if ((m_formTypes == null) || (m_formTypes.size() < 1))
            return;

        if ((this.space == null) || (this.space.getID() == null))
            throw new NullPointerException("Space or Space.m_space_id is null");

        ArrayList exceptionList = new ArrayList();

        // for each FormMenuEntry (form type)
        for (int row = 0; row < m_formTypes.size(); row++) {
            try {
                class_id = ((FormMenuEntry)m_formTypes.get(row)).getID();
                form = new FormDesigner();
                form.setID(class_id);
                form.setSpace(this.space);
                form.setUser(this.user);
                form.load();
                // Only activate the form if it is permitted to be activated
                // Otherwise, it will remain Pending
                if (form.isActivateAllowed()) {
                    form.activate(this.space);
                }
            } catch (FormException fe) {
                //We are going to collect this list of exceptions until we are
                //ready to display them to the user.  A string is a fairly crappy
                //way to propagate exceptions, but it will have to do until we
                //are prepared to fix the other code (in documents) that already
                //works this way.
                exceptionList.add(PropertyProvider.get("prm.form.menu.activatesingle.error.message", form.getName()));
            }
        }

        if (!exceptionList.isEmpty()) {
            throw new ExceptionList("Unable to activate forms.", exceptionList);
        }
    }


    /**
     * Loads all the forms from the database irresptive of users, spaces.
     * @throws PersistenceException if there is a problem loading
     */
    public void loadAll() throws PersistenceException {

        this.formNameFilter = null;
        this.spaceTypeFilter = null;
        loadData();
    }

    /**
     * Loads all the filtered forms from the database irresptive of users , spaces.
     * The filtering is done on the basis of form name.
     * @param filter a <code>String</code> value
     * @throws PersistenceException if there is a problem loading
     */

    public void loadFiltered(String filter) throws PersistenceException {
        this.formNameFilter = filter + "%";
        loadData();
    }

    /**
     * Loads all the filtered forms from the database irrespective of users, spaces, on the basis
     * of type of Space.
     * @param type a <code>String</code> containing the type of Space on which
     * filter should be operated on
     * @throws PersistenceException if there is a problem loading
     */
    public void loadFilteredType(String type) throws PersistenceException {

        this.spaceTypeFilter = type;
        this.formNameFilter = null;
        loadData();
    }

    /**
     * Gets the presentation of the component
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     *
     * @return presetation of the component
     */
    public String getPresentation() {
        return m_formatter.getPresentation(getXML());
    }

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        m_formatter.setStylesheet(styleSheetFileName);
    }

    /**
     * Loads forms based on form name filter, space type filter or all forms.
     * Does <b>not</b> limit loaded forms to space.
     * @throws PersistenceException if there is a problem loading
     */
    private void loadData() throws PersistenceException {
        String query;

        // clear the old data from the formMenu.
        clear();

        // Construct the query for loading forms
        query = "select distinct c.class_id, ct.class_type_name, c.class_name, c.class_desc, " +
            " c.class_abbreviation, c.master_table_name, c.next_data_seq-1 as active_count, c.record_status , c.owner_space_id, " +
            " c.supports_external_access, c.external_class_id, " +
            " (select  count(envelope_id) from pn_envelope_has_object where object_id = c.class_id ) as workflow " +
            " from pn_space_has_class shc, pn_class_type ct, pn_class c ";
        query += " where c.class_id = shc.class_id and ct.class_type_id = c.class_type_id and ct.class_type_name = 'form' ";


        if (m_showPending)
            query += " and c.record_status in ('A', 'P') ";
        else
            query += " and c.record_status = 'A' ";

        if (!m_showSystemClasses)
            query += " and c.is_system_class=0 ";

        if (formNameFilter != null)
            query += " and UPPER(c.class_name) like UPPER('" + formNameFilter + "')";

        query += " order by c.class_name asc";

        try {
            loadSearchedForms(query);
        } catch (FormException e) {
            // Suck it up; there will be zero entries in the FormMenu
        }

    }

    /**
     * Load the FormMenuEntry records based on the specified user.
     *
     * @throws PersistenceException if there is a problem loading
     */
    public void loadAccessibleToUser(String userID)
        throws PersistenceException, FormException {

        this.m_formTypes.clear();

        StringBuffer query = new StringBuffer();
        query.append(" select c.class_id , c.class_name ,c.class_desc , c.class_abbreviation , ");
        query.append(" c.master_table_name , c.next_data_seq-1 as active_count , c.record_status ,c.owner_space_id ,");
        query.append(" c.supports_external_access, c.external_class_id, shc.visible, ");
        query.append(" (select  count(envelope_id) from pn_envelope_has_object where object_id = c.class_id ) as workflow ");
        query.append(" from pn_space_has_person shp , ");
        query.append(" pn_space_has_class shc , pn_class c where shp.space_id = shc.space_id and ");
        query.append(" shc.class_id = c.class_id and shp.person_id = '" + userID + "'");
        query.append(" and c.record_status ='A' ");

        this.isClassTypeNameRequired = false;
        load(query.toString());

    }

    /**
     * Load the FormMenuEntry records based on the specified query.
     *
     * @throws PersistenceException if there is a problem loading
     * @throws FormException if there no forms defined for the space.
     */
    private void load(String query) throws PersistenceException, FormException {
        FormMenuEntry menuEntry = null;

        try {
            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                throw new FormException("No forms have been defined");

            boolean isExternalAccessAllowed = PropertyProvider.get("prm.externalformaccess.isenabled").equals("1") || PropertyProvider.get("prm.externalformaccess.isenabled").equals("true");
            
            // for each form type returned in query.
            do {
                try {
                    menuEntry = new FormMenuEntry();
                    menuEntry.id = db.result.getString("class_id");

                    if (this.isClassTypeNameRequired) {
                        menuEntry.type = db.result.getString("class_type_name");
                    }

                    menuEntry.name = db.result.getString("class_name");
                    menuEntry.description = db.result.getString("class_desc");
                    menuEntry.abbreviation = db.result.getString("class_abbreviation");
                    menuEntry.masterDataTableName = db.result.getString("master_table_name");
                    
                    // Check for shared form to get count for child spaces
                    boolean isSharedForm = db.result.getString("shared").equals("1");
                    Clob childIdsStr = db.result.getClob("child_ids");
                	String childIds = null;
                	if(childIdsStr != null){
                		long len = childIdsStr.length();
                		childIds = childIdsStr.getSubString(1l, (int)len);
                	}
                    menuEntry.activeCount = isSharedForm ? null 
                    										: db.result.getString("active_count");
                    
                    menuEntry.recordStatus = db.result.getString("record_status");
                    menuEntry.ownerSpaceID = db.result.getString("owner_space_id");
                    menuEntry.setCurrentSpaceID(this.space != null ? this.space.getID() : "0");
                    if(db.result.getString("workflow").equals("0")){
                    	menuEntry.hasWorkflows = "0";
                    }else{
                    	menuEntry.hasWorkflows = "1";
                    }
                    menuEntry.setOwner(db.result.getString("is_owner").equals("1"));
                    menuEntry.setVisible(db.result.getString("visible").equals("1"));
                    
                    menuEntry.space = OwnerSpaceGetter.getSpace(menuEntry.ownerSpaceID, spaceMap);

                    if(!db.result.getString("owner_space_id").equals(this.space.getID())){
                    	FormFilterConstraint formSpaceFilterConstraint = new FormFilterConstraint();
                    	formSpaceFilterConstraint.addParameter("space_id", " (" + childIds +  " ) ");
                    	formSpaceFilterConstraint.setOperator(" in ");
                        menuEntry.addFilterConstraint(formSpaceFilterConstraint);
                    }
                    
                    menuEntry.addFilterConstraint(this.formFilterConstraint);

                    if(db.result.getString("supports_external_access").equals("0") || !isExternalAccessAllowed){
                    	menuEntry.supportExternalAccess = "0";
                    }else{
                    	menuEntry.supportExternalAccess = "1";
                    }                    
                    menuEntry.externalId =db.result.getString("external_class_id");
                    
                    if (isFormDataListRequired) {
                        menuEntry.loadFormDataList();
                    }

                    if (this.spaceTypeFilter != null) {

                        if (menuEntry.space.getType().equals(this.spaceTypeFilter)) {
                            m_formTypes.add(menuEntry);
                        }

                    } else {
                        m_formTypes.add(menuEntry);
                    }

                    // Know that trying to catching exceptions for each iteration
                    // is a performance inhibitor but can't think for anything beyond for
                    // this release
                    // --- deepak
                } catch (SQLException sqlei) {
                	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + sqlei + " for " + menuEntry.id);
                } catch (PersistenceException pei) {
                	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + pei + " for" + menuEntry.ownerSpaceID);                }

            } while (db.result.next());
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + sqle);
            throw new PersistenceException("Failed to load form menu.");
        } finally {
            db.release();
        }

    }

    /*
     * This Inner class is used to get the Space object on the basis of Space ID
     * being passed. It creates one Space Object if none is found & also updates
     * HashMap containing various spaces accordingly.
     */
    private static class OwnerSpaceGetter {

        private static Space getSpace(String spaceID, HashMap spaceMap) throws PersistenceException {

            Space space = (Space)spaceMap.get(spaceID);

            if (space != null) {
                return space;
            } else {
                space = SpaceFactory.constructSpaceFromID(spaceID);
                spaceMap.put(spaceID, space);
            }
            return space;

        }

    }
    
    /**
     * Get active count of shared form records 
     * of current space including it's child space if present
     * @param spaceIds String
     * @param classId String
     * @return String active count
     */
    private String getActiveCount(String spaceIds, String classId) {
    	DBBean db = new DBBean();
    	String activeCount = "";
		StringBuffer query = new StringBuffer(
				"select sum(decode(ac.active_count, null, 0, ac.active_count)) as active_count "
						+ " from (select count(*) as active_count, ci.space_id "
						+ "	   from pn_class_instance ci "
						+ "	   where ci.record_status = 'A' "
						+ "	   and ci.class_id =" + classId
						+ "	   group by ci.space_id) ac ");
						
        if(StringUtils.isNotEmpty(spaceIds)) {
        	query.append(" where ac.space_id in (" + spaceIds  + ")");
        }
		                	
		try {
	         db.setQuery(query.toString());
	         db.executeQuery();
	         if(db.result.next()) {
	        	 activeCount = db.result.getString("active_count");
		     }
		} catch (SQLException sqle) {
	        Logger.getLogger(FormMenu.class).error("Error occurred while getting active count " + sqle);
	    } finally {
	    	db.release(); 
	    }
	    return activeCount;
    }
    
    /**
     * Load the FormMenuEntry records based on the specified query for 
     * all users and all spaces.
     *
     * @throws PersistenceException if there is a problem loading
     * @throws FormException if there no forms defined for the space.
     */
    private void loadSearchedForms(String query) throws PersistenceException, FormException {

    	FormMenuEntry menuEntry = null;

        try {
            db.setQuery(query);
            db.executeQuery();

            if (!db.result.next())
                throw new FormException("No forms have been defined");

            boolean isExternalAccessAllowed = PropertyProvider.get("prm.externalformaccess.isenabled").equals("1") || PropertyProvider.get("prm.externalformaccess.isenabled").equals("true");
            
            // for each form type returned in query.
            do {
                try {
                    menuEntry = new FormMenuEntry();
                    menuEntry.id = db.result.getString("class_id");

                    if (this.isClassTypeNameRequired) {
                        menuEntry.type = db.result.getString("class_type_name");
                    }

                    menuEntry.name = db.result.getString("class_name");
                    menuEntry.description = db.result.getString("class_desc");
                    menuEntry.abbreviation = db.result.getString("class_abbreviation");
                    menuEntry.masterDataTableName = db.result.getString("master_table_name");
                    
                    menuEntry.recordStatus = db.result.getString("record_status");
                    menuEntry.ownerSpaceID = db.result.getString("owner_space_id");
                    menuEntry.setCurrentSpaceID(this.space != null ? this.space.getID() : "0");
                    if(db.result.getString("workflow").equals("0")){
                    	menuEntry.hasWorkflows = "0";
                    }else{
                    	menuEntry.hasWorkflows = "1";
                    }
                    menuEntry.space = OwnerSpaceGetter.getSpace(menuEntry.ownerSpaceID, spaceMap);

                    menuEntry.addFilterConstraint(this.formFilterConstraint);

                    if(db.result.getString("supports_external_access").equals("0") || !isExternalAccessAllowed){
                    	menuEntry.supportExternalAccess = "0";
                    }else{
                    	menuEntry.supportExternalAccess = "1";
                    }                    
                    menuEntry.externalId = db.result.getString("external_class_id");
                    
                    if (isFormDataListRequired) {
                        menuEntry.loadFormDataList();
                    }

                    if (this.spaceTypeFilter != null) {
                        if (menuEntry.space.getType().equals(this.spaceTypeFilter)) {
                            m_formTypes.add(menuEntry);
                        }
                    } else {
                        m_formTypes.add(menuEntry);
                    }
                } catch (SQLException sqlei) {
                	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + sqlei + " for " + menuEntry.id);
                } catch (PersistenceException pei) {
                	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + pei + " for" + menuEntry.ownerSpaceID);                
                }

            } while (db.result.next());
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FormMenu.class).error("FormMenu.load failed " + sqle);
            throw new PersistenceException("Failed to load form menu.");
        } finally {
            db.release();
        }
    }
}



