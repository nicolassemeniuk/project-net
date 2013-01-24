<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="transition_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="tableHeader" align="left">
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.transitionlist.name.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.transitionlist.description.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.transitionlist.fromstep.column')"/></th>
		<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.transitionlist.tostep.column')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="transition"/>
	<xsl:if test="count(transition)=0"><xsl:call-template name="no_transitions" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="transition">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent">
    <xsl:variable name="mypos" select="position()" />
	<xsl:apply-templates select="transition_id">
	  <xsl:with-param name="pos" select="$mypos" />
    </xsl:apply-templates>
  </td>
  <td class="tableContent">
  	<a href="javascript:transitionModify('{transition_id}');"><xsl:apply-templates select="transition_verb"/></a>
  </td>
  <td class="tableContent"><xsl:apply-templates select="description" /></td>
  <td class="tableContent"><xsl:apply-templates select="begin_step_name" /></td>
  <td class="tableContent"><xsl:apply-templates select="end_step_name" /></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
  <td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_transitions">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="5">
  	<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.transitionlist.create.1.text')"/>
	<a href="javascript:transitionCreate();"><xsl:value-of select="display:get('prm.workflow.transitionlist.create.2.link')"/></a><xsl:value-of select="display:get('prm.workflow.transitionlist.create.3.text')"/>
	</td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="transition_verb"><xsl:apply-templates/></xsl:template>

<xsl:template match="description"><xsl:apply-templates/></xsl:template>

<xsl:template match="transition_id">
<xsl:param name="pos" />
<xsl:choose>
	<xsl:when test="$pos=1">
		<input type="radio" name="transition_id" value="{.}" checked="" />
	</xsl:when>
	<xsl:otherwise>
		<input type="radio" name="transition_id" value="{.}" />
	</xsl:otherwise>
</xsl:choose>
<xsl:text disable-output-escaping="yes">&amp;nbsp</xsl:text>
</xsl:template>

<xsl:template match="end_step_name"><xsl:apply-templates/></xsl:template>

<xsl:template match="begin_step_name"><xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
