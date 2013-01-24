<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="path_result" />
</xsl:template>

<xsl:template match="path_result">
     <xsl:for-each select="path/node">
		<xsl:variable name="size" select="../path_size"/>
        <xsl:variable name="level" select="./level"/>
		<xsl:element name="a">
			<xsl:attribute name="href"><xsl:value-of select="/path_result/path/jsp_root_url"/>/link/traverseFolder.jsp?module=<xsl:value-of select="/path_result/module"/>&amp;action=<xsl:value-of select="/path_result/action"/>&amp;cid=<xsl:value-of select="./object_id"/>&amp;id=<xsl:value-of select="/path_result/return_id"/>&amp;type_pick=<xsl:value-of select="/path_result/type_pick"/>&amp;search_level=<xsl:value-of select="/path_result/search_level"/></xsl:attribute> 
			<xsl:value-of select="./name"/>
		</xsl:element>
		<xsl:if test='$level != $size'> / </xsl:if>  
     </xsl:for-each>
</xsl:template>
</xsl:stylesheet>

