<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates select="ProjectXML" />
</xsl:template>

<xsl:template match="ProjectXML">
	<xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
	<xsl:apply-templates select="UserDomainCollection" />
</xsl:template>

<xsl:template match="UserDomainCollection">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<xsl:apply-templates select="UserDomain" />
	</table>
</xsl:template>

<xsl:template match="UserDomain">
	<tr class="tableContent">
		<td width="10%"><input type="radio" name="domainID" value="{id}" /></td>
		<td class="tableHeader"><xsl:value-of select="name" /></td>
	</tr>
</xsl:template>

</xsl:stylesheet>

