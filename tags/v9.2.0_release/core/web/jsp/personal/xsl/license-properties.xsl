<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:display="xalan://net.project.base.property.PropertyProvider" extension-element-prefixes="display" >


<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>


<xsl:template match="License">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
       <!-- <tr class="tableContent">
            <td class="tableHeader">Internal ID:</td> 
            <td class="tableContent"><xsl:value-of select="LicenseID" /></td>
        </tr>
	-->	
		<tr class="tableContent">
			<td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.user.label')"/></td>
		 	<td class="tableContent">
			<xsl:choose>
    			<xsl:when test="ResponsibleUser=''"><xsl:value-of select="display:get('prm.personal.profile.license.properties.user.status.none.name')"/></xsl:when>
				<xsl:otherwise> <xsl:value-of select="ResponsibleUser" /> </xsl:otherwise>
			</xsl:choose>
			</td>	
		</tr>	
		<tr class="tableContent">
			<td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.useremail.label')"/></td>
		 	<td class="tableContent">
			<xsl:choose>
    			<xsl:when test="ResponsibleUserEmail=''"><xsl:value-of select="display:get('prm.personal.profile.license.properties.useremail.status.none.name')"/></xsl:when>
				<xsl:otherwise> <xsl:value-of select="ResponsibleUserEmail" /> </xsl:otherwise>
			</xsl:choose>
			</td>	
		</tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.trial.label')"/></td>
            <td class="tableContent">
                <xsl:choose>
                    <xsl:when test="IsTrial='1'">
                        <xsl:value-of select="display:get('prm.personal.profile.license.properties.trial.status.yes.name')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="display:get('prm.personal.profile.license.properties.trial.status.no.name')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.licensekey.label')"/></td>
            <td class="tableContent"><xsl:value-of select="LicenseKey/DisplayString" /></td>
        </tr>
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:apply-templates select="LicenseCertificate/LicenseModelCollection/TimeLimit" /></td>
        </tr>
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
		
    </table>
</xsl:template>

<xsl:template match="LicenseCertificate/LicenseModelCollection/TimeLimit">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.enddate.label')"/></td>
        <td class="tableContent">
            <xsl:call-template name="Date">
                <xsl:with-param name="date" select="EndDate" />
            </xsl:call-template>
        </td>
    </tr>
    <tr class="tableContent">
		<td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.daysremaining.label')"/></td>
		<td class="tableContent"><xsl:value-of select="DaysRemaining" /></td>
	</tr>	
	<tr class="tableContent">	
        <td class="tableHeader"><xsl:value-of select="display:get('prm.personal.profile.license.properties.originaltimelimit.label')"/></td>
        <td class="tableContent"><xsl:value-of select="DayDuration" /></td>
    </tr>
</xsl:template>

<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="substring-before($date, 'T')" />
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <xsl:value-of select="substring-after($date, 'T')" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
