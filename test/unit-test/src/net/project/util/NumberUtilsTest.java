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

import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NumberUtilsTest extends TestCase {
	
    public NumberUtilsTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(NumberUtilsTest.class);
        
        return suite;
    }
    
	public void testDynamicScale() {
		//case 1: 1.00000 
		BigDecimal result = NumberUtils.dynamicScale(new BigDecimal("1.00000"));
		assertEquals(result, new BigDecimal("1"));
		
		//case 2: 1.12000 
		result = NumberUtils.dynamicScale(new BigDecimal("1.12000"));
		assertEquals(result, new BigDecimal("1.12"));
		
		//case 3: -1.12114 
		result = NumberUtils.dynamicScale(new BigDecimal("-0.12114"));
		assertEquals(result, new BigDecimal("-0.12114"));
		
		//case 4: 1
		result = NumberUtils.dynamicScale(new BigDecimal("1"));
		assertEquals(result, new BigDecimal("1"));
		
		//case 5: 0.00000
		result = NumberUtils.dynamicScale(new BigDecimal("0"));
		assertEquals(result, new BigDecimal("0"));
		
		//case 6: 0
		result = NumberUtils.dynamicScale(new BigDecimal("0"));
		assertEquals(result, new BigDecimal("0"));

		//case 7: -0.00075170267916319327027844833145362948646367489138816789496448483068538401322515104
		result = NumberUtils.dynamicScale(new BigDecimal("-0.00075170267916319327027844833145362948646367489138816789496448483068538401322515104"));
		assertEquals(result, new BigDecimal("-0.00075170267916319327027844833145362948646367489138816789496448483068538401322515104"));
	}

}
