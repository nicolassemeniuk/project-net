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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.channel.ScopeType;
import net.project.resource.PersonProperty;
import net.project.security.SessionManager;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

public class ColumnPositionList {
    private Map positionMap = new HashMap();
    private BidiMap previousPositionMap;
    
    public ColumnPositionList() {
        positionMap.put("sequence", 0);
        positionMap.put("name", 1);
        positionMap.put("phase", 2);
        positionMap.put("priority", 3);
        positionMap.put("calculationType", 4);
        positionMap.put("startDate", 5);
        positionMap.put("actualStartDate", 6);
        positionMap.put("baselineStartDate", 7);
        positionMap.put("startVariance", 8);
        positionMap.put("endDate", 9);
        positionMap.put("actualEndDate", 10);
        positionMap.put("baselineEndDate", 11);
        positionMap.put("endVariance", 12);
        positionMap.put("work", 13);
        positionMap.put("baselineWork", 14);
        positionMap.put("workVariance", 15);
        positionMap.put("workComplete", 16);
        positionMap.put("duration", 17);
        positionMap.put("baselineDuration", 18);
        positionMap.put("durationVariance", 19);
        positionMap.put("workPercentComplete", 20);
        positionMap.put("statusNotifiers", 21);
        positionMap.put("resources", 22);
        positionMap.put("dependencies", 23);  
        positionMap.put("wbs", 24);
        previousPositionMap = new DualHashBidiMap(positionMap);
    }
        


    private void populatePostionMap(String id, int position) {
        positionMap.put(id, position);
    }

    private void setColumnProp(PersonProperty props, String id) {
        String[] positionProps = props.get("prm.schedule.main.column.position", id);
        int defaultPosition = (Integer) previousPositionMap.get(id);
        if(positionProps != null && positionProps.length > 0 ) {
            int positionProp = Integer.valueOf(positionProps[0]);
            populatePostionMap(id, positionProp);
            if(positionProp > defaultPosition) {
                for(int i = defaultPosition; i < positionProp; i++) {
                    String defaultColId = (String) previousPositionMap.getKey(i + 1);
                    populatePostionMap(defaultColId, i);
                }
            } else if (positionProp < defaultPosition) {
                for(int i = positionProp; i < defaultPosition; i++) {
                    String defaultColId = (String) previousPositionMap.getKey(i);
                    populatePostionMap(defaultColId, i + 1);
                }
            } 
            //we copy the current position map to previous one 
            //to fetch the indexes set for next iteration
            previousPositionMap = new DualHashBidiMap(positionMap);
        } 
    }

    public void construct(List<String> list) {
        //Get all the position properties from the database beforehand so
        //we don't have to fetch each one.
        PersonProperty props = new PersonProperty();
        props.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        props.setMatchExactContext(true);
        props.prefetchForContextPrefix("prm.schedule.main.column.position");

        //Iterate through the columns and assign them position.
        for (Iterator<String> it = list.iterator(); it.hasNext();) {
            String colId = it.next();
            setColumnProp(props, colId);
        }
    }

    public int getPosition(String id) {
        return (Integer) positionMap.get(id);
    }

}
