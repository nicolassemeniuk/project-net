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
      	<td valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.tasks.task.column')"/></td>
      	<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.tasks.work.column')"/></th>
		<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.tasks.start.column')"/></th>
		<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.tasks.end.column')"/></th>
		<th valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.tasks.complete.column')"/></th>
		<td width="1%" align="right" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr class="tableLine">
		<td  colspan="7" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="task|summary" />
</table>
</xsl:template>

<xsl:template match="task|summary|milestone">	
	<tr class="tableContent">
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>	
		<td class="tableContent">
			<xsl:element name="A">
				<a href="{$JSPRootURL}/servlet/ScheduleController/TaskView?module=60&amp;action=1&amp;id={id}&amp;{$refLink}">				
				<xsl:text disable-output-escaping="yes"></xsl:text>
				<xsl:value-of select="./name"/>
				</a>
			</xsl:element>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
		</td>
		<td class="tableContent"><xsl:value-of select="format:formatNumber(work)"/><xsl:text> </xsl:text><xsl:value-of select="./workUnits"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
		<td class="tableContent"><xsl:value-of select="startDateTime"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
		<td class="tableContent"><xsl:value-of select="endDateTime"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
		<td class="tableContent">
            <xsl:variable name="percentComplete">
                <xsl:choose>
                    <xsl:when test="isMilestone = 1 and ./work = 0">
                        <xsl:value-of select="format:formatPercent(percentComplete)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="format:formatPercent(workPercentComplete)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$percentComplete"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </td>
		<td class="tableContent" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>	
	</tr>
	<tr class="tableLine">
	 	<td colspan="7" class="tableLine"><img src="{$JSPRootURL}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
	