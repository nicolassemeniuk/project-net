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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.admin.mvc.handler;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.admin.ApplicationSpace;
import net.project.admin.setting.SettingsEditor;
import net.project.base.PnetException;
import net.project.base.mvc.Handler;
import net.project.security.AuthorizationFailedException;
import net.project.security.SecurityProvider;
import net.project.taglibs.security.CheckSpaceAccessTag;

/**
 * Provides processing for modifying settings.
 * @author Tim Morrow
 * @since Version 7.7
 */
public class SettingsUpdateHandler extends Handler {

    private static final String SETTINGS_VIEW = "/servlet/AdminController/Settings";
    private static final String ADMIN_DASHBOARD_VIEW = "/admin/Main.jsp";

    /** The view to return to, defaulting to the admin dashboard. */
    private String viewName = ADMIN_DASHBOARD_VIEW;

    public SettingsUpdateHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Returns the appropriate view; settings editor if the "reset default"
     * button was pressed, otherwise the admin view.
     * @return the view name
     */
    public String getViewName() {
        return this.viewName;
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        SecurityProvider sp = (SecurityProvider)getSessionVar("securityProvider");
        CheckSpaceAccessTag.verifySpaceAccess(ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID, action, sp);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        String action = (String) getVar("theAction");

        if ("modify".equals(action)) {
            SettingsEditor settingsEditor = new SettingsEditor();
            settingsEditor.process(request);
            this.viewName = ADMIN_DASHBOARD_VIEW;

        } else if ("resetDefault".equals(action)) {
            SettingsEditor settingsEditor = new SettingsEditor();
            settingsEditor.resetDefault();
            this.viewName = SETTINGS_VIEW;

        } else {
            throw new PnetException("Missing or illegal theAction: " + action);

        }

        passThru(model, "module");

        return model;
    }
}
