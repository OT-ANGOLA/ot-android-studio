/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.fao.sola.clients.android.opentenure.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.fao.sola.clients.android.opentenure.OpenTenureApplication;
import org.fao.sola.clients.android.opentenure.R;
import org.fao.sola.clients.android.opentenure.filesystem.FileSystemUtilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class Person {

	Database db = OpenTenureApplication.getInstance().getDatabase();

	public static String _GROUP = "group";
	public static String _PHYSICAL = "physical";

	public java.sql.Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(java.sql.Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	@Override
	public boolean equals(Object v) {
		boolean retVal = false;

		if (v instanceof Person) {

			retVal = this.getPersonId().equals(((Person) v).getPersonId());

		}

		return retVal;
	}

	public boolean hasUploadedClaims() {
		Connection localConnection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean result = false;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CLAIM_ID FROM CLAIM WHERE PERSON_ID=? AND STATUS <> 'created' UNION (SELECT CLAIM_ID FROM CLAIM WHERE CLAIM_ID IN ( SELECT CLAIM_ID FROM SHARE WHERE ID IN ( SELECT SHARE_ID FROM OWNER WHERE PERSON_ID=?)) AND STATUS <> 'created')");
			statement.setString(1, personId);
			statement.setString(2, personId);
			rs = statement.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public static boolean hasUploadedClaims(String personId) {
		Connection localConnection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		boolean result = false;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CLAIM_ID, STATUS FROM CLAIM WHERE PERSON_ID=? AND STATUS <> 'created' UNION (SELECT CLAIM_ID FROM CLAIM WHERE CLAIM_ID IN ( SELECT CLAIM_ID FROM SHARE WHERE ID IN ( SELECT SHARE_ID FROM OWNER WHERE PERSON_ID=?)) AND STATUS <> 'created')");
			statement.setString(1, personId);
			statement.setString(2, personId);
			rs = statement.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public Person() {
		this.personId = UUID.randomUUID().toString();
	}

	@Override
	public String toString() {
		return "Person{" +
				"personId='" + personId + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", otherName='" + otherName + '\'' +
				", dateOfBirth=" + dateOfBirth +
				", idIssuanceDate=" + idIssuanceDate +
				", idExpiryDate=" + idExpiryDate +
				", idIssuanceCountryCode='" + idIssuanceCountryCode + '\'' +
				", idIssuanceProvinceCode='" + idIssuanceProvinceCode + '\'' +
				", idIssuanceMunicipalityCode='" + idIssuanceMunicipalityCode + '\'' +
				", idIssuanceCommuneCode='" + idIssuanceCommuneCode + '\'' +
				", birthCountryCode='" + birthCountryCode + '\'' +
				", birthCommuneCode='" + birthCommuneCode + '\'' +
				", residenceCommuneCode='" + residenceCommuneCode + '\'' +
				", placeOfBirth='" + placeOfBirth + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", postalAddress='" + postalAddress + '\'' +
				", mobilePhoneNumber='" + mobilePhoneNumber + '\'' +
				", contactPhoneNumber='" + contactPhoneNumber + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatusCode='" + maritalStatusCode + '\'' +
				", idType='" + idType + '\'' +
				", idNumber='" + idNumber + '\'' +
				", motherName='" + motherName + '\'' +
				", fatherName='" + fatherName + '\'' +
				", beneficiaryName='" + beneficiaryName + '\'' +
				", beneficiaryIdNumber='" + beneficiaryIdNumber + '\'' +
				", personType='" + personType + '\'' +
				'}';
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public String getContactPhoneNumber() {
		return contactPhoneNumber;
	}

	public void setContactPhoneNumber(String contactPhoneNumber) {
		this.contactPhoneNumber = contactPhoneNumber;
	}

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public static int createPerson(Person person) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("INSERT INTO PERSON(" +
							"PERSON_ID, " +
							"FIRST_NAME, " +
							"LAST_NAME, " +
							"DATE_OF_BIRTH, " +
							"PLACE_OF_BIRTH, " +
							"EMAIL_ADDRESS, " +
							"POSTAL_ADDRESS, " +
							"MOBILE_PHONE_NUMBER, " +
							"CONTACT_PHONE_NUMBER, " +
							"GENDER, " +
							"ID_TYPE, " +
							"ID_NUMBER, " +
							"PERSON_TYPE, " +
							"OTHER_NAME, " +
							"ID_ISSUANCE_DATE, " +
							"ID_EXPIRY_DATE, " +
							"ID_ISSUANCE_COUNTRY_CODE, " +
							"ID_ISSUANCE_PROVINCE_CODE, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE, " +
							"ID_ISSUANCE_COMMUNE_CODE, " +
							"BIRTH_COUNTRY_CODE, " +
							"BIRTH_COMMUNE_CODE, " +
							"RESIDENCE_COMMUNE_CODE, " +
							"MARITAL_STATUS_CODE, " +
							"FATHER_NAME, " +
							"MOTHER_NAME, " +
							"BENEFICIARY_NAME, " +
							"BENEFICIARY_ID_NUMBER " +
							") " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setString(1, person.getPersonId());
			statement.setString(2, person.getFirstName());
			if (person.getLastName() != null)
				statement.setString(3, person.getLastName());
			else
				statement.setString(3, "");
			statement.setDate(4, person.getDateOfBirth());
			statement.setString(5, person.getPlaceOfBirth());
			statement.setString(6, person.getEmailAddress());
			statement.setString(7, person.getPostalAddress());
			statement.setString(8, person.getMobilePhoneNumber());
			statement.setString(9, person.getContactPhoneNumber());
			statement.setString(10, person.getGender());
			statement.setString(11, person.getIdType());
			statement.setString(12, person.getIdNumber());
			statement.setString(13, person.getPersonType());
			statement.setString(14, person.getOtherName());
			statement.setDate(15, person.getIdIssuanceDate());
			statement.setDate(16, person.getIdExpiryDate());
			statement.setString(17, person.getIdIssuanceCountryCode());
			statement.setString(18, person.getIdIssuanceProvinceCode());
			statement.setString(19, person.getIdIssuanceMunicipalityCode());
			statement.setString(20, person.getIdIssuanceCommuneCode());
			statement.setString(21, person.getBirthCountryCode());
			statement.setString(22, person.getBirthCommuneCode());
			statement.setString(23, person.getResidenceCommuneCode());
			statement.setString(24, person.getMaritalStatusCode());
			statement.setString(25, person.getFatherName());
			statement.setString(26, person.getMotherName());
			statement.setString(27, person.getBeneficiaryName());
			statement.setString(28, person.getBeneficiaryIdNumber());
			result = statement.executeUpdate();

			FileSystemUtilities.createClaimantFolder(person.getPersonId());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public int create() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("INSERT INTO PERSON(" +
							"PERSON_ID, " +
							"FIRST_NAME, " +
							"LAST_NAME, " +
							"DATE_OF_BIRTH, " +
							"PLACE_OF_BIRTH, " +
							"EMAIL_ADDRESS, " +
							"POSTAL_ADDRESS, " +
							"MOBILE_PHONE_NUMBER, " +
							"CONTACT_PHONE_NUMBER, " +
							"GENDER, " +
							"ID_TYPE, " +
							"ID_NUMBER, " +
							"PERSON_TYPE, " +
							"OTHER_NAME, " +
							"ID_ISSUANCE_DATE, " +
							"ID_EXPIRY_DATE, " +
							"ID_ISSUANCE_COUNTRY_CODE, " +
							"ID_ISSUANCE_PROVINCE_CODE, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE, " +
							"ID_ISSUANCE_COMMUNE_CODE, " +
							"BIRTH_COUNTRY_CODE, " +
							"BIRTH_COMMUNE_CODE, " +
							"RESIDENCE_COMMUNE_CODE, " +
							"MARITAL_STATUS_CODE, " +
							"FATHER_NAME, " +
							"MOTHER_NAME, " +
							"BENEFICIARY_NAME, " +
							"BENEFICIARY_ID_NUMBER " +
							") " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setString(1, getPersonId());
			statement.setString(2, getFirstName());
			if (getLastName() != null)
				statement.setString(3, getLastName());
			else
				statement.setString(3, "");
			statement.setDate(4, getDateOfBirth());
			statement.setString(5, getPlaceOfBirth());
			statement.setString(6, getEmailAddress());
			statement.setString(7, getPostalAddress());
			statement.setString(8, getMobilePhoneNumber());
			statement.setString(9, getContactPhoneNumber());
			statement.setString(10, getGender());
			statement.setString(11, getIdType());
			statement.setString(12, getIdNumber());
			statement.setString(13, getPersonType());
			statement.setString(14, getOtherName());
			statement.setDate(15, getIdIssuanceDate());
			statement.setDate(16, getIdExpiryDate());
			statement.setString(17, getIdIssuanceCountryCode());
			statement.setString(18, getIdIssuanceProvinceCode());
			statement.setString(19, getIdIssuanceMunicipalityCode());
			statement.setString(20, getIdIssuanceCommuneCode());
			statement.setString(21, getBirthCountryCode());
			statement.setString(22, getBirthCommuneCode());
			statement.setString(23, getResidenceCommuneCode());
			statement.setString(24, getMaritalStatusCode());
			statement.setString(25, getFatherName());
			statement.setString(26, getMotherName());
			statement.setString(27, getBeneficiaryName());
			statement.setString(28, getBeneficiaryIdNumber());
			result = statement.executeUpdate();
			FileSystemUtilities.createClaimantFolder(getPersonId());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public static int deletePerson(Person person) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("DELETE FROM PERSON WHERE PERSON_ID=?");
			statement.setString(1, person.getPersonId());
			result = statement.executeUpdate();
			FileSystemUtilities.removeClaimantFolder(person.getPersonId());
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public int delete() {
		int result = 0;
		Connection localConnection = null;

		try {
			localConnection = db.getConnection();
			result = deletePerson(getPersonId(), localConnection);
			FileSystemUtilities.removeClaimantFolder(getPersonId());
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public static int deletePerson(String personId, Connection connection) {
		int result = 0;
		PreparedStatement statement = null;

		try {
			statement = connection
					.prepareStatement("DELETE FROM PERSON WHERE PERSON_ID=?");
			statement.setString(1, personId);
			result = statement.executeUpdate();
			FileSystemUtilities.removeClaimantFolder(personId);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public static int updatePerson(Person person) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE PERSON SET " +
							"FIRST_NAME=?, " +
							"LAST_NAME=?, " +
							"DATE_OF_BIRTH=?, " +
							"PLACE_OF_BIRTH=?, " +
							"EMAIL_ADDRESS=?, " +
							"POSTAL_ADDRESS=?, " +
							"MOBILE_PHONE_NUMBER=?, " +
							"CONTACT_PHONE_NUMBER=?, " +
							"GENDER=?, " +
							"ID_TYPE=?, " +
							"ID_NUMBER=?, " +
							"PERSON_TYPE=?, " +
							"OTHER_NAME=?, " +
							"ID_ISSUANCE_DATE=?, " +
							"ID_EXPIRY_DATE=?, " +
							"ID_ISSUANCE_COUNTRY_CODE=?, " +
							"ID_ISSUANCE_PROVINCE_CODE=?, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE=?, " +
							"ID_ISSUANCE_COMMUNE_CODE=?, " +
							"BIRTH_COUNTRY_CODE=?, " +
							"BIRTH_COMMUNE_CODE=?, " +
							"RESIDENCE_COMMUNE_CODE=?, " +
							"MARITAL_STATUS_CODE=?, " +
							"FATHER_NAME=?, " +
							"MOTHER_NAME=?, " +
							"BENEFICIARY_NAME=?, " +
							"BENEFICIARY_ID_NUMBER=? " +
							"WHERE PERSON_ID=?");
			statement.setString(1, person.getFirstName());
			statement.setString(2, person.getLastName());
			statement.setDate(3, person.getDateOfBirth());
			statement.setString(4, person.getPlaceOfBirth());
			statement.setString(5, person.getEmailAddress());
			statement.setString(6, person.getPostalAddress());
			statement.setString(7, person.getMobilePhoneNumber());
			statement.setString(8, person.getContactPhoneNumber());
			statement.setString(9, person.getGender());
			statement.setString(10, person.getIdType());
			statement.setString(11, person.getIdNumber());
			statement.setString(12, person.getPersonType());
			statement.setString(13, person.getOtherName());
			statement.setDate(14, person.getIdIssuanceDate());
			statement.setDate(15, person.getIdExpiryDate());
			statement.setString(16, person.getIdIssuanceCountryCode());
			statement.setString(17, person.getIdIssuanceProvinceCode());
			statement.setString(18, person.getIdIssuanceMunicipalityCode());
			statement.setString(19, person.getIdIssuanceCommuneCode());
			statement.setString(20, person.getBirthCountryCode());
			statement.setString(21, person.getBirthCommuneCode());
			statement.setString(22, person.getResidenceCommuneCode());
			statement.setString(23, person.getMaritalStatusCode());
			statement.setString(24, person.getFatherName());
			statement.setString(25, person.getMotherName());
			statement.setString(26, person.getBeneficiaryName());
			statement.setString(27, person.getBeneficiaryIdNumber());
			statement.setString(28, person.getPersonId());

			result = statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public int update() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE PERSON SET " +
							"FIRST_NAME=?, " +
							"LAST_NAME=?, " +
							"DATE_OF_BIRTH=?, " +
							"PLACE_OF_BIRTH=?, " +
							"EMAIL_ADDRESS=?, " +
							"POSTAL_ADDRESS=?, " +
							"MOBILE_PHONE_NUMBER=?, " +
							"CONTACT_PHONE_NUMBER=?, " +
							"GENDER=?, " +
							"ID_TYPE=?, " +
							"ID_NUMBER=?, " +
							"PERSON_TYPE=?, " +
							"OTHER_NAME=?, " +
							"ID_ISSUANCE_DATE=?, " +
							"ID_EXPIRY_DATE=?, " +
							"ID_ISSUANCE_COUNTRY_CODE=?, " +
							"ID_ISSUANCE_PROVINCE_CODE=?, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE=?, " +
							"ID_ISSUANCE_COMMUNE_CODE=?, " +
							"BIRTH_COUNTRY_CODE=?, " +
							"BIRTH_COMMUNE_CODE=?, " +
							"RESIDENCE_COMMUNE_CODE=?, " +
							"MARITAL_STATUS_CODE=?, " +
							"FATHER_NAME=?, " +
							"MOTHER_NAME=?, " +
							"BENEFICIARY_NAME=?, " +
							"BENEFICIARY_ID_NUMBER=? " +
							"WHERE PERSON_ID=?");
			statement.setString(1, getFirstName());
			statement.setString(2, getLastName());
			statement.setDate(3, getDateOfBirth());
			statement.setString(4, getPlaceOfBirth());
			statement.setString(5, getEmailAddress());
			statement.setString(6, getPostalAddress());
			statement.setString(7, getMobilePhoneNumber());
			statement.setString(8, getContactPhoneNumber());
			statement.setString(9, getGender());
			statement.setString(10, getIdType());
			statement.setString(11, getIdNumber());
			statement.setString(12, getPersonType());
			statement.setString(13, getOtherName());
			statement.setDate(14, getIdIssuanceDate());
			statement.setDate(15, getIdExpiryDate());
			statement.setString(16, getIdIssuanceCountryCode());
			statement.setString(17, getIdIssuanceProvinceCode());
			statement.setString(18, getIdIssuanceMunicipalityCode());
			statement.setString(19, getIdIssuanceCommuneCode());
			statement.setString(20, getBirthCountryCode());
			statement.setString(21, getBirthCommuneCode());
			statement.setString(22, getResidenceCommuneCode());
			statement.setString(23, getMaritalStatusCode());
			statement.setString(24, getFatherName());
			statement.setString(25, getMotherName());
			statement.setString(26, getBeneficiaryName());
			statement.setString(27, getBeneficiaryIdNumber());
			statement.setString(28, getPersonId());
			result = statement.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();
		} catch (Exception exception) {

			exception.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return result;
	}

	public static Person getPerson(String personId) {
		Person person = null;
		Connection localConnection = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			person = getPerson(personId, localConnection);
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return person;
	}

	public static Person getPerson(String personId, Connection connection) {
		Person person = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			statement = connection
					.prepareStatement("SELECT " +
							"FIRST_NAME, " +
							"LAST_NAME, " +
							"DATE_OF_BIRTH, " +
							"PLACE_OF_BIRTH, " +
							"EMAIL_ADDRESS, " +
							"POSTAL_ADDRESS, " +
							"MOBILE_PHONE_NUMBER, " +
							"CONTACT_PHONE_NUMBER, " +
							"GENDER, " +
							"ID_TYPE, " +
							"ID_NUMBER, " +
							"PERSON_TYPE, " +
							"OTHER_NAME, " +
							"ID_ISSUANCE_DATE, " +
							"ID_EXPIRY_DATE, " +
							"ID_ISSUANCE_COUNTRY_CODE, " +
							"ID_ISSUANCE_PROVINCE_CODE, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE, " +
							"ID_ISSUANCE_COMMUNE_CODE, " +
							"BIRTH_COUNTRY_CODE, " +
							"BIRTH_COMMUNE_CODE, " +
							"RESIDENCE_COMMUNE_CODE, " +
							"MARITAL_STATUS_CODE, " +
							"FATHER_NAME, " +
							"MOTHER_NAME, " +
							"BENEFICIARY_NAME, " +
							"BENEFICIARY_ID_NUMBER " +
							"FROM PERSON PER " +
							"WHERE PER.PERSON_ID=?");
			statement.setString(1, personId);
			rs = statement.executeQuery();
			while (rs.next()) {
				person = new Person();
				person.setPersonId(personId);
				person.setFirstName(rs.getString(1));
				person.setLastName(rs.getString(2));
				person.setDateOfBirth(rs.getDate(3));
				person.setPlaceOfBirth(rs.getString(4));
				person.setEmailAddress(rs.getString(5));
				person.setPostalAddress(rs.getString(6));
				person.setMobilePhoneNumber(rs.getString(7));
				person.setContactPhoneNumber(rs.getString(8));
				person.setGender(rs.getString(9));
				person.setIdType(rs.getString(10));
				person.setIdNumber(rs.getString(11));
				person.setPersonType(rs.getString(12));
				person.setPersonType(rs.getString(13));
				person.setOtherName(rs.getString(14));
				person.setIdIssuanceDate(rs.getDate(15));
				person.setIdExpiryDate(rs.getDate(16));
				person.setIdIssuanceCountryCode(rs.getString(17));
				person.setIdIssuanceProvinceCode(rs.getString(18));
				person.setIdIssuanceMunicipalityCode(rs.getString(19));
				person.setIdIssuanceCommuneCode(rs.getString(20));
				person.setBirthCountryCode(rs.getString(21));
				person.setBirthCommuneCode(rs.getString(22));
				person.setMaritalStatusCode(rs.getString(23));
				person.setFatherName(rs.getString(24));
				person.setMotherName(rs.getString(25));
				person.setBeneficiaryName(rs.getString(26));
				person.setBeneficiaryIdNumber(rs.getString(27));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
		}
		return person;
	}

	public static List<Person> getAllPersons() {
		List<Person> persons = new ArrayList<Person>();
		Connection localConnection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT " +
							"PERSON_ID, " +
							"FIRST_NAME, " +
							"LAST_NAME, " +
							"DATE_OF_BIRTH, " +
							"PLACE_OF_BIRTH, " +
							"EMAIL_ADDRESS, " +
							"POSTAL_ADDRESS, " +
							"MOBILE_PHONE_NUMBER, " +
							"CONTACT_PHONE_NUMBER, " +
							"GENDER, " +
							"ID_TYPE, " +
							"ID_NUMBER, " +
							"PERSON_TYPE, " +
							"OTHER_NAME, " +
							"ID_ISSUANCE_DATE, " +
							"ID_EXPIRY_DATE, " +
							"ID_ISSUANCE_COUNTRY_CODE, " +
							"ID_ISSUANCE_PROVINCE_CODE, " +
							"ID_ISSUANCE_MUNICIPALITY_CODE, " +
							"ID_ISSUANCE_COMMUNE_CODE, " +
							"BIRTH_COUNTRY_CODE, " +
							"BIRTH_COMMUNE_CODE, " +
							"RESIDENCE_COMMUNE_CODE, " +
							"MARITAL_STATUS_CODE, " +
							"FATHER_NAME, " +
							"MOTHER_NAME, " +
							"BENEFICIARY_NAME, " +
							"BENEFICIARY_ID_NUMBER " +
							"FROM PERSON ");
			rs = statement.executeQuery();
			while (rs.next()) {

				Person person = new Person();
				person.setPersonId(rs.getString(1));
				person.setFirstName(rs.getString(2));
				person.setLastName(rs.getString(3));
				person.setDateOfBirth(rs.getDate(4));
				person.setPlaceOfBirth(rs.getString(5));
				person.setEmailAddress(rs.getString(6));
				person.setPostalAddress(rs.getString(7));
				person.setMobilePhoneNumber(rs.getString(8));
				person.setContactPhoneNumber(rs.getString(9));
				person.setGender(rs.getString(10));
				person.setIdType(rs.getString(11));
				person.setIdNumber(rs.getString(12));
				person.setPersonType(rs.getString(13));
				person.setPersonType(rs.getString(14));
				person.setOtherName(rs.getString(15));
				person.setIdIssuanceDate(rs.getDate(16));
				person.setIdExpiryDate(rs.getDate(17));
				person.setIdIssuanceCountryCode(rs.getString(18));
				person.setIdIssuanceProvinceCode(rs.getString(19));
				person.setIdIssuanceMunicipalityCode(rs.getString(20));
				person.setIdIssuanceCommuneCode(rs.getString(21));
				person.setBirthCountryCode(rs.getString(22));
				person.setBirthCommuneCode(rs.getString(23));
				person.setMaritalStatusCode(rs.getString(24));
				person.setFatherName(rs.getString(25));
				person.setMotherName(rs.getString(26));
				person.setBeneficiaryName(rs.getString(27));
				person.setBeneficiaryIdNumber(rs.getString(28));
				persons.add(person);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return persons;
	}

	public static ArrayList<String> getIdsWithSharesOrClaims() {
		ArrayList<String> ids = new ArrayList<String>();
		Connection localConnection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT DISTINCT PERSON_ID FROM ((SELECT PERSON_ID FROM OWNER) UNION (SELECT PERSON_ID FROM CLAIM))");
			rs = statement.executeQuery();
			while (rs.next()) {
				ids.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
				}
			}
			if (localConnection != null) {
				try {
					localConnection.close();
				} catch (SQLException e) {
				}
			}
		}
		return ids;
	}

	public static File getPersonPictureFile(String personId) {
		return new File(FileSystemUtilities.getClaimantFolder(personId)
				+ File.separator + personId + ".jpg");
	}

	public static File getPersonPictureBmp(String personId, int size) {
		return new File(FileSystemUtilities.getClaimantFolder(personId)
				+ File.separator + personId + "_" + size + ".png");
	}
	
	public boolean addPersonPictureAsAttachment(String claimId) {
		
		File image = getPersonPictureFile(personId);
		if(image != null && image.exists()){

			Attachment att = new Attachment();
			att.setAttachmentId(personId);
			att.setClaimId(claimId);
			att.setDescription("person photo");
			att.setFileName(image.getName());
			att.setFileType("personPhoto");
			att.setMD5Sum(MD5.calculateMD5(image));
			att.setMimeType("image/jpeg");
			att.setSize(image.length());
			att.setPath(image.getPath());
			att.setStatus("created");

			// this is to avoid getting duplicate key error since the attachment id matches the person id
			att.delete();

			att.create();
		}
		return true;
	}

	public static Bitmap getPersonPicture(Context context, String personId,
			int size) {
		File file = getPersonPictureBmp(personId, size);
		if (file != null && file.exists()) {

			return BitmapFactory.decodeFile(file.getAbsolutePath());

		} else {

			return getPersonPicture(context, getPersonPictureFile(personId),
					size);

		}
	}
	
	public static Bitmap getPersonPictureForPdf(Context context, String personId, int size) {
		File file = getPersonPictureFile(personId);
		if (file != null && file.exists()) {

			return getPersonPicture(context, getPersonPictureFile(personId),
					size);

		}
		else return null;
	}

	public static Bitmap getPersonPicture(Context context,
			File personPictureFile, int size) {

		boolean save = false;
		Bitmap squareBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		Bitmap bitmap = BitmapFactory.decodeFile(personPictureFile.getPath(),
				options);
		if (bitmap == null) {
			squareBitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ic_contact_picture);
		} else {
			ExifInterface oldExif;
			String exifOrientation = null;
			try {
				oldExif = new ExifInterface(personPictureFile.getAbsolutePath());
				exifOrientation = oldExif
						.getAttribute(ExifInterface.TAG_ORIENTATION);

			} catch (IOException e) {
				e.printStackTrace();
			}
			save = true;
			int orientation = 0;
			try {
				try {
					orientation = Integer.parseInt(exifOrientation);

				} catch (Exception e) {
					System.out
							.println("Exception parsing position. orientation is "
									+ exifOrientation);
					orientation = 0;
				}

				final Matrix bitmapMatrix = new Matrix();
				switch (orientation) {
				case 1:
					break; // top left
				case 2:
					bitmapMatrix.postScale(-1, 1);
					break; // top right
				case 3:
					bitmapMatrix.postRotate(180);
					break; // bottom right
				case 4:
					bitmapMatrix.postRotate(180);
					bitmapMatrix.postScale(-1, 1);
					break; // bottom left
				case 5:
					bitmapMatrix.postRotate(90);
					bitmapMatrix.postScale(1, -1);
					break; // left top
				case 6:
					bitmapMatrix.postRotate(90);
					break; // right top
				case 7:
					bitmapMatrix.postRotate(270);
					bitmapMatrix.postScale(1, -1);
					break; // right bottom
				case 8:
					bitmapMatrix.postRotate(270);
					break; // left bottom
				default:
					break; // Unknown
				}

				int height = bitmap.getHeight();
				int width = bitmap.getWidth();

				int startOffset = 0;
				// Create new bitmap.
				if (height >= width) {
					startOffset = (height - width) / 2;
					if (orientation != 0 && orientation != 1)
						squareBitmap = Bitmap.createBitmap(bitmap, 0,
								startOffset, width, width, bitmapMatrix, false);
					else {
						squareBitmap = Bitmap.createBitmap(bitmap, 0,
								startOffset, width, width);

					}
				}
				if (width > height) {

					startOffset = (width - height) / 2;
					if (orientation != 0 && orientation != 1)
						squareBitmap = Bitmap
								.createBitmap(bitmap, startOffset, 0, height,
										height, bitmapMatrix, false);
					else {
						squareBitmap = Bitmap.createBitmap(bitmap,
								startOffset, 0, height, height);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		Bitmap icon = Bitmap.createScaledBitmap(squareBitmap, size, size,
				true);

		if (save) {

			try {
				FileOutputStream fos = new FileOutputStream(personPictureFile
						.getParentFile().getAbsolutePath()
						+ File.separator
						+ personPictureFile.getName().substring(0,
								personPictureFile.getName().length() - 4)
						+ "_"
						+ size + ".png");

				icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return icon;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public static void deleteAllBmp(String personId) {

		File folder = FileSystemUtilities.getClaimantFolder(personId);

		File[] list = folder.listFiles();

		for (int i = 0; i < list.length; i++) {
			File fileTD = list[i];

			if (fileTD.exists())
				if (fileTD.getName().endsWith("png"))
					fileTD.delete();

		}

	}

	public Person copy() {

		Person copy = new Person();

		copy.setContactPhoneNumber(this.contactPhoneNumber);
		copy.setDateOfBirth(this.dateOfBirth);
		copy.setEmailAddress(this.emailAddress);
		copy.setGender(this.gender);
		copy.setIdNumber(this.idNumber);
		copy.setIdType(this.idType);
		copy.setLastName(this.lastName);
		copy.setMobilePhoneNumber(this.mobilePhoneNumber);
		copy.setPersonType(this.personType);
		copy.setPlaceOfBirth(this.placeOfBirth);
		copy.setPostalAddress(this.postalAddress);
		copy.setFirstName(this.firstName);
		copy.setIdExpiryDate(this.idExpiryDate);
		copy.setIdIssuanceDate(this.idIssuanceDate);
		copy.setOtherName(this.otherName);
		copy.setIdIssuanceCountryCode(this.idIssuanceCountryCode);
		copy.setIdIssuanceProvinceCode(this.idIssuanceProvinceCode);
		copy.setIdIssuanceMunicipalityCode(this.idIssuanceMunicipalityCode);
		copy.setIdIssuanceCommuneCode(this.idIssuanceCommuneCode);
		copy.setBirthCountryCode(this.birthCountryCode);
		copy.setBirthCommuneCode(this.birthCommuneCode);
		copy.setResidenceCommuneCode(this.residenceCommuneCode);
		copy.setMaritalStatusCode(this.maritalStatusCode);
		copy.setMotherName(this.motherName);
		copy.setFatherName(this.fatherName);
		copy.setBeneficiaryName(this.beneficiaryName);
		copy.setBeneficiaryIdNumber(this.beneficiaryIdNumber);

		return copy;
	}

	String personId;
	String firstName;
	String lastName;
	String otherName;
	java.sql.Date dateOfBirth;
	java.sql.Date idIssuanceDate;
	java.sql.Date idExpiryDate;
	String idIssuanceCountryCode;
	String idIssuanceProvinceCode;
	String idIssuanceMunicipalityCode;
	String idIssuanceCommuneCode;
	String birthCountryCode;
	String birthCommuneCode;
	String residenceCommuneCode;
	String placeOfBirth;
	String emailAddress;
	String postalAddress;
	String mobilePhoneNumber;
	String contactPhoneNumber;
	String gender;
	String maritalStatusCode;
	String idType;
	String idNumber;
	String motherName;
	String fatherName;
	String beneficiaryName;
	String beneficiaryIdNumber;
	String personType;

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public Date getIdIssuanceDate() {
		return idIssuanceDate;
	}

	public void setIdIssuanceDate(Date idIssuanceDate) {
		this.idIssuanceDate = idIssuanceDate;
	}

	public Date getIdExpiryDate() {
		return idExpiryDate;
	}

	public void setIdExpiryDate(Date idExpiryDate) {
		this.idExpiryDate = idExpiryDate;
	}

	public String getIdIssuanceCountryCode() {
		return idIssuanceCountryCode;
	}

	public void setIdIssuanceCountryCode(String idIssuanceCountryCode) {
		this.idIssuanceCountryCode = idIssuanceCountryCode;
	}

	public String getIdIssuanceProvinceCode() {
		return idIssuanceProvinceCode;
	}

	public void setIdIssuanceProvinceCode(String idIssuanceProvinceCode) {
		this.idIssuanceProvinceCode = idIssuanceProvinceCode;
	}

	public String getIdIssuanceMunicipalityCode() {
		return idIssuanceMunicipalityCode;
	}

	public void setIdIssuanceMunicipalityCode(String idIssuanceMunicipalityCode) {
		this.idIssuanceMunicipalityCode = idIssuanceMunicipalityCode;
	}

	public String getIdIssuanceCommuneCode() {
		return idIssuanceCommuneCode;
	}

	public void setIdIssuanceCommuneCode(String idIssuanceCommuneCode) {
		this.idIssuanceCommuneCode = idIssuanceCommuneCode;
	}

	public String getBirthCountryCode() {
		return birthCountryCode;
	}

	public void setBirthCountryCode(String birthCountryCode) {
		this.birthCountryCode = birthCountryCode;
	}

	public String getBirthCommuneCode() {
		return birthCommuneCode;
	}

	public void setBirthCommuneCode(String birthCommuneCode) {
		this.birthCommuneCode = birthCommuneCode;
	}

	public String getResidenceCommuneCode() {
		return residenceCommuneCode;
	}

	public void setResidenceCommuneCode(String residenceCommuneCode) {
		this.residenceCommuneCode = residenceCommuneCode;
	}

	public String getMaritalStatusCode() {
		return maritalStatusCode;
	}

	public void setMaritalStatusCode(String maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryIdNumber() {
		return beneficiaryIdNumber;
	}

	public void setBeneficiaryIdNumber(String beneficiaryIdNumber) {
		this.beneficiaryIdNumber = beneficiaryIdNumber;
	}
}
