<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                              xmlns:display="xalan://net.project.base.property.PropertyProvider"
                              extension-element-prefixes="display" >
  
<xsl:output method="html" />

<xsl:variable name="maxDepth" select="//PortfolioTree/MaxDepth" />
<xsl:variable name="numCols" select="number($maxDepth + 1 + 7)" />

<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML/Content/PersonalPortfolioViewResults" />
</xsl:template>

<xsl:template match="PersonalPortfolioViewResults">
    <xsl:variable name="colspan" select="number($maxDepth + 1)" />

<table cellpadding="0" cellspacing="0" border="0" width="100%" name="tableWithEvenRows">
    <tr class="table-header">
        <td align="left" class="table-header" colspan="{$colspan}"><xsl:value-of select="display:get('prm.personal.main.project.label')"/></td>
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.business.label')"/></td>
        <!-- <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.responsilities.label')"/></td>
        <td align="left" class="table-header"><xsl:value-of select="display:get('prm.personal.main.status.label')"/></td> -->
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
    <xsl:apply-templates select="PortfolioEntries/PortfolioTree/Node">
        <xsl:with-param name="level">0</xsl:with-param>
    </xsl:apply-templates>
</table>
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

<tr class="tableContent" align="left" valign="middle">
    <xsl:call-template name="indent">
        <xsl:with-param name="count" select="$level" />
    </xsl:call-template>
    <td colspan="{$colspan}" class="tableContent">
        <xsl:choose>
            <xsl:when test="../IsMember = 1">
                <a class="project" href="../project/Dashboard?id={project_id}"><xsl:value-of select="name"/></a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="name" />
            </xsl:otherwise>
        </xsl:choose>
    </td>
    <td class="tableContent">
        <a id="{name}_{ParentBusinessName}" href="../business/Main.jsp?id={ParentBusinessID}"><xsl:value-of select="ParentBusinessName" /></a>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <!-- <td class="tableContent">
        <xsl:value-of select="PersonResponsiblities"/>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td>
    <td class="tableContent">
        <xsl:value-of select="status_code"/>
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </td> -->
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
    <!-- 5% width is to help Netscape constrain last column in table
         IE doesn't need any width attribute to draw the table correctly -->
    <td class="tableContent" align="center" width="5%">
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
