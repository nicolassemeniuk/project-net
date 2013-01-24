<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="rule_status">
<xsl:element name="option">
  <xsl:attribute name="value">
  	<xsl:value-of select="rule_status_id" />
  </xsl:attribute>
  <xsl:apply-templates select="name" />
</xsl:element>
</xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
