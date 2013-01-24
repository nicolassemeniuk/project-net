package net.project.hibernate.service;

import java.math.BigDecimal;

public interface IProfileService {

	public BigDecimal createPersonStub(String email, String firstName, String lastName, String displayName, String userStatus);

	public void createPersonProfile(BigDecimal personId,
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
