<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >
<xsl:output method="html"/>

  <xsl:template match="/">
    	<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
      	<xsl:apply-templates/>
	</table>
	<xsl:element name="input">
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="name">SUBMIT2</xsl:attribute>
		<xsl:attribute name="value">Add Link</xsl:attribute>
	</xsl:element>
	<xsl:element name="input">
		<xsl:attribute name="type">hidden</xsl:attribute>
		<xsl:attribute name="name">action</xsl:attribute>
		<xsl:attribute name="value">modify</xsl:attribute>
	</xsl:element>
	<P></P>
  </xsl:template>

  
  <xsl:template match="channel/table_def">
    <tr class="tableHeader" align="center"> 
      <xsl:apply-templates/>
    </tr>
    <tr class="tableLine">
      <td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
    </tr>
  </xsl:template>
  
  <xsl:template match="channel/table_def/col">
    <td align="left" nowrap="true" class="tableHeader">
      <xsl:apply-templates/>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row">
    <tr align="center" class="tableContent"> 
      <xsl:apply-templates/>
    </tr>
    <tr class="tableLine">
      <td  colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
    </tr>
  </xsl:template>

  <xsl:template match="channel/content/row/data">
    <td nowrap="true" align="left" class="tableContent">
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <xsl:template match="channel/content/row/data_href">
      <xsl:variable name="labelTruncated">
          <xsl:choose>
              <xsl:when test="string-length(label) > 40">
                  <xsl:value-of select="display:get('prm.global.textformatter.truncatedstringformat', substring(label,1,40))"/>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:value-of select="label"/>
              </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>

    <td nowrap="true" align="left" class="tableContent">
    	<xsl:element name="input">
    		<xsl:attribute name="type">radio</xsl:attribute>
    		<xsl:attribute name="name">selected</xsl:attribute>
    		<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
	</xsl:element>
	<xsl:value-of select="$labelTruncated"/>
    </td>
  </xsl:template>
  
  <xsl:template match="channel/content/row/img">
    <td nowrap="true" align="left" class="tableContent">
	<xsl:element name="img">
	  <xsl:attribute name="src"><xsl:value-of select="./src"/></xsl:attribute>
	  <xsl:attribute name="width"><xsl:value-of select="./width"/></xsl:attribute>
	  <xsl:attribute name="height"><xsl:value-of select="./height"/></xsl:attribute>
	  <xsl:attribute name="border"><xsl:value-of select="./border"/></xsl:attribute>
	</xsl:element>
    </td>
  </xsl:template>

  <xsl:template match="channel/content/row/data_img">
    <td nowrap="true" align="left" class="tableContent">
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
