<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" extension-element-prefixes="display format">

	<xsl:output method="html" />
	
    <xsl:variable name="maxDepth" select="//materials-list/maxdepth" />
	<xsl:variable name="numCols" select="number($maxDepth + 1 + 4)" />

	<!-- TODO Parametrizar las cabeceras de tablas -->
	<xsl:template match="materials-list">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0" style="overflow-x:auto">
			<tr class="table-header">
				<td class="over-table">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>

<!-- 				<td class="over-table" align="left">Material</td> -->
<!-- 				<td class="over-table" align="left">Description</td> -->
<!-- 				<td class="over-table" align="left">Type</td> -->
<!-- 				<td class="over-table" align="left">Cost</td> -->
				
				<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.materials.column.name')"/></td>
				<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.materials.column.description')"/></td>
				<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.materials.column.type')"/></td>
				<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.materials.column.cost')"/></td>
				
				
				<td class="over-table">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>
			</tr>
			<xsl:apply-templates select="material">
			</xsl:apply-templates>
		</table>
	</xsl:template>

	<xsl:template match="material">
	
		<tr  class="tableContent">
		
			<!-- Radio Option -->
			<td class="tableContent" align="center">
				<input type="radio" name="selected" value="{materialid}" />
			</td>

			<td class="tableContent">
				<xsl:value-of select="materialname" />
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
			<td class="tableContent">
				<xsl:value-of select="materialdescription" />
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
			<td class="tableContent">
				<xsl:value-of select="materialtype" />
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
			<td class="tableContent">
				<xsl:value-of select="materialcost" />
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
		</tr>
			 
		<tr class="tableLine">
			<td colspan="{$numCols}">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
			</td>
		</tr>
	</xsl:template>


</xsl:stylesheet>
