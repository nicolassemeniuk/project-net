<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="strictness_list">
	<xsl:apply-templates select="strictness" />
</xsl:template>

<xsl:template match="strictness">
	<xsl:element name="option">
		<xsl:attribute name="value">
			<xsl:value-of select="strictness_id"/>
		</xsl:attribute>
		<xsl:value-of select="name"/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>
