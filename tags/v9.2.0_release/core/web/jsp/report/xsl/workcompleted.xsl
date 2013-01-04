<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
  	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html" />

    <xsl:template match="/">
        <xsl:apply-templates select="WorkCompleted"/>
    </xsl:template>

    <xsl:template match="WorkCompleted">
        <xsl:apply-templates select="ReportInformation"/>
        <xsl:apply-templates select="ReportParameters"/>
        <xsl:apply-templates select="DetailedData"/>
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

    <xsl:template match="DetailedData">
        <table class="detailedTable" width="97%" cellpadding="0" cellspacing="0">
            <tr class="channelHeader">
                <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
                <td class="channelHeader" colspan="8"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
            </tr>
            <tr class="tableHeader">
                <td></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.assignment.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.actualstart.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.actualfinish.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.scheduledwork.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.hourslogged.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.remaininghours.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.datelogged.name')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.report.workcompleted.assignee.name')"/></td>
                <td></td>
            </tr>
            <tr>
                <td></td>
                <td class="headerSep" colspan="8"></td>
                <td></td>
            </tr>
            <xsl:apply-templates select="AssignmentWorkLogEntry|Group"/>
        </table>
    </xsl:template>

    <xsl:template match="AssignmentWorkLogEntry">
        <tr class="tableContent">
            <td></td>
            <td>
            	<a class="pnet-links">
            		<xsl:attribute name="href">
            			<xsl:value-of select="ContextUrl"/>
             		</xsl:attribute>
             		<xsl:value-of select="ObjectName"/>
            	</a>
            </td>
            <td><xsl:value-of select="StartDate"/></td>
            <td><xsl:value-of select="EndDate"/></td>
            <td><xsl:value-of select="TotalWorkFormatted"/></td>
            <td><xsl:value-of select="HoursWorkedFormatted"/></td>
            <td><xsl:value-of select="TotalRemainingWorkFormatted"/></td>
            <td><xsl:value-of select="LogDate"/></td>
            <td><xsl:value-of select="AssigneeName"/></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td class="RowSep" colspan="8"></td>
            <td></td>
        </tr>
    </xsl:template>

    <xsl:template match="Group">
        <tr>
            <td></td>
            <td colspan="8" class="tableContentHighlight"><font class="groupFont"><xsl:value-of select="."/></font></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td colspan="8" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
            <td></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>