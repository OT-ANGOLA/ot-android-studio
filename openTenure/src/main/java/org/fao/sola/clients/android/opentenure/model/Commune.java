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

public class Commune implements Comparable<Commune>{

	Database db = OpenTenureApplication.getInstance().getDatabase();
	DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());

	String code;
	String displayValue;
	String description;
	String status;
	String municipalityCode;
	String provinceCode;
	String countryCode;
	Boolean active;

	@Override
	public String toString() {
		return dnl.getLocalizedDisplayName(getDisplayValue());
	}

	@Override
	public int compareTo(@NonNull Commune another) {
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

	public String getMunicipalityCode() {
		return municipalityCode;
	}

	public void setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
					.prepareStatement("INSERT INTO COMMUNE(CODE, DESCRIPTION, DISPLAY_VALUE, MUNICIPALITY_CODE, ACTIVE) VALUES (?,?,?,?,'true')");

			statement.setString(1, getCode());
			statement.setString(2, getDescription());
			statement.setString(3, getDisplayValue());
			statement.setString(4, getMunicipalityCode());

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

	public static List<Commune> getActiveCommunes() {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT " +
							"COM.CODE, " +
							"COM.DESCRIPTION, " +
							"COM.DISPLAY_VALUE, " +
							"COM.MUNICIPALITY_CODE, " +
							"COU.CODE, " +
							"PRO.CODE " +
							"FROM " +
							"COUNTRY COU, " +
							"PROVINCE PRO, " +
							"MUNICIPALITY MUN, " +
							"COMMUNE COM " +
							"WHERE " +
							"COM.MUNICIPALITY_CODE=MUN.CODE " +
							"AND MUN.PROVINCE_CODE=PRO.CODE " +
							"AND PRO.COUNTRY_CODE=COU.CODE " +
							"AND COM.ACTIVE = 'true'");
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(rs.getString(4));
				commune.setCountryCode(rs.getString(5));
				commune.setProvinceCode(rs.getString(6));
				communes.add(commune);
			}
			// To allow for municipalities without communes
			Commune commune = new Commune();
			commune.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setMunicipalityCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setProvinceCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			communes.add(commune);
			return communes;

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
		return communes;
	}

	public static List<Commune> getCommunes() {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase().getConnection();
			statement = localConnection
					.prepareStatement("SELECT " +
							"COM.CODE, " +
							"COM.DESCRIPTION, " +
							"COM.DISPLAY_VALUE, " +
							"COM.MUNICIPALITY_CODE, " +
							"COU.CODE, " +
							"PRO.CODE " +
							"FROM " +
							"COUNTRY COU, " +
							"PROVINCE PRO, " +
							"MUNICIPALITY MUN, " +
							"COMMUNE COM " +
							"WHERE " +
							"COM.MUNICIPALITY_CODE=MUN.CODE " +
							"AND MUN.PROVINCE_CODE=PRO.CODE " +
							"AND PRO.COUNTRY_CODE=COU.CODE ");
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(rs.getString(4));
				commune.setCountryCode(rs.getString(5));
				commune.setProvinceCode(rs.getString(6));
				communes.add(commune);
			}
			// To allow for municipalities without communes
			Commune commune = new Commune();
			commune.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setMunicipalityCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setProvinceCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			communes.add(commune);
			return communes;

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
		return communes;
	}

	public static Commune getCommune(String code) {
		ResultSet result = null;
		Connection localConnection = null;
		PreparedStatement statement = null;
		Commune commune = new Commune();
		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT " +
							"COM.CODE, " +
							"COM.DESCRIPTION, " +
							"COM.DISPLAY_VALUE, " +
							"COM.MUNICIPALITY_CODE, " +
							"COU.CODE, " +
							"PRO.CODE " +
							"FROM " +
							"COUNTRY COU, " +
							"PROVINCE PRO, " +
							"MUNICIPALITY MUN, " +
							"COMMUNE COM " +
							"WHERE " +
							"COM.MUNICIPALITY_CODE=MUN.CODE " +
							"AND MUN.PROVINCE_CODE=PRO.CODE " +
							"AND PRO.COUNTRY_CODE=COU.CODE " +
							"AND COM.CODE=?");
			statement.setString(1, code);

			result = statement.executeQuery();

			if (result.next()) {

				commune.setCode(result.getString(1));
				commune.setDescription(result.getString(2));
				commune.setDisplayValue(result.getString(3));
				commune.setMunicipalityCode(result.getString(4));
				commune.setCountryCode(result.getString(5));
				commune.setProvinceCode(result.getString(6));

				return commune;
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
	
	public int updateCommune() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE COMMUNE SET DESCRIPTION=?, DISPLAY_VALUE=?, MUNICIPALITY_CODE=?, ACTIVE='true' WHERE CODE = ?");
			statement.setString(1, getDescription());
			statement.setString(2, getDisplayValue());
			statement.setString(3, getMunicipalityCode());
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
	
	
	public static int setAllCommunesInactive() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {
			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("UPDATE COMMUNE SET ACTIVE='false' WHERE  ACTIVE= 'true'");

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
	public static int communeIndex(List<Commune> communesList, String communeCode){
		if(communeCode==null){
			return communeIndex(communesList, OpenTenureApplication.getActivity().getResources().getString(R.string.na));
		}
		int i = 0;
		for(Commune commune:communesList){
			if(commune.getCode().trim().equalsIgnoreCase(communeCode.trim())){
				return i;
			}else{
				i++;
			}
		}
		return 0;
	}
	public static List<Commune> filterCommunesByMunicipality(List<Commune> communes, String municipalityCode){
		List<Commune> filteredCommunes = new ArrayList<Commune>();
		for(Commune commune:communes){
			if(commune.getMunicipalityCode().equalsIgnoreCase(municipalityCode)){
				filteredCommunes.add(commune);
			}
		}
		if(filteredCommunes.size() <= 0){
			// To account for municipalities without communes
			Commune commune = new Commune();
			commune.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setCountryCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			commune.setMunicipalityCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			filteredCommunes.add(commune);
			return filteredCommunes;

		}
		return filteredCommunes;
	}
}
