<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >
<xsl:output method="html" />

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates/>
</xsl:template>

  <xsl:template match="UserDomainCollection">

  	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="3%" ></td>
			<td class="tableHeader" >Domain Name</td>
			<td class="tableHeader" >Description</td>
			<td class="tableHeader" >Directory Provider Type</td>
			<td class="tableHeader">Users</td>
		</tr>
		<tr class="tableLine">

			<td colspan="5" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
			<xsl:apply-templates select="UserDomain"/>
	</table>
	</xsl:template>

	<xsl:template match="UserDomain">
		
		<tr align="left" valign="middle" class="tableContent">
		
		 <td class="tableContent"><input name="selected" type="radio" value="{id}"/></td>
			
			<td class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">DomainProperties.jsp?selected=<xsl:value-of select="id"/><xsl:text disable-output-escaping="yes">&amp;</xsl:text>module=240</xsl:attribute>
					<xsl:value-of select="./name"/>
				</xsl:element>
			</td>
            
			<td class="tableContent">
				<xsl:value-of select="description"/>
			</td>

			<td class="tableContent">
				<xsl:value-of select="directoryProviderTypeName"/>
			</td>

			<td class="tableContent">
				<xsl:value-of select="format:formatNumber(userCount)"/>
			</td>
         </tr>

	<tr class="tableLine">
		<td colspan="5">
			<xsl:element name="img">
				<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>
	</xsl:template>


</xsl:stylesheet>

