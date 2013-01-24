<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="step_group_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader" align="left">
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.name.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.participant.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.notified.column')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="step_group">
	    <xsl:sort select="entry_type" order="ascending" />
	    <xsl:sort select="name" order="ascending" />
	    <xsl:sort select="description" order="ascending" />
	</xsl:apply-templates>
	<xsl:if test="count(step_group)=0"><xsl:call-template name="no_groups" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="step_group">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent"><xsl:apply-templates select="entry_type" /></td>
  <td class="tableContent"><xsl:call-template name="entryName" /></td>
  <td class="tableContent"><xsl:apply-templates select="is_participant" /></td>
  <td class="tableContent"><xsl:apply-templates select="is_notified" /></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
  <td colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_groups">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="4">
  	<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.stepedit.roles.add.1.text')"/>
	<a href="javascript:stepGroupModify();"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.add.2.link')"/></a><xsl:value-of select="display:get('prm.workflow.stepedit.roles.add.3.text')"/>
  </td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="description"><xsl:apply-templates/></xsl:template>

<xsl:template match="entry_type">
<xsl:choose>
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

<xsl:template match="is_participant">
<xsl:choose>
  <xsl:when test=".=1"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.option.yes.name')"/></xsl:when>
  <xsl:when test=".=0"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.option.no.name')"/></xsl:when>
</xsl:choose>
</xsl:template>

<xsl:template match="is_notified">
<xsl:choose>
  <xsl:when test=".=1"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.option.yes.name')"/></xsl:when>
  <xsl:when test=".=0"><xsl:value-of select="display:get('prm.workflow.stepedit.roles.option.no.name')"/></xsl:when>
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
