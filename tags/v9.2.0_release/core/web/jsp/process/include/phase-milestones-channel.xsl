<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
<xsl:output method="html" encoding="UTF-8"/>
	
	<xsl:variable name="translation" 
    	select="/ProjectXML/Properties/Translation/property" />
   	<xsl:variable name="refLink" select="$translation[@name='refLink']" />
	<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

	<xsl:template match="ProjectXML">
    	<xsl:apply-templates select="Content" />
	</xsl:template>

	<xsl:template match="Content">
    	<xsl:apply-templates />
	</xsl:template>
	
<xsl:template match="schedule"> 
 
<table width="100%" cellpadding="0" border="0" cellspacing="0">
	<tr class="tableHeader" align="left">
		<td width="1%" align="left" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
      	<td valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.milestones.milestone.column')"/></td>
      	<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.milestones.date.column')"/></th>
		<td width="1%" align="right" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr class="tableLine">
		<td  colspan="3" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="task|summary" />
</table>
</xsl:template>

<xsl:template match="task|summary|milestone">
	<tr class="tableContent">
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>	
		<td class="tableContent">
			<img src="../images/milestone.gif" height="10" width="10" border="0" />
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			<xsl:element name="A">
				<a href="{$JSPRootURL}/servlet/ScheduleController/TaskView?module=60&amp;action=1&amp;id={id}&amp;{$refLink}">
				<xsl:text disable-output-escaping="yes"></xsl:text>
				<xsl:value-of select="./name"/>
				</a>
			</xsl:element>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
			
		</td>
		<td class="tableContent" valign="top"><xsl:value-of select="startDateTime"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>	
	</tr>
	<tr class="tableLine">
	 	<td colspan="3" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
	