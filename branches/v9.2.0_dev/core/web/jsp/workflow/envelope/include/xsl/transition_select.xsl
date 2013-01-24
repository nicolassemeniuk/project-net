<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        extension-element-prefixes="display" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="transition_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="transition"/>
	<xsl:if test="count(transition)=0"><xsl:call-template name="no_transitions" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="transition">
<tr class="tableContent" align="left">
	<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td class="tableContent">
		<a href="javascript:performTransition('{./transition_id}')"><xsl:value-of select="./transition_verb" /></a>
	</td>
	<td class="tableContent">
		<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		<xsl:value-of select="display:get('prm.workflow.envelope.include.transitionselect.to.label')"/> <xsl:value-of select="end_step_name" />
	</td>
	<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
</xsl:template>

<xsl:template name="no_transitions">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent"><xsl:value-of select="display:get('prm.workflow.envelope.include.transitionselect.noneavailable.error.message')"/></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

</xsl:stylesheet>
