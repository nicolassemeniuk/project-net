<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

    <xsl:output method="html"/>

    <xsl:template match="LicenseList">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td colspan="6">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr class="tableFilterContent">
                            <td>
                                <span class="tableFilterHeader">Licenses Found:</span>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <xsl:value-of select="count(License)"/>
                            </td>
                            <td>
                                <span class="tableFilterHeader">Total Licenses:</span>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <!-- Avinash: -->
                                <xsl:choose>
                                	<xsl:when test="sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount) &gt; 0" ><xsl:value-of select="sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount)"/></xsl:when>
   	                                <xsl:otherwise><xsl:text disable-output-escaping="yes">0</xsl:text></xsl:otherwise> 
                                </xsl:choose>	
                            </td>
                            <td>
                                <span class="tableFilterHeader">Licenses Used:</span>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <xsl:choose>
                                	<xsl:when test="sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount) &gt; 0" ><xsl:value-of select="sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount)"/></xsl:when>
	                                <xsl:otherwise><xsl:text disable-output-escaping="yes">0</xsl:text></xsl:otherwise> 
                                </xsl:choose>
                            </td>
                            <td>
                                <span class="tableFilterHeader">Licenses Available:</span>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <xsl:choose>
                                	<xsl:when test="(sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount) - sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount)) &gt; 0" ><xsl:value-of select="(sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount) - sum(License/LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount))"/></xsl:when>
	                                <xsl:otherwise><xsl:text disable-output-escaping="yes">0</xsl:text></xsl:otherwise> 
                                </xsl:choose>
                                <!-- Avinash: -->                                
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr class="tableHeader">
                <td align="left" class="tableHeader">
                    <xsl:value-of select="display:get('prm.license.listview.licenseinfo.key.column')"/>
                </td>
                <td align="left" class="tableHeader">
                    <a href="javascript:sort('LicenseStatus')">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.status.column')"/>
                    </a>
                </td>
                <td align="left" class="tableHeader">
                    <a href="javascript:sort('IsTrial')">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.trial.column')"/>
                    </a>
                </td>
                <td align="left" class="tableHeader">
                    <a href="javascript:sort('UsageLimit')">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.usage.column')"/>
                    </a>
                </td>
                <td align="left" class="tableHeader">
                    <a href="javascript:sort('ValidDate')">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.validuntil.column')"/>
                    </a>
                </td>
                <td class="tableHeader" colspan="2">
                    <a href="javascript:sort('PaymentInfo')">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.payment.column')"/>
                    </a>
                </td>
            </tr>
            <tr class="tableLine">
                <td colspan="6" class="tableLine">
                    <img src="../../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/>
                </td>
            </tr>

            <xsl:variable name="sort-field">
                <xsl:value-of select="SortField"/>
            </xsl:variable>

            <xsl:variable name="sort-order">
                <xsl:value-of select="SortOrder"/>
            </xsl:variable>

            <!-- Reverse the order for the next time -->
            <xsl:variable name="nextSortOrder">
                <xsl:choose>
                    <xsl:when test="$sort-order='descending'">
                        ascending
                    </xsl:when>
                    <xsl:otherwise>
                        descending
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <!-- Hidden field to record next sort order -->
            <input type="hidden" name="sortOrder" value="${nextSortOrder}" />

            <xsl:choose>
                <xsl:when test="$sort-order='descending'">
                    <xsl:choose>
                        <xsl:when test="$sort-field='LicenseStatus'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseStatus" order="descending"/>
                                <xsl:sort select="IsTrial" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='IsTrial'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="IsTrial" order="descending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='UsageLimit'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='ValidDate'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='PaymentInformation'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="$sort-field='LicenseStatus'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='IsTrial'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='UsageLimit'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="ascending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='ValidDate'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="ascending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="$sort-field='PaymentInformation'">
                            <xsl:apply-templates select="License">
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="License">
                                <xsl:sort select="LicenseStatus" order="ascending"/>
                                <xsl:sort select="IsTrial" order="ascending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/ChargeCode" order="descending"/>
                                <xsl:sort select="PaymentInformation/PaymentModel/CreditCard" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/TimeLimit/EndDate" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/MaxCount" order="descending"/>
                                <xsl:sort select="LicenseCertificate/LicenseModelCollection/UsageLimit/CurrentCount" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>


    <xsl:template match="License">

        <tr class="tableContent">
            <td align="left" class="tableContent">
                <xsl:text disable-output-escaping="yes">&lt;a href="</xsl:text>
                <xsl:value-of select="//JSPRootURL"/>/admin/license/LicenseDetailView.jsp?licenseKey=<xsl:value-of select="LicenseKey/DisplayString"/>&amp;module=240&amp;action=1<xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="LicenseKey/DisplayString"/>
                <xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text>
            </td>

            <xsl:choose>
                <xsl:when test="LicenseStatus='200'">
                    <td align="left" class="warnText">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.status.option.disabled.name')"/>
                    </td>
                </xsl:when>
                <xsl:when test="LicenseStatus='300'">
                    <td align="left" class="warnText">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.status.option.cancelled.name')"/>
                    </td>
                </xsl:when>
                <xsl:otherwise>
                    <td align="left" class="tableContent">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.status.option.enabled.name')"/>
                    </td>
                </xsl:otherwise>
            </xsl:choose>

            <td align="left" class="tableContent">
                <xsl:choose>
                    <xsl:when test="IsTrial='1'">
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.trial.option.yes.name')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="display:get('prm.license.listview.licenseinfo.trial.option.no.name')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>

            <xsl:apply-templates select="LicenseCertificate"/>
            <xsl:apply-templates select="PaymentInformation"/>

        </tr>
        <tr class="tableLine">
            <td colspan="6" class="tableLine">
                <img src="../../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="LicenseCertificate">

        <xsl:apply-templates select="LicenseModelCollection"/>
        <xsl:apply-templates select="LicenseCostCollection"/>

    </xsl:template>

    <xsl:template match="LicenseModelCollection">

        <xsl:if test="count(./UsageLimit)=1">
            <xsl:apply-templates select="UsageLimit"/>
            <!--       <xsl:apply-templates select="TimeLimit"/> -->
        </xsl:if>
        <xsl:if test="count(./UsageLimit)=0">
            <td align="left" class="tableContent">
                <xsl:value-of select="display:get('prm.license.listview.licenseinfo.usage.option.unlimited.name')"/>
            </td>
        </xsl:if>
        <xsl:if test="count(./TimeLimit)=1">
            <xsl:apply-templates select="TimeLimit"/>
        </xsl:if>
        <xsl:if test="count(./TimeLimit)=0">
            <td align="left" class="tableContent">
                <xsl:value-of select="display:get('prm.license.listview.licenseinfo.validuntil.option.nolimit.name')"/>
            </td>
        </xsl:if>
        <!-- <xsl:apply-templates /> -->
    </xsl:template>

    <xsl:template match="LicenseCostCollection">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- LicenseModelCollection/UsageLimit -->
    <xsl:template match="UsageLimit">
        <!-- Current Users / Maximum Users -->
        <td align="left" class="tableContent">
            <xsl:value-of select="format:formatNumber(CurrentCount)"/>/
            <xsl:value-of select="format:formatNumber(MaxCount)"/>
        </td>

    </xsl:template>
    <!-- LicenseModelCollection/TimeLimit -->
    <xsl:template match="TimeLimit">
        <!-- End Date -->
        <td align="left" class="tableContent">
            <xsl:call-template name="Date">
                <xsl:with-param name="date" select="EndDate"/>
            </xsl:call-template>
        </td>

    </xsl:template>


    <xsl:template match="LicenseModelCollection/NodeLocked">
        <!--
             <td class="tableContent"><xsl:value-of select="NodeID/DisplayString" /></td>
        -->
    </xsl:template>

    <xsl:template match="LicenseCostCollection/Base">
        <!-- <tr class="tableContent">
             <td class="tableHeader">Base License Cost:</td>
             <td class="tableContent">USD <xsl:value-of select="UnitPrice/Money/Value" /></td>
         </tr> -->
    </xsl:template>

    <xsl:template match="LicenseCostCollection/Maintenance">
        <!--  <tr class="tableContent">
              <td class="tableHeader">Maintenance License Percentage:</td>
              <td class="tableContent"><xsl:value-of select="Percentage/Percentage/Value" />%</td>
          </tr> -->
    </xsl:template>

    <xsl:template match="PaymentInformation">

        <xsl:apply-templates select="PaymentModel"/>

    </xsl:template>

    <xsl:template match="PaymentModel">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Charge Code -->
    <xsl:template match="PaymentModel/ChargeCode">
        <td class="tableContent">
            <xsl:value-of select="Value"/>
        </td>
    </xsl:template>
    <!-- Credit Card Number -->
    <xsl:template match="PaymentModel/CreditCard">
        <td class="tableContent">
            Credit Card: <xsl:value-of select="CardNumber"/>
        </td>
    </xsl:template>
    <!-- Trial -->
    <xsl:template match="PaymentModel/Trial">

        <td class="tableContent" colspan="2">
            <xsl:value-of select="display:get('prm.license.listview.licenseinfo.payment.option.trial.name')"/>
        </td>

    </xsl:template>

    <xsl:template name="Date">
        <xsl:param name="date"/>
        <xsl:choose>
            <xsl:when test="$date">
                <xsl:value-of select="format:formatISODateTime($date)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
