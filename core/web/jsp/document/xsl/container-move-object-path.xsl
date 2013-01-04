<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

  <xsl:template match="/">

     <xsl:for-each select="path/node">

		<xsl:variable name="size" select="../path_size"/>
	        <xsl:variable name="level" select="./level"/>	


			<xsl:variable name="page">
				<xsl:text disable-output-escaping="no"></xsl:text>
			</xsl:variable>

			<xsl:element name="a">
				<xsl:attribute name="href"><xsl:value-of select = "/path/jsp_root_url" />/document/TraverseFolderProcessing.jsp?id=<xsl:value-of select="./object_id"/>&amp;module=10&amp;Page=/document/MoveObjectFolderBrowser.jsp</xsl:attribute>
				<xsl:value-of select="./name"/>
			</xsl:element><xsl:if test='$level != $size'> / </xsl:if>  

     </xsl:for-each>

  </xsl:template>
</xsl:stylesheet>

