<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    xmlns:personProperty="xalan://net.project.resource.PersonalPropertyMap"
    xmlns:taskVisibility="xalan://net.project.schedule.VisibilityListUtil"
    extension-element-prefixes="display format personProperty taskVisibility" >

<xsl:output method="html"/>

<!-- Grab the properties appended to the XML -->
<xsl:variable name="property" select="/ProjectXML/Properties/Translation/property"/>
<xsl:variable name="JSPRootURL" select="$property[@name='JSPRootURL']"/>

<!-- Initialize objects needed to construct the page -->
<xsl:variable name="properties" select="personProperty:new('prm.schedule.tasklist')"/>
<xsl:variable name="visibility" select="taskVisibility:new()"/>

<!-- Now use XML to get max hierarchy level -->
<xsl:variable name="maxLevels" select="/ProjectXML/Content/schedule/max_hierarchy_level" />

<xsl:template match="/">
    <xsl:apply-templates select="/ProjectXML/Content/schedule" />
</xsl:template>

<xsl:template match="/ProjectXML/Content/schedule">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr class="tableHeader" align="left">
    <th width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
    <th valign="bottom"><input type="checkbox" name="changeCheckedState" onClick="changeSelection();" title="{display:get('prm.schedule.list.togglecheckboxes.message')}"/></th>
    <th valign="bottom">
	    <a href="javascript:sort(0);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.sequence.column')"/></b></font></a>
	</th>
    <th valign="bottom">
        <a href="javascript:sort(1);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.task.column')"/></b></font></a>
	</th>
	<th valign="bottom">
        <a href="javascript:sort(3);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.work.column')"/></b></font></a>
	</th>
   	<th valign="bottom">
        <a href="javascript:sort(4);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.duration.column')"/></b></font></a>
    </th>
    <th valign="bottom">
        <a href="javascript:sort(5);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.startdate.column')"/></b></font></a>
	</th>
   	<th valign="bottom">
        <a href="javascript:sort(6);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.enddate.column')"/></b></font></a>
	</th>
   	<th valign="bottom">
        <a href="javascript:sort(7);"><font color="darkblue"><b><xsl:value-of select="display:get('prm.schedule.list.complete.column')"/></b></font></a>
	</th>
	<th valign="bottom">
	</th>
	<th width="1%" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
</tr>
<tr>
	<td colspan="10" class="headerSep"></td>
</tr>
<xsl:apply-templates select="task|summary" />
</table>
</xsl:template>

<xsl:template match="task|summary">
    <xsl:variable name="rowClass">
        <xsl:choose>
            <xsl:when test="boolean(StatusNotifiers/Completed) and personProperty:propertyExists($properties, 'completedTasksColor')"><xsl:value-of select="personProperty:getProperty($properties, 'completedTasksColor')"/></xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="boolean(StatusNotifiers/Late) and personProperty:propertyExists($properties, 'lateTasksColor')"><xsl:value-of select="personProperty:getProperty($properties, 'lateTasksColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/UnassignedWork) and personProperty:propertyExists($properties, 'unassignedTasksColor')"><xsl:value-of select="personProperty:getProperty($properties, 'unassignedTasksColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/HasAssignments) and personProperty:propertyExists($properties, 'hasAssignmentColor')"><xsl:value-of select="personProperty:getProperty($properties, 'hasAssignmentColor')"/></xsl:when>
                    <!--<xsl:when test="boolean(StatusNotifiers/OverAllocated) and personProperty:propertyExists($properties, 'overallocatedResourcesColor')"><xsl:value-of select="personProperty:getProperty($properties, 'overallocatedResourcesColor')"/></xsl:when>-->
                    <xsl:when test="boolean(StatusNotifiers/CriticalPath) and personProperty:propertyExists($properties, 'isCriticalPathColor')"><xsl:value-of select="personProperty:getProperty($properties, 'isCriticalPathColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/ComingDue) and personProperty:propertyExists($properties, 'tasksComingDueColor')"><xsl:value-of select="personProperty:getProperty($properties, 'tasksComingDueColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/HasDependencies) and personProperty:propertyExists($properties, 'taskDependenciesExistColor')"><xsl:value-of select="personProperty:getProperty($properties, 'taskDependenciesExistColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/isDateConstrained) and personProperty:propertyExists($properties, 'isDateConstrainedColor')"><xsl:value-of select="personProperty:getProperty($properties, 'isDateConstrainedColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/AfterDeadline) and personProperty:propertyExists($properties, 'afterDeadlineColor')"><xsl:value-of select="personProperty:getProperty($properties, 'afterDeadlineColor')"/></xsl:when>
                    <xsl:when test="boolean(StatusNotifiers/ExternalTask) and personProperty:propertyExists($properties, 'isExternalTaskColor')"><xsl:value-of select="personProperty:getProperty($properties, 'isExternalTaskColor')"/></xsl:when>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="summaryRow">
        <xsl:if test="local-name(.) = 'summary'">summaryTask</xsl:if>
    </xsl:variable>

    <xsl:variable name="treeVisibility">
        <xsl:if test="taskVisibility:isHidden($visibility, id)">hidden</xsl:if>
    </xsl:variable>

    <xsl:variable name="kidsVisible">
        <xsl:if test="taskVisibility:childrenVisible($visibility, id)=false">false</xsl:if>
    </xsl:variable>

<tr class="tableContent {$rowClass} {$summaryRow} {$treeVisibility}" id="{id}" level="{hierarchy_level}">
    <xsl:if test="$kidsVisible='false'">
        <xsl:attribute name="kidsShown">false</xsl:attribute>
    </xsl:if>
	<td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td>
        <input id="task{name}" type="checkbox" name="selected" onClick="toggleSelection();" value="{id}" />
	</td>
    <td><xsl:value-of select="sequence"/></td>
    <xsl:element name="td">
        <xsl:attribute name="nowrap" />
        <xsl:attribute name="width">20%</xsl:attribute>
        <!-- Do appropriate indent for the hierarchy level -->
        <xsl:call-template name="insertSpacers">
            <xsl:with-param name="numSpacers" select="./hierarchy_level - 1" />
            <xsl:with-param name="isOrphan" select="is_orphan" />
        </xsl:call-template>
		<!-- Milestone elements have a milestone image -->
		<xsl:if test="isMilestone=1">
			<img src="{$JSPRootURL}/images/milestone.gif" height="10" width="10" border="0" />
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</xsl:if>
        <xsl:if test="isSummaryTask=1">
            <xsl:choose>
                <xsl:when test="$kidsVisible='false'">
                    <img src="{$JSPRootURL}/images/expand.gif" height="11" width="11" id="toggler{id}" onClick="toggleTree({id});"/>
                </xsl:when>
                <xsl:otherwise>
                    <img src="{$JSPRootURL}/images/unexpand.gif" height="11" width="11" id="toggler{id}" onClick="toggleTree({id});"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:if>
        <!-- Summary Tasks have a expand/collapse icon -->
        <xsl:choose>
            <xsl:when test="string-length(./name) > 40">
                <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView?module=60&amp;action=1&amp;id={id}">
                    <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(name,1,40))"/>
                </a>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView?module=60&amp;action=1&amp;id={id}">
                    <xsl:value-of select="./name"/>
                </a>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:element>
	<td><xsl:value-of select="./work"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="workUnits"/> </td>
	<td><xsl:value-of select="./duration"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
	<td>
        <!-- Defines an anchor name for linking to the start date; a unique anchor name is required
             The anchor name and ID must be the same -->
        <xsl:variable name="anchor">sA<xsl:value-of select="id" /></xsl:variable>
        <a href="javascript:showDateTimes('{$anchor}', {id});" name="{$anchor}" id="{$anchor}" class="dateAnchor" title="{display:get('prm.schedule.list.startdate.title')}">
            <xsl:value-of select="format:formatISODate(startDateTime)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </a>
    </td>
	<td>
        <!-- Defines an anchor name for linking to the end date; a unique anchor name is required
             The anchor name and ID must be the same -->
        <xsl:variable name="anchor">eA<xsl:value-of select="id" /></xsl:variable>
        <a href="javascript:showDateTimes('{$anchor}', {id});" name="{$anchor}" id="{$anchor}" class="dateAnchor" title="{display:get('prm.schedule.list.enddate.title')}">
            <xsl:value-of select="format:formatISODate(endDateTime)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </a>
    </td>
	<td>
        <xsl:value-of select="format:formatPercent(workPercentComplete, 0, 2)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td>
        <!--Dynamically Created Icons -->
        <xsl:if test="boolean(StatusNotifiers/ExternalTask)">
            <xsl:choose>
                <xsl:when test="not(personProperty:propertyExists($properties, 'isExternalTaskImage'))">
                    <a href="{$JSPRootURL}/project/Main.jsp?module=150&amp;id={StatusNotifiers/ExternalTask/@fromSpaceID}&amp;page={$JSPRootURL}%2Fschedule%2FMain.jsp%3Fmodule%3D60" title="{display:get('prm.schedule.list.sharingproject.title')}">
                        <img hspace="2" border="0">
                            <xsl:attribute name="src">../images/schedule/externalTask.gif</xsl:attribute>
                            <xsl:attribute name="title"><xsl:value-of select="StatusNotifiers/ExternalTask/@fromSpaceName"/></xsl:attribute>
                        </img>
                    </a>
                    <xsl:element name="input">
                        <xsl:attribute name="name">hi<xsl:value-of select="id" /></xsl:attribute>
			            <xsl:attribute name="id">hi<xsl:value-of select="id" /></xsl:attribute>
                        <xsl:attribute name="type"><xsl:text>hidden</xsl:text></xsl:attribute>
                        <xsl:attribute name="value"><xsl:value-of select="./name"/></xsl:attribute>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{$JSPRootURL}/project/Main.jsp?module=150&amp;id={StatusNotifiers/ExternalTask/@fromSpaceID}&amp;page={$JSPRootURL}%2Fschedule%2FMain.jsp%3Fmodule%3D60">
                        <img hspace="2" border="0">
                            <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'isExternalTaskImage')"/></xsl:attribute>
                            <xsl:attribute name="title"><xsl:value-of select="StatusNotifiers/ExternalTask/@fromSpaceName"/></xsl:attribute>
                        </img>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="boolean(StatusNotifiers/HasDependencies)">
            <xsl:choose>
                <xsl:when test="not(personProperty:propertyExists($properties, 'taskDependenciesExistImage'))">
                    <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView/Dependencies?action=1&amp;module=60&amp;id={id}" onmouseover="hiLite({TaskDependencyList/idlist});dPopup('{TaskDependencyList/PopupInfo}');" onmouseout="unLite({TaskDependencyList/idlist});dClose();">
                        <img src="{$JSPRootURL}/images/schedule/dependency.gif" hspace="2" border="0"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView/Dependencies?action=1&amp;module=60&amp;id={id}" onmouseover="hiLite({TaskDependencyList/idlist});dPopup('{TaskDependencyList/PopupInfo}');" onmouseout="unLite({TaskDependencyList/idlist});dClose();">
                        <img hspace="2" border="0">
                            <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'taskDependenciesExistImage')"/></xsl:attribute>
                        </img>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="boolean(StatusNotifiers/isDateConstrained)">
            <xsl:choose>
                <xsl:when test="not(personProperty:propertyExists($properties, 'isDateConstrainedImage'))">
                    <img src="{$JSPRootURL}/images/schedule/constraint.gif" hspace="2" title="{TaskConstraint/tooltip}" border="0"/>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView/Advanced?action=1&amp;module=60&amp;id={id}">
                        <img hspace="2" title="{TaskConstraint/tooltip}" border="0">
                            <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'isDateConstrainedImage')"/></xsl:attribute>
                        </img>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="boolean(StatusNotifiers/HasAssignments)">
            <xsl:choose>
                <xsl:when test="not(personProperty:propertyExists($properties, 'hasAssignmentImage'))">
                    <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView/Assignments?action=1&amp;module=60&amp;id={id}">
                        <img src="{$JSPRootURL}/images/group_person_small.gif" hspace="2" border="0" onmouseover="aPopup('{StatusNotifiers/HasAssignments/AssignmentTooltip}');" onmouseout="aClose();"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{$JSPRootURL}/servlet/ScheduleController/TaskView/Assignments?action=1&amp;module=60&amp;id={id}">
                        <img hspace="2" border="0" onmouseover="aPopup('{StatusNotifiers/HasAssignments/AssignmentTooltip}');" onmouseout="aClose();">
                            <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'hasAssignmentImage')"/></xsl:attribute>
                        </img>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="personProperty:propertyExists($properties, 'isCriticalPathImage')">
            <xsl:if test="boolean(StatusNotifiers/CriticalPath)">
                <img src="{$JSPRootURL}/images/schedule/critical_path.gif" hspace="2" border="0" title="{display:get('prm.schedule.list.criticalpath.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'isCriticalPathImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        <xsl:if test="personProperty:propertyExists($properties, 'afterDeadlineImage')">
            <xsl:if test="boolean(StatusNotifiers/AfterDeadline)">
                <img src="{$JSPRootURL}/images/schedule/after_deadline.gif" hspace="2" border="0" title="{display:get('prm.schedule.list.afterdeadline.message', format:formatISODate(TaskConstraint/deadline))}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'afterDeadlineImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        <xsl:if test="personProperty:propertyExists($properties, 'unassignedTasksImage')">
            <xsl:if test="boolean(StatusNotifiers/UnassignedWork)">
                <img border="0" hspace="2" title="{display:get('prm.schedule.list.noresources.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'unassignedTasksImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        <xsl:if test="personProperty:propertyExists($properties, 'tasksComingDueImage')">
            <xsl:if test="boolean(StatusNotifiers/ComingDue)">
                <img border="0" hspace="2" title="{display:get('prm.schedule.list.taskcomingdue.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'tasksComingDueImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        <!--
        <xsl:if test="personProperty:propertyExists($properties, 'overallocatedResourcesImage')">
            <xsl:if test="boolean(StatusNotifiers/OverAllocated)">
                <img border="0" hspace="2" title="{display:get('prm.schedule.list.overallocatedresources.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'overallocatedResourcesImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        -->
        <xsl:if test="personProperty:propertyExists($properties, 'completedTasksImage')">
            <xsl:if test="boolean(StatusNotifiers/Completed)">
                <img border="0" hspace="2" title="{display:get('prm.schedule.list.completetask.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'completedTasksImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
        <xsl:if test="personProperty:propertyExists($properties, 'lateTasksImage')">
            <xsl:if test="boolean(StatusNotifiers/Late)">
                <img border="0" hspace="2" title="{display:get('prm.schedule.list.latetask.message')}">
                    <xsl:attribute name="src"><xsl:value-of select="personProperty:getProperty($properties, 'lateTasksImage')"/></xsl:attribute>
                </img>
            </xsl:if>
        </xsl:if>
    </td>
	<td width="1%" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr rowSep="{hierarchy_level}" class="{$treeVisibility}">
 	<td colspan="10" class="rowSep"></td>
</tr>
</xsl:template>

<xsl:template name="insertSpacers">
    <xsl:param name="numSpacers" />
    <xsl:param name="isOrphan" />

    <xsl:if test="$numSpacers > 0">
        <img src="{$JSPRootURL}/images/spacers/trans.gif" height="1" width="{$numSpacers*20}"/>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>