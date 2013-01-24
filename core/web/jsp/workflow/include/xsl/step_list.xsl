<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:display="xalan://net.project.base.property.PropertyProvider" 
		extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="step_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader" align="left">
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.steplist.name.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.steplist.description.column')"/></th>
        <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.steplist.sequence.column')"/></th>
        <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.steplist.fromstep.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.steplist.tostep.column')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="8" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="step"/>
	<xsl:if test="count(step)=0"><xsl:call-template name="no_steps" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="step_list/step">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent">
    <xsl:variable name="mypos" select="position()" />
	<xsl:apply-templates select="step_id">
	  <xsl:with-param name="pos" select="$mypos" />
    </xsl:apply-templates>
  </td>
  <td class="tableContent">
    <a href="javascript:stepModify('{step_id}');"><xsl:apply-templates select="name"/></a>
  </td>
  <td class="tableContent"><xsl:apply-templates select="description" /></td>
    <td class="tableContent"><xsl:apply-templates select="step_sequence" /></td>
  <td class="tableContent"><xsl:apply-templates select="begin_transition_count" /></td>
  <td class="tableContent"><xsl:apply-templates select="end_transition_count" /></td>
  <td class="tableContent" align="right"><xsl:call-template name="special_step" /></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
  <td colspan="8" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_steps">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="4">
  	<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.steplist.create.1.text')"/>
	<a href="javascript:stepCreate();"><xsl:value-of select="display:get('prm.workflow.steplist.create.2.link')"/></a><xsl:value-of select="display:get('prm.workflow.steplist.create.3.link')"/>
	</td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

<xsl:template match="description"><xsl:apply-templates/></xsl:template>

<xsl:template name="special_step">
<xsl:choose>
  <xsl:when test="is_initial_step=1">
  	<xsl:element name="img">
		<xsl:attribute name="src">../images/flag_green_small.gif</xsl:attribute>
		<xsl:attribute name="width">17</xsl:attribute>
		<xsl:attribute name="height">15</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.steplist.image.initial.alttext')"/></xsl:attribute>
	</xsl:element>
  </xsl:when>
  <xsl:when test="is_final_step=1">
    <xsl:element name="img">
		<xsl:attribute name="src">../images/flag_checkered_small.gif</xsl:attribute>
		<xsl:attribute name="width">17</xsl:attribute>
		<xsl:attribute name="height">15</xsl:attribute>
		<xsl:attribute name="border">0</xsl:attribute>
		<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.workflow.steplist.image.final.alttext')"/></xsl:attribute>
	</xsl:element>
  </xsl:when>
  <xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="step_id">
<xsl:param name="pos" />
<xsl:choose>
	<xsl:when test="$pos=1">
		<input name="step_id" type="radio" value="{.}" checked="" />
	</xsl:when>
	<xsl:otherwise>
		<input name="step_id" type="radio" value="{.}" />
	</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="begin_transition_count"><xsl:apply-templates/></xsl:template>

<xsl:template match="end_transition_count"><xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
