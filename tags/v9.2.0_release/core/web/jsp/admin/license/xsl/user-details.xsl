<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >

<xsl:template match="person">
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.name.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="full_name"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.username.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="username"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.title.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="title"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.emailaddress.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="email_address"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.company.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="company"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader"><xsl:value-of select="display:get('prm.license.userview.details.userstatus.label')"/></th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="user_status"/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>

