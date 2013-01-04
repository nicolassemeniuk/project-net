<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="FormItemSummaryReport">
        <xsl:apply-templates select="ReportInformation"/>
        <xsl:apply-templates select="ReportParameters"/>
        <xsl:apply-templates select="Chart"/>
        <xsl:apply-templates select="SummaryData"/>
    </xsl:template>

    <xsl:template match="ReportInformation">
        <table class="titleTable" cellpadding="0" cellspacing="0" width="97%">
            <tr>
                <td class="pageTitle" colspan="2"><xsl:value-of select="Title"/></td>
            </tr>
            <tr>
                <td class="workspaceName pageTitle" width="5%" nowrap="true"><xsl:value-of select="display:get('prm.schedule.report.common.workspace.name')"/></td>
                <td class="workspaceName pageTitle"><xsl:value-of select="WorkspaceName"/></td>
            </tr>
            <tr>
                <td class="tableContent" nowrap="true"><xsl:value-of select="display:get('prm.schedule.report.common.preparedby.name')"/></td>
                <td class="tableContent"><xsl:value-of select="Creator"/></td>
            </tr>
            <tr>
                <td class="tableContent" nowrap="true"><xsl:value-of select="display:get('prm.schedule.report.common.dateprepared.name')"/></td>
                <td class="tableContent"><xsl:value-of select="format:formatISODate(CreationDate)"/></td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="ReportParameters">
        <xsl:if test="count(child::ReportParameter)">
        <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
        <tr class="channelHeader">
            <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
            <td class="channelHeader"><xsl:value-of select="display:get('prm.schedule.report.common.reportparameterstitle.name')"/></td>
            <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        </tr>
        <xsl:apply-templates select="ReportParameter"/>
        </table>
        </xsl:if>
    </xsl:template>

    <xsl:template match="ReportParameter">
        <tr>
            <td></td>
            <td class="tableContent"><xsl:value-of select="."/></td>
            <td></td>
        </tr>
    </xsl:template>

    <xsl:template match="Chart">
        <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
            <tr class="channelHeader">
                <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
                <td class="channelHeader"></td>
                <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
            </tr>
            <tr>
                <td></td>
                <td align="center"><img src="{ChartURL}"/></td>
                <td></td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="SummaryData">
        <xsl:variable name="SummaryTableWidth" select="ColumnDefinition/@Count"/>

        <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
            <tr class="channelHeader">
                <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
                <td class="channelHeader" colspan="{$SummaryTableWidth}"></td>
                <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
            </tr>
            <tr>
                <td></td>
                <xsl:apply-templates select="ColumnDefinition/Column"/>
                <td></td>
            </tr>
            <tr>
                <td></td>
                <td class="tableLine" colspan="{$SummaryTableWidth}"><img src="../images/trans.gif" height="2" width="1"/></td>
                <td></td>
            </tr>
            <xsl:apply-templates select="DataRows/DataRow"/>
        </table>
    </xsl:template>

    <xsl:template match="ColumnDefinition/Column">
            <td class="tableHeader"><xsl:value-of select="."/></td>
    </xsl:template>

    <xsl:template match="DataRow">
        <xsl:variable name="SummaryTableWidth" select="../../ColumnDefinition/@Count"/>
        <tr>
            <td></td>
            <xsl:apply-templates select="Column"/>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td class="tableLine" colspan="{$SummaryTableWidth}"><img src="../images/trans.gif" height="1" width="1"/></td>
            <td></td>
        </tr>
    </xsl:template>

    <xsl:template match="DataRow/Column">
        <td class="tableContent"><xsl:value-of select="."/></td>
    </xsl:template>
</xsl:stylesheet>

