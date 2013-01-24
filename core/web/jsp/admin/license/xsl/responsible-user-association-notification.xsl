<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >

<xsl:output method="text" />

<xsl:template match="LicenseNotification">
Hello <xsl:apply-templates select="License/ResponsibleUser" />,
	
You are notified that the following user has joined the license you are responsible for.
<xsl:apply-templates select="user" />
<xsl:apply-templates select="person" />
	
You can manage the license by navigating to Personal Space --> Setup --> Setup Responsible Licenses
<xsl:apply-templates select="License" />
</xsl:template>

<xsl:template match="user">
    User Information
    ----------------
    Full Name : <xsl:value-of select="full-name" />
    Login Name: <xsl:value-of select="login" />
    Email     : <xsl:value-of select="email" />
</xsl:template>

<xsl:template match="person">
    User Information
    ----------------
    Full Name : <xsl:value-of select="full-name" />
    Login Name: <xsl:value-of select="username" />
    Email     : <xsl:value-of select="email_address" />
</xsl:template>

<xsl:template match="License">
	License Information 
	-------------------
	<xsl:choose>
          	<xsl:when test="LicenseStatus='200'">This license has been disabled by the administrator. </xsl:when>
			<xsl:when test="LicenseStatus='300'">This license has been cancelled by the administrator.</xsl:when>
            <xsl:otherwise> </xsl:otherwise>
   	</xsl:choose>
	<xsl:choose>
	<xsl:when test="ResponsibleUser=''">
    Responsible User : None </xsl:when>
	<xsl:otherwise>	
    Responsible User: <xsl:value-of select="ResponsibleUser" /></xsl:otherwise>
	</xsl:choose>	
    <xsl:choose>
    <xsl:when test="IsTrial='1'">
    Trial License: Yes </xsl:when>
    <xsl:otherwise> 
    Trial License: No </xsl:otherwise>
    </xsl:choose>
    License Key: <xsl:value-of select="LicenseKey/DisplayString" />
     <xsl:apply-templates select="LicenseCertificate" />
    Payment Information: <xsl:apply-templates select="PaymentInformation" />
</xsl:template>

<xsl:template match="LicenseCertificate">
        <xsl:apply-templates select="LicenseModelCollection" />
        <xsl:apply-templates select="LicenseCostCollection" />
</xsl:template>

<xsl:template match="LicenseModelCollection">
  <!--  <xsl:apply-templates /> -->
  <xsl:apply-templates select="UsageLimit"/>
  <xsl:apply-templates select="TimeLimit"/>
</xsl:template>

<xsl:template match="LicenseCostCollection">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="LicenseModelCollection/UsageLimit">
    Maximum Users: <xsl:value-of select="format:formatNumber(MaxCount)" />
    Current Users: <xsl:value-of select="format:formatNumber(CurrentCount)" />
    <xsl:variable name="remaining_users" select="(MaxCount - CurrentCount)" />
    Remaining Users: <xsl:value-of select="format:formatNumber($remaining_users)" />
</xsl:template>

<xsl:template match="LicenseModelCollection/TimeLimit">
    Create/Modify Date:        
        <xsl:call-template name="Date">
        	<xsl:with-param name="date" select="StartDate" />
        </xsl:call-template>
    End Date:
       <xsl:call-template name="Date">
          <xsl:with-param name="date" select="EndDate" />
       </xsl:call-template>
    Days Remaining: <xsl:value-of select="DaysRemaining" />
    Original Time Limit(Days): <xsl:value-of select="DayDuration" />
</xsl:template>

<xsl:template match="LicenseCostCollection/Base">
    Base License Cost: USD <xsl:value-of select="UnitPrice/Money/Value" />
</xsl:template>

<xsl:template match="LicenseCostCollection/Maintenance">
    Maintenance License Percentage: <xsl:value-of select="format:formatPercent(Percentage/Percentage/Value)" />
</xsl:template>

<xsl:template match="PaymentInformation">
   <xsl:apply-templates select="PaymentModel" />
</xsl:template>

<xsl:template match="PaymentModel">
    <xsl:apply-templates />
</xsl:template>


<xsl:template match="PaymentModel/ChargeCode">
    Charge Code: <xsl:value-of select="Value" />
</xsl:template>

<xsl:template match="PaymentModel/CreditCard">
    Credit Card Number: <xsl:value-of select="CardNumber" />
    Expires: <xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" />
</xsl:template>

<xsl:template match="PaymentModel/Trial">
    Trial
</xsl:template>

<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODateTime($date)" />
        </xsl:when>
        <xsl:otherwise>
         </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>