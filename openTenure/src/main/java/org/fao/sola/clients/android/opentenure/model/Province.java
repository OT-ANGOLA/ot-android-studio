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

import android.support.annotation.NonNull;

import org.fao.sola.clients.android.opentenure.DisplayNameLocalizer;
import org.fao.sola.clients.android.opentenure.OpenTenureApplication;
import org.fao.sola.clients.android.opentenure.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Province implements Comparable<Province> {

	Database db = OpenTenureApplication.getInstance().getDatabase();
	DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());

	String code;
	String displayValue;
	String description;
	String status;
	String countryCode;
	Boolean active;

	@Override
	public String toString() {
		return dnl.getLocalizedDisplayName(getDisplayValue());
	}

	@Override
	public int compareTo(@NonNull Province another) {
		String thisKey = dnl.getLocalizedDisplayName(getDisplayValue())+getCode();
		String anotherKey = dnl.getLocalizedDisplayName(another.getDisplayValue())+another.getCode();
		return thisKey.compareTo(anotherKey);
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int add() {

		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("INSERT INTO PROVINCE(CODE, DESCRIPTION, DISPLAY_VALUE, COUNTRY_CODE, ACTIVE) VALUES (?,?,?,?,'true')");

			statement.setString(1, getCode());
			statement.setString(2, getDescription());
			statement.setString(3, getDisplayValue());
			statement.setString(4, getCountryCode());

			result = statement.executeUpdate();
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

	public static List<Province> getActiveProvinces() {

		List<Province> provinces = new ArrayList<Province>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, COUNTRY_CODE FROM PROVINCE WHERE ACTIVE = 'true'");
			rs = statement.executeQuery();

			while (rs.next()) {
				Province province = new Province();
				province.setCode(rs.getString(1));
				province.setDescription(rs.getString(2));
				province.setDisplayValue(rs.getString(3));
				province.setCountryCode(rs.getString(4));

				provinces.add(province);

			}
			// To allow for countries without provinces
			Province province = new Province();
			province.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));

			provinces.add(province);
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
		return provinces;
	}

	public static List<Province> getProvinces() {

		List<Province> provinces = new ArrayList<Province>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, COUNTRY_CODE FROM PROVINCE");
			rs = statement.executeQuery();

			while (rs.next()) {
				Province province = new Province();
				province.setCode(rs.getString(1));
				province.setDescription(rs.getString(2));
				province.setDisplayValue(rs.getString(3));
				province.setCountryCode(rs.getString(4));

				provinces.add(province);

			}
			// To allow for countries without provinces
			Province province = new Province();
			province.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));

			provinces.add(province);

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
		return provinces;

	}

	public static Province getProvince(String code) {
		ResultSet result = null;
		Connection localConnection = null;
		PreparedStatement statement = null;
		Province province = new Province();
		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, COUNTRY_CODE FROM PROVINCE WHERE CODE=?");
			statement.setString(1, code);

			result = statement.executeQuery();

			if (result.next()) {

				province.setCode(result.getString(1));
				province.setDescription(result.getString(2));
				province.setDisplayValue(result.getString(3));
				province.setCountryCode(result.getString(4));

				return province;
			}
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
		return null;
	}
	
	public int updateProvince() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE PROVINCE SET DESCRIPTION=?, DISPLAY_VALUE=?, COUNTRY_CODE=?, ACTIVE='true' WHERE CODE = ?");
			statement.setString(1, getDescription());
			statement.setString(2, getDisplayValue());
			statement.setString(3, getCountryCode());
			statement.setString(4, getCode());

			result = statement.executeUpdate();
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
	
	
	public static int setAllProvincesInactive() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE PROVINCE SET ACTIVE='false' WHERE  ACTIVE= 'true'");

			result = statement.executeUpdate();
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
	public static int provinceIndex(String provinceCode, List<Province> provincesList){
		if(provinceCode == null){
			return provinceIndex(OpenTenureApplication.getActivity().getResources().getString(R.string.na), provincesList);
		}
		int i = 0;
		for(Province province:provincesList){
			if(province.getCode().trim().equalsIgnoreCase(provinceCode.trim())){
				return i;
			}else{
				i++;
			}
		}
		return 0;
	}
	public static List<Province> filterProvincesByCountry(List<Province> provinces, String countryCode){
		List<Province> filteredProvinces = new ArrayList<Province>();
		for(Province province:provinces){
			if(province.getCountryCode().equalsIgnoreCase(countryCode)){
				filteredProvinces.add(province);
			}
		}
		if(filteredProvinces.size() <= 0){
			// To account for countries without provinces
			Province province = new Province();
			province.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			province.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			filteredProvinces.add(province);
			return filteredProvinces;

		}
		return filteredProvinces;
	}
}
