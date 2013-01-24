/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;

/**
 * Provides supported currency codes.
 * <p>
 * Provides an Html option list of currency codes.
 * Given a currency code, the corresponding currency object may be fetched by: <code>java.util.Currency.getInstance(code)</code>.
 * </p>
 * <p>
 * The descriptions of each currency code are hard coded.  Java does not provide a locale-based method for returning
 * currency descriptions.  There are a number of requests to make Currency work similarly to Locale in future releases.
 * Until that time, we will continue to support English-only currency descriptions.
 * </p>
 * @see java.util.Currency
 * @author Tim
 * @since Gecko Update 4
 */
public class Currency {

    /**
     * Returns a collection of all currency codes.
     * @return the collection where each element is a String currency code
     */
    public static java.util.Collection getAllCurrencyCodes() {
        return Collections.unmodifiableSet(CURRENCY_DESCRIPTION_MAP.keySet());
    }

    /**
     * Returns an HTML option list containing currency codes.
     * The value part is the currency code.  The display part
     * is descriptive name for the currency code.<br>
     * For example: <code>&lt;option value="USD"&gt;US Dollar&lt;/option&gt;</code>
     * @return the HTML option list
     */
    public static String getHtmlOptionList() {
        return getHtmlOptionList(null);
    }

    /**
     * Returns an HTML option list containing currency codes selecting the specified code by default.
     * The value part is the currency code.  The display part
     * is descriptive name for the currency code. Sorted by currency display name (ignores case).<br>
     * For example: <code>&lt;option value="USD"&gt;US Dollar&lt;/option&gt;</code>
     * @param selectedCode the code to select in the option list; no option is selected if the code is not
     * found in the list or is null
     * @return the HTML option list
     */
    public static String getHtmlOptionList(String selectedCode) {

        // Sort the list of currencies by display name
        List currencyList = Arrays.asList(CURRENCY_DESCRIPTIONS);
        Collections.sort(currencyList, new CurrencyDisplayNameComparator());

        return HTMLOptionList.makeHtmlOptionList(currencyList, selectedCode);
    }

    /**
     * Returns the full display name for a <code>Currency</code>.
     * @param currency the currency for which to get the full display name
     * @return the full display name, of the form <code>description (code)</code>
     */
    public static String getFullDisplayName(java.util.Currency currency) {
        String displayName = null;

        CurrencyDescription description = (CurrencyDescription) CURRENCY_DESCRIPTION_MAP.get(currency.getCurrencyCode());
        if (description != null) {
            displayName = description.getFullDisplayName();
        }

        // If we don't have a description for the currency, just return
        // Java's display (which is currently only the code)
        if (displayName == null) {
            displayName = currency.toString();
        }

        return displayName;
    }

    /**
     * Returns the system default currency as defined by the code in the
     * property <code>prm.project.defaultcurrency.code</code> or null if no valid code was found.
     * @return the system default currency or null
     */
    public static java.util.Currency getSystemDefaultCurrency() {
        java.util.Currency currency = null;

        String systemDefaultCurrencyCode = PropertyProvider.get("prm.project.defaultcurrency.code");
        if (systemDefaultCurrencyCode != null) {
            try {
                currency = java.util.Currency.getInstance(systemDefaultCurrencyCode);
            } catch (IllegalArgumentException e) {
                // system default code not legal
                // We don't throw an error since this is user error
                // They'll simply be required to select a currency during project create
            }
        }

        return currency;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Currency() {
        // Do nothing
    }

    //
    // Nested top-level classes
    //

    /**
     * Simple data structure that maintains currency code and display name.
     * This will be replaced when the Java API finally provides Localized currency descriptions.
     */
    private static final class CurrencyDescription implements IHTMLOption {

        final String code;
        final String displayNameToken;

        /**
         * Creates a new CurrencyDescription for the specified code with display name token.
         * @param code the ISO 4217 currency code
         * @param displayNameToken the token to use for the display name
         */
        CurrencyDescription(String code, String displayNameToken) {
            this.code = code;
            this.displayNameToken = displayNameToken;
        }

        /**
         * Returns the display name for this currency description.
         * @return the display name, looked from the token
         */
        String getDisplayName() {
            return PropertyProvider.get(this.displayNameToken);
        }

        /**
         * Returns a full display name including the displayName and currency code
         * in parentheses.
         * @return the full display name
         */
        String getFullDisplayName() {
            return new StringBuffer(getDisplayName()).append(" (")
                    .append(this.code).append(")").toString();
        }

        /**
         * Returns the value to use in an HTML option tag value attribute
         * for this currency.
         * @return the currency code
         */
        public String getHtmlOptionValue() {
            return this.code;
        }

        /**
         * Returns the value to use in the display of an HTML option tag for
         * this currency.
         * @return the full display name
         */
        public String getHtmlOptionDisplay() {
            return getFullDisplayName();
        }
    }


    /**
     * Comparator based on <code>CurrencyDescription</code> display names.
     * Compares display names ignoring case.
     */
    private static class CurrencyDisplayNameComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return (((CurrencyDescription) o1).getDisplayName().compareToIgnoreCase(((CurrencyDescription) o2).getDisplayName()));
        }

    }


    /**
     * The currently known ISO 4217 currency codes and display name tokens.
     * Excludes European currencies that are no longer in use due to Euro.
     */
    private static CurrencyDescription[] CURRENCY_DESCRIPTIONS = {
        new CurrencyDescription("ADP", "prm.global.currency.andorranpeseta.name"),
        new CurrencyDescription("AED", "prm.global.currency.uaedirham.name"),
        new CurrencyDescription("AFA", "prm.global.currency.afghani.name"),
        new CurrencyDescription("ALL", "prm.global.currency.lek.name"),
        new CurrencyDescription("AMD", "prm.global.currency.armeniandram.name"),
        new CurrencyDescription("ANG", "prm.global.currency.netherlandsantillianguilder.name"),
        new CurrencyDescription("AOA", "prm.global.currency.kwanza.name"),
        new CurrencyDescription("ARS", "prm.global.currency.argentinepeso.name"),
        new CurrencyDescription("AUD", "prm.global.currency.australiandollar.name"),
        new CurrencyDescription("AWG", "prm.global.currency.arubanguilder.name"),
        new CurrencyDescription("AZM", "prm.global.currency.azerbaijanianmanat.name"),
        new CurrencyDescription("BAM", "prm.global.currency.convertiblemarks.name"),
        new CurrencyDescription("BBD", "prm.global.currency.barbadosdollar.name"),
        new CurrencyDescription("BDT", "prm.global.currency.taka.name"),
        new CurrencyDescription("BGL", "prm.global.currency.lev.name"),
        new CurrencyDescription("BGN", "prm.global.currency.bulgarianlev.name"),
        new CurrencyDescription("BHD", "prm.global.currency.bahrainidinar.name"),
        new CurrencyDescription("BIF", "prm.global.currency.burundifranc.name"),
        new CurrencyDescription("BMD", "prm.global.currency.bermudiandollar.name"),
        new CurrencyDescription("BND", "prm.global.currency.bruneidollar.name"),
        new CurrencyDescription("BOB", "prm.global.currency.boliviano.name"),
        new CurrencyDescription("BOV", "prm.global.currency.mvdol.name"),
        new CurrencyDescription("BRL", "prm.global.currency.brazilianreal.name"),
        new CurrencyDescription("BSD", "prm.global.currency.bahamiandollar.name"),
        new CurrencyDescription("BTN", "prm.global.currency.ngultrum.name"),
        new CurrencyDescription("BWP", "prm.global.currency.pula.name"),
        new CurrencyDescription("BYR", "prm.global.currency.belarussianruble.name"),
        new CurrencyDescription("BZD", "prm.global.currency.belizedollar.name"),
        new CurrencyDescription("CAD", "prm.global.currency.canadiandollar.name"),
        new CurrencyDescription("CDF", "prm.global.currency.franccongolais.name"),
        new CurrencyDescription("CHF", "prm.global.currency.swissfranc.name"),
        new CurrencyDescription("CLF", "prm.global.currency.unidadesdefomento.name"),
        new CurrencyDescription("CLP", "prm.global.currency.chileanpeso.name"),
        new CurrencyDescription("CNY", "prm.global.currency.yuanrenminbi.name"),
        new CurrencyDescription("COP", "prm.global.currency.colombianpeso.name"),
        new CurrencyDescription("CRC", "prm.global.currency.costaricancolon.name"),
        new CurrencyDescription("CUP", "prm.global.currency.cubanpeso.name"),
        new CurrencyDescription("CVE", "prm.global.currency.capeverdeescudo.name"),
        new CurrencyDescription("CYP", "prm.global.currency.cypruspound.name"),
        new CurrencyDescription("CZK", "prm.global.currency.czechkoruna.name"),
        new CurrencyDescription("DJF", "prm.global.currency.djiboutifranc.name"),
        new CurrencyDescription("DKK", "prm.global.currency.danishkrone.name"),
        new CurrencyDescription("DOP", "prm.global.currency.dominicanpeso.name"),
        new CurrencyDescription("DZD", "prm.global.currency.algeriandinar.name"),
        new CurrencyDescription("EEK", "prm.global.currency.kroon.name"),
        new CurrencyDescription("EGP", "prm.global.currency.egyptianpound.name"),
        new CurrencyDescription("ERN", "prm.global.currency.nakfa.name"),
        new CurrencyDescription("ETB", "prm.global.currency.ethiopianbirr.name"),
        new CurrencyDescription("EUR", "prm.global.currency.euro.name"),
        new CurrencyDescription("FJD", "prm.global.currency.fijidollar.name"),
        new CurrencyDescription("FKP", "prm.global.currency.falklandislandspound.name"),
        new CurrencyDescription("GBP", "prm.global.currency.poundsterling.name"),
        new CurrencyDescription("GEL", "prm.global.currency.lari.name"),
        new CurrencyDescription("GHC", "prm.global.currency.cedi.name"),
        new CurrencyDescription("GIP", "prm.global.currency.gibraltarpound.name"),
        new CurrencyDescription("GMD", "prm.global.currency.dalasi.name"),
        new CurrencyDescription("GNF", "prm.global.currency.guineafranc.name"),
        new CurrencyDescription("GTQ", "prm.global.currency.quetzal.name"),
        new CurrencyDescription("GWP", "prm.global.currency.guineabissaupeso.name"),
        new CurrencyDescription("GYD", "prm.global.currency.guyanadollar.name"),
        new CurrencyDescription("HKD", "prm.global.currency.hongkongdollar.name"),
        new CurrencyDescription("HNL", "prm.global.currency.lempira.name"),
        new CurrencyDescription("HRK", "prm.global.currency.croatiankuna.name"),
        new CurrencyDescription("HTG", "prm.global.currency.gourde.name"),
        new CurrencyDescription("HUF", "prm.global.currency.forint.name"),
        new CurrencyDescription("IDR", "prm.global.currency.rupiah.name"),
        new CurrencyDescription("ILS", "prm.global.currency.newisraelisheqel.name"),
        new CurrencyDescription("INR", "prm.global.currency.indianrupee.name"),
        new CurrencyDescription("IQD", "prm.global.currency.iraqidinar.name"),
        new CurrencyDescription("IRR", "prm.global.currency.iranianrial.name"),
        new CurrencyDescription("ISK", "prm.global.currency.icelandkrona.name"),
        new CurrencyDescription("JMD", "prm.global.currency.jamaicandollar.name"),
        new CurrencyDescription("JOD", "prm.global.currency.jordaniandinar.name"),
        new CurrencyDescription("JPY", "prm.global.currency.yen.name"),
        new CurrencyDescription("KES", "prm.global.currency.kenyanshilling.name"),
        new CurrencyDescription("KGS", "prm.global.currency.som.name"),
        new CurrencyDescription("KHR", "prm.global.currency.riel.name"),
        new CurrencyDescription("KMF", "prm.global.currency.comorofranc.name"),
        new CurrencyDescription("KPW", "prm.global.currency.northkoreanwon.name"),
        new CurrencyDescription("KRW", "prm.global.currency.won.name"),
        new CurrencyDescription("KWD", "prm.global.currency.kuwaitidinar.name"),
        new CurrencyDescription("KYD", "prm.global.currency.caymanislandsdollar.name"),
        new CurrencyDescription("KZT", "prm.global.currency.tenge.name"),
        new CurrencyDescription("LAK", "prm.global.currency.kip.name"),
        new CurrencyDescription("LBP", "prm.global.currency.lebanesepound.name"),
        new CurrencyDescription("LKR", "prm.global.currency.srilankarupee.name"),
        new CurrencyDescription("LRD", "prm.global.currency.liberiandollar.name"),
        new CurrencyDescription("LSL", "prm.global.currency.loti.name"),
        new CurrencyDescription("LTL", "prm.global.currency.lithuanianlitus.name"),
        new CurrencyDescription("LVL", "prm.global.currency.latvianlats.name"),
        new CurrencyDescription("LYD", "prm.global.currency.libyandinar.name"),
        new CurrencyDescription("MAD", "prm.global.currency.moroccandirham.name"),
        new CurrencyDescription("MDL", "prm.global.currency.moldovanleu.name"),
        new CurrencyDescription("MGF", "prm.global.currency.malagasyfranc.name"),
        new CurrencyDescription("MKD", "prm.global.currency.denar.name"),
        new CurrencyDescription("MMK", "prm.global.currency.kyat.name"),
        new CurrencyDescription("MNT", "prm.global.currency.tugrik.name"),
        new CurrencyDescription("MOP", "prm.global.currency.pataca.name"),
        new CurrencyDescription("MRO", "prm.global.currency.ouguiya.name"),
        new CurrencyDescription("MTL", "prm.global.currency.malteselira.name"),
        new CurrencyDescription("MUR", "prm.global.currency.mauritiusrupee.name"),
        new CurrencyDescription("MVR", "prm.global.currency.rufiyaa.name"),
        new CurrencyDescription("MWK", "prm.global.currency.malawikwacha.name"),
        new CurrencyDescription("MXN", "prm.global.currency.mexicanpeso.name"),
        new CurrencyDescription("MYR", "prm.global.currency.malaysianringgit.name"),
        new CurrencyDescription("MZM", "prm.global.currency.metical.name"),
        new CurrencyDescription("NAD", "prm.global.currency.namibiadollar.name"),
        new CurrencyDescription("NGN", "prm.global.currency.naira.name"),
        new CurrencyDescription("NIO", "prm.global.currency.cordobaoro.name"),
        new CurrencyDescription("NOK", "prm.global.currency.norwegiankrone.name"),
        new CurrencyDescription("NPR", "prm.global.currency.nepaleserupee.name"),
        new CurrencyDescription("NZD", "prm.global.currency.newzealanddollar.name"),
        new CurrencyDescription("OMR", "prm.global.currency.rialomani.name"),
        new CurrencyDescription("PAB", "prm.global.currency.balboa.name"),
        new CurrencyDescription("PEN", "prm.global.currency.nuevosol.name"),
        new CurrencyDescription("PGK", "prm.global.currency.kina.name"),
        new CurrencyDescription("PHP", "prm.global.currency.philippinepeso.name"),
        new CurrencyDescription("PKR", "prm.global.currency.pakistanrupee.name"),
        new CurrencyDescription("PLN", "prm.global.currency.zloty.name"),
        new CurrencyDescription("PYG", "prm.global.currency.guarani.name"),
        new CurrencyDescription("QAR", "prm.global.currency.qataririal.name"),
        new CurrencyDescription("ROL", "prm.global.currency.leu.name"),
        new CurrencyDescription("RUB", "prm.global.currency.russianrublenew.name"),
        new CurrencyDescription("RUR", "prm.global.currency.russianruble.name"),
        new CurrencyDescription("RWF", "prm.global.currency.rwandafranc.name"),
        new CurrencyDescription("SAR", "prm.global.currency.saudiriyal.name"),
        new CurrencyDescription("SBD", "prm.global.currency.solomonislandsdollar.name"),
        new CurrencyDescription("SCR", "prm.global.currency.seychellesrupee.name"),
        new CurrencyDescription("SDD", "prm.global.currency.sudanesedinar.name"),
        new CurrencyDescription("SEK", "prm.global.currency.swedishkrona.name"),
        new CurrencyDescription("SGD", "prm.global.currency.singaporedollar.name"),
        new CurrencyDescription("SHP", "prm.global.currency.sainthelenapound.name"),
        new CurrencyDescription("SIT", "prm.global.currency.tolar.name"),
        new CurrencyDescription("SKK", "prm.global.currency.slovakkoruna.name"),
        new CurrencyDescription("SLL", "prm.global.currency.leone.name"),
        new CurrencyDescription("SOS", "prm.global.currency.somalishilling.name"),
        new CurrencyDescription("SRG", "prm.global.currency.surinameguilder.name"),
        new CurrencyDescription("STD", "prm.global.currency.dobra.name"),
        new CurrencyDescription("SVC", "prm.global.currency.elsalvadorcolon.name"),
        new CurrencyDescription("SYP", "prm.global.currency.syrianpound.name"),
        new CurrencyDescription("SZL", "prm.global.currency.lilangeni.name"),
        new CurrencyDescription("THB", "prm.global.currency.baht.name"),
        new CurrencyDescription("TJS", "prm.global.currency.somoni.name"),
        new CurrencyDescription("TMM", "prm.global.currency.manat.name"),
        new CurrencyDescription("TND", "prm.global.currency.tunisiandinar.name"),
        new CurrencyDescription("TOP", "prm.global.currency.paanga.name"),
        new CurrencyDescription("TPE", "prm.global.currency.timorescudo.name"),
        new CurrencyDescription("TRL", "prm.global.currency.turkishlira.name"),
        new CurrencyDescription("TTD", "prm.global.currency.trinidadandtobagodollar.name"),
        new CurrencyDescription("TWD", "prm.global.currency.newtaiwandollar.name"),
        new CurrencyDescription("TZS", "prm.global.currency.tanzanianshilling.name"),
        new CurrencyDescription("UAH", "prm.global.currency.hryvnia.name"),
        new CurrencyDescription("UGX", "prm.global.currency.ugandashilling.name"),
        new CurrencyDescription("USD", "prm.global.currency.usdollar.name"),
        new CurrencyDescription("UYU", "prm.global.currency.pesouruguayo.name"),
        new CurrencyDescription("UZS", "prm.global.currency.uzbekistansum.name"),
        new CurrencyDescription("VEB", "prm.global.currency.bolivar.name"),
        new CurrencyDescription("VND", "prm.global.currency.dong.name"),
        new CurrencyDescription("VUV", "prm.global.currency.vatu.name"),
        new CurrencyDescription("WST", "prm.global.currency.tala.name"),
        new CurrencyDescription("XAF", "prm.global.currency.cfafrancbeac.name"),
        new CurrencyDescription("XCD", "prm.global.currency.eastcaribbeandollar.name"),
        new CurrencyDescription("XDR", "prm.global.currency.sdr.name"),
        new CurrencyDescription("XOF", "prm.global.currency.cfafrancbceao.name"),
        new CurrencyDescription("XPF", "prm.global.currency.cfpfranc.name"),
        new CurrencyDescription("YER", "prm.global.currency.yemenirial.name"),
        new CurrencyDescription("YUM", "prm.global.currency.yugoslaviandinar.name"),
        new CurrencyDescription("ZAR", "prm.global.currency.rand.name"),
        new CurrencyDescription("ZMK", "prm.global.currency.zambiankwacha.name"),
        new CurrencyDescription("ZWD", "prm.global.currency.zimbabwedollar.name")
    };

    /**
     * A map from currency code to description.
     */
    private static Map CURRENCY_DESCRIPTION_MAP = new HashMap();

    // Construct the map from the descriptions
    static {
        for (Iterator it = Arrays.asList(CURRENCY_DESCRIPTIONS).iterator(); it.hasNext();) {
            CurrencyDescription nextDescription = (CurrencyDescription) it.next();
            CURRENCY_DESCRIPTION_MAP.put(nextDescription.code, nextDescription);
        }

    }

}