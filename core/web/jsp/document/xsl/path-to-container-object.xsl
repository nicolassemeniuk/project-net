<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

  <xsl:template match="/">

     <xsl:for-each select="path/node">

		<xsl:variable name="size" select="../path_size"/>
	        <xsl:variable name="level" select="./level"/>	

		<xsl:value-of select="./name"/><xsl:if test='$level != $size'> / </xsl:if>  

     </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>

