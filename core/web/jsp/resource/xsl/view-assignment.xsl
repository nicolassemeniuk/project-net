<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html"/>

    <xsl:template match="/assignment">
        <table border="0">
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.name')"/></td>
            <td><xsl:value-of select="object_name"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.space')"/></td>
            <td><xsl:value-of select="space_name"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.assignmenttype')"/></td>
            <td><xsl:value-of select="object_type_pretty"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.effort')"/></td>
            <td><xsl:value-of select="format:formatPercent(percent_assigned)"/></td>
        </tr>

        <tr><td colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>

        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.startdate')"/></td>
            <td><xsl:value-of select="format:formatISODateTime(start_time)"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.enddate')"/></td>
            <td><xsl:value-of select="format:formatISODateTime(end_time)"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.work')"/></td>
            <td><xsl:value-of select="work"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.workcomplete')"/></td>
            <td><xsl:value-of select="work_complete"/></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.resource.viewassignment.column.percentworkcomplete')"/></td>
            <td><xsl:value-of select="format:formatPercent(percent_complete)"/></td>
        </tr>

        </table>
    </xsl:template>
</xsl:stylesheet>