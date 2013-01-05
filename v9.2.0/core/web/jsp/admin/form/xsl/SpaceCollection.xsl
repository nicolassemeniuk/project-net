<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
 <xsl:template match="SpaceList">
  	<table cellpadding="0" cellspacing="0" border="0" width="90%">
		<tr class="tableHeader" align="left" valign="top">
			<td class="tableHeader" width="8%" >
                				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            			</td>
			<td class="tableHeader" width="40%">WorkSpace Name</td>
			<td colspan="2" class="tableHeader">WorkSpace Type</td>
		</tr>
		<tr class="tableLine">
			<td colspan="8" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
	<xsl:apply-templates select="Space">
		<xsl:sort select="name" /> 
	</xsl:apply-templates>
		<xsl:if test="count(Space)=0">
            			<xsl:call-template name="NoSpaces"/>
        		</xsl:if>
	</table>
  </xsl:template>
  <xsl:template match="SpaceList/Space">
		      <tr align="left" valign="middle" class="tableContent">
     			<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
				<input name="selected"  type="checkbox" value="{id}"/></td>	
    			<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="name"/></td>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./SpaceType/name"/></td>
                		</tr>
		<tr class="tableLine">
		<td colspan="8">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
 		</tr>
  </xsl:template>
 <xsl:template name="NoSpaces">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> No Such Space found</td>
        </tr>
    </xsl:template>
  </xsl:stylesheet>

