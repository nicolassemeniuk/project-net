<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:template match="/shareList/share">
        <tr class="tableContent">
            <td><input type="checkbox" name="selected" value="{@id}"/></td>
            <td>
                <!-- Draw an icon for this type of object, if we've created one.-->
                <xsl:choose>
                    <xsl:when test="@type='plan'">
                        <img src="../../images/crossspace/Workplan.gif"/>
                        <img src="../../images/spacers/trans.gif" height="1" width="4"/>
                    </xsl:when>
                    <xsl:when test="@type='task'">
                        <img src="../../images/crossspace/TaskIcon.gif"/>
                        <img src="../../images/spacers/trans.gif" height="1" width="4"/>
                    </xsl:when>
                </xsl:choose>

                <xsl:value-of select="@name"/>
            </td>
            <td><xsl:value-of select="@count"/></td>
            <td><xsl:value-of select="./permissionType"/></td>
            <td>
                <xsl:apply-templates select="exportingSpace"/>
            </td>
        </tr>
        <tr>
            <td class="tableLine" colspan="5"></td>
        </tr>
    </xsl:template>

    <xsl:template match="exportingSpace[position()=1]">
            <xsl:value-of select="@name"/>
    </xsl:template>
    <xsl:template match="exportingSpace[position()!=1]">
        <tr class="tableContent">
            <td colspan="4"></td>
            <td>
                <xsl:value-of select="@name"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>