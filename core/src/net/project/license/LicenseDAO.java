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
package net.project.license;

import java.sql.SQLException;

import net.project.persistence.PersistenceException;
import net.project.security.EncryptionException;

/**
 * Utility class which provides some functionality to construct a sql statement
 * by providing the code string for doing so.  Additionally, this class provides
 * the means to encrypt or decrypt a license certificate.
 *
 * @author Tim
 * @since Gecko Update 2
 */
public final class LicenseDAO implements java.io.Serializable {
    /**
     * Encrypts the source using the license security key.
     * @param source the source to encrypt
     * @throws EncryptionException if there is a problem encrypting
     * @see net.project.security.EncryptionManager#encryptBlowfish
     */
    static String encrypt(String source) throws EncryptionException {
        return net.project.security.EncryptionManager.encryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE);
    }

    /**
     * Decrypts the source using the license security key.
     * @param source the encrypted source to decrypt
     * @throws EncryptionException if there is a problem decrypting
     * @see net.project.security.EncryptionManager#decryptBlowfish
     */
    static String decrypt(String source) throws EncryptionException {
        return net.project.security.EncryptionManager.decryptBlowfish(
            source, net.project.security.crypto.SecretKeyType.LICENSE_CERTIFICATE);
    }


    /**
     * Returns the select part to load one or more licenses.
     * <li><code>PN_LICENSE</code> aliased to <code>L</code>
     * <li>No <code>WHERE</code> clause
     * @return the select part of a query
     */
    public static String getQueryLoadLicense() {
        StringBuffer query = new StringBuffer();
        query.append("select l.license_id, l.certificate_id, l.payment_id, l.license_key_value," + 
		     " l.is_trial, l.license_status, l.status_reason_code, l.responsible_user_id ");
        query.append("from pn_license l ");
        return query.toString();
    }

    /**
     * Returns the select part to load one or more license certificates.
     * <li><code>PN_LICENSE_CERTIFICATE</code> aliased to <code>lc</code>
     * <li>No <code>WHERE</code> caluse
     * @return the select part of the query
     */
    public static String getQueryLoadLicenseCertificate() {
        StringBuffer query = new StringBuffer();
        query.append("select lc.certificate_id ");
        query.append("from pn_license_certificate lc ");
        return query.toString();
    }

    /**
     * Populates a license object from a resultset.
     * This fully populates the license (including certificate, payment, key etc.)
     * @param result the resultset containing data to populate
     * @param license the license object to populate
     * @throws InvalidLicenseCertificateException if there is a problem reading
     * the license certificate
     * @throws SQLException if there is a problem reading columns from the result
     * @throws PersistenceException if there is a problem populating the license
     */
    public static void populateLicense(java.sql.ResultSet result, License license)
            throws InvalidLicenseCertificateException, SQLException, PersistenceException {

        license.setID(result.getString("license_id"));
        license.loadCertificate(result.getString("certificate_id"));
        license.loadPaymentInformation(result.getString("payment_id"));
        license.createKeyForValue(result.getString("license_key_value"));
        license.setTrial(net.project.util.Conversion.toBoolean(result.getString("is_trial")));
	    license.setStatus(net.project.util.Conversion.toInt(result.getString("license_status")), net.project.util.Conversion.toInt(result.getString("status_reason_code")));
        license.setResponsiblePersonID(result.getString("responsible_user_id"));
    }

    /**
     * Populates a license certificate object from a result set.
     * @param result the result set containing data to populate
     * @param certificate the license certificate to populate
     * @throws SQLException if there is a problem reading columns from the result
     * @throws PersistenceException if there is a problem populating the license certificate
     */
    public static void populateLicenseCertificate(java.sql.ResultSet result, LicenseCertificate certificate)
            throws java.sql.SQLException, PersistenceException {

        certificate.setID(result.getString("certificate_id"));
    
    }
        

}
