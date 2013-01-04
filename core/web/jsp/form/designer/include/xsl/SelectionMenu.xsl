<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

    <xsl:output method="html"/>

    <!-- Build translation properties node list -->
    <xsl:variable name="translation" select="/ProjectXML/Properties/Translation/property"/>

    <xsl:template match="ProjectXML">
        <xsl:apply-templates select="Content" />
    </xsl:template>

    <xsl:template match="Content">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="FieldDomain">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr align="left" class="tableHeader">
                <td class="tableHeader" width="10"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td class="tableHeader"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.name.column')"/></td>
                <td class="tableHeader" align="center"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.default.column')"/></td>
                <td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.action.column')"/></td>
                <td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            </tr>
            <tr align="left" class="tableLine">
                <td colspan="5"><img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
            </tr>
            <xsl:apply-templates select="FieldDomainValue" />
            <xsl:if test="count(FieldDomainValue)=0">
                <xsl:call-template name="NoFieldDomainValues"/>
            </xsl:if>
            <!-- Show an add button so the user can add additional elements -->
            <xsl:if test="count(FieldDomainValue)>1">
            <tr>
                <td></td>
                <td><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.nodefault.label')"/></td>
		            <td class="tableContent" align="center">
		                <xsl:element name="input">
		                    <xsl:attribute name="type">radio</xsl:attribute>
		                    <xsl:attribute name="name">DefaultValue</xsl:attribute>
		                    <xsl:attribute name="value">-1</xsl:attribute>
		                    <xsl:if test="starts-with(noDefault,'true')">
	                        	<xsl:attribute name="checked">true</xsl:attribute>
	                        </xsl:if>
		                </xsl:element>
	            </td>                
                <td></td>
                <td></td>
            </tr>
            </xsl:if>    
            <xsl:call-template name="FieldDomainValueSeparator"/>        
            <tr>
                <td></td>
                <td>
                    <input type="text" name="DomainValueName" size="20" maxlength="80" value=""/>
                    <xsl:element name="input">
						<xsl:attribute name="type">button</xsl:attribute>
						<xsl:attribute name="name">add</xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.add.button.label')"/></xsl:attribute>
						<xsl:attribute name="onClick">create();</xsl:attribute>
					</xsl:element>
                </td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <xsl:call-template name="FieldDomainValueSeparator"/>
        </table>
    </xsl:template>

    <xsl:template match="FieldDomainValue">
        <tr>
            <td align="center"><input type="radio" name="selected" value="{id}"/></td>
            <td class="tableContent"><xsl:value-of select="name"/></td>
            <td class="tableContent" align="center">
                <xsl:element name="input">
                    <xsl:attribute name="type">radio</xsl:attribute>
                    <xsl:attribute name="name">DefaultValue</xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="id"/></xsl:attribute>
                    <xsl:if test="starts-with(default,'true')">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </xsl:element>
            </td>
            <td class="tableContent">
                <a href="javascript:promoteMenuChoice({id});">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/arrow_white_up.gif</xsl:attribute>
					<xsl:attribute name="width">20</xsl:attribute>
					<xsl:attribute name="height">16</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
					<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.promote.alttext')"/></xsl:attribute>
				</xsl:element>
				</a>
                <a href="javascript:demoteMenuChoice({id});">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/arrow_white_down.gif</xsl:attribute>
					<xsl:attribute name="width">20</xsl:attribute>
					<xsl:attribute name="height">16</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
					<xsl:attribute name="alt"><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.demote.alttext')"/></xsl:attribute>
				</xsl:element>
				</a>
            </td>
            <td class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        </tr>
        <xsl:call-template name="FieldDomainValueSeparator"/>
    </xsl:template>

    <xsl:template name="FieldDomainValueSeparator">
        <!-- Draw a line between each domain value -->
        <tr class="tableLine">
            <td colspan="5">
                <xsl:element name="img">
                    <xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
                    <xsl:attribute name="width">1</xsl:attribute>
                    <xsl:attribute name="height">1</xsl:attribute>
                    <xsl:attribute name="border">0</xsl:attribute>
                </xsl:element>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="NoFieldDomainValues">
        <tr>
            <td align="center" colspan="4" class="tableContent"><i><xsl:value-of select="display:get('prm.form.designer.fieldedit.menuchoices.novalues.message')"/></i></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
