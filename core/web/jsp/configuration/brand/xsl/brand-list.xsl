<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="brand_collection">
	<xsl:apply-templates select="./brand" />
</xsl:template>

<xsl:template match="brand" name="brand">
 <tr>
		<td class="tableContent"><a href="BrandTokenEdit.jsp?brandID={brand_id}&amp;language={default_language}&amp;suppressTokens=true"><xsl:value-of select="name" /></a></td>
		<td class="tableContent"><xsl:value-of select="abbreviation" /></td>
		<td class="tableContent"><xsl:value-of select="default_language" /></td>
</tr>
</xsl:template>

</xsl:stylesheet>
