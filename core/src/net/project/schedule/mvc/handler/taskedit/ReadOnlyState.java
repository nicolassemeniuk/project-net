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

 package net.project.schedule.mvc.handler.taskedit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.project.schedule.Schedule;
import net.project.schedule.ScheduleEntry;
import net.project.schedule.TaskType;
import net.project.taglibs.input.InputTagFilter;
import net.project.util.CollectionUtils;

/**
 * A filter for input tags that will allow us to set up the TaskEdit.jsp without
 * having to put an overwhelming amount of if/else on the page to determine when
 * certain fields shouldn't be displayed.
 *
 * This is a bit of an experiment.  It seems like a good idea right now, we'll
 * see if it turns out to be good in practice.
 *
 * @author Matthew Flower
 * @since Version 8.2.0
 */
public class ReadOnlyState implements InputTagFilter {
    private final boolean isShared;
    private final boolean isSummaryTask;
    private final boolean autocalc;
    private final boolean isReadOnlyShare;

    private Set disabledWhenSharedGroup = new HashSet(Arrays.asList(new String[] {
        "taskCalculationTypeFixedElementID",
        "effortDriven",
        "startTimeString",
        "startTime_NotSubmitted",
        "endTimeString",
        "endTime_NotSubmitted",
        "duration",
        "actualEndDate",
        "actualStartDate",
        "duration_units",
        "work",
        "work_units",
        "work_complete",
        "work_complete_units",
        "work_percent_complete",
        "constraintTypeID",
        "constraintDateString"
    }));

    private Set disabledWhenSharedGroupRegex = new HashSet(Arrays.asList(new String[] {
        "constraintDateTime.*"
    }));

    private Set disabledWhenReadOnlyShareRegex = new HashSet(Arrays.asList(new String[] {
        "dependency_.*"
    }));


    private Set disabledWhenAutocalcSummaryTaskGroup = new HashSet(Arrays.asList(new String[] {
        "taskCalculationTypeFixedElementID",
        "effortDriven",
        "startTimeString",
        "startTime_NotSubmitted",
        "endTimeString",
        "endTime_NotSubmitted",
        "duration",
        "actualEndDate",
        "actualStartDate",
        "duration_units",
        "work",
        "work_units",
        "work_complete",
        "work_complete_units",
        "work_percent_complete"
    }));

    public ReadOnlyState(ScheduleEntry entry, Schedule schedule) {
        isShared = entry.isFromShare();
        isSummaryTask = entry.getTaskType().equals(TaskType.SUMMARY);
        autocalc = schedule.isAutocalculateTaskEndpoints();
        isReadOnlyShare = entry.isShareReadOnly();
    }

    private boolean setDisabled(Map attributeValueMap, boolean disabled) {
        attributeValueMap.put("disabled", Boolean.valueOf(disabled));
        return disabled;
    }

    public void filter(Map attributeValueMap) {
        boolean disabled = false;
        String name = (String)attributeValueMap.get("name");

        if (isShared && disabledWhenSharedGroup.contains(name)) {
            disabled = setDisabled(attributeValueMap, true);
        } else if (isSummaryTask && autocalc && disabledWhenAutocalcSummaryTaskGroup.contains(name)) {
            disabled = setDisabled(attributeValueMap, true);
        } else {
            disabled = setDisabled(attributeValueMap, false);
        }

        if (!disabled && isReadOnlyShare && name != null && CollectionUtils.stringCollectionMatches(disabledWhenReadOnlyShareRegex, name)) {
            setDisabled(attributeValueMap, true);
        }

        if (!disabled && isShared && name != null && CollectionUtils.stringCollectionMatches(disabledWhenSharedGroupRegex, name)) {
            setDisabled(attributeValueMap, true);
        }
    }
}

