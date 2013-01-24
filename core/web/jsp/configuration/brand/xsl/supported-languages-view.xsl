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

<xsl:template match="SupportedLanguages"><xsl:apply-templates select="Language[IsSupported=1]" />
</xsl:template>

<xsl:template match="Language">
	<xsl:value-of select="LanguageName" />
	<xsl:if test="position()!=last()">,<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:if>
</xsl:template>

</xsl:stylesheet>
