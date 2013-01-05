<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectStatusReport">
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
            <td class="channelHeader" width="1%"><k src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
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
    <table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
        <xsl:call-template name="DetailedDataHeader"/>
        <xsl:apply-templates select="ProjectData"/>
    </table>
</xsl:template>

<xsl:template name="DetailedDataHeader">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader" colspan="16"><xsl:value-of select="display:get('prm.business.report.projectstatusreport.name')"/></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" width="10%"><xsl:value-of select="display:get('prm.project.columndefs.sponsor')"/></td>
        <td class="tableHeader" width="10%"><xsl:value-of select="display:get('prm.project.columndefs.name')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.id')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.datestart')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.datefinish')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.workplandatestart')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.workplandatefinish')"/></td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.status')"/></td>
        <!-- Overall Status -->
        <td class="tableHeader" width="2%" align="center" title="{display:get('prm.project.portfolio.column.overallstatus.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.overallstatus.label')" />
        </td>
        <!-- Financial Status -->
        <td class="tableHeader" width="2%" align="center" title="{display:get('prm.project.portfolio.column.financialstatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.financialstatus.label')" />
        </td>
        <!-- Schedule Status -->
        <td class="tableHeader" width="2%" align="center" title="{display:get('prm.project.portfolio.column.schedulestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.schedulestatus.label')" />
        </td>
        <!-- Resource Status -->
        <td class="tableHeader" width="2%" align="center" title="{display:get('prm.project.portfolio.column.resourcestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.resourcestatus.label')" />
        </td>
		<td colspan="2" width="15%" class="tableHeader">
            <xsl:value-of select="display:get('prm.project.portfolio.column.percentcomplete.label.title')" />
		</td>
        <td class="tableHeader" width="5%"><xsl:value-of select="display:get('prm.project.columndefs.workplanlastmodified')"/></td>
        <td class="tableHeader" width="20%"><xsl:value-of select="display:get('prm.project.columndefs.description')"/></td>   
        <td></td>   
    </tr>
    <tr>
        <td></td>    
        <td colspan="16" class="tableLine"><img src="../images/trans.gif" height="2" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="ProjectData">
    <xsl:apply-templates select="Group"/>
    <tr>
        <td></td>        
        <td class="tableContent"><xsl:value-of select="ProjectSpace/Sponsor"/></td>
        <td class="tableContent"><xsl:value-of select="ProjectSpace/name"/></td>
        <td class="tableContent"><xsl:value-of select="ProjectSpace/Meta/ExternalProjectID"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(ProjectSpace/StartDate)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(ProjectSpace/EndDate)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(schedule/start_date)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(schedule/end_date)"/></td>
        <td class="tableContent"><xsl:value-of select="ProjectSpace/status_code"/></td>
        <xsl:apply-templates select="ProjectSpace/OverallStatus" />
        <xsl:apply-templates select="ProjectSpace/FinancialStatus" />
        <xsl:apply-templates select="ProjectSpace/ScheduleStatus" />
        <xsl:apply-templates select="ProjectSpace/ResourceStatus" />
        <!-- Percentage complete horizontal bar -->
        <td class="tableContent" align="left">
            <table border="1" width="100" height="8" cellspacing="0" cellpadding="0">
            <tr>
                <td bgcolor="#FFFFFF" title="{format:formatPercent(ProjectSpace/percent_complete, 0, 2)}">
                    <xsl:variable name="percentageWidth">
                        <xsl:choose>
                            <!-- A zero width box will render 1 pixel wide on IE and 8 pixels wide on
                                 Netscape.  Let's make all of them display one pixel. -->
                            <xsl:when test="percent_complete = 0">1</xsl:when>
                            <xsl:when test="percent_complete = ''">1</xsl:when>
                            <xsl:otherwise><xsl:value-of select="format:formatPercent(ProjectSpace/percent_complete, 0, 0)"/></xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <img src="../images/lgreen.gif" width="{$percentageWidth}" height="8" />
                </td>
            </tr>
            </table>
        </td>
        <td class="tableContent" align="left" ><xsl:value-of select="format:formatPercent(percent_complete, 0, 0)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(schedule/lastModified)"/></td>
        <td class="tableContent"><xsl:value-of select="ProjectSpace/description"/></td>
        <td></td>    
    </tr>
    <tr>
        <td></td>    
        <td colspan="16" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="Group">
    <tr>
        <td></td>
        <td colspan="16" class="tableContentHighlight"><font class="groupFont"><xsl:value-of select="."/></font></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="16" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="ProjectSpace/OverallStatus|ProjectSpace/FinancialStatus|ProjectSpace/ScheduleStatus|ProjectSpace/ResourceStatus">
    <td class="tableContent" align="center">
        <xsl:choose>
            <xsl:when test="not(string(ImageURL))">
                <img src="../images/trans.gif" alt="" title="" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="improvementCodeName" select="display:get(ImprovementCode/NameToken)" />
                <xsl:variable name="colorCodeName" select="display:get(ColorCode/NameToken)" />
                <img src="..{ImageURL}" title="{display:get(ImprovementCode/ImageTitleToken, $improvementCodeName, $colorCodeName)}" />
            </xsl:otherwise>
        </xsl:choose>
    </td>
</xsl:template>

</xsl:stylesheet>
