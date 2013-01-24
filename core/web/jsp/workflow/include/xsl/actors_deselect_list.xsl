<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:display="xalan://net.project.base.property.PropertyProvider" 
	extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="entry_list">
<xsl:comment>Deselect List Table</xsl:comment>
<!-- Insert a surroudning table so we can fill up the entire space with a box, but still
     keep the "Name" row at the top when there is only one row -->
<table border="1" cellspacing="0" cellpadding="0" width="100%" height="100%" frame="box" rules="none" bordercolor="#333399">
<tr><td valign="top">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr>
    <th class="tableHeader" align="left">
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	</th>
    <th class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.deselect.name.column')"/></th>
    <th class="tableHeader" align="left">
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	</th>
  </tr>
  <tr valign="top"  class="tableLine">
    <td colspan="3" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
  </tr>
  <xsl:apply-templates select="entry">
    <xsl:sort select="entry_type" order="ascending" />
    <xsl:sort select="name" order="ascending" />
	<xsl:sort select="description" order="ascending" />
  </xsl:apply-templates>
</table>
</td></tr>
</table>
<xsl:comment>End of Deselect List Table</xsl:comment>
</xsl:template>

<xsl:template match="entry"><xsl:if test="is_selected=0">
<tr valign="top" class="tableContent">
  <td>
    <xsl:apply-templates select="entry_type" />
  </td>
  <td>
    <xsl:call-template name="entryName" />
  </td>
  <td align="left">
    <xsl:element name="input">
	  <xsl:attribute name="type">checkbox</xsl:attribute>
	  <xsl:attribute name="id">sel_<xsl:value-of select="name" /></xsl:attribute>
	  <xsl:attribute name="name">sel</xsl:attribute>
	  <xsl:attribute name="value"><xsl:value-of select="group_id" /></xsl:attribute>
    </xsl:element>
  </td>
</tr>
<tr valign="top" class="tableLine">
  <td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:if></xsl:template>

<xsl:template match="entry_type"><xsl:choose>
  <xsl:when test=".='group'">
	<xsl:element name="img">
		<xsl:attribute name="src">../images/group_group_small.gif</xsl:attribute>
		<xsl:attribute name="width">16</xsl:attribute>
		<xsl:attribute name="height">16</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.image.role.alttext')"/></xsl:attribute>
	</xsl:element>
  </xsl:when>
  <xsl:when test=".='person'">
   	<xsl:element name="img">
		<xsl:attribute name="src">../images/group_person_small.gif</xsl:attribute>
		<xsl:attribute name="width">16</xsl:attribute>
		<xsl:attribute name="height">16</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.image.person.alttext')"/></xsl:attribute>
	</xsl:element>
  </xsl:when>
</xsl:choose>
</xsl:template>

<xsl:template name="entryName">
<xsl:choose>
  <!-- Use the name if there is one -->
  <xsl:when test="name"><xsl:value-of select="name" /></xsl:when>
  <xsl:otherwise><xsl:value-of select="description" /></xsl:otherwise>
</xsl:choose>
</xsl:template>



</xsl:stylesheet>
