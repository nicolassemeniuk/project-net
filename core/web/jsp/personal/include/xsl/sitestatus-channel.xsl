<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:display="xalan://net.project.base.property.PropertyProvider"
    			extension-element-prefixes="display" >
				
<xsl:output method="html" />
  <xsl:template match="status_message">
		<table cellpadding="0" cellspacing="0" border="0" width="100%" name="tableWithEvenRows">
			<tr align="left" valign="middle" class="table-header">
				<td colspan="2" class="table-header">
					<xsl:value-of disable-output-escaping="yes" select="./message_title" />
				</td>
			</tr>
            <tr align="left" valign="middle" class="tableContent">
				<td class="tableContent">
					<xsl:value-of disable-output-escaping="yes" select="message_text"/>
				</td>
			</tr>
	   </table>
	</xsl:template>
</xsl:stylesheet>
