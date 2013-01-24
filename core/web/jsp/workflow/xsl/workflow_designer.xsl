<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<!--=====================================================================
|
|    $RCSfile$
|   $Revision: 17900 $
|       $Date: 2008-08-18 07:40:40 -0300 (lun, 18 ago 2008) $
|     $Author: vmalykhin $
|
| Workflow Designer translates a workflow_menu structure and
| includes radio group for selecting a workflow.
======================================================================-->

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow_list">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="tableHeader" align="left">
	<th class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
	<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.name.column')"/></th>
	<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.description.column')"/></th>
	<th class="tableHeader"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.availability.column')"/></th>
  </tr>
  <tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
  </tr>
  <!-- Insert line items or a message if there are none -->
  <xsl:apply-templates select="workflow"/>
  <xsl:if test="count(workflow)=0"><xsl:call-template name="no_workflows" /></xsl:if>

</table>
</xsl:template>

<xsl:template match="workflow"><tr class="tableContent" align="left"> 
  <td class="tableContent">
	  <input id="workflow{name}" name="workflow_id" type="radio" value="{workflow_id}" />
  </td>
  <td class="tableContent">
  	<a href="javascript:modify('{workflow_id}');"><xsl:value-of select="name"/></a>
  </td>
  <td class="tableContent"><xsl:value-of select="description"/></td>
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
