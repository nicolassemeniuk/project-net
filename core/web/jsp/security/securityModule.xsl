<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
    <xsl:output method="html"/>

	<xsl:template match="PermissionList">
		<table width="100%" border="0" cellpadding="0" cellspacing="0" >
		    <tr class="channelHeader">
		      <td width="1%"><img src="../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
		      <th class="channelHeader" align="left" width="40%"><xsl:value-of select="display:get('prm.security.main.module.module.column')"/></th>
		      <th class="channelHeader" colspan="6" align="left" nowrap="true"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text><xsl:value-of select="display:get('prm.security.main.module.actions.column')"/></th>
		      <td align="right" width="1%"><img src="../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>

		    </tr>

		    <tr>
		    <td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		      <td width="21%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		      <xsl:apply-templates select="permittedActions/action" /> 
		      <td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		    </tr>
		    <tr class="tableLine">
		    	<td  colspan="8" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		    </tr>
		    <xsl:apply-templates select="line" /> 
		  </table>
	</xsl:template>


	<xsl:template match="PermissionList/permittedActions/action">
		  <xsl:variable name="description" select = "." />
		  <td class="tableHeader"><xsl:value-of select="$description" /></td>
       </xsl:template>

	<xsl:template match = "PermissionList/line">
		 <tr>
		 <td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<xsl:variable name="module_id" select = "id" />
			      <td width="21%" nowrap="true" class="tableContent">
			          <xsl:value-of select="objectDesc" />
			      </td>


			<xsl:for-each select = "objectPermission/permission">

			     <xsl:variable name="is_checked" select = "." />
  			     <xsl:variable name="checked">checked</xsl:variable>
			     
			      <td width="10%"> 
					
					<xsl:element name="input">
						<xsl:attribute name="type">checkbox</xsl:attribute>
						<xsl:attribute name="name"> <xsl:value-of select = "$module_id" /> </xsl:attribute>
						<xsl:attribute name="value"><xsl:value-of select="@bitmask"/></xsl:attribute>
					
						<xsl:if test="$is_checked = $checked ">
						       <xsl:attribute name="checked" />
						</xsl:if>

					</xsl:element>
					
			      </td>
	
			</xsl:for-each>
		<td width="1%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		 </tr>
	    <tr class="tableLine">
		<td  colspan="8" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	    </tr>

	</xsl:template>

</xsl:stylesheet>
