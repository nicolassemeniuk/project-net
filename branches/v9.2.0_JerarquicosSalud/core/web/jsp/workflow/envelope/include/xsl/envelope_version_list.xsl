<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format">

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="envelope_version_list">
<table align="left" border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr class="tableHeader" align="left">
        <th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
        <th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
        <th class="tableHeader" width="40%"><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.comments.label')"/></th>
        <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.by.label')"/></th>
        <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.on.label')"/></th>
        <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.transitiontostep.label')"/></th>
		<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	</tr>
	<tr class="tableLine">
		<td colspan="6" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<!-- Insert envelop line items or a message if there are none -->
	<xsl:apply-templates select="envelope_version"/>
	<xsl:if test="count(envelope_version)=0"><xsl:call-template name="no_envelope_versions" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="envelope_version">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" valign="top"><xsl:value-of select="position()" /></td>
  <td class="tableContent" valign="top"><xsl:apply-templates select="comments" /></td>
  <td class="tableContent" valign="top"><xsl:apply-templates select="created_by_full_name"/></td>
  <td class="tableContent" valign="top"><xsl:apply-templates select="created_datetime" /></td>
  <td class="tableContent" valign="top"><xsl:apply-templates select="transition_verb"/><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.colon.separator')"/><xsl:apply-templates select="step_name" /></td>
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr>
<tr class="tableLine">
  <td colspan="6" class="tableLine"><img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr></xsl:template>

<xsl:template name="no_envelope_versions">
<tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
  <td class="tableContent" colspan="5"><xsl:value-of select="display:get('prm.workflow.envelope.include.versionlist.noversions.error.message')"/></td>

  <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
</tr></xsl:template>

<xsl:template match="transition_verb">
<xsl:choose>
	<xsl:when test=".=''">(none)</xsl:when>
	<xsl:otherwise><xsl:value-of select="." /></xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="comments"><xsl:value-of select="format:formatText(.)" disable-output-escaping="yes" /></xsl:template>

<xsl:template match="created_by_full_name"><xsl:apply-templates/></xsl:template>

<xsl:template match="created_datetime"><xsl:apply-templates/></xsl:template>

<xsl:template match="step_name"><xsl:apply-templates/></xsl:template>

</xsl:stylesheet>
