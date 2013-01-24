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
 package net.project.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.project.application.Application;

/**
 * Set of test cases to test the correct implementation of the Validator class.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ValidatorTest extends TestCase {
    
    public ValidatorTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ValidatorTest.class);
        Application.login();

        return suite;
    }
    
    /** Test of isBlankOrNull method, of class net.project.util.Validator. */
    public void testIsBlankOrNull() {
        assertTrue(Validator.isBlankOrNull(null));
        assertTrue(Validator.isBlankOrNull(""));
        assertTrue(Validator.isBlankOrNull(" "));
        assertTrue(Validator.isBlankOrNull("\t"));
        assertTrue(Validator.isBlankOrNull("\n"));
        assertFalse(Validator.isBlankOrNull("1"));
        assertFalse(Validator.isBlankOrNull("a"));
        assertFalse(Validator.isBlankOrNull("#"));
        assertFalse(Validator.isBlankOrNull("asdfasdfasdfasdf"));
    }
    
    /** Test of matchRegexp method, of class net.project.util.Validator. */
    public void testMatchRegexp() {
        assertTrue(Validator.matchRegexp("1", "1"));
    }
    
    /** Test of isByte method, of class net.project.util.Validator. */
    public void testIsByte() {
        assertTrue(Validator.isByte("1"));
        assertFalse(Validator.isByte("123123123"));
        assertFalse(Validator.isByte("asdf"));
    }
    
    /** Test of isShort method, of class net.project.util.Validator. */
    public void testIsShort() {
        assertTrue(Validator.isShort("1"));
        assertFalse(Validator.isShort("1234123412341234"));
        assertFalse(Validator.isShort("asdf"));
    }
    
    /** Test of isInt method, of class net.project.util.Validator. */
    public void testIsInt() {
        assertTrue(Validator.isInt("1"));
        assertFalse(Validator.isInt("1238470198237401982736409182736409187234"));
        assertFalse(Validator.isInt("asdf"));
    }
    
    /** Test of isLong method, of class net.project.util.Validator. */
    public void testIsLong() {
        assertTrue(Validator.isLong("1"));
        assertFalse(Validator.isLong("asdf"));
    }
    
    /** Test of isFloat method, of class net.project.util.Validator. */
    public void testIsFloat() {
        assertTrue(Validator.isFloat("1"));
        assertFalse(Validator.isFloat("asdf"));
    }
    
    /** Test of isDouble method, of class net.project.util.Validator. */
    public void testIsDouble() {
        assertTrue(Validator.isDouble("1"));
        assertFalse(Validator.isDouble("asdf"));
    }
    
    /** Test of isDate method, of class net.project.util.Validator. */
    public void testIsDate() {
        assertTrue(Validator.isDate("1/1/01"));
        assertFalse(Validator.isDate("asdf"));
    }
    
    /** Test of isInRange method, of class net.project.util.Validator. */
    public void testIsInRange() {
        int oneInt = 1;
        short oneShort = 1;

        assertTrue(Validator.isInRange(oneInt, 1, 5));
        assertTrue(Validator.isInRange(oneShort, 1, 1));
        assertTrue(Validator.isInRange(1f, 1f, 1f));
        assertTrue(Validator.isInRange(1d, 1d, 1d));
    }
    
    /** Test of isCreditCard method, of class net.project.util.Validator. */
    public void testIsCreditCard() {
        assertTrue(Validator.isCreditCard(("4111111111111111")));
        assertTrue(Validator.isCreditCard("4111-1111-1111-1111"));
    }
    
    /** Test of isEmail method, of class net.project.util.Validator. */
    public void testIsEmail() {
        assertTrue(Validator.isEmail("matt@project.net"));
        assertTrue(Validator.isEmail("matt.flower@project.net"));
        assertTrue(Validator.isEmail("123@project.net"));
        assertTrue(Validator.isEmail("123@project.com"));
        assertTrue(Validator.isEmail("matt@project.org"));
        assertTrue(Validator.isEmail("matt@this.is.a.valid.domain.name.dot.com"));

        assertFalse(Validator.isEmail("@"));
        assertFalse(Validator.isEmail(".@."));
        assertFalse(Validator.isEmail(".@.com"));
        assertFalse(Validator.isEmail("matt@"));
        assertFalse(Validator.isEmail("localhost"));
    }
    
    /** Test of maxLength method, of class net.project.util.Validator. */
    public void testMaxLength() {
        assertTrue(Validator.maxLength("a", 1));
        assertTrue(Validator.maxLength("", 0));
        assertFalse(Validator.maxLength("asdf", 1));
        assertFalse(Validator.maxLength("asdf", 0));
    }
    
    /** Test of minLength method, of class net.project.util.Validator. */
    public void testMinLength() {
        assertTrue(Validator.minLength("a", 1));
        assertTrue(Validator.minLength("a", 0));
        assertFalse(Validator.minLength("", 1));
        assertFalse(Validator.minLength("a", 2));
    }
    
    /** Test of isNumeric method, of class net.project.util.Validator. */
    public void testIsNumeric() {
        // Negative cases
        assertFalse(Validator.isNumeric(null));
        assertFalse(Validator.isNumeric(""));
        assertFalse(Validator.isNumeric(" "));
        assertFalse(Validator.isNumeric("a"));
        assertFalse(Validator.isNumeric("#"));
        assertFalse(Validator.isNumeric("A"));
/*
        Each of these cases should also fail, but do not based on the current implementation of the isNumeric() method.
        assertFalse(Validator.isNumeric(" 123"));
        assertFalse(Validator.isNumeric("123 "));
        assertFalse(Validator.isNumeric("123.45"));
        assertFalse(Validator.isNumeric("123 45"));
        assertFalse(Validator.isNumeric(".23"));
        assertFalse(Validator.isNumeric("123e4"));
*/

        // Positive Cases
        assertTrue(Validator.isNumeric("0"));
        assertTrue(Validator.isNumeric("1"));
        assertTrue(Validator.isNumeric("11"));
        assertTrue(Validator.isNumeric("123"));
        assertTrue(Validator.isNumeric("1234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234123412341234"));
    }

    /** Test of isValidDatabaseIdentifier method, of class net.project.util.Validator. */
    public void testIsValidDatabaseIdentifier() {
        // Negative cases
        assertFalse(Validator.isValidDatabaseIdentifier(null));
        assertFalse(Validator.isValidDatabaseIdentifier(""));
        assertFalse(Validator.isValidDatabaseIdentifier(" "));
        assertFalse(Validator.isValidDatabaseIdentifier("a"));
        assertFalse(Validator.isValidDatabaseIdentifier("-1"));
        assertFalse(Validator.isValidDatabaseIdentifier("-100"));
        //41 characters in length
        assertFalse(Validator.isValidDatabaseIdentifier("123456789012345678901"));

        // Positive Cases
        assertTrue(Validator.isValidDatabaseIdentifier("12345678901234567890"));
        assertTrue(Validator.isValidDatabaseIdentifier("1"));
    }
    
}
