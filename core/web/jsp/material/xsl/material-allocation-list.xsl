<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:variable name="translation"
        select="/ProjectXML/Properties/Translation/property" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="ProjectXML">
        <xsl:apply-templates select="Content" />
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="assignment_list">
        <table border="0" width="100%">
            <tr>
                <td class="tableHeader"><xsl:value-of select="display:get('prm.material.allocationlist.dates.name')"/></td>
                <td class="tableHeader"><xsl:value-of select="display:get('prm.material.allocationlist.spacename.name')"/></td>
                <td class="tableHeader"><xsl:value-of select="display:get('prm.material.allocationlist.percentassigned.name')"/></td>
                <td class="tableHeader"><xsl:value-of select="display:get('prm.material.allocationlist.taskname.name')"/></td>
            </tr>
            <tr>
                <td class="headerSep" colspan="4"></td>
            </tr>
            <xsl:apply-templates select="assignment"/>
        </table>
    </xsl:template>

    <xsl:template match="assignment">
        <xsl:variable name="timeRange">
            <xsl:choose>
                <xsl:when test="one_day_assignment">
                    <xsl:value-of select="format:formatISODate(start_time)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="format:formatISODate(start_time)"/> - <xsl:value-of select="format:formatISODate(end_time)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <tr>
            <td class="tableContent"><xsl:value-of select="$timeRange"/></td>
            <td class="tableContent"><xsl:value-of select="space_name"/></td>
            <td class="tableContent"><xsl:value-of select="format:formatPercent(percent_assigned)"/></td>
            <td class="tableContent"><xsl:value-of select="object_name"/></td>
        </tr>
        <tr>
            <td class="rowSep" colspan="4"></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>