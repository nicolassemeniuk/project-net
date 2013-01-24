<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="/ProjectXML/Content/schedule"/>
</xsl:template>

<xsl:template match="schedule">
<table width="100%" cellpadding="0" border="0" cellspacing="0">
	<tr class="tableHeader" align="left">
		<td width="1%" align="left" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
      	<td valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.milestones.milestone.column')"/></td>
      	<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.milestones.date.column')"/></th>
      	<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.milestones.percentcomplete.column')"/></th>
		<td width="1%" align="right" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr class="tableLine">
		<td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="task|summary"/>
</table>
</xsl:template>

<xsl:template match="task|summary">
    <xsl:variable name="lateStyle">
        <xsl:if test="StatusNotifiers/Late">color:red</xsl:if>
    </xsl:variable>
	<tr class="tableContent">
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>	
		<td class="tableContent">
			<img src="../images/milestone.gif" height="10" width="10" border="0" />
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<xsl:element name="A">
				<xsl:attribute name="href"><xsl:text disable-output-escaping="yes">../servlet/ScheduleController/TaskView?module=60&amp;action=1&amp;id=</xsl:text><xsl:value-of select="./id"/></xsl:attribute>
				<xsl:value-of select="./name"/>
			</xsl:element>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</td>
		<td class="tableContent" style="{$lateStyle}" valign="top">
            <xsl:value-of select="format:formatISODateTime(endDateTime)"/>
            <xsl:if test="StatusNotifiers/Late">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <xsl:value-of select="display:get('prm.project.milestones.late.decorator')"/>
            </xsl:if>
        </td>
        <td class="tableContent" style="{$lateStyle}">
            <xsl:value-of select="format:formatPercent(workPercentComplete)"/>
        </td>
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
	</tr>
	<tr class="tableLine">
	 	<td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
	