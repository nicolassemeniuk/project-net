<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

<xsl:output method="html" indent="yes" />

<xsl:variable name="numCols" select="number(8)" />

<xsl:template match="/">
	<xsl:apply-templates select="ProjectXML/Content/PersonalPortfolioViewResults" />	
</xsl:template>

<xsl:template match="PersonalPortfolioViewResults">

<table cellpadding="0" cellspacing="0" border="0" width="100%" name="tableWithEvenRows">
    <tr class="table-header">
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.project.label')"/></td>
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.business.label')"/></td>
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.responsilities.label')"/></td>
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.status.label')"/></td>
        <!-- Overall Status -->
        <td class="table-header" align="center" title="{display:get('@prm.project.portfolio.column.overallstatus.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.overallstatus.label')" />
        </td>
        <!-- Financial Status -->
        <td class="table-header" align="center" title="{display:get('@prm.project.portfolio.column.financialstatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.financialstatus.label')" />
        </td>
        <!-- Schedule Status -->
        <td class="table-header" align="center" title="{display:get('@prm.project.portfolio.column.schedulestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.schedulestatus.label')" />
        </td>
        <!-- Resource Status -->
        <td class="table-header" align="center" title="{display:get('@prm.project.portfolio.column.resourcestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.resourcestatus.label')" />
        </td>
    </tr>
    <!-- tr class="tableLine">
        <td colspan="{$numCols}" class="tableLine">
            <img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
        </td>
    </tr -->
	<xsl:apply-templates select="PortfolioEntries/ProjectPortfolioEntry" />
</table>
</xsl:template>

<xsl:template match="ProjectPortfolioEntry">

<tr class="tableContent" align="left" valign="middle">
    <td class="tableContent">
        <a class="project" href="../project/Dashboard?id={project_id}"><xsl:value-of select="name"/></a>
    </td>
    <td class="tableContent">
        <a href="../business/Main.jsp?id={ParentBusinessID}"><xsl:value-of select="ParentBusinessName" /></a>
    </td>
    <td class="tableContent">
        <xsl:value-of select="PersonResponsiblities"/>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td class="tableContent">
        <xsl:value-of select="status_code"/>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <xsl:apply-templates select="OverallStatus" />
    <xsl:apply-templates select="FinancialStatus" />
    <xsl:apply-templates select="ScheduleStatus" />
    <xsl:apply-templates select="ResourceStatus" />
</tr>
<!-- tr class="tableLine">
    <td colspan="{$numCols}">
        <img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
    </td>
</tr -->
</xsl:template>

<xsl:template match="OverallStatus|FinancialStatus|ScheduleStatus|ResourceStatus">
    <td class="tableContent" align="center">
        <xsl:choose>
            <xsl:when test="not(string(ImageURL))">
                <img src="../images/trans.gif" width="12" alt="" title="" />
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