<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="LateTaskReport">
    <xsl:apply-templates select="ReportInformation"/>
    <xsl:apply-templates select="ReportParameters"/>
    <xsl:apply-templates select="SummaryData"/>
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

<xsl:template match="SummaryData">
    <xsl:variable name="overallcolor">
	    <xsl:value-of select="OverallStatus/ColorCode/ColorRGB"/>
    </xsl:variable>
    <xsl:variable name="overallimprovement">
	   <xsl:value-of select="OverallStatus/ImprovementCode/NameToken"/>
    </xsl:variable>

    <xsl:variable name="schedulecolor">
	    <xsl:value-of select="ScheduleStatus/ColorCode/ColorRGB"/>
    </xsl:variable>
    <xsl:variable name="scheduleimprovement">
	   <xsl:value-of select="ScheduleStatus/ImprovementCode/NameToken"/>
    </xsl:variable>

    <xsl:variable name="resourcecolor">
	    <xsl:value-of select="ResourceStatus/ColorCode/ColorRGB"/>
    </xsl:variable>
    <xsl:variable name="resourceimprovement">
	   <xsl:value-of select="ResourceStatus/ImprovementCode/NameToken"/>
    </xsl:variable>

    <xsl:variable name="financialcolor">
	    <xsl:value-of select="FinancialStatus/ColorCode/ColorRGB"/>
    </xsl:variable>
    <xsl:variable name="financialimprovement">
	   <xsl:value-of select="FinancialStatus/ImprovementCode/NameToken"/>
    </xsl:variable>

    <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.schedule.report.common.workspace.name')"/></td>
        <td class="channelHeader" colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatuslastmodified')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="format:formatISODate(DateModified)"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusbusinessarea')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="BusinessName"/></td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectname')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="Name"/></td>
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectno')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="Number"/></td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectdesc')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="Description"/></td>
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectstartdate')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="format:formatISODate(StartDate)"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.dummy')"/></td>
        <td class="tableContent" align="right" width="10%"><xsl:value-of select="Dummy"/></td>    
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectenddate')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="format:formatISODate(FinishDate)"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.dummy')"/></td>
        <td class="tableContent" align="right" width="10%"><xsl:value-of select="Dummy"/></td>    
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectoverallcomplete')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="format:formatPercent(OverallComplete)"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.dummy')"/></td>
        <td class="tableContent" align="right" width="10%"><xsl:value-of select="Dummy"/></td>
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectoverallstatus')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="OverallStage"/></td>
        <td></td>
    </tr>
	<tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectoverallcolor')"/></td>
		<td align="left" width="10%">
			<xsl:attribute name="class">tableContentFontOnly</xsl:attribute>
			<xsl:attribute name="bgcolor">
			<xsl:value-of select="$overallcolor"/>
			</xsl:attribute>
		</td>
		<td class="tableContent" align="left" width="30%"><xsl:value-of select="display:get($overallimprovement)"/></td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectschedulecolor')"/></td>
		<td>
			<xsl:attribute name="class">tableContentFontOnly</xsl:attribute>
			<xsl:attribute name="bgcolor">
			<xsl:value-of select="$schedulecolor"/>
			</xsl:attribute>
		</td>
		<td class="tableContent"><xsl:value-of select="display:get($scheduleimprovement)"/></td>
        <td></td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectresourcecolor')"/></td>
		<td>
			<xsl:attribute name="class">tableContentFontOnly</xsl:attribute>
			<xsl:attribute name="bgcolor">
			<xsl:value-of select="$resourcecolor"/>
			</xsl:attribute>
		</td>
		<td class="tableContent"><xsl:value-of select="display:get($resourceimprovement)"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectfinancialcolor')"/></td>
		<td>
			<xsl:attribute name="class">tableContentFontOnly</xsl:attribute>
			<xsl:attribute name="bgcolor">
			<xsl:value-of select="$financialcolor"/>
			</xsl:attribute>
		</td>
		<td class="tableContent"><xsl:value-of select="display:get($financialimprovement)"/></td>
        <td></td>    
    </tr>
	<tr>
        <td></td>
        <td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.dummy')"/></td>
        <td class="tableContent" align="right" width="10%"><xsl:value-of select="Dummy"/></td>    
		<td class="tableHeader" align="left" width="30%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusprojectcomments')"/></td>
        <td class="tableContent" align="left" width="10%"><xsl:value-of select="Comments"/></td>
        <td></td>
    </tr>
    </table>
</xsl:template>

<xsl:template match="DetailedData">
    <table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
    <xsl:call-template name="DetailedDataHeader"/>
    <xsl:apply-templates select="TaskData"/>
    </table>
</xsl:template>

<xsl:template name="DetailedDataHeader">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusmilestones')"/></td>
        <td class="channelHeader" colspan="7"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" width="17%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusmilestone')"/></td>
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusmilestonestartdate')"/></td>
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusmilestonefinishdate')"/></td>
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.project.columndefs.projectstatusmilestonecomplete')"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="8" class="tableLine"><img src="../images/trans.gif" height="2" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="TaskData">
    <xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="summary|task">
    <xsl:apply-templates select="Group"/>
    <tr>
        <td></td>        
        <td class="tableContent"><xsl:value-of select="name"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(startDateTime)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(endDateTime)"/></td>
        <td class="tableContent">
            <xsl:variable name="percentComplete">
                <xsl:choose>
                    <xsl:when test="isMilestone = 1 and ./work = 0">
                        <xsl:value-of select="format:formatPercent(percentComplete)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="format:formatPercent(workPercentComplete)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$percentComplete"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td colspan="8" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
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
