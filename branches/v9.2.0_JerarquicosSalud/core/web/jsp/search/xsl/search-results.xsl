<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="/"><xsl:apply-templates/>  </xsl:template>

<xsl:template match="channel/table_def"><tr class="tableHeader" align="center"> 
	<xsl:apply-templates />
</tr></xsl:template>

<xsl:template match="channel/table_def/col"><td align="left" nowrap="true" class="tableHeader">
	<xsl:apply-templates />
</td>
  </xsl:template>

<xsl:template match="channel/content/row">
    <tr align="center" class="tableContent"> 
      <xsl:apply-templates/>
    </tr>
    <tr class="tableLine">
      <td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
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
	<xsl:element name="A">
	  <xsl:attribute name="href"><xsl:value-of select="./href"/></xsl:attribute>
	  <xsl:value-of select="$labelTruncated"/>
	</xsl:element>
    </td>
  </xsl:template>

<xsl:template match="channel/content/row/img">
    <td nowrap="true" align="left" class="tableContent">
	<xsl:element name="img">
	  <xsl:attribute name="src">..<xsl:value-of select="./src"/></xsl:attribute>
	  <xsl:attribute name="width"><xsl:value-of select="./width"/></xsl:attribute>
	  <xsl:attribute name="height"><xsl:value-of select="./height"/></xsl:attribute>
	  <xsl:attribute name="border"><xsl:value-of select="./border"/></xsl:attribute>
	</xsl:element>
    </td>
  </xsl:template>

<xsl:template match="channel/content/row/data_img">
    <td nowrap="true" align="left" class="tableContent">
	<xsl:element name="img">
	  <xsl:attribute name="src">..<xsl:value-of select="./src"/></xsl:attribute>
	  <xsl:attribute name="width"><xsl:value-of select="./width"/></xsl:attribute>
	  <xsl:attribute name="height"><xsl:value-of select="./height"/></xsl:attribute>
	  <xsl:attribute name="border"><xsl:value-of select="./border"/></xsl:attribute>
	</xsl:element>
	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	<xsl:value-of select="./label"/>
    </td>
  </xsl:template>

<xsl:template match="SearchResults"><table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
	<xsl:apply-templates select="//channel" />
</table>
</xsl:template>

<xsl:template match="channel"><xsl:apply-templates select="table_def" />
<tr class="tableLine">
	<td colspan="100" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
</tr>
<xsl:apply-templates select="content" />
</xsl:template>

<xsl:template match="content"><xsl:apply-templates /></xsl:template>

</xsl:stylesheet>
