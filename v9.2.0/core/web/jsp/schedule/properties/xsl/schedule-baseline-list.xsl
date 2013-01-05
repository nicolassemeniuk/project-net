<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:template match="/BaselineList">
        <table width="97%">
            <tr class="tableHeader">
                <td></td>
                <td><xsl:value-of select="display:get('prm.schedule.baseline.fields.name')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.baseline.fields.creationdate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.baseline.fields.modifieddate')"/></td>
                <td><xsl:value-of select="display:get('prm.schedule.baseline.fields.isdefaultbaseline')"/></td>
            </tr>
            <tr>
                <td colspan="5" class="headerSep"></td>
            </tr>
            <xsl:apply-templates select="baseline"/>
        </table>
    </xsl:template>

    <xsl:template match="baseline">
        <tr class="tableContent">
            <td><input type="radio" name="id" value="{id}"/></td>
            <td><xsl:value-of select="name"/></td>
            <td><xsl:value-of select="format:formatISODateTime(creationDate)"/></td>
            <td><xsl:value-of select="format:formatISODateTime(modifiedDate)"/></td>
            <td>
                <xsl:if test="isDefaultForObject = '1'">
                    <img src="../../../images/check_green.gif"/>
                </xsl:if>
            </td>
        </tr>
        <tr>
            <td colspan="5" class="rowSep"></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>