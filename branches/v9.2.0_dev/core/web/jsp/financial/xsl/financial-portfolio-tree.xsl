<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  xmlns:display="xalan://net.project.base.property.PropertyProvider"
                  xmlns:format="xalan://net.project.util.XSLFormat"
                  extension-element-prefixes="display format">

<xsl:output method="html" indent="yes" />

<xsl:variable name="maxDepth" select="//PortfolioTree/MaxDepth" />
<xsl:variable name="numCols" select="number($maxDepth + 1 + 4)" />

<xsl:template match="/">
    <xsl:apply-templates select="ProjectXML/Content/PortfolioTree" />
</xsl:template>

<xsl:template match="PortfolioTree">
    <xsl:variable name="colspan" select="number($maxDepth + 1)" />

<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr align="left" class="tableHeader">
	<td class="tableHeader" width="1%"><xsl:text disable-output-escaping="yes"></xsl:text></td>
        <td class="tableHeader" colspan="{$colspan}"><xsl:value-of select="display:get('prm.business.businessportfolio.business.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.business.businessportfolio.businesstype.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.business.businessportfolio.projects.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.business.businessportfolio.people.label')" /></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.business.businessportfolio.people.label')" /></td>
    </tr>
    <tr class="tableLine">
        <td colspan="{$numCols}" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0"/></td>
    </tr>
    <xsl:apply-templates select="Node">
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
    <xsl:apply-templates select="FinancialSpace">
        <xsl:with-param name="level" select="$level" />
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="Children">
    <xsl:param name="level" />
    <xsl:apply-templates select="Node">
        <xsl:with-param name="level" select="$level" />
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="FinancialSpace">
    <xsl:param name="level" />
    <xsl:variable name="colspan" select="number($maxDepth - $level + 1)" />

<tr align="left" valign="middle" class="tableContent">
	<!-- Radio Option -->
    <td class="tableContent">
        <xsl:choose>
            <xsl:when test="../IsMember = 1">
                <input type="radio" name="selected" value="{businessID}" id="{name}" />
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
                <a href="../financial/Main.jsp?module=175&amp;page=dashboard&amp;id={financialID}"><xsl:value-of select="name"/></a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="name" />
            </xsl:otherwise>
        </xsl:choose>
    </td>
    <td class="tableContent" align="left"><xsl:value-of select="description"/></td>
    <td class="tableContent" align="left"><xsl:value-of select="format:formatNumber(totalActualCostToDate)"/></td>
    <td class="tableContent" align="left"><xsl:value-of select="format:formatNumber(totalEstimatedCurrentCost)"/></td>
    <td class="tableContent" align="left"><xsl:value-of select="format:formatNumber(totalBudgetedCost)"/></td>
</tr>
<tr class="tableLine">
    <td colspan="{$numCols}">
        <img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
    </td>
</tr>
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

</xsl:stylesheet><!-- Stylesheet edited using Stylus Studio - (c)1998-2002 eXcelon Corp. -->