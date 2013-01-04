<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >
<xsl:output method="html"/> 
<xsl:template match="/">
<table  width="100%" cellpadding="0" border="0" cellspacing="0">
	<tr>
		<td>
			<table cellpadding="0" cellspacing="0" width="100%" border="0" vspace="0">
			<tr>
				<td class="tableHeader" width="5%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="tableHeader" width="45%"><xsl:value-of select="display:get('prm.calendar.view.timeframe.name.label')"/></td>
				<td class="tableHeader" width="20%"><xsl:value-of select="display:get('prm.calendar.view.timeframe.time.label')"/></td>
				<td class="tableHeader" width="30%"><xsl:value-of select="display:get('prm.calendar.view.timeframe.location.label')"/></td>
			</tr>
		<tr class="tableLine">
			<td colspan="4" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
			<xsl:variable name="calendars" select="count(calendar)" />
			<xsl:if test="$calendars>0" >
				<xsl:apply-templates select="calendar"/>
			</xsl:if>
			</table>
		</td>
	</tr>
</table>
</xsl:template>

<xsl:template match="calendar">
			<xsl:variable name="meetings" select="count(meeting)" />
			<xsl:if test="$meetings>0" >
				<tr><td colspan="5" class="tableHeader"><xsl:value-of select="display:get('prm.calendar.view.type.meetings.label')"/></td></tr>
				<tr class="tableLine">
				    <td colspan="5" class="tableLine">
					<xsl:element name="img">
						<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
						<xsl:attribute name="width">1</xsl:attribute>
						<xsl:attribute name="height">2</xsl:attribute>
						<xsl:attribute name="border">0</xsl:attribute>
					</xsl:element>
					</td>
				</tr>
				<xsl:apply-templates select="meeting"/>
			</xsl:if>
			<xsl:variable name="events" select="count(event)" />
			<xsl:if test="$events>0" >
				<tr><td colspan="5" class="tableHeader"><xsl:value-of select="display:get('prm.calendar.view.type.events.label')"/></td></tr>
				<tr class="tableLine">
				    <td colspan="5" class="tableLine">
					<xsl:element name="img">
						<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
						<xsl:attribute name="width">1</xsl:attribute>
						<xsl:attribute name="height">2</xsl:attribute>
						<xsl:attribute name="border">0</xsl:attribute>
					</xsl:element>
					</td>
				</tr>
				<xsl:apply-templates select="event"/>
			</xsl:if>
			<xsl:variable name="milestones" select="count(task[isMilestone=1])" />
			<xsl:if test="$milestones>0" >
				<tr><td colspan="5" class="tableHeader"><xsl:value-of select="display:get('prm.calendar.view.type.milestones.label')"/></td></tr>
				<tr class="tableLine">
				    <td colspan="5" class="tableLine">
					<xsl:element name="img">
						<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
						<xsl:attribute name="width">1</xsl:attribute>
						<xsl:attribute name="height">2</xsl:attribute>
						<xsl:attribute name="border">0</xsl:attribute>
					</xsl:element>
					</td>
				</tr>
				<xsl:apply-templates select="task[isMilestone=1]|summary[isMilestone=1]"/>
			</xsl:if>
			<xsl:variable name="tasks" select="count(task[isMilestone=0]|summary[isMilestone=0])" />
			<xsl:if test="$tasks>0" >
				<tr><td colspan="5" class="tableHeader"><xsl:value-of select="display:get('prm.calendar.view.type.tasks.label')"/></td></tr>
				<tr class="tableLine">
				    <td colspan="5" class="tableLine">
					<xsl:element name="img">
						<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
						<xsl:attribute name="width">1</xsl:attribute>
						<xsl:attribute name="height">2</xsl:attribute>
						<xsl:attribute name="border">0</xsl:attribute>
					</xsl:element>
					</td>
				</tr>
				<xsl:apply-templates select="task[isMilestone=0]|summary[isMilestone=0]"/>
			</xsl:if>
</xsl:template>

<xsl:template match="calendar/meeting">
       	<tr class="tableContent">
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
				</xsl:element>
			</td>
			<td class="tableContent">
				<xsl:element name="A">
					<xsl:attribute name="href">../calendar/MeetingManager.jsp?id=<xsl:value-of select="./id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=70</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
			<td class="tableContent"><xsl:value-of select="./startTime"/> - <BR /> <xsl:value-of select="./endTime"/> <xsl:value-of select="./timeZone"/></td>
			<td class="tableContent"><b><xsl:value-of select="./facility/name"/></b></td>
         </tr>
           	<tr class="tableLine">
  	 	<td  colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	 	</tr>
</xsl:template>

<xsl:template match="calendar/event">
       	<tr class="tableContent">
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
				</xsl:element>
			</td>
			<td class="tableContent">
				<xsl:element name="A">
					<xsl:attribute name="href">../calendar/EventView.jsp?id=<xsl:value-of select="./id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=70</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
			<td class="tableContent"><xsl:value-of select="./startTime"/> - <BR /><xsl:value-of select="./endTime"/> <xsl:value-of select="./timeZone"/></td>
			<td class="tableContent"><b><xsl:value-of select="./facility/name"/></b></td>
         </tr>
                    	<tr class="tableLine">
	   	 	<td  colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	 	</tr>
</xsl:template>

<xsl:template match="calendar/task[isMilestone=1]|calendar/summary[isMilestone=1]">
       	<tr class="tableContent">
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
				</xsl:element>
			</td>
			<td class="tableContent">
				<xsl:element name="A">
					<xsl:attribute name="href">../servlet/ScheduleController/TaskView?id=<xsl:value-of select="./id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=60</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
			<td class="tableContent"><xsl:value-of select="./startDate"/></td>
			<td class="tableContent"><b><xsl:value-of select="./facility/name"/></b></td>
         </tr>
                    	<tr class="tableLine">
	   	 	<td  colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	 	</tr>
</xsl:template>

<xsl:template match="calendar/task[isMilestone=0]|calendar/summary[isMilestone=0]">
       	<tr class="tableContent">
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
				</xsl:element>
			</td>
			<td class="tableContent">
				<xsl:element name="A">
					<xsl:attribute name="href">../servlet/ScheduleController/TaskView?id=<xsl:value-of select="./id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=60</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
			<td class="tableContent">
				<xsl:value-of select="./startDate"/> - <xsl:value-of select="./endDate"/> 
			</td>
			<td class="tableContent"><b><xsl:value-of select="./facility/name"/></b></td>
         </tr>
                    	<tr class="tableLine">
	   	 	<td  colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	 	</tr>
</xsl:template>

</xsl:stylesheet>
	