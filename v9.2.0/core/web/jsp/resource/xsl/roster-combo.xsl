<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="roster">
    <select name="person_id" onChange="javascript:comboChange()">
      <xsl:apply-templates select="person"/>			    
    </select>
  </xsl:template>
  
  <xsl:template match="roster/person">
    <xsl:element name="option">
      <xsl:attribute name="value"><xsl:value-of select="./person_id"/></xsl:attribute>
      <xsl:value-of select="./full_name"/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
