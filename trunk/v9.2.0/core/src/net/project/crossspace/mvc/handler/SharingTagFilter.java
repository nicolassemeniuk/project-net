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

 package net.project.crossspace.mvc.handler;

import java.util.List;
import java.util.Map;

import net.project.crossspace.AllowableActionCollection;
import net.project.crossspace.TradeAgreement;
import net.project.taglibs.input.InputTagFilter;

public class SharingTagFilter implements InputTagFilter {
    private MultiClassExportedObject export;

    public SharingTagFilter(MultiClassExportedObject export) {
        this.export = export;
    }


    public void filter(Map attributeValueMap) {
        String tagName = (String)attributeValueMap.get("name");

        if ("permissionType".equals(tagName)) {
            String tagValue = (String)attributeValueMap.get("value");

            //"0" means no sharing has been enabled.
            if ("0".equals(tagValue) && export.permissionType.consistent && export.permissionType.value.equals(TradeAgreement.NO_ACCESS)) {
                attributeValueMap.put("checked", new Boolean(true));
            } else if ("1".equals(tagValue) && ((!export.permissionType.consistent) || !export.permissionType.value.equals(TradeAgreement.NO_ACCESS))) {
                attributeValueMap.put("checked", new Boolean(true));
            } else if ("2".equals(tagValue) && export.permissionType.consistent && export.permissionType.value.equals(TradeAgreement.ALL_ACCESS)) {
                attributeValueMap.put("checked", new Boolean(true));
            } else if ("3".equals(tagValue) && !((List)export.permittedSpaces.value).isEmpty()) {
                attributeValueMap.put("checked", new Boolean(true));
            } else if ("4".equals(tagValue) && !((List)export.permittedUsers.value).isEmpty()) {
                attributeValueMap.put("checked", new Boolean(true));
            }
        }

        if ("propagateToChildren".equals(tagName)) {
            attributeValueMap.put("checked", export.propagateToChildren.value);
        }

        if ("allowableAction".equals(tagName)) {
            String value = (String)attributeValueMap.get("value");
            if (value != null) {
                int intValue = Integer.parseInt(value);
                if ((intValue & ((AllowableActionCollection)export.allowableActions.value).getDatabaseID()) > 0) {
                    attributeValueMap.put("checked", new Boolean(true));    
                }
            }
        }
    }

}

