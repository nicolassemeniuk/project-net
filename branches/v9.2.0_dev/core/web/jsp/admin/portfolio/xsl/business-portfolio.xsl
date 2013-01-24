<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >	
<xsl:output method="html"/>
 <xsl:template match="BusinessPortfolio">
  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="3%" >
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td>
			<td class="tableHeader" width="25%">Business</td>
			<td class="tableHeader" width="25%">Business Type</td>
			<td class="tableHeader" width="20%">Projects</td>
			<td class="tableHeader">People</td>
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
	<xsl:apply-templates select="BusinessSpace"/>
			<xsl:if test="count(BusinessSpace)=0">
            	<xsl:call-template name="NoBusinesses"/>
        	</xsl:if>
	</table>
	</xsl:template>
  <xsl:template match="BusinessPortfolio/BusinessSpace">
     <tr align="left" valign="middle" class="tableContent">
		 <td class="tableContent"><input name="selected" type="radio" value="{businessID}"/></td>
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">../../business/Main.jsp?id=<xsl:value-of select="./businessID"/></xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="./businessType"/></td>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="format:formatNumber(numProjects)"/></td>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="format:formatNumber(numMembers)"/></td>
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
 <xsl:template name="NoBusinesses">
        <tr class="tableContent" align="left">
            <td class="tableContent" colspan="6"> No Such Business found</td>
        </tr>
    </xsl:template>
</xsl:stylesheet>

