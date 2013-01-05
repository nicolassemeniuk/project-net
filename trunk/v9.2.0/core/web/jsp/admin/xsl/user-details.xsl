<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"		
    extension-element-prefixes="display format" >

<xsl:template match="person">
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">Name:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="full_name"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">User name:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="username"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">Title:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="title"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">Email Address:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="email_address"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">Company:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="company"/></td>
    </tr>
    <tr>
        <th colspan="2" nowrap="nowrap" align="left" width="18%" class="tableHeader">User Status:</th>
        <td colspan="4" nowrap="nowrap" align="left" class="tableContent"><xsl:value-of select="user_status"/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>

