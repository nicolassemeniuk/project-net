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

 package net.project.document;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import net.project.security.User;

public interface IVersion extends Serializable {

    public void load();

    public boolean isVersionOf (String parentObjectID);

    public String getXML();

    public int getVersionNum();

    public String getVersionID();

    public String getParentObjectID();

    public void download(HttpServletResponse response) throws DocumentException;

    public void setUser(User user);

    public boolean isLoaded();

}
