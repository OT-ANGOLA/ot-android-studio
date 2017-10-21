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

public class Municipality implements Comparable<Municipality>{

	Database db = OpenTenureApplication.getInstance().getDatabase();
	DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());

	String code;
	String displayValue;
	String description;
	String status;
	String provinceCode;
	Boolean active;

	@Override
	public String toString() {
		return dnl.getLocalizedDisplayName(getDisplayValue());
	}

	@Override
	public int compareTo(@NonNull Municipality another) {
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

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
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
					.prepareStatement("INSERT INTO MUNICIPALITY(CODE, DESCRIPTION, DISPLAY_VALUE, PROVINCE_CODE, ACTIVE) VALUES (?,?,?,?,'true')");

			statement.setString(1, getCode());
			statement.setString(2, getDescription());
			statement.setString(3, getDisplayValue());
			statement.setString(4, getProvinceCode());

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

	public static List<Municipality> getActiveMunicipalities() {

		List<Municipality> municipalities = new ArrayList<Municipality>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, PROVINCE_CODE FROM MUNICIPALITY WHERE ACTIVE = 'true'");
			rs = statement.executeQuery();

			while (rs.next()) {
				Municipality municipality = new Municipality();
				municipality.setCode(rs.getString(1));
				municipality.setDescription(rs.getString(2));
				municipality.setDisplayValue(rs.getString(3));
				municipality.setProvinceCode(rs.getString(4));

				municipalities.add(municipality);

			}
			// To account for provinces without municipalities
			Municipality municipality = new Municipality();
			municipality.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setProvinceCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipalities.add(municipality);
			return municipalities;

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
		return municipalities;
	}

	public static List<Municipality> getMunicipalities() {

		List<Municipality> municipalities = new ArrayList<Municipality>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, PROVINCE_CODE FROM MUNICIPALITY");
			rs = statement.executeQuery();

			while (rs.next()) {
				Municipality municipality = new Municipality();
				municipality.setCode(rs.getString(1));
				municipality.setDescription(rs.getString(2));
				municipality.setDisplayValue(rs.getString(3));
				municipality.setProvinceCode(rs.getString(4));

				municipalities.add(municipality);

			}
			// To account for provinces without municipalities
			Municipality municipality = new Municipality();
			municipality.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setProvinceCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipalities.add(municipality);
			return municipalities;

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
		return municipalities;

	}

	public static Municipality getMunicipality(String code) {
		ResultSet result = null;
		Connection localConnection = null;
		PreparedStatement statement = null;
		Municipality municipality = new Municipality();
		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, PROVINCE_CODE FROM MUNICIPALITY WHERE CODE=?");
			statement.setString(1, code);

			result = statement.executeQuery();

			if (result.next()) {

				municipality.setCode(result.getString(1));
				municipality.setDescription(result.getString(2));
				municipality.setDisplayValue(result.getString(3));
				municipality.setProvinceCode(result.getString(4));

				return municipality;
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
	
	public int updateMunicipality() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE MUNICIPALITY SET DESCRIPTION=?, DISPLAY_VALUE=?, PROVINCE_CODE=?, ACTIVE='true' WHERE CODE = ?");
			statement.setString(1, getDescription());
			statement.setString(2, getDisplayValue());
			statement.setString(3, getProvinceCode());
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
	
	
	public static int setAllMunicipalitiesInactive() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE MUNICIPALITY SET ACTIVE='false' WHERE  ACTIVE= 'true'");

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
}
