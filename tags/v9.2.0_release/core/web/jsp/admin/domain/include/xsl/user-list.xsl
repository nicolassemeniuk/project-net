<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"		
    	extension-element-prefixes="display format" >

<xsl:template match="person_list">
    <!-- Table for list of persons -->
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr class="tableHeader" align="left">
            <th class="tableHeader">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </th>
            <th class="tableHeader"><a href="javascript:sort('lower(username) asc')">User Name</a></th>
            <th class="tableHeader"><a href="javascript:sort('lower(last_name), lower(first_name) asc')">Name (Last, First)</a></th>
            <th class="tableHeader"><a href="javascript:sort('lower(email) asc')">Email Address</a></th>
            <th class="tableHeader"><a href="javascript:sort('lower(user_status) asc')">User Status</a></th>
            <th class="tableHeader"><a href="javascript:sort('(last_login) desc')">Last Login</a></th>
	</tr>
        <tr class="tableLine">
            <td colspan="6" class="tableLine">
                <img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/>
            </td>
        </tr>

        <xsl:apply-templates select="person" />
        <xsl:if test="count(person)=0">
            <xsl:call-template name="no_persons"/>
        </xsl:if>
    </table>
</xsl:template>
  
<xsl:template match="person">
    <tr class="tableLine">
<xsl:variable name="x"><xsl:copy-of select="user_status"/></xsl:variable> 
<xsl:if test="contains($x,'Active')">
<td class="tableContent">
	<xsl:element name="input">
		<xsl:attribute name="name"><xsl:text>selected</xsl:text></xsl:attribute>
		<xsl:attribute name="type"><xsl:text>radio</xsl:text></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="onClick"><xsl:text>setUserStatus('A')</xsl:text></xsl:attribute>
	</xsl:element>
</td> 
</xsl:if>  
<xsl:if test="contains($x,'Disabled')">
<td class="tableContent">
<xsl:element name="input">
		<xsl:attribute name="name"><xsl:text>selected</xsl:text></xsl:attribute>
		<xsl:attribute name="type"><xsl:text>radio</xsl:text></xsl:attribute>
		<xsl:attribute name="value"><xsl:copy-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="onClick"><xsl:text>setUserStatus('D')</xsl:text></xsl:attribute>
	</xsl:element>
</td>
</xsl:if>  
<xsl:if test="contains($x,'Unregistered')">
<td class="tableContent">
<xsl:element name="input">
		<xsl:attribute name="name"><xsl:text>selected</xsl:text></xsl:attribute>
		<xsl:attribute name="type"><xsl:text>radio</xsl:text></xsl:attribute>
		<xsl:attribute name="value"><xsl:copy-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="onClick"><xsl:text>setUserStatus('R')</xsl:text></xsl:attribute>
	</xsl:element>
</td>
</xsl:if>  
<xsl:if test="contains($x,'Unconfirmed')">
<td class="tableContent">
<xsl:element name="input">
		<xsl:attribute name="name"><xsl:text>selected</xsl:text></xsl:attribute>
		<xsl:attribute name="type"><xsl:text>radio</xsl:text></xsl:attribute>
		<xsl:attribute name="value"><xsl:copy-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="onClick"><xsl:text>setUserStatus('C')</xsl:text></xsl:attribute>
	</xsl:element>
</td>
</xsl:if>  
<xsl:if test="contains($x,'Deleted')">
<td class="tableContent">
<xsl:element name="input">
		<xsl:attribute name="name"><xsl:text>selected</xsl:text></xsl:attribute>
		<xsl:attribute name="type"><xsl:text>radio</xsl:text></xsl:attribute>
		<xsl:attribute name="value"><xsl:copy-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="value"><xsl:value-of select="person_id"/></xsl:attribute>
		<xsl:attribute name="onClick"><xsl:text>setUserStatus('R')</xsl:text></xsl:attribute>
	</xsl:element>
</td>
</xsl:if>  

        <td class="tableContent"><a href="javascript:showDetail({person_id});"><xsl:value-of select="username"/></a></td>
        <td class="tableContent"><xsl:value-of select="last_name"/>, <xsl:value-of select="first_name"/></td>
        <td class="tableContent"><a href="mailto:{email_address}"><xsl:value-of select="email_address"/></a></td>
        <td class="tableContent"><a href="javascript:changeUserStatus('{person_id}', '{user_status}')"><xsl:value-of select="user_status"/></a></td>
	<td class="tableContent"><xsl:value-of select="format:formatISODateTime(last_login)"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	    </tr>
    <tr class="tableLine">
        <td colspan="6" class="tableLine">
            <img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
        </td>
    </tr>
</xsl:template>

<xsl:template name="no_persons">
    <tr class="tableContent" align="left">
        <td class="tableContent" colspan="6">There are currently no such persons in the database</td>
    </tr>
</xsl:template>

</xsl:stylesheet>


















