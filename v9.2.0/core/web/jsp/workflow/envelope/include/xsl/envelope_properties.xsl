<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="envelope">
<table width="100%" border="0" cellpadding="2" cellspacing="0">
	<tr><xsl:apply-templates select="name"/><xsl:apply-templates select="current_version/priority_name" /></tr>
	<tr><xsl:apply-templates select="description"/><xsl:apply-templates select="current_version/status_name" /></tr>
    <tr><xsl:apply-templates select="current_version/comments" /></tr>
    <!--<tr><xsl:apply-templates select="current_version/step_name" /></tr>-->
	<tr><td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
    <tr><xsl:apply-templates select="current_version/step_notes" /></tr>
	<tr><td colspan="2"></td></tr>
</table>
</xsl:template>

<xsl:template match="name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.name.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="description">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.description.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="status_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.status.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>
<xsl:template match="step_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.step.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>
<xsl:template match="step_notes">
<td class="tableHeader" align="right" valign="top" style="font-size: 16"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.instructions.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" rowspan="2" valign="top" style="font-size: 16"><xsl:value-of select="format:formatText(.)" disable-output-escaping="yes" /></td>
</xsl:template>

<xsl:template match="priority_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.priority.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="comments">
<td class="tableHeader" align="right" valign="top"><xsl:value-of select="display:get('prm.workflow.envelope.include.properties.message.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:value-of select="format:formatText(.)" disable-output-escaping="yes" /></td>
</xsl:template>

</xsl:stylesheet>
