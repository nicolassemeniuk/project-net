<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html"/>

    <!-- Declare external variables -->
    <xsl:param name="JspRootUrl" />
    
    <xsl:template match="/">
    <div>
        <table width="100%" cellpadding="0" cellspacing="0" class="row-content" name="tableWithEvenRows">
            <tr class="table-header">
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.assignment')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.project')"/></td>
                <!-- <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.startdate')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.duedate')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.workassignedtoyou')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.workyouvecompleted')"/></td> -->
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.percentcomplete')"/></td>
                <td><xsl:value-of select="display:get('prm.resource.assignments.current.column.lastupdated')"/></td>
            </tr>
            <!-- tr>
                <td colspan="4" class="headerSep"></td>
            </tr -->
            <xsl:apply-templates/>
        </table>
    </div>
    </xsl:template>

    <xsl:template match="assignment">
        <xsl:variable name="lateStyle">
            <xsl:if test="is_late">color:red</xsl:if>
        </xsl:variable>
        <xsl:variable name="anchorLateStyle">
            <xsl:choose>
                <xsl:when test="is_late">color:red</xsl:when>
                <xsl:otherwise>color:black</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>        

        <!-- <tr class="tableContent" style="{$lateStyle}"> -->
        <tr style="{$lateStyle}">
            <input type="hidden" name="objectID" value="{object_id}" />
            <td>
                <a id="currentAssignments_{object_name}" style="{$anchorLateStyle}" href="{$JspRootUrl}/servlet/AssignmentController/View?module=160&amp;objectID={object_id}&amp;personID={person_id}">
                    <xsl:value-of select="object_name"/>
                </a>
            </td>
            <td><xsl:value-of select="space_name"/></td>
            <!-- <td><xsl:value-of select="format:formatISODate(start_time)"/></td>
            <td><xsl:value-of select="format:formatISODate(due_date)"/></td>
            <td><xsl:value-of select="work"/></td>
            <td><xsl:value-of select="work_complete"/></td> -->
            <td><xsl:value-of select="format:formatPercent(percent_complete, 0, 2)"/></td>
            <td><xsl:value-of select="modified_date"/></td>
        </tr>
        <!-- tr>
            <td colspan="4" class="rowSep"></td>
        </tr -->
    </xsl:template>
</xsl:stylesheet>