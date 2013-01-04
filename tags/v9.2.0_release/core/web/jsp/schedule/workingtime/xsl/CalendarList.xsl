<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:variable name="JSPRootURL" select="/ProjectXML/Content/WorkingTimeCalendarDefinitions/JSPRootURL"/>

    <xsl:template match="/">
        <xsl:apply-templates select="/ProjectXML/Content"/>
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates select="WorkingTimeCalendarDefinitions"/>
    </xsl:template>

    <xsl:template match="WorkingTimeCalendarDefinitions">
        <table width="100%">
            <thead class="tableHeader">
                <tr>
                    <td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                    <td><xsl:value-of select="display:get('prm.schedule.workingtime.list.column.name.label')"/></td>
                    <td><xsl:value-of select="display:get('prm.schedule.workingtime.list.column.basedon.label')"/></td>
                </tr>
                <tr><td class="headerSep" colspan="3"></td></tr>
            </thead>
            <tbody class="tableContent">
                <xsl:apply-templates select="WorkingTimeCalendarDefinition" />
                <xsl:if test="count(WorkingTimeCalendarDefinition)=0">
                    <xsl:call-template name="none" />
                </xsl:if>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="WorkingTimeCalendarDefinition">
        <tr>
            <!-- Radio option to select entry -->
            <td><input type="radio" name="selected" value="{ID}"/></td>

            <!-- Name of entry; modified if it is a default calendar -->
            <td>
                <a href="{$JSPRootURL}/servlet/ScheduleController/WorkingTime/View?module=60&amp;calendarID={ID}">
                    <xsl:choose>
                        <xsl:when test="ID = ../DefaultCalendarID">
                            <xsl:value-of select="display:get('prm.schedule.workingtime.list.default', DisplayName)"/>
                        </xsl:when>
                        <xsl:otherwise><xsl:value-of select="DisplayName" /></xsl:otherwise>
                    </xsl:choose>

                </a>
            </td>

            <!-- Displays the name of the parent calendar, if it has one -->
            <td>
                <xsl:choose>
                    <xsl:when test="ParentCalendarID!=''">
                        <xsl:variable name="ParentCalendarID" select="ParentCalendarID" />
                        <xsl:value-of select="../WorkingTimeCalendarDefinition[ID=$ParentCalendarID]/DisplayName" />
                    </xsl:when>
                    <xsl:otherwise><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
        <tr><td  class="rowSep" colspan="3"></td></tr>
    </xsl:template>

    <xsl:template name="none">
        <tr class="tableContent">
            <td colspan="3"><xsl:value-of select="display:get('prm.schedule.workingtime.list.none')"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>