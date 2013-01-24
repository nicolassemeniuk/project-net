<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >
<xsl:output method="html"/>

	<xsl:template match="/DeliverableList">
	<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
	<tr class="tableHeader" align="center">
	<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.viewphase.deliverables.name.label')"/>
	</td>
	<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.description.label')"/>
	</td>
	<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.status.label')"/>
	</td>
	<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.optional.label')"/>
	</td>
	<td align="left" nowrap="true" class="tableHeader"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
	</td>
	</tr>
	
	<tr class="tableLine">
	      <td colspan="100" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr>
	<xsl:for-each select="deliverable">	
	<tr  align="center" class="tableContent">
	<td  nowrap="true" align="left" valign="top" class="tableContent">
	<xsl:element name="input">
		<xsl:attribute name="type">radio</xsl:attribute>
		<xsl:attribute name="name">selected</xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="./deliverable_id"/></xsl:attribute>
	</xsl:element>
	<xsl:element name="A">
		<xsl:attribute name="href">javascript:viewDeliverable(<xsl:value-of select="./deliverable_id"/>);</xsl:attribute>
		<xsl:value-of select="deliverable_name"/>
	</xsl:element>
	<xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
	</td>
	<td align="left" valign="top" class="tableContent"><xsl:value-of select="deliverable_desc"/><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
	<td nowrap="true" valign="top" align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text><xsl:value-of select="status"/></td>
	<td nowrap="true" valign="top" align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
	<xsl:choose>
		<xsl:when test="is_optional = 0"><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.optional.status.no.name')"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="display:get('prm.project.process.viewphase.deliverables.optional.status.yes.name')"/></xsl:otherwise>
	</xsl:choose>
	</td>
	<td>
		<xsl:choose>
               <xsl:when test="./activeEnvelopeId > 0 ">
                   <img src="{jsp_root_url}/images/document/workflow-button.gif" height="18" width="19" border="0" alt=""/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
               </xsl:when>
               <xsl:otherwise>
                   <img src="{jsp_root_url}/images/spacers/trans.gif" height="18" width="19" border="0"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
               </xsl:otherwise>
        </xsl:choose>
       </td>	
	</tr>
	<tr class="tableLine">
	<td  colspan="100" class="tableLine"><img src="{jsp_root_url}/images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
	</xsl:for-each>
	</table>
</xsl:template>
</xsl:stylesheet>