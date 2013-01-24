<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
	
<xsl:output method="html"/> 
<xsl:template match="/">
<table width="100%" cellpadding="0" border="0" cellspacing="0">
	<tr class="tableHeader" align="left">
      		<th nowrap="1" valign="bottom" class="tableHeader">Person</th>	
      		<th nowrap="1" valign="bottom" class="tableHeader">Status</th>
      		<th nowrap="1" valign="bottom" class="tableHeader">Percent</th>
		<th nowrap="1" valign="bottom" class="tableHeader">Assignment Role</th>
      		<th nowrap="1" width="20" valign="bottom" class="tableHeader">Owner</th>
	</tr>
	<tr class="tableLine">
		<td  colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:apply-templates select="assignment_list/assignment" />
</table>
</xsl:template>

<xsl:template match="assignment_list/assignment">
	<xsl:variable name="is_owner" select = "./primary_owner" />
      	<tr class="tableContent">
			<td class="tableContent"><xsl:value-of select="./person_name"/></td>
			<td class="tableContent"><xsl:value-of select="./status"/></td>
			<td class="tableContent"><xsl:value-of select="format:formatPercent(percent_assigned)"/></td>
			<td class="tableContent"><xsl:value-of select="./role"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
			<td class="tableContent"><xsl:if test="$is_owner = 1 ">
			     <img SRC="../images/blk_check.gif" BORDER="0" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</xsl:if></td>
        </tr>
        
  		<tr class="tableLine">
  		<td  colspan="5" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>
	