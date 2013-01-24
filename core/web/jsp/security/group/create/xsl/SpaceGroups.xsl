<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="ProjectXML">
        <xsl:apply-templates select="Content"/>
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="SpaceGroupsCollection">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <xsl:apply-templates select="SpaceGroups"/>
        </table>
    </xsl:template>

    <xsl:template match="SpaceGroups">
        <tr class="channelHeader">
            <td width="1%">
                <img src="../../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/>
            </td>
            <td class="channelHeader" colspan="2">
                <xsl:value-of select="Space/name"/>
            </td>
            <td align="right" width="1%">
                <img src="../../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/>
            </td>
        </tr>
        <xsl:apply-templates select="groupList"/>
        <tr><td colspan="4"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
    </xsl:template>

    <xsl:template match="groupList">
        <xsl:apply-templates select="group"/>
    </xsl:template>

    <xsl:template match="group">
        <!-- Insert a checkbox and the group name -->
        <tr>
            <td>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td>
            <td class="tableContent" colspan="2">
                <input type="checkbox" name="selected" value="{Space/id}-{id}"/>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <xsl:value-of select="group_name"/>
            </td>
            <td>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
