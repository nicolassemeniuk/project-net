<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
	extension-element-prefixes="display format">
	
	<xsl:output method="html" indent="yes" />

	<xsl:param name="materialModuleID" />
	<xsl:param name="JSPRootURL" />
	<xsl:param name="createAction" />	
	
	<xsl:template match="materials-list">
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="row-content" name="tableWithEvenRowsWOH">
		<xsl:choose>
			<xsl:when test="count(material) > 0">
				<!--Table header -->
				<tr class="table-header">
				    <td class="cell-right-border"><xsl:value-of select="display:get('prm.business.dashboard.businessmaterials.name.label')"/></td>
				    <td width="75" align="center"><xsl:value-of select="display:get('prm.business.dashboard.businessmaterials.workingcalendar.label')"/></td>
			  	</tr>
				<!--Table content -->			
				<xsl:apply-templates select="material" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="no_materials" />
			</xsl:otherwise>
		</xsl:choose>
		</table>
	</xsl:template>	

	<xsl:template match="material">
		<tr>
			<td>
				<a href="../material/MaterialDetail.jsp?module={$materialModuleID}&amp;id={id}">
					<xsl:value-of select="name" disable-output-escaping="yes"/>
				</a>
			</td>
        	<td align="center">
        		<a href="javascript:showMaterialAllocation({id})"><img src="../images/schedule/constraint.gif" border="0" /></a>
        	</td>							
		</tr>	
	</xsl:template>
	
	<xsl:template name="no_materials">
	<tr class="tableContent"> 
	  <td class="tableContent">
		<a href="{$JSPRootURL}/material/CreateMaterial.jsp?action={$createAction}&amp;module={$materialModuleID}"><xsl:value-of select="display:get('prm.business.dashboard.creatematerials.link')"/></a>
	  </td>
	</tr>
	</xsl:template>	
</xsl:stylesheet>
