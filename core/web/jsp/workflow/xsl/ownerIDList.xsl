<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="person">
	<xsl:element name="option">
		<xsl:attribute name="value">
			<xsl:value-of select="person_id"/>
		</xsl:attribute>
		<xsl:value-of select="full_name"/>
	</xsl:element>
</xsl:template>

<xsl:template match="roster"><xsl:apply-templates select="person"/></xsl:template>

</xsl:stylesheet>
