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

import org.fao.sola.clients.android.opentenure.DisplayNameLocalizer;
import org.fao.sola.clients.android.opentenure.OpenTenureApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Commune {

	Database db = OpenTenureApplication.getInstance().getDatabase();

	String code;
	String displayValue;
	String description;
	String status;
	String municipalityCode;
	Boolean active;

	@Override
	public String toString() {
		return "Municipality ["
				+ "code=" + code
				+ ", municipalityCode=" + municipalityCode
				+ ", description=" + description
				+ ", displayValue=" + displayValue
				+ ", status=" + status
				+ ", active=" + active
				+ "]";
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

	public int addMunicipality(Commune commune) {

		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("INSERT INTO COMMUNE(CODE, DESCRIPTION, DISPLAY_VALUE,MUNICIPALITY_CODE, ACTIVE) VALUES (?,?,?,?,'true')");

			statement.setString(1, commune.getCode());
			statement.setString(2, commune.getDescription());
			statement.setString(3, commune.getDisplayValue());
			statement.setString(4, commune.getMunicipalityCode());

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

	public List<Commune> getActiveCommunes() {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, MUNICIPALITY_CODE FROM COMMUNE WHERE ACTIVE = 'true'");
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(rs.getString(4));

				communes.add(commune);

			}
			return communes;

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
		return communes;
	}

	public List<Commune> getCommunes() {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, MUNICIPALITY_CODE FROM COMMUNE");
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(rs.getString(4));

				communes.add(commune);

			}
			return communes;

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
		return communes;

	}

	public List<Commune> getActiveCommunesByMunicipality(String municipalityCode) {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE FROM COMMUNE WHERE MUNICIPALITY_CODE=? AND ACTIVE = 'true'");
			statement.setString(1, municipalityCode);
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(municipalityCode);

				communes.add(commune);

			}
			return communes;

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
		return communes;
	}

	public List<Commune> getCommunesByMunicipality(String municipalityCode) {

		List<Commune> communes = new ArrayList<Commune>();
		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE FROM COMMUNE WHERE MUNICIPALITY_CODE=?");
			statement.setString(1, municipalityCode);
			rs = statement.executeQuery();

			while (rs.next()) {
				Commune commune = new Commune();
				commune.setCode(rs.getString(1));
				commune.setDescription(rs.getString(2));
				commune.setDisplayValue(rs.getString(3));
				commune.setMunicipalityCode(municipalityCode);

				communes.add(commune);

			}
			return communes;

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
		return communes;
	}

	public List<String> getDisplayValues(String localization,boolean onlyActive) {

		List<Commune> communes;

		if(!onlyActive)
			communes = getCommunes();
		else
			communes = getActiveCommunes();


		DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());
		List<String> displayList = new ArrayList<String>();

		for (Iterator<Commune> iterator = communes.iterator(); iterator.hasNext();) {
			Commune commune = (Commune) iterator
					.next();

			displayList.add(dnl.getLocalizedDisplayName(commune.getDisplayValue()));
		}
		return displayList;
	}

	public Map<String,String> getKeyValueMap(String localization,boolean onlyActive) {

		List<Commune> list;

		if(!onlyActive)
			list = getCommunes();
		else
			list = getActiveCommunes();

		DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());
		Map<String,String> keyValueMap = new HashMap<String,String>();

		for (Iterator<Commune> iterator = list.iterator(); iterator.hasNext();) {

			Commune commune = (Commune) iterator
					.next();

			keyValueMap.put(commune.getCode().toLowerCase(),dnl.getLocalizedDisplayName(commune.getDisplayValue()));
		}
		return keyValueMap;
	}

	public Map<String,String> getValueKeyMap(String localization,boolean onlyActive) {

		List<Commune> communes;

		if(!onlyActive)
			communes = getCommunes();
		else
			communes = getActiveCommunes();

		DisplayNameLocalizer dnl = new DisplayNameLocalizer(OpenTenureApplication.getInstance().getLocalization());
		Map<String,String> keyValueMap = new HashMap<String,String>();

		for (Iterator<Commune> iterator = communes.iterator(); iterator.hasNext();) {

			Commune commune = (Commune) iterator
					.next();

			keyValueMap.put(dnl.getLocalizedDisplayName(commune.getDisplayValue()),commune.getCode());
		}
		return keyValueMap;
	}

	public int getIndexByCodeType(String code,boolean onlyActive) {

		List<Commune> list;

		if(!onlyActive)
			list = getCommunes();
		else
			list = getActiveCommunes();


		int i = 0;

		for (Iterator<Commune> iterator = list.iterator(); iterator.hasNext();) {
			Commune commune = (Commune) iterator
					.next();

			if (commune.getCode().equals(code)) {

				return i;

			}

			i++;
		}
		return 0;

	}

	public String getCommuneByDisplayValue(String value) {

		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT CODE FROM COMMUNE WHERE DISPLAY_VALUE LIKE  '%' || ? || '%' ");
			statement.setString(1, value);
			rs = statement.executeQuery();

			while (rs.next()) {
				return rs.getString(1);
			}
			return null;

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
		return null;

	}

	public String getDisplayValueByCode(String value) {

		ResultSet rs = null;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("SELECT DISPLAY_VALUE FROM COMMUNE WHERE CODE = ?");
			statement.setString(1, value);
			rs = statement.executeQuery();

			while (rs.next()) {
				return rs.getString(1);
			}
			return null;

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
		return null;

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
					.prepareStatement("SELECT CODE, DESCRIPTION, DISPLAY_VALUE, MUNICIPALITY_CODE FROM COMMUNE WHERE CODE=?");
			statement.setString(1, code);

			result = statement.executeQuery();

			if (result.next()) {

				commune.setCode(result.getString(1));
				commune.setDescription(result.getString(2));
				commune.setDisplayValue(result.getString(3));
				commune.setMunicipalityCode(result.getString(4));

				return commune;
			}
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
}
