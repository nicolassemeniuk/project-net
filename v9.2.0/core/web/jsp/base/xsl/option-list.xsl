<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<!-- Build translation properties node list -->
<xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property"/>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content"/>
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="optionlist">
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="option">
    <option value="{./optionvalue}"><xsl:value-of select="./optiontext"/></option>
</xsl:template>

</xsl:stylesheet>
