<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

  <xsl:template match="/">

     <xsl:for-each select="path/node">

		<xsl:variable name="size" select="../path_size"/>
	        <xsl:variable name="level" select="./level"/>	

		<a href="{/path/jsp_root_url}/document/TraverseFolderProcessing.jsp?id={./object_id}&amp;module=10"><xsl:value-of select="./name"/></a>
		<xsl:if test='$level != $size'> / </xsl:if>  

     </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>

