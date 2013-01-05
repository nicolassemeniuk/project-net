<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

    <xsl:output method="html"/>

	<xsl:template match="/deliverable">
		<table border="0" align="left" width="100%"  cellpadding="0" cellspacing="0">
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.name.label')"/>
			</td>
			<td colspan="3" align="left" valign="top" class="tableContent"><xsl:value-of select="deliverable_name"/>
			</td>
		</tr>
		
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.description.label')"/>
			</td>
			<td colspan="3" align="left" valign="top" class="tableContent"><xsl:value-of select="deliverable_desc"/>
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
			</td>
		</tr>
			
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.status.label')"/>
			</td>
			<td colspan="3" nowrap="true" align="left" class="tableContent"><xsl:value-of select="status"/>
			</td>
		</tr>
		
		<tr>
			<td align="left" nowrap="true" class="tableHeader"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.optional.label')"/>
			</td>
			<td colspan="3" nowrap="true" align="left" class="tableContent">
			<xsl:choose>
				<xsl:when test="is_optional = 0"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.optional.status.no.name')"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.optional.status.yes.name')"/></xsl:otherwise>
			</xsl:choose>
			</td>
		</tr>
		
		<tr>
			<td align="left" nowrap="true" class="tableHeader" colspan="4"><xsl:value-of select="display:get('prm.project.process.viewdeliverable.info.comments.label')"/></td>
		</tr>
        <tr>
            <td colspan="4" align="left" class="tableContent">
                <xsl:value-of select="format:formatText(deliverable_comments)" disable-output-escaping="yes" />
            </td>
        </tr>

		</table>
</xsl:template>
</xsl:stylesheet>