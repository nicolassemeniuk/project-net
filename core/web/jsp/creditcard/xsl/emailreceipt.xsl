<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="display">

    <xsl:output method="text" indent="no"/>

    <!-- Place your <xsl:template> objects here -->
    <xsl:template match="CreditCardReceipt">
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.thankyou.message')"/>
        <xsl:text>&#x0A;&#x0A;</xsl:text>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.youpurchased.message')"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:apply-templates select="LineItems"/>
        <xsl:apply-templates select="Summary"/>
        <xsl:call-template name="closing"/>
    </xsl:template>

    <xsl:template match="LineItems">
        <!-- Get the number of pad characters between each column title -->
        <xsl:variable name="quantity.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="9-string-length(display:get('prm.global.creditcard.receipt.qty.columnheader'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="quantity-pad" select="translate($quantity.pad.tmp,'#',' ')"/>

        <xsl:variable name="description.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="31-string-length(display:get('prm.global.creditcard.receipt.item.columnheader'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="description-pad" select="translate($description.pad.tmp,'#',' ')"/>

        <xsl:variable name="price.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="12 - string-length(display:get('prm.global.creditcard.receipt.price.columnheader'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="price-pad" select="translate($price.pad.tmp,'#',' ')"/>


        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.qty.columnheader')"/><xsl:value-of select="$quantity-pad"/><xsl:value-of select="display:get('prm.global.creditcard.receipt.item.columnheader')"/><xsl:value-of select="$description-pad"/><xsl:value-of select="display:get('prm.global.creditcard.receipt.price.columnheader')"/><xsl:value-of select="$price-pad"/><xsl:value-of select="display:get('prm.global.creditcard.receipt.subtotal.columnheader')"/><xsl:text>&#x0A;</xsl:text>
        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
        <xsl:apply-templates select="LineItem"/>
        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
    </xsl:template>

    <xsl:template name="pad">
        <xsl:param name="padChar" select="'#'"/>
        <xsl:param name="padCount" select="0"/>
        <xsl:value-of select="$padChar"/>
        <xsl:if test="$padCount&gt;1">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="number($padCount) - 1"/>
                <xsl:with-param name="padChar" select="$padChar"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <xsl:template match="LineItem">
        <!-- Get the padding required between the variables -->
        <xsl:variable name="quantity.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="9-string-length(Quantity)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="quantity-pad" select="translate($quantity.pad.tmp,'#',' ')"/>

        <xsl:variable name="description.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="31-string-length(Description)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="description-pad" select="translate($description.pad.tmp,'#',' ')"/>

        <xsl:variable name="price.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="12 - string-length(UnitPrice)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="price-pad" select="translate($price.pad.tmp,'#',' ')"/>

        <xsl:value-of select="Quantity"/>
        <xsl:value-of select="$quantity-pad"/>
        <xsl:value-of select="Description"/>
        <xsl:value-of select="$description-pad"/>
        <xsl:value-of select="UnitPrice"/>
        <xsl:value-of select="$price-pad"/>
        <xsl:value-of select="LineSubtotal"/>
        <xsl:text>&#x0A;</xsl:text>
    </xsl:template>

    <xsl:template match="Summary">
        <!-- Get the padding required between the variables -->
        <xsl:variable name="subtotal.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="26-string-length(display:get('prm.global.creditcard.receipt.subtotal.name'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subtotal-pad" select="translate($subtotal.pad.tmp,'#',' ')"/>

        <xsl:value-of select="$subtotal-pad"/>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.subtotal.name')"/>
        <xsl:text>  </xsl:text><xsl:value-of select="Subtotal"/>

        <xsl:variable name="ccsurcharge.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="26-string-length(display:get('prm.global.creditcard.receipt.creditcardsurcharge.name'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="ccsurcharge-pad" select="translate($ccsurcharge.pad.tmp,'#',' ')"/>

        <xsl:if test="CreditCardServiceCharge">
            <xsl:text>&#x0A;</xsl:text>
            <xsl:value-of select="$ccsurcharge-pad"/>
            <xsl:value-of select="display:get('prm.global.creditcard.receipt.creditcardsurcharge.name')"/>
            <xsl:text>  </xsl:text><xsl:value-of select="CreditCardServiceCharge"/>
        </xsl:if>

        <xsl:variable name="grandtotal.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="26-string-length(display:get('prm.global.creditcard.receipt.grandtotal.name'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="grandtotal-pad" select="translate($grandtotal.pad.tmp,'#',' ')"/>

        <xsl:text>&#x0A;&#x0A;</xsl:text>
        <xsl:value-of select="$grandtotal-pad"/>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.grandtotal.name')"/>
        <xsl:text>  </xsl:text><xsl:value-of select="Total"/>

        <xsl:variable name="paidbycc.pad.tmp">
            <xsl:call-template name="pad">
                <xsl:with-param name="padCount" select="26-string-length(display:get('prm.global.creditcard.receipt.paidbycreditcard.name'))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="paidbycc-pad" select="translate($paidbycc.pad.tmp,'#',' ')"/>

        <xsl:text>&#x0A;&#x0A;</xsl:text>
        <xsl:value-of select="$paidbycc-pad"/>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.paidbycreditcard.name')"/>
        <xsl:text>  </xsl:text><xsl:value-of select="Total"/>
        <xsl:text>&#x0A;</xsl:text>
    </xsl:template>

    <xsl:template name="closing">
        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.closing1.message')"/>
        <xsl:text>&#x0A;&#x0A;</xsl:text>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.closing2.message')"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
        <xsl:value-of select="display:get('prm.global.creditcard.receipt.closing3.message')"/>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:text>---------------------------------------------------------------------&#x0A;</xsl:text>
    </xsl:template>
</xsl:stylesheet>