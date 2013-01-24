<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    	xmlns:format="xalan://net.project.util.XSLFormat">
    <xsl:output method="html"/>

    <xsl:template match="/">
        <table border="0" width="100%">
        <xsl:apply-templates select="/objectInfo/mySpaces"/>
        </table>
    </xsl:template>

    <xsl:template match="mySpaces">
        <tr>
            <td>
                <table cellpadding="0" cellspacing="0" border="0" row="1" column="1" width="100%">
                <tr id="row1" childRows="{./children}" class="tableContent">
                    <td>
                        <img src="../images/unexpand.gif" id="row1Image" onClick="collapse('def', 1);"/>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <xsl:value-of select="display:get('prm.crossspace.objectlist.myspaces.label')"/>
                    </td>
                </tr>
                <xsl:apply-templates select="child::object"/>
                </table>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="object[@type='project']">
        <tr id="row{@id}" childRows="{./children}" class="tableContent" visibleState="visible">
            <td>
                <img src="../images/spacers/trans.gif" height="1" width="{count(ancestor::*)*15}"/>
                <img src="../images/expand.gif" id="row{@id}Image" onClick="expand('def', {@id})"/>
                <img src="../images/spacers/trans.gif" height="1" width="4"/>
                <xsl:value-of select="@name"/>
            </td>
        </tr>
        <xsl:apply-templates select="child::object"/>
    </xsl:template>

    <xsl:template match="object">
        <tr id="row{@id}" childRows="{./children}" class="hidden" visibleState="hidden">
            <td>
                <img src="../images/spacers/trans.gif" height="1" width="{count(ancestor::*)*15}"/>
                <xsl:if test="count(child::object) > 0">
                    <img src="../images/spacers/trans.gif" height="1" width="4"/>
                    <img src="../images/expand.gif" id="row{@id}Image" onClick="expand('def', {@id})"/>
                </xsl:if>
                <xsl:if test="@enabled>0">
                    <img src="../images/spacers/trans.gif" height="1" width="4"/>
                    <input type="checkbox" name="{@type}" value="{@id}" id="{@id}checkbox" actionsAllowed="{@actionsAllowed}" onClick="shareChanged();"/>
                </xsl:if>

                <!-- Draw an icon for this type of object, if we've created one.-->
                <xsl:choose>
                    <xsl:when test="@type='plan'">
                        <img src="../images/spacers/trans.gif" height="1" width="4"/>
                        <img src="../images/crossspace/Workplan.gif"/>
                    </xsl:when>
                    <xsl:when test="@type='task'">
                        <img src="../images/spacers/trans.gif" height="1" width="4"/>
                        <img src="../images/crossspace/TaskIcon.gif"/>
                    </xsl:when>
                </xsl:choose>

                <!-- Draw the text for the object. -->
                <img src="../images/spacers/trans.gif" height="1" width="4"/>
                <label for="{@id}checkbox"><xsl:value-of select="@name"/></label>

                <xsl:apply-templates select="child::object"/>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>