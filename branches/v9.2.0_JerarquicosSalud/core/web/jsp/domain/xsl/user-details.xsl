<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
			xmlns:display="xalan://net.project.base.property.PropertyProvider"
    			extension-element-prefixes="display" >

<xsl:template match="user">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="tableContent">
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        <td class="tableHeader"><xsl:value-of select="display:get('prm.domain.domainmigrationresults.name.label')"/></td>
        <td class="tableContent"><xsl:value-of select="full-name"/></td>
    </tr>
    <tr>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.domain.domainmigrationresults.username.label')"/></td>
        <td class="tableContent"><xsl:value-of select="login"/></td>
    </tr>
    <tr>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.domain.domainmigrationresults.emailaddress.label')"/></td>
        <td class="tableContent"><xsl:value-of select="email"/></td>
    </tr>
	<tr>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.domain.domainmigrationresults.alternateemail1.label')"/></td>
        <td class="tableContent"><xsl:value-of select="alternate_email_1"/></td>
    </tr>
	<tr>
		<td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
		<td class="tableHeader"><xsl:value-of select="display:get('prm.domain.domainmigrationresults.alternateemail2.label')"/></td>
        <td class="tableContent"><xsl:value-of select="alternate_email_2"/></td>
    </tr>
</table>	
</xsl:template>
</xsl:stylesheet>
