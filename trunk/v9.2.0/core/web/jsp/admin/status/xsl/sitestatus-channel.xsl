<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="/">
    <table border="0" align="left" width="100%" cellpadding="0" cellspacing="0">
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  
  <xsl:template match="channel/table_def">
    <tr class="tableHeader" align="left"> 
      <xsl:apply-templates/>
    </tr>
    <tr class="tableLine">
        <td colspan="5"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
    </tr>
  </xsl:template>
  
  <xsl:template match="channel/table_def/col">
    <td nowrap="true" class="tableHeader">
      <xsl:apply-templates/>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row">
    <tr align="left"> 
      <xsl:apply-templates/>
    </tr>
    <tr class="tableLine">
      <td colspan="5"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
    </tr>
  </xsl:template>

  <xsl:template match="channel/content/row/data">
    <td nowrap="1" class="tableContent">
      <xsl:apply-templates/>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row/data_href">
    <td nowrap="1" class="tableContent">
	<xsl:element name="a">
	  <xsl:attribute name="href"><xsl:value-of select="./href"/></xsl:attribute>
	  <xsl:value-of select="./label"/>
	</xsl:element>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row/img">
    <td nowrap="1" class="tableContent">
	<xsl:element name="img">
	  <xsl:attribute name="src"><xsl:value-of select="./src"/></xsl:attribute>
	  <xsl:attribute name="width"><xsl:value-of select="./width"/></xsl:attribute>
	  <xsl:attribute name="height"><xsl:value-of select="./height"/></xsl:attribute>
	  <xsl:attribute name="border"><xsl:value-of select="./border"/></xsl:attribute>
	</xsl:element>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row/data_img">
    <td nowrap="1" class="tableContent">
	<xsl:element name="img">
	  <xsl:attribute name="src"><xsl:value-of select="./src"/></xsl:attribute>
	  <xsl:attribute name="width"><xsl:value-of select="./width"/></xsl:attribute>
	  <xsl:attribute name="height"><xsl:value-of select="./height"/></xsl:attribute>
	  <xsl:attribute name="border"><xsl:value-of select="./border"/></xsl:attribute>
	</xsl:element>
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	<xsl:value-of select="./label"/>
    </td>
  </xsl:template>

</xsl:stylesheet>



