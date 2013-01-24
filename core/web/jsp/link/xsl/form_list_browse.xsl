<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:java="http://xml.apache.org/xslt/java" 
		xmlns:format="xalan://net.project.util.XSLFormat"
		extension-element-prefixes="display format java" exclude-result-prefixes="java" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="FormMenu">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader">
		<th align="left" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th align="left" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text></th>
		<th align="left" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th align="left" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.name.column')"/></th>
		<th align="left" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.description.column')"/></th>
		<th align="left" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.numberoffields.column')"/></th>
		<th align="left" class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.default.column')"/></th>
	</tr>
	<tr class="tableLine">
	    <td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="FormMenuEntry"/>
</table>
</xsl:template>

<xsl:template match="FormMenuEntry">
<!-- Only if there is at least one FormList (which there should be, of course) -->
<xsl:if test="./FormList">

<tr class="tableContent"> 
	<td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td colspan="6" align="left" class="tableContent">
		<span class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.formname.label')"/></span><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="./name"/> 
		<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
		<span class="tableHeader"><xsl:value-of select="display:get('prm.global.links.formlist.browse.description.label')"/></span><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="./description"/>
	</td>
</tr>
<xsl:apply-templates select="FormList" />
<tr class="tableLine">
      <td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>

</xsl:if></xsl:template>

<xsl:template match="FormList">
<tr>
	<td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td align="left" class="tableContent">
		<input type="radio" name="selected" value="{./id}" />
	</td>
	<td align="left" class="tableContent"><xsl:apply-templates select="name" /></td>
	<td align="left" class="tableContent"><xsl:apply-templates select="description" /></td>
	<td align="left" class="tableContent"><xsl:value-of select="format:formatNumber(field_count)" /></td>
	<td align="left" class="tableContent"><xsl:apply-templates select="is_default" /></td>
</tr>
</xsl:template>

<xsl:template match="is_default">
<xsl:choose>
	<xsl:when test=".='true'"><xsl:value-of select="display:get('prm.global.links.formlist.browse.default.yes.value')"/></xsl:when>
</xsl:choose>
</xsl:template>
</xsl:stylesheet>
