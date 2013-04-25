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
    <table class="summaryTable" cellpadding="0" cellspacing="0" width="97%">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.material.report.common.reportsummary.name')"/></td>
        <td class="channelHeader" colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" align="left" width="40%"><xsl:value-of select="display:get('prm.material.report.totalmaterials.name')"/></td>
        <td class="tableContent" align="left" width="9%"><xsl:value-of select="MaterialCount"/></td>
        <td></td>    
    </tr>    
    <tr>
        <td></td>    
        <td class="tableHeader"><xsl:value-of select="display:get('prm.material.report.totalcost.name')"/></td>
        <td class="tableContent"><xsl:value-of select="MaterialTotalCost"/></td>
        <td></td>    
    </tr>
    </table>
</xsl:template>

<xsl:template match="DetailedData">
    <table class="detailedTable" cellpadding="0" cellspacing="0" width="97%">
    <xsl:call-template name="DetailedDataHeader"/>
    <xsl:apply-templates select="MaterialData"/>
    </table>
</xsl:template>

<xsl:template name="DetailedDataHeader">
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="../images/icons/channelbar-left_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
        <td class="channelHeader"><xsl:value-of select="display:get('prm.material.columndefs.materials.name')"/></td>
        <td class="channelHeader" colspan="7"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="channelHeader" align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="10" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
    </tr>
    <tr>
        <td></td>    
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.material.columndefs.material.name')"/></td>
        <td class="tableHeader" width="17%"><xsl:value-of select="display:get('prm.material.columndefs.material.description')"/></td>        
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.material.columndefs.material.type')"/></td>
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.material.columndefs.material.cost')"/></td>
        <td class="tableHeader" width="8%"><xsl:value-of select="display:get('prm.material.columndefs.material.consumable')"/></td>
        <td></td>
    </tr>
    <tr>
        <td></td>    
        <td colspan="8" class="tableLine"><img src="../images/trans.gif" height="2" border="0"/></td>
        <td></td>    
    </tr>
</xsl:template>

<xsl:template match="MaterialData">
    <xsl:apply-templates select="material"/>
</xsl:template>

<xsl:template match="material" >
	<tr align="left" valign="middle" class="tableContent">
		<td></td>
		<td class="tableContent">
			<xsl:value-of select="name" disable-output-escaping="yes"/>
		</td>
		<td class="tableContent" align="left">
			<xsl:value-of select="description" />
		</td>
		<td class="tableContent" align="left">
			<xsl:value-of select="type" />
		</td>
		<td class="tableContent" align="left">
			<xsl:value-of select="cost" />
		</td>
		<td class="tableContent" align="left">
			<xsl:choose>
				<xsl:when test="consumable = 'true'">
<!-- 					<img src="../images/check_green.gif" /> -->
					<xsl:text disable-output-escaping="yes">yes</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>				
		</td>
		<td></td> 			
	</tr>
	 
	<tr class="tableLine">				
		<td colspan="5">
			<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
		</td>		
	</tr>
	 
</xsl:template>


<!-- <xsl:template match="summary|task"> -->
<!--     <xsl:apply-templates select="Group"/> -->
<!--     <tr> -->
<!--         <td></td>         -->
<!--         <td class="tableContent"><xsl:value-of select="name"/></td> -->
<!--         <td class="tableContent"><xsl:value-of select="format:formatISODate(startDateTime)"/></td> -->
<!--         <td class="tableContent"><xsl:value-of select="format:formatISODate(endDateTime)"/></td> -->
<!--         <td class="tableContent"> -->
<!--             <xsl:variable name="percentComplete"> -->
<!--                 <xsl:choose> -->
<!--                     <xsl:when test="isMilestone = 1 and ./work = 0"> -->
<!--                         <xsl:value-of select="format:formatPercent(percentComplete)"/> -->
<!--                     </xsl:when> -->
<!--                     <xsl:otherwise> -->
<!--                         <xsl:value-of select="format:formatPercent(workPercentComplete)"/> -->
<!--                     </xsl:otherwise> -->
<!--                 </xsl:choose> -->
<!--             </xsl:variable> -->
<!--             <xsl:value-of select="$percentComplete"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> -->
<!--         </td> -->
<!--         <td></td>     -->
<!--     </tr> -->
<!--     <tr> -->
<!--         <td></td>     -->
<!--         <td colspan="8" class="tableLine"><img src="../images/trans.gif" height="1" border="0"/></td> -->
<!--         <td></td>     -->
<!--     </tr> -->
<!-- </xsl:template> -->

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
