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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import net.project.base.property.PropertyProvider;
import net.project.security.crypto.SecretKeyType;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * This class allows you to encrypt and decrypt strings using two algorithms.
 * <li>Password Based Encryption (PBE) - uses the password to encrypt itself in a one-way
 * encryption algorithm
 * <li>Blowfish - Uses a secret key to perform two-way encryption
 * It returns encrypted values encoded as hexadecimal.
 *
 * @author unascribed
 * @author Tim Morrow
 * @since Version 1.0
 */
public class EncryptionManager {

    /**
     * The valid characters for the PBE scheme.
     */
    private static final String VALID_PBE_PASSWORD_CHARACTERS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    /**
     * All strings used in the symmetric encryption algorithm (Blowfish)
     * are assumed to be of this encoding.
     * <b>Note:</b> Changing this is likely to break existing encryption or
     * decryption.  It is used to convert strings to bytes.  The conversion
     * must be identical over time.
     */
    private static final String ENCODING = "utf-8";

    //
    // PBE Encryption methods
    //

    /**
     * Encrypts the specified string using Password Based Encryption (PBE).
     * See <a href="http://www.rsasecurity.com/solutions/developers/whitepapers.html">RSA Security | Developers Solutions</a>
     * and <a href="http://www.rsasecurity.com/rsalabs/pkcs/pkcs-5/">RSA Laboratories | PKCS | #5 - Password-Based Cryptography Standard</a>
     * for additional documentation.
     * <p>
     * <b>Note:</b> Due to the algoritm we use, the password used for encryption
     * must be ASCII only.  Testing has proven that the bytes in the password
     * must be in the range decimal 32 to decimal 126 inclusive, which are:
     * <code><pre> !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</pre></code> <br>
     * Since the single parameter to this method is both the plain text to be
     * encrypted AND the password used for encryption, it limits the plain text
     * to ASCII only, thus limiting passwords in our application to ASCII only.
     * </p>
     * @param pass the password to encrypt; this is also used as the password
     * for the PBE algorithm,
     * @return the encrypted string; this is the cipher text encoded as
     * hexadecimal.  Since it does not include the salt, it cannot be decrypted.
     * @throws NullPointerException if password is null
     * @throws InvalidPasswordForEncryptionException if the specified password
     * was invalid for the PBE algorithm to use as a key
     * @throws EncryptionException if there is some problem encrypting
     */
    public static String pbeEncrypt(String pass) throws InvalidPasswordForEncryptionException, EncryptionException {

        if (pass == null) {
            throw new NullPointerException("password is required");
        }

        // Salt - DO NOT MODIFY
        // The Salt is used to modify the digesting of the password.
        // It is supposedly a random number
        // The same salt must be used to produce the same result after encryption.
        // If the salt was modify in a product version, all existing User's
        // passwords would fail to be validated
        // With password based encryption, the salt does not have to be secret
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        // Iteration Count - DO NOT MODIFY
        // The iteration count is used to complicate the key derivation function
        // With password based encryption, the iteration count does not have to be secret
        int count = 20;

        // Encrypt the password string using the specified parameters
        // DO NOT change the algorithm
        // Converts the encrypted to string to hex form
        // MD5 returns a 16 byte value
        return getHexString(pbeEncrypt(pass, salt, count, "PBEWithMD5AndDES"));
    }

    /**
     * Encrypts the specified password using PBE encryption.
     * The password is both the plaintext being encrypted AND the password used
     * to encrypt it.
     * @param password both the plain text to encrypt and the password to use for
     * the encryption
     * @param salt the salt to use
     * @param iteration the number of iterations to use
     * @param algorithm the algorithm to use
     * @throws InvalidPasswordForEncryptionException if the specified password
     * was invalid for the PBE algorithm to use as a key
     * @throws EncryptionException if there is a problem encrypting
     */
    private static byte[] pbeEncrypt(String password, byte[] salt, int iteration, String algorithm) throws InvalidPasswordForEncryptionException, EncryptionException {
        PBEKeySpec pbeKeySpec = null;
        PBEParameterSpec pbeParamSpec = null;
        SecretKey pbeKey = null;
        Cipher pbeCipher = null;
        byte[] ciphertext = null;

        // Create PBE Key Spec using the password
        // The key spec is used to generate the key
        pbeKeySpec = new PBEKeySpec(password.toCharArray());

        // Generate a secret key for the specified algorithm
        // in the default provider package using the PBE Key Spec
        try {
            //pbeKey = SecretKeyFactory.getInstance(algorithm, new BouncyCastleProvider()).generateSecret(pbeKeySpec);
        	pbeKey = SecurityInstances.secretKeyFactoryMD5.generateSecret(pbeKeySpec);
        	
        } catch (InvalidKeySpecException e) {
            // The password cannot be used as the secret key spec
            // For example, the password contains non-ascii characters
            throw new InvalidPasswordForEncryptionException("Unable to encrypt password; the password contains illegal characters. " +
                    "Characters must be in the range ASCII decimal 32 to 126 inclusive: " + e, e);

        } 

        // Create PBE parameter set using the salt and iteration count
        pbeParamSpec = new PBEParameterSpec(salt, iteration);

        // Create and Initialize PBE Cipher with key and parameters
        try {
            //pbeCipher = Cipher.getInstance(algorithm, new BouncyCastleProvider());
        	pbeCipher = SecurityInstances.cipherMD5;
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        } catch (InvalidKeyException e) {
            // This error may occur if the JCE policy files have not been
            // updated with those allowing maximum strength encryption
            throw new EncryptionException("Unable to encrypt password; key is inappropriate or key size " +
                    "exceeds maximum allowable as determined by current policy.  " +
                    "Check the JCE Policy files: " + e, e);

        } catch (GeneralSecurityException e) {
            // NoSuchAlgorithmException
            // NoSuchPaddingException
            // InvalidAlgorithmParameterException
            // These are likely to be programming errors
            throw new EncryptionException("Unable to encrypt password: " + e, e);

        }

        // Then encrypt the password
        try {
            // 01/09/2003 - Vishwajeet
            // We switched from using platform default encoding to a specific encoding
            // This is necessary so that encryption has a deterministic behavior
            ciphertext = pbeCipher.doFinal(password.getBytes(EncryptionManager.ENCODING));

        } catch (GeneralSecurityException e) {
            // IllegalStateException
            // IllegalBlockSizeException
            // BadPaddingException
            // Programming error
            throw new EncryptionException("Unable to encrypt password: " + e, e);

        } catch (UnsupportedEncodingException e) {
            // EncryptionManager.ENCODING is not a supported encoding
            // Programming error
            throw new EncryptionException("Unable to encrypt password: " + e, e);

        }

        return ciphertext;
    }

    /**
     * Indicates whether the specified password contains valid characters.
     * If a password is valid then {@link #pbeEncrypt} can be called
     * without throwing an <code>InvalidPasswordForEncryptionException</code>.
     * @param password the password to check
     * @return true if it each character is valid; false otherwise
     */
    public static boolean isValidPasswordCharactersForPBE(String password) {

        boolean isInvalid = false;

        final char[] passwordChars = password.toCharArray();

        // Check each character to ensure it is not invalid
        for (int i = 0; i < passwordChars.length && !isInvalid; i++) {
            if (VALID_PBE_PASSWORD_CHARACTERS.indexOf(passwordChars[i]) == -1) {
                isInvalid = true;
            }

        }

        return !isInvalid;
    }

    //
    // Blowfish encryption methods
    //

    /**
     * Encrypts the specified string using the Blowfish algorithm and the
     * default secret key type.
     * @param str the string to encrypt
     * @throws EncryptionException if there is a problem encrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    public static String encryptBlowfish(String str) throws EncryptionException {
        byte[] encrypted = encryptBlowfishBytes(str, SecretKeyType.DEFAULT);
        return getHexString(encrypted);
    }

    /**
     * Encrypts the specified string using the Blowfish algorithm returning
     * a string made up of 2-digit hexadecimal values.
     * @param str the string to encrypt
     * @param secretKeyType the secret key type to use
     * @return the encrypted string made up of pairs of hexadecimal characters
     * @throws EncryptionException if there is a problem encrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    public static String encryptBlowfish(String str, SecretKeyType secretKeyType) throws EncryptionException {
        byte[] encrypted = encryptBlowfishBytes(str, secretKeyType);
        return getHexString(encrypted);
    }

    /**
     * Decrypts the specified string that was encrypted using <code>{@link #encryptBlowfish}</code>
     * with the default secret key type.
     * @param str the encrypted string where each pair of hexadecimal digits
     * is assumed to be one byte.
     * @return the decrypted string
     * @throws EncryptionException if there is a problem decrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    public static String decryptBlowfish(String str) throws EncryptionException {
        byte[] encrypted = getBytesFromHex(str);
        return decryptBlowfishBytes(encrypted, SecretKeyType.DEFAULT);
    }

    /**
     * Decryopts the specified string that was encrypted using <code>{@link #encryptBlowfish}</code>.
     * @param str the encrypted string where each pair of hexadecimal digits
     * is assumed to be one byte.
     * @param secretKeyType the secret key type to use
     * @return the decrypted string
     * @throws EncryptionException if there is a problem decrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    public static String decryptBlowfish(String str, SecretKeyType secretKeyType) throws EncryptionException {
        byte[] encrypted = getBytesFromHex(str);
        return decryptBlowfishBytes(encrypted, secretKeyType);
    }


    /**
     * Encrypts the specified string using the Blowfish algorithm.
     * @param str the string to encrypt
     * @param secretKeyType the secret key type to use
     * @return encrypted byte array; each value is an actual byte
     * @throws EncryptionException if there is a problem decrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    private static byte[] encryptBlowfishBytes(String str, SecretKeyType secretKeyType) throws EncryptionException {
        byte[] encrypted = null;

        try {
            Cipher cipher = null;
            // Create the Cipher and initialize it with the secret key spec
            cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding", new BouncyCastleProvider());
            //cipher = SecurityInstances.cipherBlowfish;
            cipher.init(Cipher.ENCRYPT_MODE, secretKeyType.getKey());

            // Encrypt the string using the encoding
            encrypted = cipher.doFinal(str.getBytes(EncryptionManager.ENCODING));

        } catch (java.security.InvalidKeyException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting.  Invalid security key: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);

        } catch (java.io.UnsupportedEncodingException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);

        } catch (java.security.GeneralSecurityException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);

        }

        return encrypted;
    }

    /**
     * Decrypts the specified encrypted byte array that was encrypted using
     * <code>{@link #encryptBlowfishBytes}</code>.
     * @param encrypted the encrypted bytes
     * @param secretKeyType the secret key type to use
     * @return the decrypted string
     * @throws EncryptionException if there is a problem decrypting, for example the secret
     * key cannot be read or the algorithm is not supported
     */
    private static String decryptBlowfishBytes(byte[] encrypted, SecretKeyType secretKeyType) throws EncryptionException {
        String decrypted = null;

        try {
            // Create the Cipher and initialize it with the secret key spec
            Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding", new BouncyCastleProvider());
        	cipher.init(Cipher.DECRYPT_MODE, secretKeyType.getKey());

            // Decrypt the string
            // Now return the decrypted bytes as a string using the platform default
            // encoding
            // 01/10/2002 - Tim - Shouldn't this construct it using the EncryptionManager.ENCODING
            // encoding?  Since that would have been utilized during encryption?
            //01/09/2003 - Vishwajeet - We agreed that not using a known encoding would create problem if
            //platform default encoding changes in future. So we must use the EncryptionManager.ENCODING and
            //we should set it to "utf-8".
            decrypted = new String(cipher.doFinal(encrypted), EncryptionManager.ENCODING);

        } catch (java.security.InvalidKeyException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting.  Invalid security key: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);
            
        } catch (BadPaddingException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting: " + e.getMessage());
            throw new EncryptionException(PropertyProvider.get("prm.global.license.decryption.badpadding.error.message")+e.getMessage(),e);
            
        } catch (java.security.GeneralSecurityException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);

        } catch (UnsupportedEncodingException e) {
        	Logger.getLogger(EncryptionManager.class).error("Error encrypting: " + e);
            throw new EncryptionException("Encryption operation failed: " + e, e);
        }

        return decrypted;
    }

    //
    // Utility Methods
    //

    /**
     * Converts an array of bytes into an array of characters representing the hexidecimal values 
     * of each byte in order and returns in a string.
     * @param ba the bytes to covnert
     * @return the string of hexadecimal values 
     */
    private static String getHexString(byte[] ba) {
    	
        char[] buf = null;
        //Convert encoded array of bytes into an array of characters 
          //representing the hexidecimal values of each byte in order.
        buf = Hex.encodeHex(ba);
       
      // return the string created using this character array  
        return new String(buf);
    }


    /**
     * Returns a byte array from a string of hexadecimal digits.
     * @param hex the hexadecimal string 
     */
    private static byte[] getBytesFromHex(String hex) throws EncryptionException{
        char[] charArrFromHexStr = null;
        byte[] buf = null;
        try{
        //We have the string of hexadecimal characters. convert it into array of characters
        	charArrFromHexStr = hex.toCharArray();
        //Convert this array of characters representing hexidecimal values into an array of bytes
        //of those same values.
        	buf = Hex.decodeHex(charArrFromHexStr);
        }catch(DecoderException dd){
        	throw new EncryptionException("decoding failed "+dd.getMessage(),dd);
        }
        return buf;
    }
    //
    // Unit test
    //

    public static void main(String[] args) {
        try {
        	String symmetricClear = "This is a test.";
            String symmetricDefaultCipher = null;
            
            System.out.println("Testing all secret keys");
            System.out.println("Source: " + symmetricClear);
            System.out.println("Default encryption: " + (symmetricDefaultCipher = encryptBlowfish(symmetricClear)));
            System.out.println("Default reversed: " + decryptBlowfish(symmetricDefaultCipher));
            System.out.println("License encryption: " + (symmetricDefaultCipher = encryptBlowfish(symmetricClear, SecretKeyType.LICENSE_CERTIFICATE)));
            System.out.println("License reversed: " + decryptBlowfish(symmetricDefaultCipher, SecretKeyType.LICENSE_CERTIFICATE));

        } catch (Exception e) {
            e.printStackTrace();

        }


    }

}