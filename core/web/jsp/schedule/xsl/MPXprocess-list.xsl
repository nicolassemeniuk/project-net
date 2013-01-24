<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
<xsl:output method="html"/> 
<xsl:template match="/">
<table width="100%" cellpadding="0" border="0" cellspacing="0">
<tr align="left">
	<td class="tableHeader" cellspan="9"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td> 
</tr>

<tr class="channelHeader">
	<td width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
	<td nowrap="1" class="channelHeader" colspan="7"><nobr><xsl:value-of select="./task_list/@typeName"/></nobr></td>
	<td align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
</tr>
<tr align="left">
	<td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td> 
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </th>	
	<td nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.task.column')"/></td>
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.work.column')"/></th>
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.duration.column')"/></th>
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.startdate.column')"/></th>
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.enddate.column')"/></th>
	<th nowrap="1" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.schedule.list.complete.column')"/></th>
	<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
	<td  colspan="9" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
</tr>
	<xsl:apply-templates select="task_list/task|task_list/milestone|task_list/summary" />
</table>
</xsl:template>

<xsl:template match="task_list/summary|task_list/milestone|task_list/task">

       	<tr>
       			<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableContent">
				<xsl:element name="input">
					<xsl:attribute name="type">checkbox</xsl:attribute>
					<xsl:attribute name="name">selected</xsl:attribute>
					<xsl:attribute name="checked">true</xsl:attribute>
					<xsl:attribute name="value">
						<xsl:number count="milestone|summary|task" />_<xsl:value-of select="/task_list/@type"/>
					</xsl:attribute>
				</xsl:element>
			</td>
			<td width="300" class="tableContent"><xsl:value-of select="./name"/></td>
			<td class="tableContent"><xsl:value-of select="./work"/><xsl:text> </xsl:text><xsl:value-of select="./workUnits"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:value-of select="./duration"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:value-of select="format:formatISODate(startDateTime)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:value-of select="format:formatISODate(endDateTime)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:value-of select="format:formatPercent(workPercentComplete)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

         </tr>
 <tr class="tableLine">
 	<td  colspan="9" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:template>



</xsl:stylesheet>
	