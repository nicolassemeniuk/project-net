<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="NewUserReport">
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
        <td class="tableHeader" align="left" width="40%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.totalnumberofinvitedusers.name')"/></td>
        <td class="tableContent" align="left" width="9%"><xsl:value-of select="InvitedUsers"/></td>
        <td class="chartCell" width="49%" align="center" rowspan="5"><xsl:if test="ChartURL"><img src="{ChartURL}"/></xsl:if></td>
        <td></td>
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.report.newuserreport.invitedinthelast7days.name')"/></td>
        <td class="tableContent"><xsl:value-of select="InvitedInLast7Days"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.report.newuserreport.invitedinthelastmonth.name')"/></td>
        <td class="tableContent"><xsl:value-of select="InvitedInLastMonth"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.report.newuserreport.respondedinthelast7days.name')"/></td>
        <td class="tableContent"><xsl:value-of select="RespondedInLast7Days"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.report.newuserreport.respondedinthelastmonth.name')"/></td>
        <td class="tableContent"><xsl:value-of select="RespondedInLastMonth"/></td>
        <td></td>    
    </tr>    
    </table>
</xsl:template>

<xsl:template match="DetailedData">
    <table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
    <xsl:call-template name="DetailedDataHeader"/>
    <xsl:apply-templates select="InviteeData"/>
    </table>
</xsl:template>

<xsl:template name="DetailedDataHeader">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.resource.report.newuserreport.invitedusers.name')"/></td>
        <td class="channelHeader" colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" width="24%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.invitee.name')"/></td>
        <td class="tableHeader" width="23%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.emailaddress.name')"/></td>
        <td class="tableHeader" width="23%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.invitor.name')"/></td>
        <td class="tableHeader" width="14%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.dateinvited.name')"/></td>
        <td class="tableHeader" width="14%"><xsl:value-of select="display:get('prm.resource.report.newuserreport.dateresponded.name')"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="5" class="tableLine"><img src="../images/trans.gif" height="2" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="InviteeData">
    <xsl:apply-templates select="*"/>
</xsl:template>

<xsl:template match="Invitee">
    <tr>
        <td></td>        
        <td class="tableContent"><xsl:value-of select="DisplayName"/></td>
        <td class="tableContent"><xsl:value-of select="Email"/></td>
        <td class="tableContent"><xsl:value-of select="InvitorDisplayName"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(InvitedDate)"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatISODate(ResponseDate)"/></td>
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
