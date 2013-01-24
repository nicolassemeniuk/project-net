<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
 <xsl:template match="FormMenu">
  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="3%" >
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td>
			<td class="tableHeader" width="30%">Form Name</td>
			<td class="tableHeader" width="30%">WorkSpace Name</td>
			<td colspan="2" class="tableHeader">WorkSpace Type</td>
		</tr>
		<tr class="tableLine">

			<td colspan="8" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
	<xsl:apply-templates select="FormMenuEntry">
		<xsl:sort select="./Space/name" /> 
	</xsl:apply-templates>
	<xsl:if test="count(FormMenuEntry)=0">
            		<xsl:call-template name="NoForms"/>
        	</xsl:if>
	</table>
	</xsl:template>
  <xsl:template match="FormMenu/FormMenuEntry">
     <tr align="left" valign="middle" class="tableContent">
		 <td class="tableContent"><input name="selected" type="radio" value="{id}"/></td>
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">../../form/designer/DefinitionEdit.jsp?id=<xsl:value-of select="./id"/><xsl:text>&amp;module=30&amp;action=2&amp;spaceID=</xsl:text><xsl:value-of select="./Space/id"/></xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./Space/name"/></td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./Space/SpaceType/name"/></td>
		</tr>
		<tr class="tableLine">
		<td colspan="8">
			<xsl:element name="img">
				<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
		</tr>
  </xsl:template>
 <xsl:template name="NoForms">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> No Such Form found</td>
        </tr>
    </xsl:template>
</xsl:stylesheet>

