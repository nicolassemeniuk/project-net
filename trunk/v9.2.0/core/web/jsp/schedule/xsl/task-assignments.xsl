<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
				
	<xsl:variable name="translation" 
    	select="/ProjectXML/Properties/Translation/property" />
	<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

	<xsl:template match="/">
    	<xsl:apply-templates />	
	</xsl:template>

	<xsl:template match="ProjectXML">
    	<xsl:apply-templates select="Content" />
	</xsl:template>

	<xsl:template match="Content">
    	<xsl:apply-templates />
	</xsl:template>

	<xsl:output method="html" encoding="UTF-8"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="assignment">
		<xsl:variable name="modified_href">  
			<xsl:value-of select="href"/>
			<xsl:text disable-output-escaping="yes">&amp;</xsl:text>
			<xsl:value-of select="$translation[@name='refLink']" />
		</xsl:variable>  
	
		<tr class="tableContent">
			<td class="tableContent">
				<a href="{$modified_href}"><xsl:value-of select="./object_name"/></a>
			</td>
			<td class="tableContent">
				<xsl:value-of select="./space_name"/>
			</td>
			<td class="tableContent">
				<xsl:value-of select="./status"/>
			</td>
			<td class="tableContent">
				<xsl:value-of select="format:formatPercent(percent_assigned)"/>
			</td>
			<td class="tableContent">
				<xsl:value-of select="./role"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
			<td class="tableContent">
				<xsl:value-of select="format:formatISODate(due_date)"/>
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
		</tr>
		<tr class="tableLine">
			<td colspan="6" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="ProjectXML">
		<xsl:apply-templates select="Content"/>
	</xsl:template>
	<xsl:template match="Content">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="assignment_list">
		<table width="100%" cellpadding="0" border="0" cellspacing="0">
			<tr class="tableHeader" align="left">
				<th nowrap="1" width="24%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.task.label')"/></th>
				<th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.space.label')"/></th>
				<th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.status.label')"/></th>
				<th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.percent.label')"/></th>
				<th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.role.label')"/></th>
				<th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.dueon.label')"/></th>
			</tr>
			<tr class="tableLine">
				<td colspan="6" class="tableLine">
					<img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/>
				</td>
			</tr>
			<xsl:apply-templates select="assignment"/>
		</table>
	</xsl:template>
</xsl:stylesheet>

