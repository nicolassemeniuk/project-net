<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display" >

    <xsl:output method="html"/>

    <xsl:variable name="SpacerImage" select="MenuList/SpacerImage"/>
    <xsl:variable name="SmallBallImage" select="MenuList/SmallBallImage"/>
    <xsl:variable name="MediumBallImage" select="MenuList/MediumBallImage"/>

    <xsl:template match="MenuList">
            <xsl:apply-templates select="Search"/>
            <xsl:apply-templates select="Menu|Separator">
                    <xsl:with-param name="depth" select="1"/>
            </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Search">
    </xsl:template>

    <xsl:template match="ObjectTypeList">
        <select name="otype" class="searchModule">
            <option value="all" selected="true">
                <xsl:value-of select="display:get('prm.global.search.manager.option.all.name')" />
            </option>
            <xsl:apply-templates select="ObjectType"/>
        </select>
    </xsl:template>

    <xsl:template match="ObjectType">
        <option value="{Type}"><xsl:value-of select="Description"/></option>
    </xsl:template>

    <xsl:template match="Menu">
        <xsl:param name="depth"/>

        <xsl:variable name="tdclass">
            <xsl:choose>
                <xsl:when test="$depth=1">leftNavHeader</xsl:when>
                <xsl:otherwise>leftNavLink</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="ball">
            <xsl:choose>
                <xsl:when test="$depth=1"><xsl:value-of select="$MediumBallImage" /></xsl:when>
                <xsl:otherwise><xsl:value-of select="$SmallBallImage" /></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="ballwidth">
            <xsl:choose>
                <xsl:when test="$depth=1"><xsl:value-of select="13" /></xsl:when>
                <xsl:otherwise><xsl:value-of select="9" /></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
                <xsl:choose>
                    <xsl:when test="string-length(URL)>0">
			<span><a href="{URL}"><xsl:value-of select="Label"/></a></span>
                      </xsl:when>
                    <xsl:otherwise>
			<span><xsl:value-of select="Label"/></span>
                    </xsl:otherwise>
                </xsl:choose>
        <xsl:apply-templates select="Menu">
            <xsl:with-param name="depth" select="$depth+1"/>
        </xsl:apply-templates>
        <xsl:if test="$depth=1">
            <xsl:call-template name="Separator"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="Separator" match="Separator">
    </xsl:template>
</xsl:stylesheet>
