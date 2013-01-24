<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" 
	extension-element-prefixes="display format" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow_list">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="tableHeader" align="left">
    <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.name.column')"/></th>
    <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.description.column')"/></th>
    <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.active.column')"/></th>
    <th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.availability.column')"/></th>
  </tr>
  <tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
  </tr>
  <xsl:apply-templates select="workflow"/>
  <xsl:if test="count(workflow)=0"><xsl:call-template name="no_workflows" /></xsl:if>
</table>
</xsl:template>

<xsl:template match="workflow">	<tr class="tableContent" align="left"> 
		<td class="tableContent">
			<a href="{../jsp_root_url}/workflow/Properties.jsp?id={workflow_id}&amp;module=200&amp;action=1"><xsl:value-of select="name"/></a>
		</td>
		<td class="tableContent"><xsl:value-of select="description"/></td>
		<td class="tableContent"><xsl:apply-templates select="active_envelope_count" /></td>
		<td class="tableContent"><xsl:apply-templates select="is_published" /></td>
	</tr>
	<tr class="tableLine">
	      <td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

<xsl:template match="is_published"><xsl:choose>
<xsl:when test="boolean(number(.))">
<xsl:value-of select="display:get('prm.workflow.main.workflowlist.status.published.name')"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="display:get('prm.workflow.main.workflowlist.status.unpublished.name')"/>
</xsl:otherwise>
</xsl:choose></xsl:template>

<xsl:template match="description"><xsl:apply-templates/></xsl:template>

<xsl:template match="active_envelope_count"><xsl:apply-templates/></xsl:template>

<xsl:template match="name"><xsl:apply-templates/></xsl:template>

<xsl:template name="no_workflows">
<tr class="tableContent" align="left"> 
  <td class="tableContent" colspan="4">
  	<xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.main.workflowlist.createworkflow.1.text')"/>
	<a href="javascript:create()"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.createworkflow.2.link')"/></a><xsl:value-of select="display:get('prm.workflow.main.workflowlist.createworkflow.3.text')"/>
</td>
</tr>
</xsl:template>

</xsl:stylesheet>
