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
package net.project.hibernate.service;



public interface IProfileService {

	public Integer createPersonStub(String email, String firstName, String lastName, String displayName, String userStatus);

	public void createPersonProfile(Integer personId,
			String firstName,
			String lastName,
			String displayName,
			String username,
			String email,
			String alternateEmail1,
			String alternateEmail2,
			String alternateEmail3,
			String prefixName,
			String middleName,
			String secondLastName,
			String suffixName,
			String localeCode,
			String languageCode,
			String timezoneCode,
			String verificationCode,
			String address1,
			String address2,
			String address3,
			String address4,
			String address5,
			String address6,
			String address7,
			String city,
			String cityDistrict,
			String region,
			String stateProvence,
			String countryCode,
			String zipcode,
			String officePhone,
			String faxPhone);

}
