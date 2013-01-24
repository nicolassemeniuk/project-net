<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:display="xalan://net.project.base.property.PropertyProvider"
        xmlns:format="xalan://net.project.util.XSLFormat"
        extension-element-prefixes="display format" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="envelope">
<table width="100%" border="0" cellpadding="2">
	<tr><xsl:apply-templates select="name"/></tr>
    <tr><xsl:apply-templates select="description"/></tr>
    <tr>
		<xsl:apply-templates select="current_version/status_name" />
		<td rowspan="4" align="right"> 
        	<table width="100%" border="0" cellpadding="2">
            	<tr><xsl:apply-templates select="created_by_full_name"/></tr>
	            <tr><xsl:apply-templates select="created_datetime"/></tr>
            	<tr><xsl:apply-templates select="modified_by_full_name"/></tr>
            	<tr><xsl:apply-templates select="modified_datetime"/></tr>
            	<tr><xsl:apply-templates select="space_name"/></tr>
            </table>
        </td>
	</tr>
    <tr>
    	<xsl:apply-templates select="workflow_name" />
    </tr>
    <tr><xsl:apply-templates select="current_version/priority_name" /></tr>
    <tr><xsl:apply-templates select="strictness_name" /></tr>
    <tr><xsl:apply-templates select="current_version/comments" /></tr>
    <tr><xsl:apply-templates select="current_version/step_name" /></tr>
    <tr><xsl:apply-templates select="current_version/step_notes" /></tr>
</table>
</xsl:template>

<xsl:template match="name">

<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.name.label')"/></td>

<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="description">

<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.description.label')"/></td>

<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="status_name">

<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.status.label')"/></td>

<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="step_name">

<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.currentstep.label')"/></td>

<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="step_notes">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.stepinstructions.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:value-of select="format:formatText(.)" disable-output-escaping="yes"/></td>
</xsl:template>

<xsl:template match="workflow_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.workflow.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="priority_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.priority.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="strictness_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.ruleenforcement.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="comments">
<td class="tableHeader" align="right" valign="top"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.message.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2">
    <xsl:value-of select="format:formatText(.)" disable-output-escaping="yes"/>
 </td>
</xsl:template>

<xsl:template match="created_by_full_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.createdby.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="created_datetime">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.createdon.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent">
    <xsl:value-of select="format:formatISODateTime(.)" disable-output-escaping="yes" />
</td>
</xsl:template>

<xsl:template match="modified_by_full_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.lastmodifiedby.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="modified_datetime">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.lastmodifiedon.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    <td class="tableContent">
        <xsl:value-of select="format:formatISODateTime(.)" disable-output-escaping="yes"/>
    </td>
</xsl:template>

<xsl:template match="space_name">
<td class="tableHeader" align="right"><xsl:value-of select="display:get('prm.workflow.envelope.include.propertiesdetail.spacename.label')"/></td>
<td class="tableHeader" align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

</xsl:stylesheet>
