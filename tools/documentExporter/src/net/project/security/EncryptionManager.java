/* 
 * Copyright 2000-2006 Project.net Inc.
 *
 * Licensed under the Project.net Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://dev.project.net/licenses/PPL1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 15489 $
|       $Date: 2006-10-02 20:41:03 +0200 (Mon, 02 Oct 2006) $
|     $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

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
	
	private final Logger logger = Logger.getLogger(getClass());

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

    /**
     * Used for converting bytes to hex digits.
     */
    private static final char[] hexDigits = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    static {
    	Security.addProvider(new BouncyCastleProvider());
    }

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
    public static String pbeEncrypt(String pass)
            throws InvalidPasswordForEncryptionException, EncryptionException {

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
    private static byte[] pbeEncrypt(String password, byte[] salt, int iteration, String algorithm)
            throws InvalidPasswordForEncryptionException, EncryptionException {

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
            pbeKey = SecretKeyFactory.getInstance(algorithm, new BouncyCastleProvider()).generateSecret(pbeKeySpec);

        } catch (InvalidKeySpecException e) {
            // The password cannot be used as the secret key spec
            // For example, the password contains non-ascii characters
            throw new InvalidPasswordForEncryptionException("Unable to encrypt password; the password contains illegal characters. " +
                    "Characters must be in the range ASCII decimal 32 to 126 inclusive: " + e, e);

        } catch (NoSuchAlgorithmException e) {
            // Programming error
            throw new EncryptionException("Unable to encrypt password; algorithm '" + algorithm + "' is not supported: " + e, e);

        }

        // Create PBE parameter set using the salt and iteration count
        pbeParamSpec = new PBEParameterSpec(salt, iteration);

        // Create and Initialize PBE Cipher with key and parameters
        try {
            pbeCipher = Cipher.getInstance(algorithm, new BouncyCastleProvider());
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



    /**
     * Converts each character in the specified byte area to a two-digit
     * hexadecimal value and returns in a string.
     * @param ba the bytes to covnert
     * @return the string of hexadecimal values where the first hexadecimal
     * value (given by the first two characters in the string) is converted
     * from the zero-th element of the byte array
     */
    private static String getHexString(byte[] ba) {
        char[] buf = new char[ba.length * 2];
        int j = 0;
        int nextByte;

        // Iterate over bytes converting each byte to a two-character hexadecimal
        // value
        for (int i = 0; i < ba.length; i++) {
            nextByte = ba[i];

            // It is better to use this manual method rather than
            // Integer.toHexString() since the latter method may return
            // a single hex character if the high order nibble is 0.
            // E.g. byte "00001111" = "f"; with our method "0f"

            // Convert high order nibble
            buf[j++] = hexDigits[(nextByte >>> 4) & 0x0F];
            // Convert low order nibble
            buf[j++] = hexDigits[nextByte & 0x0F];
        }

        return new String(buf);
    }


    /**
     * Returns a byte array from a string of hexadecimal digits.
     * @param hex the hexadecimal string where each pair of hexadecimal digits
     * is converted to a byte; the zero-th element of the byte array is the byte
     * converted from the first pair of hexadecimal characters in the string.
     * If there are an odd number of hex digits, the first digit is converted
     * to a byte whose value is >= 00001111.
     */
    private static byte[] getBytesFromHex(String hex) {
        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;

        // If there were an odd number of characters, convert the first
        // character as the least significant nibble of a byte
        if ((len % 2) == 1) {
            buf[j++] = (byte)fromDigit(hex.charAt(i++));
        }

        // Now process the remaining pairs of hex digits
        while (i < len) {
            buf[j++] = (byte)((fromDigit(hex.charAt(i++)) << 4) |
                fromDigit(hex.charAt(i++)));
        }

        return buf;
    }

    /**
     * Returns the number from 0 to 15 corresponding to the specified hex digit.
     * @param ch the hex character in the range 0..9, a..f or A..F
     * @return the value in the range 0..15
     */
    private static int fromDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;

        throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
    }


}
