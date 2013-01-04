<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">
    
    <xsl:output method="html"/>
    
    <xsl:template match="/scheduleHistory">
        <table width="97%">
            <tr class="tableHeader">
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.asof')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.baselinestartdate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.startdate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.startvariance')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.baselineenddate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.enddate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.history.columns.endvariance')"/></td>
            </tr>
            <tr>
                <td colspan="8" class="headerSep"></td>
            </tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    
    
    <xsl:template match="baseline">
        <tr class="tableContent" style="background-color:#CCCCCC; font-weight: bold;">
            <td colspan="8">Baseline: <xsl:value-of select="name"/></td>
        </tr>
    </xsl:template>
    
    <xsl:template match="pre-baseline">
        <tr class="tableContent" style="background-color:#CCCCCC; font-weight: bold;">
            <td colspan="8">Plans versions before baseline was established</td>
        </tr>
    </xsl:template>
    
    <xsl:template match="schedule">

        <xsl:variable name="startVarianceClass">
             <xsl:choose>
                <xsl:when test="start_date_variance_amount &lt; 0">
                  negativeVariance
                </xsl:when>
                <xsl:when test="start_date_variance_amount &gt; 0">
                  positiveVariance
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="endVarianceClass">
                     <xsl:choose>
                        <xsl:when test="end_date_variance_amount &lt; 0">
                          negativeVariance
                        </xsl:when>
                        <xsl:when test="end_date_variance_amount &gt; 0">
                          positiveVariance
                        </xsl:when>
                    </xsl:choose>
                </xsl:variable>


        <tr class="tableContent">
            <td>
                    <xsl:value-of select="format:formatISODateTime(lastModified)"/>
            </td>
            <td>
                <div title="{baselineStart}">
                    <xsl:value-of select="baselineStart"/>
                </div>
            </td>
            <td>
                <div title="{start_date}">
                    <xsl:value-of select="start_date"/>
                </div>
            </td>
                  <td class="{$startVarianceClass}"><xsl:value-of select="start_date_variance"/></td>
            <td>
                <div title="{baselineEnd}">
                    <xsl:value-of select="baselineEnd"/>
                </div>
            </td>
            <td>
                <div title="{end_date}">
                    <xsl:value-of select="end_date"/>
            	</div>
            </td>
            <td class="{$endVarianceClass}"><xsl:value-of select="end_date_variance"/></td>
        </tr>
        <tr>
            <td colspan="8" class="rowSep"></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>