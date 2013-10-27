<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat" extension-element-prefixes="display format">

	<xsl:output method="html" />

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="LateTaskReport">
		<xsl:apply-templates select="ReportInformation" />
		<xsl:apply-templates select="ReportParameters" />
		<xsl:apply-templates select="SummaryData" />
		<xsl:apply-templates select="DetailedData" />
	</xsl:template>

	<xsl:template match="ReportInformation">
		<table class="titleTable" cellpadding="0" cellspacing="0" width="97%">
			<tr>
				<td class="pageTitle" colspan="2">
					<xsl:value-of select="Title" />
				</td>
			</tr>
			<tr>
				<td class="workspaceName pageTitle" width="5%" nowrap="true">
					<xsl:value-of select="display:get('prm.schedule.report.common.workspace.name')" />
				</td>
				<td class="workspaceName pageTitle">
					<xsl:value-of select="WorkspaceName" />
				</td>
			</tr>
			<tr>
				<td class="tableContent" nowrap="true">
					<xsl:value-of select="display:get('prm.schedule.report.common.preparedby.name')" />
				</td>
				<td class="tableContent">
					<xsl:value-of select="Creator" />
				</td>
			</tr>
			<tr>
				<td class="tableContent" nowrap="true">
					<xsl:value-of select="display:get('prm.schedule.report.common.dateprepared.name')" />
				</td>
				<td class="tableContent">
					<xsl:value-of select="format:formatISODate(CreationDate)" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="ReportParameters">
		<xsl:if test="count(child::ReportParameter)">
			<table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
				<tr class="channelHeader">
					<td class="channelHeader" width="1%">
						<img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
					</td>
					<td class="channelHeader">
						<xsl:value-of select="display:get('prm.schedule.report.common.reportparameterstitle.name')" />
					</td>
					<td class="channelHeader" align="right" width="1%">
						<img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
					</td>
				</tr>
				<xsl:apply-templates select="ReportParameter" />
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="ReportParameter">
		<tr>
			<td></td>
			<td class="tableContent">
				<xsl:value-of select="." />
			</td>
			<td></td>
		</tr>
	</xsl:template>

	<xsl:template match="SummaryData">
		<table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
			<tr class="channelHeader">
				<td class="channelHeader" width="1%">
					<img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
				</td>
				<td class="channelHeader">
					<xsl:value-of select="display:get('prm.financial.report.common.reportsummary.name')" />
				</td>
				<td class="channelHeader" colspan="3">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</td>
				<td class="channelHeader" align="right" width="1%">
					<img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td class="tableHeader" align="left" width="40%">
					<xsl:value-of select="display:get('prm.financial.report.totalprojects.name')" />
				</td>
				<td class="tableContent" align="left" width="9%">
					<xsl:value-of select="ProjectCount" />
				</td>
				<td class="chartCell" width="49%" align="center" rowspan="5">
					<xsl:if test="ChartURL">
						<img src="{ChartURL}" />
					</xsl:if>
				</td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td class="tableHeader">
					<xsl:value-of select="display:get('prm.financial.report.resourcestotalactualcosttodate.name')" />
				</td>
				<td class="tableContent">
					<xsl:value-of select="ResourcesTotalActualCostToDate" />
				</td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td class="tableHeader">
					<xsl:value-of select="display:get('prm.financial.report.materialstotalactualcosttodate.name')" />
				</td>
				<td class="tableContent">
					<xsl:value-of select="MaterialsTotalActualCostToDate" />
				</td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td class="tableHeader">
					<xsl:value-of select="display:get('prm.financial.report.discretionaltotalactualcosttodate.name')" />
				</td>
				<td class="tableContent">
					<xsl:value-of select="DiscretionalTotalActualCostToDate" />
				</td>
				<td></td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template match="DetailedData">
		<table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
			<xsl:call-template name="DetailedDataHeader" />
			<xsl:apply-templates select="ProjectData" />
		</table>
	</xsl:template>

	<xsl:template name="DetailedDataHeader">
		<tr class="channelHeader">
			<td class="channelHeader" width="1%">
				<img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
			</td>
			<td class="channelHeader">
				<xsl:value-of select="display:get('prm.financial.columndefs.projects.name')" />
			</td>
			<td class="channelHeader" colspan="7">
				<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
			<td class="channelHeader" align="right" width="1%">
				<img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td class="tableHeader" width="8%">
				<xsl:value-of select="display:get('prm.financial.columndefs.project.name')" />
			</td>
			<td class="tableHeader" width="17%">
				<xsl:value-of select="display:get('prm.financial.columndefs.project.description')" />
			</td>
			<td class="tableHeader" width="8%">
				<xsl:value-of select="display:get('prm.financial.columndefs.project.resourcesactualcosttodate')" />
			</td>
			<td class="tableHeader" width="8%">
				<xsl:value-of select="display:get('prm.financial.columndefs.project.materialsactualcosttodate')" />
			</td>
			<td class="tableHeader" width="8%">
				<xsl:value-of select="display:get('prm.financial.columndefs.project.discretionalactualcosttodate')" />
			</td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="8" class="tableLine">
				<img src="../images/trans.gif" height="2" border="0" />
			</td>
			<td></td>
		</tr>
	</xsl:template>

	<xsl:template match="ProjectData">
		<xsl:apply-templates select="Group" />
		<xsl:apply-templates select="ProjectSpace" />
	</xsl:template>

	<xsl:template match="ProjectSpace">
		<tr>
			<td></td>
			<td class="tableContent">
				<xsl:value-of select="name" disable-output-escaping="yes" />
			</td>
			<td class="tableContent" align="left">
				<xsl:value-of select="description" />
			</td>
			<xsl:apply-templates select="Meta" />
			<td></td>
		</tr>

		<tr>
			<td></td>
			<td colspan="5" class="tableLine">
				<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
			</td>
			<td></td>
		</tr>

	</xsl:template>

	<xsl:template match="Meta">
		<td class="tableContent" align="left">
			<xsl:value-of select="ResourcesActualCostToDate" />
		</td>
		<td class="tableContent" align="left">
			<xsl:value-of select="MaterialsActualCostToDate" />
		</td>
		<td class="tableContent" align="left">
			<xsl:value-of select="DiscretionalActualCostToDate" />
		</td>

	</xsl:template>

	<xsl:template match="Group">
		<tr>
			<td></td>
			<td colspan="8" class="tableContentHighlight">
				<font class="groupFont">
					<xsl:value-of select="." />
				</font>
			</td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td colspan="8" class="tableLine">
				<img src="../images/trans.gif" height="1" border="0" />
			</td>
			<td></td>
		</tr>
	</xsl:template>

</xsl:stylesheet>
