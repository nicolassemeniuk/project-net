<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:output method="html"/>

<!--=====================================================================
|
|    $RCSfile$
|   $Revision: 17496 $
|       $Date: 2008-05-23 20:04:45 -0300 (vie, 23 may 2008) $
|     $Author: vivana $
|
| Workflow Designer translates a workflow_menu structure and
| includes radio group for selecting a workflow.
======================================================================-->

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow_list">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr class="tableHeader" align="left">
	<td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	<td class="tableHeader" align="center"><xsl:value-of disable-output-escaping="yes" select="display:get('prm.workflow.forms.menu.current.column')"/></td>
	<td class="tableHeader"><xsl:value-of select="display:get('prm.workflow.forms.menu.name.column')"/></td>
	<td class="tableHeader"><xsl:value-of select="display:get('prm.workflow.forms.menu.description.column')"/></td>
  </tr>
  <tr class="tableLine">
    <td colspan="4" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
  </tr>
  <!-- Insert line items or a message if there are none -->
  <xsl:apply-templates select="workflow"/>
  <xsl:if test="count(workflow)=0"><xsl:call-template name="no_workflows" /></xsl:if>

</table>
</xsl:template>

<xsl:template match="workflow"><tr class="tableContent" align="left"> 
  <td class="tableContent"><xsl:apply-templates select="workflow_id" mode="selectLink" /></td>
  <td class="tableContent" align="center"><xsl:apply-templates select="workflow_id" mode="selectedFlag" /></td>
  <td class="tableContent">
  	<a href="{jsp_root_url}/workflow/Properties.jsp?id={workflow_id}&amp;module=200&amp;action=1"><xsl:value-of select="name"/></a>
  </td>
  <td class="tableContent"><xsl:value-of select="description"/></td>
</tr>
<tr class="tableLine">
  <td colspan="4" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
</tr>
</xsl:template>

<xsl:template match="workflow_id" mode="selectedFlag">
<script>document.write(selectedWorkflowPresentation('<xsl:value-of select="." />'));</script></xsl:template>

<xsl:template name="no_workflows">
<tr class="tableContent" align="left"> 
  <td class="tableContent" colspan="4">
    <xsl:value-of select="display:get('prm.workflow.forms.menu.noworkflow.message')"/>
  </td>
</tr>
</xsl:template>

<xsl:template match="workflow_id" mode="selectLink">
<a href="javascript:selectWorkflow('{.}');"><xsl:value-of select="display:get('prm.workflow.forms.menu.select.link')"/></a>
</xsl:template>

</xsl:stylesheet>
