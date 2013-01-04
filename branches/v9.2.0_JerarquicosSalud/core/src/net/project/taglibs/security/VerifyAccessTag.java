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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/

package net.project.taglibs.security;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.security.Action;
import net.project.security.AuthorizationFailedException;

public class VerifyAccessTag extends TagSupport {

    public static final int DEFAULT_ERROR_VALUE = -1;

    private String id = null;
    private int action = DEFAULT_ERROR_VALUE;
    private int module = DEFAULT_ERROR_VALUE;

    /* -------------------------------  Constructors  ------------------------------- */

    public VerifyAccessTag() {
        super();
    }


    /* -------------------------------  Implementing setters and getters  ------------------------------- */

    public void setObjectID (String id) {
        this.id = id;
    }

    public void setAction (String action) {
        this.action = Action.actionStringToInt(action);
    }

    public void setModule (int module) {
        this.module = module;
    }

    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() throws JspTagException, AuthorizationFailedException {
        verifyAccess();
        return SKIP_BODY;
    }

    public void release() {
        this.id = null;
        this.action = DEFAULT_ERROR_VALUE;
        this.module = DEFAULT_ERROR_VALUE;

        super.release();
    }


    /* -------------------------------  Implementing utility methods  ------------------------------- */

    private void verifyAccess() throws AuthorizationFailedException {
        net.project.security.AccessVerifier.verifyAccess(module, action, id);
    } // verifyAccess

}
