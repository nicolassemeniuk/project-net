<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>
<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="Form">
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr class="tableHeader"> 
		<th nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th align="left" nowrap="true" class="tableHeader">List Name</th>
		<th align="left" nowrap="true" class="tableHeader">Description</th>
		<th align="left" nowrap="true" width="55" class="tableHeader"># Fields</th>
		<th align="left" nowrap="true" width="55" class="tableHeader">Shared</th>
		<th align="left" nowrap="true" width="55" class="tableHeader">Default</th>
	</tr>
	<tr class="tableLine">
	      <td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
		<xsl:apply-templates select="FormList"/>
	</table>
</xsl:template>

<xsl:template match="Form/FormList">
	<tr class="tableContent" align="left"> 
		<td align="center" class="tableContent"> 
			<xsl:element name="input">
				<xsl:attribute name="type">radio</xsl:attribute>
				<xsl:attribute name="name">selected</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
			</xsl:element>
		</td>
		<td align="left" class="tableContent">
			<xsl:element name="a">
				<xsl:attribute name="href">javascript:modify('<xsl:value-of select="./id"/>');</xsl:attribute>
				<xsl:value-of select="./name"/>
			</xsl:element>
		</td>
		<td align="left" class="tableContent"><xsl:value-of select="./description"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="center" class="tableContent"><xsl:value-of select="format:formatNumber(field_count)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="center" class="tableContent"><xsl:value-of select="./is_shared"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td align="center" class="tableContent"><xsl:value-of select="./is_default"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr class="tableLine">
	      <td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
