<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html"/>

    <xsl:template match="/MetricsGroup">
        <table width="100%" cellpadding="0" cellspacing="0" border="0" name="tableWithEvenRowsWithTwoHRows">
            <tr class="table-header">
                <td style="vertical-align: middle" rowspan="2"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.metric')"/></td>
                <td colspan="2" style="padding-right:3px; text-align:center; "><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.week')"/></td>
                <td colspan="2" style="text-align:center;"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.month')"/></td>
            </tr>
            <tr class="table-header">
                <td style="width:25px; text-align:center;"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.week.quantity')"/></td>
                <td style="width:50px; text-align:center;"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.week.work')"/></td>
                <td style="width:25px; text-align:center;"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.month.quantity')"/></td>
                <td style="width:50px; text-align:center;"><xsl:value-of select="display:get('prm.personal.assignmentmetrics.column.month.work')"/></td>
            </tr>
            <!-- tr>
                <td colspan="6" class="headerSep"></td>
            </tr -->

            <xsl:apply-templates select="PersonalAssignmentMetrics" />

        </table>
    </xsl:template>



    <xsl:template match="PersonalAssignmentMetrics">

        <!-- xsl:choose>
            <xsl:when test="@type ='groupTotal'">
                <tr><td colspan="6" class="headerSep"></td></tr>
            </xsl:when>
        </xsl:choose -->

        <tr class="tableContent">

            <xsl:choose>

                <xsl:when test="@type ='task'">
                    <td><xsl:value-of select="TaskAssignmentMetricsType/DisplayName"/></td>
                </xsl:when>

                <xsl:when test="@type='meeting'">
                    <td><xsl:value-of select="DisplayName"/></td>
                </xsl:when>

                <xsl:when test="@type ='groupTotal'">
                    <td><xsl:value-of select="DisplayName"/></td>
                </xsl:when>

            </xsl:choose>

            <xsl:apply-templates select="WeekAndMonthMetric/QuantityAndWorkMetric[@type='week']" />
            <xsl:apply-templates select="WeekAndMonthMetric/QuantityAndWorkMetric[@type='month']" />
        </tr>
        <!-- tr>
            <td colspan="9" class="rowSep"></td>
        </tr -->
    </xsl:template>


    <xsl:template match="QuantityAndWorkMetric">
        <td style="text-align:center;"><xsl:value-of select="format:formatNumber(Quantity)"/></td>
        <td style="text-align:center;"><xsl:value-of select="Work"/></td>
    </xsl:template>


</xsl:stylesheet>