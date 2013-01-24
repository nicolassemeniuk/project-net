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
|   $Revision: 16593 $
|       $Date: 2007-12-01 13:23:29 +0530 (Sat, 01 Dec 2007) $
|     $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import java.io.UnsupportedEncodingException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests EncryptionManager.
 *
 * @author Tim Morrow
 * @since Version 7.6
 */
public class EncryptionManagerTest extends TestCase {

    public EncryptionManagerTest(String testName) {
        super(testName);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(EncryptionManagerTest.class);

        return suite;
    }

    /**
     * Tests the {@link EncryptionManager#pbeEncrypt(String)} method with
     * a null value.
     */
    public void testPbeEncryptNull() {

        try {
            EncryptionManager.pbeEncrypt(null);
            fail("Unexpected success when encrypting null value");

        } catch (NullPointerException e) {
            // Successful result

        } catch (EncryptionException e) {
            fail("Error encrypting null value: " + e);

        }

    }

    /**
     * Tests the {@link EncryptionManager#pbeEncrypt(String)} method with
     * an empty value.
     */
    public void testPbeEncryptEmpty() {

        try {
            // Expected result is a non-empty cipher
            assertFalse("Encrypting an empty string returned an empty string", EncryptionManager.pbeEncrypt("").equals(""));

        } catch (EncryptionException e) {
            fail("Error encrypting empty value: " + e);

        }

    }

    /**
     * Tests the {@link EncryptionManager#pbeEncrypt(String)} method with
     * a range of values.
     */
    public void testPbeEncrypt() {

        String plainText;

        // Test value
        plainText = "asdf;lkj";

        try {

            // Ensure that result of encryption is different from plain text
            // Expected result is a non-empty string different from plain text
            assertFalse("Encrypted value was same as plain text", EncryptionManager.pbeEncrypt(plainText).equals(plainText));

            // Ensure that two encryptions of same plain text result in same cipher
            // Expected result is two encryptions return the same value
            assertEquals("Performing an encryption twice resulted in different ciphers", EncryptionManager.pbeEncrypt(plainText), EncryptionManager.pbeEncrypt(plainText));

            // Ensure all ASCII characters may be used
            // Actually, the only ASCII characters supported are from
            // decimal 32 to decimal 126 inclusive
            // Expected result is encrypted value is a non-empty string not
            // equal to the plain text argument
            int firstValue = 32;
            int lastValue = 126;
            byte[] bytes = new byte[lastValue - firstValue + 1];
            for(int i = firstValue; i <= lastValue; i++) {
                bytes[i-firstValue] = (byte)(i & 0xFF);
            }
            plainText = new String(bytes, "US-ASCII");
            assertFalse("Encrypting all ASCII characters returned a result equal to is argument", EncryptionManager.pbeEncrypt(plainText).equals(plainText));

            // Ensure when non-ascii characters are used an error occurs
            // Expected result is an EncryptionException
            plainText = "testötest";
            try {
                EncryptionManager.pbeEncrypt(plainText);
                // This should not fail, because internally Encryption mgr does not use the charset of sting parameter passed
                // fail("Unexpected successful result when encrypting non-ASCII characters");

            } catch (InvalidPasswordForEncryptionException e) {
                // failure condition
            	fail("Unexpected result when encrypting non-ASCII characters");
            }

        } catch (EncryptionException e) {
            e.printStackTrace();
            fail("Unexpected encryption error: " + e);

        } catch (UnsupportedEncodingException e) {
            fail("Unexpected error: " + e);

        }

    }

}
