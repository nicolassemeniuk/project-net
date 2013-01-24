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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.gui.error.IErrorProvider;
import net.project.gui.error.ValidationErrors;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.space.SpaceTypes;

import org.apache.log4j.Logger;

/**
 * The Wizard used for creating a group.
 * It consists of a number of pages that store information gathered during
 * the wizard.
 */
public class CreateGroupWizard {

    // 
    // Constants
    //

    /**
     * The name of the Create page.
     * @see #setCurrentPageForName
     */
    public static final String CREATE_PAGE = "create";

    /**
     * The name of the Select Space page.
     * @see #setCurrentPageForName
     */
    public static final String SELECT_SPACE_PAGE = "selectSpace";

    /**
     * The name of the Select Role page.
     * @see #setCurrentPageForName
     */
    public static final String SELECT_ROLE_PAGE = "selectRole";

    /**
     * The name of the Inherit Security page.
     */
    public static final String INHERIT_SECURITY_PAGE = "inheritSecurity";

    //
    // End of constants
    //


    /**
     * The current user context.
     */
    private User user = null;

    /**
     * The current space context.
     */
    private Space space = null;

    /**
     * The current page in the wizard.
     */
    private WizardPage currentPage = null;

    /**
     * The collection of pages in this wizard.
     */
    private ArrayList pages = null;

    /**
     * Setting of createAction radio group.
     */
    private String createAction = null;

    /**
     * Current group (when createAction is "create").
     */
    private Group group = null;

    /**
     * Current selection of inheritFrom checkboxes (when createAction is "inherit").
     */
    private ArrayList inheritFrom = null;

    /**
     * Current workspaces from which a selection may be made.
     */
    private SpaceList workspaces = null;

    /**
     * Current selection of spaces.
     */
    private ArrayList selectedSpaces = null;

    /**
     * Current selection of domains.
     */
    private ArrayList selectedDomains = null;

    /**
     * Current selection of groups.
     */
    private ArrayList selectedGroups = null;

    /**
     * Current permission selection (none, view, default).
     */
    private String permissionSelection = null;

    /**
     * Visibility of the project -- used with everyone group.
     */
    private String projectVisibilityID;

    /**
     * Creates a new Wizard, defaulting to the first page.
     */
    public CreateGroupWizard() {
        initialize();
    }


    /**
     * Clears out the Wizard, resets to first page.
     */
    public void clear() {
        initialize();
    }


    /**
     * Initializes the Wizard, setting the first page.
     */
    private void initialize() {
        this.pages = new ArrayList();

        this.pages.add(new CreatePage());
        this.pages.add(new SelectSpacePage());
        this.pages.add(new SelectRolePage());
        this.pages.add(new InheritSecurityPage());

        setCurrentPageForName(CREATE_PAGE);
        setCreateAction("create");
        setGroup(new UserDefinedGroup());
        setInheritFrom(new ArrayList());
        setWorkspaces(null);
        setSelectedSpaces(new ArrayList());
        setSelectedDomains(new ArrayList());
        setSelectedGroups(new ArrayList());
        setPermissionSelection("none");
    }


    /**
     * Sets the current space context.
     * @param space the current space
     * @see #getSpace
     */
    public void setSpace(Space space) {
        this.space = space;
    }


    /**
     * Returns the current space context.
     * @return the current space
     * @see #setSpace
     */
    Space getSpace() {
        return this.space;
    }


    /**
     * Sets the current user context.
     * @param user the current user
     * @see #getUser
     */
    public void setUser(User user) {
        this.user = user;
    }


    /**
     * Returns the current user context.
     * @return the current user
     * @see #setUser
     */
    User getUser() {
        return this.user;
    }


    /**
     * Sets the current page.
     *
     * @param pageName the name of the current page
     */
    public void setCurrentPageForName(String pageName) {
        this.currentPage = pageForName(pageName);
    }


    /**
     * Returns the current wizard page.
     * @return the current page
     * @see #setCurrentPageForName
     */
    public WizardPage getCurrentPage() {
        return this.currentPage;
    }


    /**
     * Sets the currently selected create action from the radio group.
     * @param createAction the current action ("create" or "inherit")
     * @see #getCreateAction
     */
    void setCreateAction(String createAction) {
        this.createAction = createAction;
    }

    /**
     * Returns the currently selected create action.
     * @return the selected action
     * @see #setCreateAction
     */
    String getCreateAction() {
        return this.createAction;
    }


    /**
     * Sets the selection of "inheritFrom" checkboxes.
     * Currently "workspace" or "domain".
     * @param inheritFrom the selection of inheritFrom values
     * @see #getInheritFrom
     */
    void setInheritFrom(Collection inheritFrom) {
        this.inheritFrom = new ArrayList(inheritFrom);
    }


    /**
     * Returns the current selection of "inheritFrom" checkboxes.
     * @return the selection of inheritFrom values
     * @see #setInheritFrom
     */
    List getInheritFrom() {
        return this.inheritFrom;
    }


    /**
     * Sets the current group begin created (when createAction is "create").
     * @param group the current group
     * @see #getGroup
     */
    void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Returns the current group being create.
     * @return the current group
     * @see #setGroup
     */
    Group getGroup() {
        return this.group;
    }


    /**
     * Sets the current workspaces available for selection.
     * @param spaces the spaces from which the user can make a selection
     * @see #getWorkspaces
     */
    void setWorkspaces(SpaceList spaces) {
        this.workspaces = spaces;
    }


    /**
     * Returns the workspaces available for selection.
     * @return the workspaces
     * @see #setWorkspaces
     */
    SpaceList getWorkspaces() {
        return this.workspaces;
    }
    

    /**
     * Sets the current selection of spaces.
     * Each element is a space ID.
     * @param selectedSpaces the currently selected spaces
     * @see #getSelectedSpaces
     */
    void setSelectedSpaces(Collection selectedSpaces) {
        this.selectedSpaces = new ArrayList(selectedSpaces);
    }


    /**
     * Returns the current selection of spaces.
     * @return the currently selected spaces
     * @see #setSelectedSpaces
     */
    List getSelectedSpaces() {
        return this.selectedSpaces;
    }


    /**
     * Sets the current selection of domains.
     * Each element is a domain ID.
     * @param selectedDomains the currently selected domains
     * @see #getSelectedDomains
     */
    void setSelectedDomains(Collection selectedDomains) {
        this.selectedDomains = new ArrayList(selectedDomains);
    }


    /**
     * Returns the current selection of domains.
     * @return the currently selected domains
     * @see #setSelectedDomains
     */
    List getSelectedDomains() {
        return this.selectedDomains;
    }


    /**
     * Sets the current selection of groups.
     * Each element is a group ID.
     * @param selectedGroups the currently selected groups
     * @see #getSelectedGroups
     */
    void setSelectedGroups(Collection selectedGroups) {
        this.selectedGroups = new ArrayList(selectedGroups);
    }


    /**
     * Returns the current selection of groups.
     * @return the currently selected groups
     * @see #setSelectedGroups
     */
    List getSelectedGroups() {
        return this.selectedGroups;
    }


    /**
     * Sets the selected permission setting ("none", "view", "default").
     * @param selection the selection
     * @see #getPermissionSelection
     */
    public void setPermissionSelection(String selection) {
        this.permissionSelection = selection;
    }


    /**
     * Returns the selected permission settings.
     * @return the selection
     * @see #setPermissionSelection
     */
    public String getPermissionSelection() {
        return this.permissionSelection;
    }


    /**
     * Returns the page for the specified name.
     * @param pageName the name of the page to get
     * @return the wizard page with that name
     */
    private WizardPage pageForName(String pageName) {
        WizardPage page = null;

        // Iterate over all pages in this wizard looking for the first page
        // that matches the specified name
        Iterator it = this.pages.iterator();
        while (it.hasNext()) {
            page = (WizardPage) it.next();
            if (page.getName().equals(pageName)) {
                break;
            }
        }

        return page;
    }


    //
    // Inner classes
    //


    /**
     * Provides a Wizard Page.
     */
    public static abstract class WizardPage implements IErrorProvider {
        
        /** The name of the page. */
        private String name = null;


        /**
         * Creates a new wizard page with the specified name.
         * @param name the page name
         */
        public WizardPage(String name) {
            this.name = name;
        }


        /**
         * Returns the name of this wizard page.
         * @return the name of the page
         */
        public String getName() {
            return this.name;
        }
    
        /**
         * Validates the current wizard page.
         * @see #hasErrors
         */
        public abstract void validate();


        //
        // Implementing IErrorProvider
        //

        /** Errors generated since last {@link #clearErrors} call. */
        protected ValidationErrors validationErrors = new ValidationErrors();

        /**
         * Clears all errors.
         */
        public void clearErrors() {
            validationErrors.clearErrors();
        }

       /**
        * Indicates whether there are any errors.
        * @return true if there are errors; false otherwise
        */
        public boolean hasErrors() {
            return validationErrors.hasErrors();
        }

        /**
         * Gets the Error Flag for the Field.  This method is used for
         * flagging a field label as having an error.  If an error is present
         * for the field with the specified id, the specified label is returned
         * but formatted to indicate there is an error.  Currently this uses
         * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
         * for the field with the specified id, the label is returned untouched.
         * @param fieldID the id of the field which may have the error
         * @param label the label to modify to indicate there is an error
         * @return the HTML formatted label
         */
        public String getFlagError(String fieldID, String label) {
            return validationErrors.getFlagErrorHTML(fieldID, label);
        }

        /**
         * Gets the Error Message for the Field.
         * @param fieldID  the id of the field for which to get the error message
         * @return the HTML formatted error message
         */
        public String getErrorMessage(String fieldID) {
            return validationErrors.getErrorMessageHTML(fieldID);
        }

        /**
         * Gets the Error Message for the Field.
         * @return HTML formatted error messages
         */
        public String getAllErrorMessages() {
            return validationErrors.getAllErrorMessagesHTML();
        }

        //
        // End of IErrorProvider
        //

    }


    /**
     * Provides storage of the values of the Create Page.
     */
    public class CreatePage extends WizardPage {
        /**
         * Constructs a new CreatePage.
         */
        CreatePage() {
            super(CREATE_PAGE);
        }


        /**
         * Specifies the create action to take ("create" or "inherit").
         * @param action the current action
         * @see #getCreateAction
         */
        public void setCreateAction(String action) {
            CreateGroupWizard.this.setCreateAction(action);
        }


        /**
         * Returns the currently selected create action.
         * @return the current action ("create" or "inherit")
         * @see #setCreateAction
         */
        public String getCreateAction() {
            return CreateGroupWizard.this.getCreateAction();
        }


        /**
         * Sets the group created when action is "create".
         * @param group the group
         * @see #getGroup
         */
        public void setGroup(Group group) {
            CreateGroupWizard.this.setGroup(group);
        }


        /**
         * Returns the current group created when action is "create".
         * @return the group
         * @see #setGroup
         */
        public Group getGroup() {
            return CreateGroupWizard.this.getGroup();
        }


        /**
         * Clears the inheritFrom stored values.
         */
        public void clearInheritFrom() {
            CreateGroupWizard.this.getInheritFrom().clear();
        }


        /**
         * Returns the number of selected items in the inheritFrom checkbox.
         * @return the count of items
         */
        public int getInheritFromCount() {
            return CreateGroupWizard.this.getInheritFrom().size();
        }


        /**
         * Specifies which items to inherit from when action is "inherit".
         * @param values the values of the inheritFrom checkbox
         */
        public void setInheritFrom(String[] values) {
            if (values != null) {
                CreateGroupWizard.this.setInheritFrom(Arrays.asList(values));
            } else {
                CreateGroupWizard.this.setInheritFrom(java.util.Collections.EMPTY_LIST);
            }
        }


        /**
         * Returns the checked inheritFrom values.
         * @return the values
         */
        public List getInheritFromValues() {
            return CreateGroupWizard.this.getInheritFrom();
        }


        /**
         * Indicates whether specified value is checked.
         * @param inheritFromValue the value of the inheritFrom checkbox
         * @return <code>"checked"</code> if the value is checked; the empty
         * string otheriwse
         */
        public String getInheritFromChecked(String inheritFromValue) {
            String checked = "";

            if (CreateGroupWizard.this.getInheritFrom().contains(inheritFromValue)) {
                checked = "checked";
            }

            return checked;
        }

        public String getProjectVisibilityID() {
            return CreateGroupWizard.this.projectVisibilityID;
        }

        public void setProjectVisibilityID(String projectVisibilityID) {
            CreateGroupWizard.this.projectVisibilityID = projectVisibilityID;
        }

        /**
         * Checks that all items on this pages are valid.
         */
        public void validate() {
            if (getCreateAction().equals("create")) {
                // Check group stuff
            } else if (getCreateAction().equals("inherited")) {
                // Check inherit stuff
                validateInherit();
            } else if (getCreateAction().equals("everyone")) {
                validateEveryone();
            } else {
                throw new PnetRuntimeException("Unrecognized create action.");
            }
        }

        private void validateEveryone() {
            try {
                if( getProjectVisibilityID() == null )
                	this.validationErrors.put("everyone", PropertyProvider.get("prm.directory.roles.groupcreate.everyone.whichusers.message"));
                else { // avoid multiple errors 
	            	for (Iterator it = space.getOwnedGroups().iterator(); it.hasNext();) {
	                    Group group = (Group)it.next();
	                    if (group.getGroupTypeID().equals(GroupTypeID.EVERYONE)) {
	                        this.validationErrors.put("everyone", PropertyProvider.get("prm.security.group.creategroupwizard.validateeveryone.message"));
	                    }
	                }
                }
            } catch (PersistenceException e) {
                throw new RuntimeException(e);
            }
        }


        /**
         * Validates that all conditions are satisified when "inherit" selected.
         */
        public void validateInherit() {

            if (getInheritFromCount() == 0) {
                // Need at least one selection
                this.validationErrors.put("inherit", PropertyProvider.get("prm.security.group.creategroupwizard.validateinherit.message"));
            }
        }

    } // class CreatePage


    /**
     * Provides storage of the values on the SelectSpace page.
     */
    public class SelectSpacePage extends WizardPage {

        /**
         * Creates a new SelectSpacePage.
         */
        SelectSpacePage() {
            super(SELECT_SPACE_PAGE);
        }

        /**
         * Indicates whether workspace was selected on previous page.
         * @return true if workspace was selected; false otherwise
         */
        public boolean isInheritFromWorkspace() {
            return CreateGroupWizard.this.getInheritFrom().contains("workspace");
        }

        /**
         * Indicates whether domain was selected in previous page.
         * @return true if domain was selected; false otherwise
         */
        public boolean isInheritFromDomain() {
            return CreateGroupWizard.this.getInheritFrom().contains("domain");
        }


        /**
         * Sets the workspaces from which the user may select.
         * Strips out any Personal spaces.
         * @param spaces the workspaces
         * @see #getWorkspaces
         */
        public void setWorkspaces(SpaceList spaces) {
            
            Iterator it = spaces.iterator();
            while (it.hasNext()) {
                Space space = (Space) it.next();

                if (space.getType().equals(SpaceTypes.PERSONAL.getID())) {
                    it.remove();
                }
            }
            
            CreateGroupWizard.this.setWorkspaces(spaces);
        }


        /**
         * Returns the workspaces from which the user may select.
         * @return the workspaces
         * @see #setWorkspaces
         */
        public SpaceList getWorkspaces() {
            return CreateGroupWizard.this.getWorkspaces();
        }


        /**
         * Clears the selected space id list.
         */
        public void clearSelectedSpaces() {
            CreateGroupWizard.this.getSelectedSpaces().clear();
        }


        /**
         * Returns the number of selected space ids.
         * @return the count of selected space ids
         */
        public int getSelectedSpacesCount() {
            return CreateGroupWizard.this.getSelectedSpaces().size();
        }


        /**
         * Sets the selected space ids to the values specified.
         * @param values the selected space ids
         */
        public void setSelectedSpaces(String[] values) {
            if (values != null) {
                CreateGroupWizard.this.setSelectedSpaces(Arrays.asList(values));
            } else {
                CreateGroupWizard.this.setSelectedSpaces(java.util.Collections.EMPTY_LIST);
            }
        }


        /**
         * Clears the selected domain id list.
         */
        public void clearSelectedDomains() {
            CreateGroupWizard.this.getSelectedDomains().clear();
        }


        /**
         * Returns the number of selected domain ids.
         * @return the count of selected domain ids
         */
        public int getSelectedDomainsCount() {
            return CreateGroupWizard.this.getSelectedDomains().size();
        }


        /**
         * Sets the selected domain ids to the values specified.
         * @param values the selected domain ids
         */
        public void setSelectedDomains(String[] values) {
            if (values != null) {
                CreateGroupWizard.this.setSelectedDomains(Arrays.asList(values));
            } else {
                CreateGroupWizard.this.setSelectedDomains(java.util.Collections.EMPTY_LIST);
            }
        }


        /**
         * Checks that all items on this pages are valid.
         */
        public void validate() {
            String needSelectionMessage = null;

            if (isInheritFromWorkspace() && isInheritFromDomain()) {
                // Selecting from both
                // Provide a message if both are empty
                if (getSelectedSpacesCount() == 0 && getSelectedDomainsCount() == 0) {
                    needSelectionMessage = PropertyProvider.get("prm.security.group.creategroupwizard.validate.selectionmessage1");
                }
            
            } else {
                
                if (isInheritFromWorkspace() && getSelectedSpacesCount() == 0) {
                    // Provide a message if inheriting from workspace and no selected spaces
                    needSelectionMessage = PropertyProvider.get("prm.security.group.creategroupwizard.validate.selectionmessage2");
                
                }

                if (isInheritFromDomain() && getSelectedDomainsCount() == 0) {
                    // Provide a message if inheriting from domain and no selected domains
                    needSelectionMessage = PropertyProvider.get("prm.security.group.creategroupwizard.validate.selectionmessage1");
                }
            }

            if (needSelectionMessage != null) {
                this.validationErrors.put("select", needSelectionMessage);
            }
        }

    } // class SelectSpacePage


    /**
     * Provides storage of the values on the Select Role page.
     */
    public class SelectRolePage extends WizardPage {

        /**
         * Creates a new Select Role page.
         */
        public SelectRolePage() {
            super(SELECT_ROLE_PAGE);
        }


        /**
         * Sets the selected permission setting ("none", "view", "default").
         * @param selection the selection
         * @see #getPermissionSelection
         */
        public void setPermissionSelection(String selection) {
            CreateGroupWizard.this.setPermissionSelection(selection);
        }


        /**
         * Returns the selected permission settings.
         * @return the selection
         * @see #setPermissionSelection
         */
        public String getPermissionSelection() {
            return CreateGroupWizard.this.getPermissionSelection();
        }


        /**
         * Returns the XML for the selected space roles.
         * @return the XML, including the XML version tag.
         */
        public String getSpaceGroupsXML() {
            return net.project.persistence.IXMLPersistence.XML_VERSION + 
                getSpaceGroupsXMLBody();
        }


        /**
         * Clears the currently selected groups.
         */
        public void clearSelectedGroups() {
            CreateGroupWizard.this.getSelectedGroups().clear();
        }


        /**
         * Indicates the number of selected groups.
         * @return the count of selected groups
         */
        public int getSelectedGroupsCount() {
            return CreateGroupWizard.this.getSelectedGroups().size();
        }


        /**
         * Sets the groups selected in the wizard.
         * @param values the array of group IDs for the selected groups
         */
        public void setSelectedGroups(String[] values) {
            if (values != null) {
                CreateGroupWizard.this.setSelectedGroups(Arrays.asList(values));
            } else {
                CreateGroupWizard.this.setSelectedGroups(java.util.Collections.EMPTY_LIST);
            }
        }


        /**
         * Returns the groups selected in the wizard.
         * @return the list of group IDs of the selected groups
         */
        public List getSelectedGroupValues() {
            return CreateGroupWizard.this.getSelectedGroups();
        }


        /**
         * Returns the XML for the selected space roles.
         * Example: <code><pre>
         * &lt;SpaceGroupsCollection&gt;
         *     &lt;SpaceGroups&gt;
         *         &lt;Space&gt;...&lt;/Space&gt;
         *         &lt;GroupCollection&gt;...&lt;/GroupCollection&gt;
         *     &lt;/SpaceGroups&gt;
         * &lt;/SpaceGroupsCollection&gt;
         * </pre></code>
         * @return the XML
         * @see net.project.space.Space#getXMLProperties
         * @see net.project.security.group.GroupCollection#getXMLBody
         */
        public String getSpaceGroupsXMLBody() {
            StringBuffer xml = new StringBuffer();
            
            xml.append("<SpaceGroupsCollection>");

            Iterator it = getSelectedSpaceGroups().iterator();
            while (it.hasNext()) {
                SpaceGroups spaceGroups = (SpaceGroups) it.next();

                xml.append("<SpaceGroups>");
                xml.append(spaceGroups.space.getXMLProperties());
                xml.append(spaceGroups.groups.getXMLBody());
                xml.append("</SpaceGroups>");

            }

            xml.append("</SpaceGroupsCollection>");

            return xml.toString();
        }


        /**
         * Returns a collection of <code>{@link SpaceGroups}</code> which 
         * provide the selected spaces and all groups in those spaces.
         * @return the collection
         */
        private ArrayList getSelectedSpaceGroups() {
            ArrayList selectedSpaceGroups = new ArrayList();
            
            try {
                Iterator spaceIDIt = CreateGroupWizard.this.getSelectedSpaces().iterator();
                while (spaceIDIt.hasNext()) {
                	String nextSpaceID = null;
                    nextSpaceID = (String) spaceIDIt.next();
                    
                    Space space = null;
                    // Get the space for this selected id
                    space = getSpaceByID(nextSpaceID, CreateGroupWizard.this.getWorkspaces());
                    
                    GroupCollection groups = new GroupCollection();
                    // Load the groups for that space
                    groups.setSpace(space);
                    groups.loadOwned();
                    groups.removePrincipalGroups();

                    SpaceGroups spaceGroups = null;
                    // Add the Space / Groups combination to the arraylist
                    spaceGroups = new SpaceGroups();
                    spaceGroups.space = space;
                    spaceGroups.groups = groups;
                    selectedSpaceGroups.add(spaceGroups);
                }
            
            } catch (PersistenceException pe) {
                // problem loading groups for a space
                // Abort loading of space groups

            }
            
            return selectedSpaceGroups;
        }


        /**
         * Returns the Space for the specified ID from the specified colleciton.
         * @param spaceID the id of the space to get
         * @param spaces the collection of <code>Space</code>s from which
         * to get the space
         * @return the space with the specified id; or null if not present
         * in the collection
         */
        private Space getSpaceByID(String spaceID, Collection spaces) {
            Space space = null;

            // Iterate over all spaces in collection until we find the one
            // with macthing ID
            Iterator it = spaces.iterator();
            while (it.hasNext()) {
                Space nextSpace = (Space) it.next();

                if ( nextSpace.getID().equals(spaceID)) {
                    space = nextSpace;
                    break;
                }
            }

            return space;
        }


        /**
         * Causes the selected groups to be inherited into the current space.
         * @throws PersistenceException if there is problem inheriting
         */
        public void inheritSelectedGroups() throws PersistenceException {
            GroupProvider groupProvider = new GroupProvider();
            groupProvider.inheritGroupsFromSpace(
                CreateGroupWizard.this.getSpace(), 
                CreateGroupWizard.this.getSelectedGroups(), 
                GroupProvider.PermissionSelection.forID(CreateGroupWizard.this.getPermissionSelection()) );
        }


        /**
         * Validates all items on page are OK.
         */
        public void validate() {
            if (getSelectedGroupsCount() == 0) {
                this.validationErrors.put("selectedGroups", PropertyProvider.get("prm.security.group.creategroupwizard.selectrolepage.validate.message"));
            }
        }

    } // class SelectRolePage


    /**
     * Data structure for holding space / group collection.
     */
    private static class SpaceGroups {
        Space space = null;
        GroupCollection groups = null;
    }

    public class InheritSecurityPage extends WizardPage {

        public InheritSecurityPage() {
            super(INHERIT_SECURITY_PAGE);
        }

        public void validate() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void inheritSecurity() throws PersistenceException {
            DBBean db = new DBBean();
            try {
                Space space = CreateGroupWizard.this.getSpace();
                Group group = CreateGroupWizard.this.getGroup();
                GroupProvider.PermissionSelection permissions = GroupProvider.PermissionSelection.forID(CreateGroupWizard.this.getPermissionSelection());

                GroupProvider groupProvider = new GroupProvider();
                groupProvider.retrofitSecurity(db, space, group, permissions);
                groupProvider.setNewObjectPermissions(db, space, group, permissions);
                groupProvider.grantModulePermissions(db, space, group, permissions);
            } catch (SQLException sqle) {
                Logger.getLogger(CreateGroupWizard.class).debug(sqle);
                throw new PersistenceException(sqle);
            } finally {
                db.release();
            }
        }

        /**
         * Sets the selected permission setting ("none", "view", "default").
         * @param selection the selection
         * @see #getPermissionSelection
         */
        public void setPermissionSelection(String selection) {
            CreateGroupWizard.this.setPermissionSelection(selection);
        }

        /**
         * Returns the selected permission settings.
         * @return the selection
         * @see #setPermissionSelection
         */
        public String getPermissionSelection() {
            return CreateGroupWizard.this.getPermissionSelection();
        }

        public String getProjectVisibilityID() {
            return CreateGroupWizard.this.projectVisibilityID;
        }

        public void setProjectVisibilityID(String projectVisibilityID) {
            CreateGroupWizard.this.projectVisibilityID = projectVisibilityID;
        }

        public void setGroup(Group group) {
            CreateGroupWizard.this.setGroup(group);
        }

        public Group getGroup() {
            return CreateGroupWizard.this.getGroup();
        }
    }

}


