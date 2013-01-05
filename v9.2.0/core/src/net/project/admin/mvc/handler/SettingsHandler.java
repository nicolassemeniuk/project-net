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
 * Provides setup of model for settings Main page.
 * @author Tim Morrow
 * @since Version 7.7
 */
public class SettingsHandler extends Handler {
    public SettingsHandler(HttpServletRequest request) {
        super(request);
    }

    /**
     * Returns the view <code>/admin/settings/Main.jsp</code>.
     * @return the path to the view
     */
    public String getViewName() {
        return "/admin/settings/Main.jsp";
    }

    public void validateSecurity(int module, int action, String objectID, HttpServletRequest request) throws AuthorizationFailedException, PnetException {
        SecurityProvider sp = (SecurityProvider)getSessionVar("securityProvider");
        CheckSpaceAccessTag.verifySpaceAccess(ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID, action, sp);
    }

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        SettingsEditor settingsEditor = new SettingsEditor();

        model.put("settingsEditor", settingsEditor);

        passThru(model, "module");
        passThru(model, "action");

        return model;
    }
}
