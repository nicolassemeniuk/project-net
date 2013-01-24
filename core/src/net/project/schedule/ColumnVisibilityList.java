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
package net.project.schedule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.project.channel.ScopeType;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;

public class ColumnVisibilityList {
    private Map visibilityMap = new HashMap();
    
    private final static List<String> defaultList =
        Arrays.asList(new String[] {"id", "sequence", "name", "startDate", "endDate", "work", "duration", "workPercentComplete", "statusNotifiers"});

    private void populateVisibilityMap(String id, boolean visible) {
        visibilityMap.put(id, Boolean.valueOf(visible));
    }

    private boolean getColumnProp(PersonProperty props, String id) {
        String[] visibilityProps = props.get("prm.schedule.main.column", id, true);
        String visibilityProp = (visibilityProps != null && visibilityProps.length > 0 ? visibilityProps[0]: String.valueOf(defaultList.contains(id)));
        return (visibilityProp != null && visibilityProp.equals("true"));
    }

    public void construct(List<String> list) {
        //Get all the visibility properties from the database beforehand so
        //we don't have to fetch each one.
        PersonProperty props = new PersonProperty();
        props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        props.setMatchExactContext(true);
        props.prefetchForContextPrefix("prm.schedule.main.column");

        //Iterate through the columns and assign them visibility.
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            String colId = it.next();
            boolean visible = getColumnProp(props, colId);
            populateVisibilityMap(colId, visible);
            if(!visible) {
                it.remove();
            }
        }
    }

    public boolean isVisible(String id) {
        return ((Boolean) visibilityMap.get(id)).booleanValue();
    }

}
