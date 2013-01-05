<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
	<xsl:output method="html" encoding="UTF-8"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="TaskComment">
		<tr class="tableLine">
			<td colspan="6" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
			</td>
		</tr>
		<tr class="tableContent">
			<xsl:variable name="dateTime" select="format:formatISODate(CreatedDatetime)" />
			<td>
				<xsl:value-of select="display:get('prm.schedule.task.commentsview.createdby.message', $dateTime, ./CreatedByDisplayName)" /><br/>
                <xsl:value-of select="format:formatText(Text)" disable-output-escaping="yes"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="ProjectXML">
		<xsl:apply-templates select="Content"/>
	</xsl:template>
	<xsl:template match="Content">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="TaskComments">
		<table width="100%" cellpadding="0" border="0" cellspacing="0">
			<xsl:apply-templates select="TaskComment"/>
		</table>
	</xsl:template>
</xsl:stylesheet>
