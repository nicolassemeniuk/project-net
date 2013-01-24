<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="fieldContent"><xsl:apply-templates select="is_published"/></td>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
</table>
</xsl:template>

<xsl:template match="is_published">
<xsl:choose>
	<xsl:when test=".=1">
<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.publish.published.message')"/>
</xsl:when>
	<xsl:otherwise>
	<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.publish.notpublished.message')"/>
</xsl:otherwise>
</xsl:choose></xsl:template>

</xsl:stylesheet>
