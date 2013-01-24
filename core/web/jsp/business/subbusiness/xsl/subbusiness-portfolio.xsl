<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"			
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/>


<xsl:template match="BusinessPortfolio">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr align="left" class="tableHeader">	
			<td nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.business.subbusiness.business.label')"/></td>
			<td nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.business.subbusiness.businesstype.label')"/></td>
			<td nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.business.subbusiness.projects.label')"/></td>
			<td nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.business.subbusiness.people.label')"/></td>
		</tr>
		<tr class="tableLine"><td colspan="5" ><img src="../images/spacers/trans.gif" width="1" height="2" border="0"/></td></tr>
		<xsl:apply-templates select="BusinessSpace"/>
	</table>
</xsl:template>


<xsl:template match="BusinessPortfolio/BusinessSpace">
		<tr align="center" class="tableContent"> 
			<td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td align="left" class="tableContent">
				<xsl:element name="a">
					<xsl:attribute name="href">../Main.jsp?id=<xsl:value-of select="businessID"/></xsl:attribute>
					<xsl:value-of select="name"/>
				</xsl:element>
		  	</td>
			
		  <td align="left" class="tableContent">
			<xsl:value-of select="businessType"/>
		  </td>
		  
	       <td class="tableContent" align="left">
			<xsl:value-of select="format:formatNumber(numProjects)"/>
		   </td>
	
	       <td class="tableContent" align="left">
			<xsl:value-of select="format:formatNumber(numMembers)"/>
		   </td>
	
		</tr>  
		<tr class="tableLine">
			<td colspan="5">
				<xsl:element name="img">
					<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">1</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>
</xsl:template>

</xsl:stylesheet>
