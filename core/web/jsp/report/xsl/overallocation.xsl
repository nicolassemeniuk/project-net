<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
  	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="OverallocatedResourcesReport">
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
    <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.schedule.report.common.reportsummary.name')"/></td>
        <td class="channelHeader" colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="40%"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.number.name')"/></td>
        <td class="tableContent" align="left" width="60%"><xsl:value-of select="OverallocatedDayCount"/></td>
        <td></td>
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.totalnumberofoverallocatedresources.name')"/></td>
        <td class="tableContent" align="left"><xsl:value-of select="OverallocatedResourceCount"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.highestpercentageofallocation.name')"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatPercent(HighestPercentageOfAllocation)"/></td>
        <td></td>
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.mostfrequentlyoverallocatedresource.name')"/></td>
        <td class="tableContent"><xsl:value-of select="MostOverallocatedResource"/></td>
        <td></td>    
    </tr>    
    </table>
</xsl:template>

<xsl:template match="DetailedData">
    <table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
    <xsl:call-template name="DetailedDataHeader"/>
    <xsl:apply-templates select="OverallocatedResource"/>
    </table>
</xsl:template>

<xsl:template name="DetailedDataHeader">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader" colspan="4"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.allocatedresources.name')"/></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" width="15%"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.date.name')"/></td>
        <td class="tableHeader" width="25%"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.resourcename.name')"/></td>
        <td class="tableHeader" width="15%"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.percentallocated.name')"/></td>
        <td class="tableHeader" width="45%"><xsl:value-of select="display:get('prm.schedule.report.resourceallocation.tasknames.name')"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="6" class="tableLine"><img src="../images/trans.gif" height="2" border="0"/></td>
        <td></td>
    </tr>
</xsl:template>

<xsl:template match="OverallocatedResource">
    <xsl:apply-templates select="Group"/>
    <tr>
        <td></td>        
        <td class="tableContent"><xsl:value-of select="format:formatISODate(AssignmentDate)"/></td>
        <td class="tableContent"><xsl:value-of select="ResourceName"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatPercent(PercentAssigned)"/></td>
        <td class="tableContent"><xsl:value-of select="TaskNames"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="5" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="Group">
    <tr>
        <td></td>
        <td colspan="5" class="tableContentHighlight"><font class="groupFont"><xsl:value-of select="."/></font></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="5" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

</xsl:stylesheet>
