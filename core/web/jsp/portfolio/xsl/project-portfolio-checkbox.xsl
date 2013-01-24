<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html" indent="yes" />

<xsl:variable name="maxDepth" select="//PortfolioTree/MaxDepth" />
<xsl:variable name="numCols" select="number($maxDepth + 1 + 11)" />

<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML/Content/PersonalPortfolioViewResults" />
</xsl:template>

<xsl:template match="PersonalPortfolioViewResults">
	<xsl:variable name="colspan" select="number($maxDepth + 1)" />

<form id="iFrameForm">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr class="tableHeader" align="left" valign="top">
		<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader" colspan="{$colspan}"><xsl:value-of select="display:get('prm.project.portfolio.column.projectname.label')" /></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.project.portfolio.column.businessname.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.project.portfolio.column.startdate.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.project.portfolio.column.enddate.label')" /></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.project.portfolio.column.status.label')" /></td>
        <!-- Overall Status -->
        <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.overallstatus.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.overallstatus.label')" />
        </td>
        <!-- Financial Status -->
        <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.financialstatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.financialstatus.label')" />
        </td>
        <!-- Schedule Status -->
        <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.schedulestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.schedulestatus.label')" />
        </td>
        <!-- Resource Status -->
        <td class="tableHeader" align="center" title="{display:get('@prm.project.portfolio.column.resourcestatus.label.title')}">
            <xsl:value-of select="display:get('prm.project.portfolio.column.resourcestatus.label')" />
        </td>
        <td colspan="2" class="tableHeader">
            <xsl:value-of select="display:get('prm.project.portfolio.column.percentcomplete.label.title')" />
        </td>
	</tr>
	<tr class="tableLine">
		<td colspan="{$numCols}" class="tableLine">
			<img src="../images/spacers/trans.gif" width="1" height="2" border="0" />
		</td>
	</tr>
	<xsl:apply-templates select="PortfolioEntries/PortfolioTree/Node">
		<xsl:with-param name="level">0</xsl:with-param>
	</xsl:apply-templates>
</table>
<input type="hidden" name="unitsOfWork" id="unitsOfWork" value=""/>
<input type="hidden" name="timeBuckets" id="timeBuckets" value=""/>
<input type="hidden" name="startDateString" id="startDateString" value=""/>
<input type="hidden" name="endDateString" id="endDateString" value=""/>
<input type="hidden" name="theAction" id="theAction" value="/servlet/AssignmentResourcesProjects"/>
</form>
</xsl:template>

<xsl:template match="Node">
	<xsl:param name="level" />
	<xsl:apply-templates select="Value">
		<xsl:with-param name="level" select="$level" />
	</xsl:apply-templates>
	<xsl:apply-templates select="Children">
		<xsl:with-param name="level" select="number($level + 1)" />
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="Value">
	<xsl:param name="level" />
	<xsl:apply-templates select="ProjectPortfolioEntry">
		<xsl:with-param name="level" select="$level" />
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="Children">
	<xsl:param name="level" />
	<xsl:apply-templates select="Node">
		<xsl:with-param name="level" select="$level" />
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="ProjectPortfolioEntry">
	<xsl:param name="level" />
	<xsl:variable name="colspan" select="number($maxDepth - $level + 1)" />

<tr align="left" valign="middle" class="tableContent">
	<!-- Checkbox Option -->
    <td class="tableContent">
        <xsl:choose>
            <xsl:when test="../IsMember = 1">
                <input type="checkbox" id="selected" name="selected" value="{project_id}" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </td>
	<!-- Insert appropriate number of padding columns -->
	<xsl:call-template name="indent">
		<xsl:with-param name="count" select="$level" />
	</xsl:call-template>
	<td colspan="{$colspan}" align="left">
		<xsl:choose>
			<xsl:when test="../IsMember = 1">
				<a href="../../project/Dashboard?id={project_id}"><xsl:value-of select="name"/></a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="name" />
			</xsl:otherwise>
		</xsl:choose>
	</td>
	<td class="tableContent" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="ParentBusinessName"/></td>
    <td class="tableContent" align="left"><xsl:value-of select="format:formatISODate(StartDate)" /></td>
    <td class="tableContent" align="left"><xsl:value-of select="format:formatISODate(EndDate)" /></td>
    <td class="tableContent" align="left"><xsl:value-of select="status_code" /></td>
    <xsl:apply-templates select="OverallStatus" />
    <xsl:apply-templates select="FinancialStatus" />
    <xsl:apply-templates select="ScheduleStatus" />
    <xsl:apply-templates select="ResourceStatus" />
    <!-- Percentage complete horizontal bar -->
	<td class="tableContent" width="110" align="left">
		<table border="1" width="100" height="8" cellspacing="0" cellpadding="0">
		<tr>
			<td bgcolor="#FFFFFF" title="{format:formatPercent(percent_complete, 0, 2)}">
				<xsl:variable name="percentageWidth">
					<xsl:choose>
                    	<!-- A zero width box will render 1 pixel wide on IE and 8 pixels wide on
                             Netscape.  Let's make all of them display one pixel. -->
                        <xsl:when test="percent_complete = 0">1</xsl:when>
                        <xsl:when test="percent_complete = ''">1</xsl:when>
                        <xsl:otherwise><xsl:value-of select="format:formatPercent(percent_complete, 0, 0)"/></xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<img src="../images/lgreen.gif" width="{$percentageWidth}" height="8" />
			</td>
		</tr>
		</table>
	</td>
	<td class="tableContent" align="left" ><xsl:value-of select="format:formatPercent(percent_complete, 0, 0)"/></td>
</tr>
<tr class="tableLine">
	<td colspan="{$numCols}">
		<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
	</td>
</tr>
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

<xsl:template name="indent">
	<xsl:param name="count" />
	
	<xsl:if test="$count > 0">
		<xsl:choose>
			<xsl:when test="$count = 1">
				<td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif" /></td>
			</xsl:when>
			<xsl:otherwise>
				<td class="tableContent" align="left" width="2%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="indent">
			<xsl:with-param name="count" select="number($count - 1)" />
		</xsl:call-template>
	</xsl:if>
</xsl:template>

</xsl:stylesheet>