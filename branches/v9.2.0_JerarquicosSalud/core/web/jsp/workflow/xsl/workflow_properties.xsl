<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<!--====================================================================
|
|    $RCSfile$
|   $Revision: 15473 $
|       $Date: 2006-09-21 20:07:08 -0300 (jue, 21 sep 2006) $
|     $Author: carlos $
|
| Transforms a single workflow structure into its properties sheet
+========================================================================
-->

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="workflow">
      <table width="100%" border="0" cellpadding="2">
       <tr> 
		  <xsl:apply-templates select="name"/>
        </tr>
        <tr> 
  		  <xsl:apply-templates select="description"/>
        </tr>
        <tr> 
		  <xsl:apply-templates select="notes"/>
        </tr>
        <tr> 
	   	  <xsl:apply-templates select="owner_full_name"/>
          <td rowspan="4" align="left"> 
            <table width="100%" border="0" cellpadding="2">
              <tr> 
	  		    <xsl:apply-templates select="created_by_full_name"/>
              </tr>
              <tr> 
			    <xsl:apply-templates select="created_datetime"/>
              </tr>
              <tr> 
                <xsl:apply-templates select="modified_by_full_name"/>
              </tr>
              <tr> 
	            <xsl:apply-templates select="modified_datetime"/>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
		  <xsl:apply-templates select="is_published"/>
        </tr>
        <tr> 
  	      <xsl:apply-templates select="strictness_name"/>
        </tr>
        <tr> 
  	      <xsl:apply-templates select="active_envelope_count"/>
        </tr>
      </table>
</xsl:template>

<xsl:template match="name">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('')"/><xsl:value-of select="display:get('prm.workflow.properties.name.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="description">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.description.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="notes">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.notes.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent" colspan="2"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="owner_full_name">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.owner.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="created_by_full_name">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.createdby.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="created_datetime">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.createdon.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="active_envelope_count">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.activeenvelopes.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="modified_by_full_name">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.lastmodifiedby.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="is_published">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.availability.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<xsl:choose>
<xsl:when test="boolean(number(.))">
<td class="tableContent"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.status.published.name')"/></td>
</xsl:when>
<xsl:otherwise>
<td class="tableContent"><xsl:value-of select="display:get('prm.workflow.main.workflowlist.status.unpublished.name')"/></td>
</xsl:otherwise>
</xsl:choose></xsl:template>

<xsl:template match="modified_datetime">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.modifiedon.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

<xsl:template match="strictness_name">
<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.workflow.properties.ruleenforcement.label')"/></td>
<td class="tableHeader" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
<td class="tableContent"><xsl:apply-templates/></td>
</xsl:template>

</xsl:stylesheet>
