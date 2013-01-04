<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
<xsl:output method="html"/>
<xsl:template match="/">
    <table border="0" cellpadding="0" cellspacing="0" width="100%" name="tableWithEvenRows">
        <tr align="left" class="table-header">	
            <td class="table-header"><xsl:value-of select="display:get('prm.form.modified.formname.column')"/></td>
            <td class="table-header"><xsl:value-of select="display:get('prm.form.modified.formdata.column')"/></td>
			<td class="table-header"><xsl:value-of select="display:get('prm.form.modified.workspace.column')"/></td>
            <td class="table-header"><xsl:value-of select="display:get('prm.form.modified.datecreated.column')"/></td>
			<td class="table-header"><xsl:value-of select="display:get('prm.form.modified.datemodified.column')"/></td>
			<td class="table-header"><xsl:value-of select="display:get('prm.form.modified.modifiedby.column')"/></td>
        </tr>
        <!-- tr class="tableLine">
            <td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="2" border="0"/></td>
        </tr -->
        <xsl:apply-templates select="FormMenu/FormMenuEntry"/>
        <xsl:if test="count(FormMenu/FormMenuEntry/FormDataList)=0">
            <xsl:call-template name="NoFormDataList"/>
        </xsl:if>
    </table>
</xsl:template>

<xsl:template match="FormMenuEntry">
    <xsl:apply-templates select="FormDataList" />
</xsl:template>

<xsl:template match="FormDataList">
	<xsl:apply-templates select="FormData" />
</xsl:template>
 
<xsl:template match="FormData">
    <tr align="center" class="tableContent"> 
        <td align="left" class="tableContent">
            <xsl:value-of select="../../name" />
        </td>
        <td align="left" class="tableContent">
            <a href="../form/FormEdit.jsp?module=30&amp;action=1&amp;id={id}&amp;spaceID={../../Space/id}">
                <xsl:value-of select="../../abbreviation" />
                <xsl:text>-</xsl:text><xsl:value-of select="seqNum" />
            </a>
        </td>
        <td align="left" class="tableContent">
            <xsl:value-of select="workSpaceName" />
        </td>
        <td align="left" class="tableContent">
            <xsl:value-of select="dateCreated"/>
        </td>
        <td align="left" class="tableContent">
            <xsl:value-of select="dateModified"/>
        </td>
        <td align="left" class="tableContent">
            <xsl:choose>
                <xsl:when test="modifier_name != '-1'"><xsl:value-of select="modifier_name"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="creator_email"/></xsl:otherwise>
            </xsl:choose>
        </td>
    </tr>  
    <!-- tr class="tableLine">
        <td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"/></td>
    </tr -->
</xsl:template>
	
<xsl:template name="NoFormDataList">
    <tr class="tableContent" align="left">
        <td class="tableContent" colspan="6"><xsl:value-of select="display:get('prm.form.modified.noforms.message')"/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>
