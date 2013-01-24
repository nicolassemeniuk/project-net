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
package net.project.security;

import java.security.SecureRandom;

/**
 * Provides a number of security related functions, such as generating
 * random strings of characters.
 * @author Roger Bly
 */
public class Crypto
        implements java.io.Serializable {

    //
    // Static members
    //

    /**
     * The characters that we will present (30 of them).
     * This omits potentially amiguous characters like <code>0, 1, I, l, O, Q</code>.
     */
    private static final char[] presentableCharacters = 
        {'2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','J','K','M','N','P','R','S','T','U','V','W','X','Y','Z'};


    /** 
     * Convenient array of characters useful for converting bytes (255 possible values)
     * to a presentable character without having to calculate <code>byte % 30</code>
     * each time.  This array actually has 270 elements.
     */
    private static final char[] byteToPresentableCharacterConverter = new char[Crypto.presentableCharacters.length * 9];
    static {
        for (int i = 0; i < byteToPresentableCharacterConverter.length; i++) {
            byteToPresentableCharacterConverter[i] = presentableCharacters[i % Crypto.presentableCharacters.length];
        }
    }


    //
    // Instance members
    //


    /**
     * Generates a verification code for registration purposes.
     * This is associated with the email address for final confirmation of the registration process.
     * The verification code is generated in such a way that it is not possible to predict based on the email or sequence of generation.
     * The returned verification code is not guaranteed to be unique within the system.  However the code is very difficult to predict from call to call.
     * @param size the number of characters in the returned code.
     * @return a unique, secure, random verification code String.
     * @see #generatePresentableRandomCharacterString
     */
    public static String generateVerificationCode(int size) {
        return generatePresentableRandomCharacterString(size);
    }


    /**
     * Generates a random string of characters where characters are
     * presentable for display.
     * The returned code string is apha-numeric, capital characters only
     * and contains only the following characters:
     * <code>'2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','J','K','M','N','P','R','S','T','U','V','W','X','Y','Z'</code>
     * <br>
     * Uses the SHA1PRNG secure pseudo-random number generation (PRNG) algorithm supplied by the SUN provider.
     * This implementation follows the IEEE P1363 standard, Appendix G.7: "Expansion of source bits", and uses SHA1
     * as the foundation of the PRNG. It computes the SHA1 hash over a true-random seed value concatenated with a 64-bit
     * counter which is incremented by 1 for each operation. From the 160-bit SHA1 output, only 64 bits are used.
     * @param size the number of characters in the resultant string
     * @return the string of random characters, with the specified number of characters
     */
    public static String generatePresentableRandomCharacterString(int size) {
        java.util.Random random = null;
        byte randomBytes[] = new byte[size];
        char result[]  = new char[size];

        /*
            01/18/2002 - Tim Morrow
            Explanation of previously-observed performance problems with SecureRandom class.
            
            Description
            ===========
            
            The problem lies not with SecureRandom.getInstance(), but with the
            first call to nextBytes().
            The first call to nextBytes requires the SecureRandom object to
            "seed" itself.  A complex and unpredictable seed is the absolute
            key to generating good random numbers.
            This seeding process takes 10 seconds on my PC.  However it occurs
            only once for the VM (I believe).  Additionally, it is possible
            to manually seed the object - but since this is the essence of
            a good random number, the seed must be unpredictable itself.
            Platform dependent solutions are offered:  For example, on Linux
            you can read from /dev/random to get a random seed.  I have not
            seen a solution for Windows platforms though.
            
            Finally, an explanation of the real difference between SecureRandom
            and Random.  On a PC, the Random function appears to use the clock
            to generate random number.  However, the clock only changes about
            every 18ms.  Therefore, every call to Random.nextBytes in an 18ms
            period will return THE SAME random value.  Additionally, it is
            possible to predict a random number with knowledge of the algorithm
            and the clock time in milliseconds.
            SecureRandom uses a specified algorithm (e.g. SHA1PRNG) to generate
            random numbers.  This means it can be called many times in succession
            and return different and unpredictable random numbers, even within
            milliseconds.
            
            Conclusion
            ==========
            I suggest we turn SecureRandom back on.  However we must
            fully test the functionality to ensure that it really does only
            take the hit ONCE per VM.
         */
            
 
        // Get secure random class
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        
        } catch (java.security.NoSuchAlgorithmException e) {
            // Runtime exception since this method never previously declared one
            throw new IllegalStateException("Error generating random string: " + e);
        }

        // Generate next random number and populate the randomBytes array
        // This takes some time for first usage in the VM
        random.nextBytes(randomBytes);

        // Convert random bytes into presentable string
        for (int i = 0; i < size; i++) {
            // strip sign off the number, must be unsigned int
            // in range 0..255 for array index
            result[i] = byteToPresentableCharacterConverter[(randomBytes[i] & 0xFF)];
        }
        
        // Return result characters as a string
        return new String(result);
    }

}
