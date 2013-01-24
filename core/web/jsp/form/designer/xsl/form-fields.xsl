<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html"/>

<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>

<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>

<xsl:template match="Form">
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr class="channelHeader" align="left">
		<td align="left" class="channelHeader" width="1%"><img src="../../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
		<th class="channelHeader" colspan="7"><xsl:value-of select="display:get('prm.form.designer.fieldedit.channel.defined.title')"/></th>
		<td align="right" class="channelHeader" width="1%"><img src="../../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"/></td>
	</tr>
	<tr>
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td></td>
		<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.form.designer.fieldedit.field.column')"/></td>
		<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.form.designer.fieldedit.type.column')"/></td>
		<td class="tableHeader" align="center"><xsl:value-of select="display:get('prm.form.designer.fieldedit.row.column')"/></td>
		<td class="tableHeader" align="left"><xsl:value-of select="display:get('prm.form.designer.fieldedit.column.column')"/></td>
        <td class="tableHeader" align="center"><xsl:value-of select="display:get('prm.form.designer.fieldedit.required.column')"/></td>
        <td class="tableHeader" align="center">
			<xsl:choose>
                <xsl:when test="support_external_access = 'true'">
					<xsl:value-of select="display:get('prm.form.designer.fieldedit.hidden.column')"/>                
                </xsl:when>
                <xsl:otherwise>
					<img src="../../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>        
        	
        </td>
        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr>
        <td></td>
        <td colspan="7" class="headerSep"></td>
        <td></td>
	</tr>

		<xsl:apply-templates select="FormField"/>
	<tr>
        <td></td>
		<td colspan="7" align="center"></td>
        <td></td>
	</tr>
	</table>

</xsl:template>

<xsl:template match="Form/FormField">
  <xsl:if test="./designable='true'">
	<tr class="tableContent" align="left">
        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableContent">
			<xsl:element name="input">
				<xsl:attribute name="type">radio</xsl:attribute>
				<xsl:attribute name="name">selected</xsl:attribute>
				<xsl:attribute name="value"><xsl:value-of select="./id"/></xsl:attribute>
			</xsl:element>
		</td>
		<td class="tableContent">
			<xsl:element name="a">
				<xsl:attribute name="href">javascript:modify('<xsl:value-of select="./id"/>');</xsl:attribute>
				<xsl:value-of select="./label"/>
			</xsl:element>
		</td>
		<td class="tableContent"><xsl:value-of select="./element_label"/></td>
		<td class="tableContent" align="center"><xsl:value-of select="format:formatNumber(row_num)"/></td>
		<td class="tableContent" align="left"><xsl:value-of select="./column_name"/></td>
        <td class="tableContent" align="center"><xsl:if test="required='true'"><img src="../../images/blk_check.gif" height="15" width="20" border="0"/></xsl:if></td>
        <td class="tableContent" align="center">
			<xsl:choose>
                <xsl:when test="support_external_access = 'true'">
		        	<xsl:if test="hiddenForEaf='false'">
		        		<img src="../../images/default/form/eyeicon_1.gif" height="15" width="20" border="0"/>
		        	</xsl:if>                
                </xsl:when>
                <xsl:otherwise>
					<img src="../../images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>                    
        </td>
        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	<tr>
        <td></td>
        <td colspan="7" class="rowSep"></td>
        <td></td>
	</tr>
 </xsl:if>
</xsl:template>

</xsl:stylesheet>
