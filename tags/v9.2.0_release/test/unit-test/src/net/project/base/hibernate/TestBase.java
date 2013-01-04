package net.project.base.hibernate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class TestBase extends TestCase{
	
	public TestBase(){
		super();
	}
	
	/**
	 * Create date 
	 * 
	 * @param year  - date year
	 * @param month - date month
	 * @param day   - date day
	 * @return      - specified darte
	 */
	public Date createDate(int year, int month, int day){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		return cal.getTime();
	}
}
