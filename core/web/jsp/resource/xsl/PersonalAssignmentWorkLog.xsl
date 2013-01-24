<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:template match="/">
        <xsl:apply-templates select="AssignmentWorkLog" />
    </xsl:template>

    <xsl:template match="AssignmentWorkLog">

        <table width="100%" border="0">
            <thead>
                <tr class="tableHeader" align="left">
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.logdate')"/>
                    </th>
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.hoursworked')"/>
                    </th>
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.percentcomplete')"/>
                    </th>
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.totalremainingwork')"/>
                    </th>
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.startdate')"/>
                    </th>
                    <th>
                        <xsl:value-of select="display:get('prm.resource.viewassignment.worklog.column.enddate')"/>
                    </th>
                </tr>
                <tr class="tableLine">
                    <td colspan="12" class="headerSep"></td>
                </tr>
            </thead>
            <tbody>
                <xsl:apply-templates select="AssignmentWorkLogEntry" />
            </tbody>
        </table>

    </xsl:template>

    <xsl:template match="AssignmentWorkLogEntry">
        <tr class="tableContent" align="left">
            <td><xsl:value-of select="format:formatISODateTime(LogDate)"/></td>
            <td><xsl:value-of select="HoursWorkedFormatted"/></td>
            <td><xsl:value-of select="format:formatPercentDecimal(PercentComplete)"/></td>
            <td><xsl:value-of select="TotalRemainingWorkFormatted"/></td>
            <td><xsl:value-of select="format:formatISODateTime(StartDate)"/></td>
            <td><xsl:value-of select="format:formatISODateTime(EndDate)"/></td>
        </tr>
        <tr class="tableLine">
            <td colspan="12" class="rowSep"></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
