<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" extension-element-prefixes="display format">
	<xsl:output method="html" indent="yes" />

	<xsl:template match="personSalaryList">
		<table class="compactTable" style="overflow-x:auto">
			<tr align="left" class="table-header">
				<td class="over-table"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				<td class="over-table"><xsl:value-of select="display:get('prm.personal.salary.roster.column.startdate')"/></td>
				<td class="over-table"><xsl:value-of select="display:get('prm.personal.salary.roster.column.enddate')"/></td>
				<td class="over-table"><xsl:value-of select="display:get('prm.personal.salary.roster.column.costbyhour')"/></td>				
			</tr>
			<tr class="tableLine">
				<td colspan="7" class="tableLine">
					<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
				</td>
			</tr>
			<xsl:apply-templates select="personSalary">
			</xsl:apply-templates>			
		</table>
	</xsl:template>
	
	<xsl:template match="personSalary" >
		<tr align="left" valign="middle" class="tableContent">
			<!-- Radio Option -->
			<td class="tableContent" align="center">
			<input type="radio" name="selected" value="{id}" />			
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="startDate" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="endDate" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="costByHour" />
			</td>
		</tr>
		<tr class="tableLine">
			<td colspan="7">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>