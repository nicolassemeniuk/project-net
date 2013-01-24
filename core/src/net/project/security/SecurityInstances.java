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
package net.project.security;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SecurityInstances {

	private static Logger log = Logger.getLogger(SecurityInstances.class);

	public static SecretKeyFactory secretKeyFactoryMD5 = null;

	public static Cipher cipherMD5 = null;

	public static Cipher cipherBlowfish = null;

	static {
		try {
			long time = System.currentTimeMillis();
			Security.addProvider(new BouncyCastleProvider());
			secretKeyFactoryMD5 = SecretKeyFactory.getInstance("PBEWithMD5AndDES", new BouncyCastleProvider());
			cipherMD5 = Cipher.getInstance("PBEWithMD5AndDES", new BouncyCastleProvider());
			cipherBlowfish = Cipher.getInstance("Blowfish/ECB/PKCS5Padding", new BouncyCastleProvider());
			if (log.isDebugEnabled()) {
				log.debug(" SecurityInstances initialized for:" + (System.currentTimeMillis() - time) + " milis");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testPolicy() throws Exception {
		byte[] data = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };

		// create a 64 bit secret key from raw bytes

		SecretKey key64 = new SecretKeySpec(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish");

		// create a cipher and attempt to encrypt the data block with our key

		Cipher c = Cipher.getInstance("Blowfish/ECB/NoPadding");
		try {
			c.init(Cipher.ENCRYPT_MODE, key64);
			c.doFinal(data);
			log.info("64 bit test: passed");
		} catch (Exception e) {
			log.info("64 bit test: failed");
		}
		// create a 192 bit secret key from raw bytes

		SecretKey key192 = new SecretKeySpec(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13,
				0x14, 0x15, 0x16, 0x17 }, "Blowfish");

		// now try encrypting with the larger key
		try {
			c.init(Cipher.ENCRYPT_MODE, key192);
			c.doFinal(data);
			log.info("192 bit test: passed");
		} catch (Exception e) {
			log.info("192 bit test: failed");
			log.info("Unlimited Strength Jurisdiction Policy Files are not installed properly !!!");
		}
		log.info("You currently use Java with JRE installed in:"+System.getProperty("java.home"));
		log.info("You currently use Java Runtime Environment version:"+System.getProperty("java.version"));
		log.info("Unlimited Strength Jurisdiction Policy Files tests completed");
	}

}
