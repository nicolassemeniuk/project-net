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
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.links;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import net.project.link.LinkManagerBean;

/**
  * Extra info for Insert tag.
  */
public class InsertTEI extends TagExtraInfo {

    public InsertTEI() {
        super();
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo info = null;
        String id = null;
        String klass = null;

        id = data.getAttributeString("name");
        if (id == null) {
            id = InsertTag.DEFAULT_DISPLAY_LINK_MANAGER_BEAN_NAME;
        }
        klass = LinkManagerBean.class.getName();
        info = new VariableInfo(id, klass, true, VariableInfo.AT_BEGIN);
        
        return new VariableInfo[] {info};
    }

}
