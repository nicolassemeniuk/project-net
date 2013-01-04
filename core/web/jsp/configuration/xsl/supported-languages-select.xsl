<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html"/>

<!--
	 
	     $RCSfile$
	    $Revision: 10492 $
	        $Date: 2003-02-07 22:14:14 -0300 (vie, 07 feb 2003) $
	      $Author: matt $
	 
-->

<xsl:template match="*|/">
		<xsl:apply-templates/>
	</xsl:template>

<xsl:template match="text()|@*">
		<xsl:value-of select="."/>
	</xsl:template>

<xsl:template match="SupportedLanguages">
	<select name="supportedLanguages" multiple="" height="3">
		<xsl:apply-templates select="Language" />
	</select>
</xsl:template>

<xsl:template match="Language">
<xsl:element name="option">
	<xsl:attribute name="value"><xsl:value-of select="LanguageCode" /></xsl:attribute>
	<xsl:if test="IsSupported=1">
		<xsl:attribute name="selected" />
	</xsl:if>
	<xsl:value-of select="LanguageName" />
</xsl:element>
</xsl:template>

</xsl:stylesheet>
