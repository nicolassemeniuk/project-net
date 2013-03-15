<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:variable name="translation"
        select="/ProjectXML/Properties/Translation/property" />
    <xsl:variable name="nextMonth" select="$translation[@name='nextMonth']" />
    <xsl:variable name="prevMonth" select="$translation[@name='prevMonth']" />
    <xsl:variable name="materialID" select="$translation[@name='materialID']"/>

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="ProjectXML">
        <xsl:apply-templates select="Content" />
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="MaterialResourceAllocationCalendar">
        <table width="100%" border="0">
            <tr>
                <td width="1%">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>MaterialResourceAllocations.jsp?module=260&amp;startDate=</xsl:text>
                            <xsl:value-of select="$prevMonth"/>
                            <xsl:text>&amp;materialID=</xsl:text>
                            <xsl:value-of select="$materialID"/>
                        </xsl:attribute>
                        <img src="../images/icons/prev_page_sm.gif" border="0"/>
                    </a>
                </td>
                <xsl:apply-templates select="Month"/>
                <td width="1%">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>MaterialResourceAllocations.jsp?module=260&amp;startDate=</xsl:text>
                            <xsl:value-of select="$nextMonth"/>
                            <xsl:text>&amp;materialID=</xsl:text>
                            <xsl:value-of select="$materialID"/>
                        </xsl:attribute>
                        <img src="../images/icons/next_page_sm.gif" border="0"/>
                    </a>
                </td>
                <td></td>
            </tr>
            <tr><td></td></tr>
            <tr>
                <xsl:call-template name="legend"/>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="Month">
        <td align="center" valign="top">
        <table class="monthTable">
            <tr><td colspan="7" align="center" class="tableHeader"><xsl:value-of select="DisplayName"/></td></tr>
            <xsl:apply-templates select="DaysOfWeek"/>
            <xsl:apply-templates select="Allocations/Row"/>
        </table>
        </td>
    </xsl:template>

    <xsl:template match="DaysOfWeek">
        <tr>
            <xsl:apply-templates select="DayOfWeek"/>
        </tr>
    </xsl:template>

    <xsl:template match="DayOfWeek">
        <td align="center" valign="middle" class="monthDayOfWeekLabel tableContent"><xsl:value-of select="."/></td>
    </xsl:template>

    <xsl:template match="Row">
        <tr><xsl:apply-templates select="Col"/></tr>
    </xsl:template>

    <!-- This template will draw the invidual "calendar dates" in the calendar -->
    <xsl:template match="Col">
        <xsl:variable name="cellClass">
            <xsl:choose>
                <xsl:when test="Allocation/PercentNumeric = 0">none</xsl:when>
                <xsl:when test="Allocation/PercentNumeric &lt; 100">some</xsl:when>
                <xsl:when test="Allocation/PercentNumeric = 100">full</xsl:when>
                <xsl:when test="Allocation/PercentNumeric &gt; 100">over</xsl:when>
                <xsl:otherwise>unknown</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <td align="center" valign="middle" title="{Allocation/Percent}">
            <xsl:attribute name="class">
                <xsl:value-of select="$cellClass"/>
                <xsl:text> dayText</xsl:text>
            </xsl:attribute>
            <xsl:value-of select="Allocation/Day"/>
        </td>
    </xsl:template>

    <xsl:template name="legend">
        <td colspan="5" align="center">
            <table width="50%" class="legendTable">
                <tr>
                    <td colspan="2" class="tableHeader"><xsl:value-of select="display:get('prm.resource.allocation.legend.title')"/></td>
                </tr>
                <tr>
                    <td width="1%" class="none legendColorCell"><img src="../images/spacers/trans.gif" height="15" width="15" border="0"/></td>
                    <td class="tableContent"><xsl:value-of select="display:get('prm.resource.allocation.legend.none.name')"/></td>
                </tr>
                <tr>
                    <td width="1%" class="some legendColorCell"><img src="../images/spacers/trans.gif" height="15" width="15" border="0"/></td>
                    <td class="tableContent"><xsl:value-of select="display:get('prm.resource.allocation.legend.some.name')"/></td>
                </tr>
                <tr>
                    <td width="1%" class="full legendColorCell"><img src="../images/spacers/trans.gif" height="15" width="15" border="0"/></td>
                    <td class="tableContent"><xsl:value-of select="display:get('prm.resource.allocation.legend.full.name')"/></td>
                </tr>
                <tr>
                    <td width="1%" class="over legendColorCell"><img src="../images/spacers/trans.gif" height="15" width="15" border="0"/></td>
                    <td class="tableContent"><xsl:value-of select="display:get('prm.resource.allocation.legend.over.name')"/></td>
                </tr>
            </table>
        </td>
    </xsl:template>
</xsl:stylesheet>