<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns:display="xalan://net.project.base.property.PropertyProvider"
     xmlns:format="xalan://net.project.util.XSLFormat"
     extension-element-prefixes="display format" >

<xsl:output method="html" />

<xsl:template match="/">
	<xsl:apply-templates />
</xsl:template>


<xsl:template match="License">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td valign="top">
                <xsl:call-template name="LicenseInformation"/>
            </td>
            <xsl:if test="Purchaser">
                <td width="5%"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                <td width="48%" valign="top">
                    <xsl:call-template name="PurchaserInformation"/>
                </td>
            </xsl:if>
        </tr>
    </table>
</xsl:template>

<xsl:template name="PurchaserInformation">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.purchasedby.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/full_name"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.address1.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/address1"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.address2.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/address2"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.city.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/city"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.state.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/state"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.zipcode.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/zipcode"/></td>
        </tr>
        <tr>
            <td colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.email.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/email_address"/></td>
        </tr>
        <tr>
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.purchaser.officephone.label')"/></td>
            <td class="tableContent"><xsl:value-of select="Purchaser/person/Address/officePhone"/></td>
        </tr>
    </table>
</xsl:template>

<xsl:template name="LicenseInformation">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr class="tableContent">
            <xsl:choose>
                 <xsl:when test="LicenseStatus='200'">
                    <td class="errorText">
                           <xsl:value-of select="display:get('prm.license.detailview.details.licensedisabled.message')"/>
                    </td>
                </xsl:when>
                <xsl:when test="LicenseStatus='300'">
                    <td class="errorText">
                           <xsl:value-of select="display:get('prm.license.detailview.details.licensecancelled.message')"/>
                    </td>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
              </xsl:choose>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.responsibleuser.label')"/></td>
            <xsl:choose>
                <xsl:when test="ResponsibleUser=''">
                    <td class="tableContent"><xsl:value-of select="display:get('prm.license.detailview.details.responsibleuser.status.none.name')"/></td>
                </xsl:when>
                <xsl:otherwise>
                    <td class="tableContent"><xsl:value-of select="ResponsibleUser" /></td>
                </xsl:otherwise>
            </xsl:choose>
        </tr>
        <tr>
            <td class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.internalid.label')"/></td>
            <td class="tableContent"><xsl:value-of select="LicenseID" /></td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.triallicense.label')"/></td>
            <td class="tableContent">
                <xsl:choose>
                    <xsl:when test="IsTrial='1'">
                        <xsl:value-of select="display:get('prm.license.detailview.details.triallicense.status.yes.name')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="display:get('prm.license.detailview.details.triallicense.status.no.name')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
        <tr class="tableContent">
            <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.licensekey.label')"/></td>
            <td class="tableContent"><xsl:value-of select="LicenseKey/DisplayString" /></td>
        </tr>
        <xsl:apply-templates select="LicenseCertificate/LicenseModelCollection/NodeLocked" />
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:apply-templates select="LicenseCertificate" /></td>
        </tr>
        <tr class="tableContent"><td class="tableContent" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></tr>
        <tr class="tableContent">
            <td class="tableContent" colspan="2"><xsl:apply-templates select="PaymentInformation" /></td>
        </tr>
    </table>
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
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.maxusers.label')"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatNumber(MaxCount)" /></td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.currentusers.label')"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatNumber(CurrentCount)" /></td>
    </tr>
	<xsl:variable name="remaining_users" select="(MaxCount - CurrentCount)" />
	<tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.remainingusers.label')"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatNumber($remaining_users)" /></td>
    </tr>
	<tr class="tableContent">
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
</xsl:template>

<xsl:template match="LicenseModelCollection/TimeLimit">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.createmodifydate.label')"/></td>
        <td class="tableContent">
            <xsl:call-template name="Date">
                <xsl:with-param name="date" select="StartDate" />
            </xsl:call-template>
        </td>
    </tr>
	<tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.enddate.label')"/></td>
        <td class="tableContent">
            <xsl:call-template name="Date">
                <xsl:with-param name="date" select="EndDate" />
            </xsl:call-template>
        </td>
    </tr>
	<tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.daysremaining.label')"/></td>
        <td class="tableContent">
            <xsl:value-of select="DaysRemaining" />
       </td>
    </tr>
	<tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.originaltimelimit.label')"/></td>
        <td class="tableContent"><xsl:value-of select="DayDuration" /></td>
    </tr>
    <tr class="tableContent">
		<td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
</xsl:template>

<xsl:template match="LicenseModelCollection/NodeLocked">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.nodeid.label')"/></td>
        <td class="tableContent"><xsl:value-of select="NodeID/DisplayString" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseCostCollection/Base">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.basecost.label')"/></td>
        <td class="tableContent"><xsl:value-of select="display:get('prm.license.detailview.details.basecost.currency.text')"/><xsl:value-of select="UnitPrice/Money/Value" /></td>
    </tr>
</xsl:template>

<xsl:template match="LicenseCostCollection/Maintenance">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.maintenancepercentage.label')"/></td>
        <td class="tableContent"><xsl:value-of select="format:formatPercent(Percentage/Percentage/Value)" /></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentInformation">
    <xsl:apply-templates select="PaymentModel" />
</xsl:template>

<xsl:template match="PaymentModel">
    <xsl:apply-templates />
</xsl:template>


<xsl:template match="PaymentModel/ChargeCode">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.chargecode.label')"/></td>
        <td class="tableContent"><xsl:value-of select="Value" /></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentModel/CreditCard">
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.creditcardnumber.label')"/></td>
        <td class="tableContent"><xsl:value-of select="CardNumber" /></td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.expires.label')"/></td>
        <td class="tableContent"><xsl:value-of select="ExpiryMonth" /> / <xsl:value-of select="ExpiryYear" /></td>
    </tr>
    <tr class="tableContent">
        <td class="tableHeader"><xsl:value-of select="display:get('prm.license.detailview.details.transactionid.label')"/></td>
        <td class="tableContent"><xsl:value-of select="TransactionID"/></td>
    </tr>
</xsl:template>

<xsl:template match="PaymentModel/Trial">
    <tr class="tableContent">
        <td class="tableContent" colspan="2"><xsl:value-of select="display:get('prm.license.detailview.details.trial.label')"/></td>
    </tr>
</xsl:template>

<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODateTime($date)" />
        </xsl:when>
        <xsl:otherwise>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
