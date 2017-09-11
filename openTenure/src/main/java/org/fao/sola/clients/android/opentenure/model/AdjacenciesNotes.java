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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fao.sola.clients.android.opentenure.OpenTenureApplication;

public class AdjacenciesNotes {

	static Database db = OpenTenureApplication.getInstance().getDatabase();

	private String northAdjacency;
	private String northAdjacencyTypeCode;
	private String southAdjacency;
	private String southAdjacencyTypeCode;
	private String eastAdjacency;
	private String eastAdjacencyTypeCode;
	private String westAdjacency;
	private String westAdjacencyTypeCode;
	private String claimId;

	public String getNorthAdjacencyTypeCode() {
		return northAdjacencyTypeCode;
	}

	public void setNorthAdjacencyTypeCode(String northAdjacencyTypeCode) {
		this.northAdjacencyTypeCode = northAdjacencyTypeCode;
	}


	public String getSouthAdjacencyTypeCode() {
		return southAdjacencyTypeCode;
	}

	public void setSouthAdjacencyTypeCode(String southAdjacencyTypeCode) {
		this.southAdjacencyTypeCode = southAdjacencyTypeCode;
	}

	public String getWestAdjacencyTypeCode() {
		return westAdjacencyTypeCode;
	}

	public void setWestAdjacencyTypeCode(String westAdjacencyTypeCode) {
		this.westAdjacencyTypeCode = westAdjacencyTypeCode;
	}


	public String getEastAdjacencyTypeCode() {
		return eastAdjacencyTypeCode;
	}

	public void setEastAdjacencyTypeCode(String eastAdjacencyTypeCode) {
		this.eastAdjacencyTypeCode = eastAdjacencyTypeCode;
	}

	public String getNorthAdjacency() {
		return northAdjacency;
	}

	public void setNorthAdjacency(String northAdjacency) {
		this.northAdjacency = northAdjacency;
	}

	public String getSouthAdjacency() {
		return southAdjacency;
	}

	public void setSouthAdjacency(String southAdjacency) {
		this.southAdjacency = southAdjacency;
	}

	public String getEastAdjacency() {
		return eastAdjacency;
	}

	public void setEastAdjacency(String eastAdjacency) {
		this.eastAdjacency = eastAdjacency;
	}

	public String getWestAdjacency() {
		return westAdjacency;
	}

	public void setWestAdjacency(String westAdjacency) {
		this.westAdjacency = westAdjacency;
	}

	public String getClaimId() {
		return claimId;
	}

	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	@Override
	public String toString() {
		return "AdjacenciesNotes ["
				+ "northAdjacency=" + northAdjacency
				+ ", northAdjacencyTypeCode=" + northAdjacencyTypeCode
				+ ", southAdjacency=" + southAdjacency
				+ ", southAdjacencyTypeCode=" + southAdjacencyTypeCode
				+ ", eastAdjacency=" + eastAdjacency
				+ ", eastAdjacencyTypeCode=" + eastAdjacencyTypeCode
				+ ", westAdjacency=" + westAdjacency
				+ ", westAdjacencyTypeCode=" + westAdjacencyTypeCode
				+ "]";
	}

	public static int createAdjacenciesNotes(AdjacenciesNotes adjacenciesNotes) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement(" INSERT INTO ADJACENCIES_NOTES (" +
							"CLAIM_ID, " +
							"NORTH_ADJACENCY, " +
							"NORTH_ADJACENCY_TYPE_CODE, " +
							"SOUTH_ADJACENCY, " +
							"SOUTH_ADJACENCY_TYPE_CODE, " +
							"EAST_ADJACENCY, " +
							"EAST_ADJACENCY_TYPE_CODE, " +
							"WEST_ADJACENCY, " +
							"WEST_ADJACENCY_TYPE_CODE" +
							") VALUES(?,?,?,?,?,?,?,?,?)");
			statement.setString(1, adjacenciesNotes.getClaimId());
			statement.setString(2, adjacenciesNotes.getNorthAdjacency());
			statement.setString(3, adjacenciesNotes.getNorthAdjacencyTypeCode());
			statement.setString(4, adjacenciesNotes.getSouthAdjacency());
			statement.setString(5, adjacenciesNotes.getSouthAdjacencyTypeCode());
			statement.setString(6, adjacenciesNotes.getEastAdjacency());
			statement.setString(7, adjacenciesNotes.getEastAdjacencyTypeCode());
			statement.setString(8, adjacenciesNotes.getWestAdjacency());
			statement.setString(9, adjacenciesNotes.getWestAdjacencyTypeCode());

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

	public int create() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement(" INSERT INTO ADJACENCIES_NOTES (" +
							"CLAIM_ID, " +
							"NORTH_ADJACENCY, " +
							"NORTH_ADJACENCY_TYPE_CODE, " +
							"SOUTH_ADJACENCY, " +
							"SOUTH_ADJACENCY_TYPE_CODE, " +
							"EAST_ADJACENCY, " +
							"EAST_ADJACENCY_TYPE_CODE, " +
							"WEST_ADJACENCY, " +
							"WEST_ADJACENCY_TYPE_CODE" +
							") VALUES(?,?,?,?,?,?,?,?,?)");
			statement.setString(1, getClaimId());
			statement.setString(2, getNorthAdjacency());
			statement.setString(3, getNorthAdjacencyTypeCode());
			statement.setString(4, getSouthAdjacency());
			statement.setString(5, getSouthAdjacencyTypeCode());
			statement.setString(6, getEastAdjacency());
			statement.setString(7, getEastAdjacencyTypeCode());
			statement.setString(8, getWestAdjacency());
			statement.setString(9, getWestAdjacencyTypeCode());

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

	public int delete() {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("DELETE FROM ADJACENCIES_NOTES WHERE CLAIM_ID=?");
			statement.setString(1, getClaimId());
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

	public static int deleteAdjacenciesNotes(AdjacenciesNotes adjacenciesNotes) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = db.getConnection();
			statement = localConnection
					.prepareStatement("DELETE FROM ADJACENCIES_NOTES WHERE CLAIM_ID=?");
			statement.setString(1, adjacenciesNotes.getClaimId());
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

	public static int deleteAdjacenciesNotes(String claimId, Connection connection) {
		int result = 0;
		PreparedStatement statement = null;

		try {

			statement = connection
					.prepareStatement("DELETE FROM ADJACENCIES_NOTES WHERE CLAIM_ID=?");
			statement.setString(1, claimId);
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
		}
		return result;
	}

	public static int updateAdjacenciesNotes(AdjacenciesNotes adjacenciesNotes) {
		int result = 0;
		Connection localConnection = null;
		PreparedStatement statement = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement(" UPDATE ADJACENCIES_NOTES SET " +
							"NORTH_ADJACENCY=?, " +
							"NORTH_ADJACENCY_TYPE_CODE=?, " +
							"SOUTH_ADJACENCY=?, " +
							"SOUTH_ADJACENCY_TYPE_CODE=?, " +
							"EAST_ADJACENCY=?, " +
							"EAST_ADJACENCY_TYPE_CODE=?, " +
							"WEST_ADJACENCY=?, " +
							"WEST_ADJACENCY_TYPE_CODE=? " +
							"WHERE CLAIM_ID=? ");

			statement.setString(9, adjacenciesNotes.getClaimId());
			statement.setString(1, adjacenciesNotes.getNorthAdjacency());
			statement.setString(2, adjacenciesNotes.getNorthAdjacencyTypeCode());
			statement.setString(3, adjacenciesNotes.getSouthAdjacency());
			statement.setString(4, adjacenciesNotes.getSouthAdjacencyTypeCode());
			statement.setString(5, adjacenciesNotes.getEastAdjacency());
			statement.setString(6, adjacenciesNotes.getEastAdjacencyTypeCode());
			statement.setString(7, adjacenciesNotes.getWestAdjacency());
			statement.setString(8, adjacenciesNotes.getWestAdjacencyTypeCode());

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

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement(" UPDATE ADJACENCIES_NOTES SET " +
							"NORTH_ADJACENCY=?, " +
							"NORTH_ADJACENCY_TYPE_CODE=?, " +
							"SOUTH_ADJACENCY=?, " +
							"SOUTH_ADJACENCY_TYPE_CODE=?, " +
							"EAST_ADJACENCY=?, " +
							"EAST_ADJACENCY_TYPE_CODE=?, " +
							"WEST_ADJACENCY=?, " +
							"WEST_ADJACENCY_TYPE_CODE=? " +
							"WHERE CLAIM_ID=? ");

			statement.setString(9, getClaimId());
			statement.setString(1, getNorthAdjacency());
			statement.setString(2, getNorthAdjacencyTypeCode());
			statement.setString(3, getSouthAdjacency());
			statement.setString(4, getSouthAdjacencyTypeCode());
			statement.setString(5, getEastAdjacency());
			statement.setString(6, getEastAdjacencyTypeCode());
			statement.setString(7, getWestAdjacency());
			statement.setString(8, getWestAdjacencyTypeCode());

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

	public static AdjacenciesNotes getAdjacenciesNotes(String claimId) {
		AdjacenciesNotes adjacenciesNotes = null;
		Connection localConnection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

		try {

			localConnection = OpenTenureApplication.getInstance().getDatabase()
					.getConnection();
			statement = localConnection
					.prepareStatement("SELECT " +
							"NORTH_ADJACENCY, " +
							"NORTH_ADJACENCY_TYPE_CODE, " +
							"SOUTH_ADJACENCY, " +
							"SOUTH_ADJACENCY_TYPE_CODE, " +
							"EAST_ADJACENCY, " +
							"EAST_ADJACENCY_TYPE_CODE, " +
							"WEST_ADJACENCY, " +
							"WEST_ADJACENCY_TYPE_CODE " +
							"FROM ADJACENCIES_NOTES " +
							"WHERE CLAIM_ID=?");
			statement.setString(1, claimId);
			rs = statement.executeQuery();
			while (rs.next()) {
				adjacenciesNotes = new AdjacenciesNotes();
				adjacenciesNotes.setClaimId(claimId);
				adjacenciesNotes.setNorthAdjacency(rs.getString(1));
				adjacenciesNotes.setNorthAdjacencyTypeCode(rs.getString(2));
				adjacenciesNotes.setSouthAdjacency(rs.getString(3));
				adjacenciesNotes.setSouthAdjacencyTypeCode(rs.getString(4));
				adjacenciesNotes.setEastAdjacency(rs.getString(5));
				adjacenciesNotes.setEastAdjacencyTypeCode(rs.getString(6));
				adjacenciesNotes.setWestAdjacency(rs.getString(7));
				adjacenciesNotes.setWestAdjacencyTypeCode(rs.getString(8));
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
		return adjacenciesNotes;
	}

}
