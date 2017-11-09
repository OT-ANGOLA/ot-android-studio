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
package org.fao.sola.clients.android.opentenure.filesystem.json.model;

import java.util.List;

import org.fao.sola.clients.android.opentenure.form.FormPayload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

public class Claim {

	@SerializedName("id")
	String id;

	@SerializedName("nr")
	String nr;

	@SerializedName("decisionDate")
	String decisionDate;

	@SerializedName("statusCode")
	String statusCode;

	@SerializedName("landUseCode")
	String landUseCode;

	@SerializedName("description")
	String description;

	@SerializedName("challengeExpiryDate")
	String challengeExpiryDate;

	@SerializedName("lodgementDate")
	String lodgementDate;

	@SerializedName("mappedGeometry")
	String mappedGeometry;

	@SerializedName("gpsGeometry")
	String gpsGeometry;

	@SerializedName("challengedClaimId")
	String challengedClaimId;

	@SerializedName("attachments")
	List<Attachment> attachments;
	
	@SerializedName("locations")
	List<Location> locations;
	
	@SerializedName("version")
	String version;

	@SerializedName("dynamicForm")
	FormPayload dynamicForm;

	@SerializedName("claimant")
	Claimant claimant;

	@SerializedName("startDate")
	String startDate;

	@SerializedName("typeCode")
	String typeCode;

	@SerializedName("notes")
	String notes;

	@SerializedName("northAdjacency")
	String northAdjacency;

	@SerializedName("northAdjacencyTypeCode")
	String northAdjacencyTypeCode;

	@SerializedName("southAdjacency")
	String southAdjacency;

	@SerializedName("southAdjacencyTypeCode")
	String southAdjacencyTypeCode;

	@SerializedName("westAdjacency")
	String westAdjacency;

	@SerializedName("westAdjacencyTypeCode")
	String westAdjacencyTypeCode;

	@SerializedName("eastAdjacency")
	String eastAdjacency;

	@SerializedName("eastAdjacencyTypeCode")
	String eastAdjacencyTypeCode;

	@SerializedName("claimArea")
	long claimArea;
	
	@SerializedName("recorderName")
	String recorderName;

	@SerializedName("shares")
	List<Share> shares;
	
	@SerializedName("serverUrl") 
	@JsonIgnore(true)
	String serverUrl;

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

	public String getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(String issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public void setPlotNumber(String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(String blockNumber) {
		this.blockNumber = blockNumber;
	}

	public boolean isHasConstructions() {
		return hasConstructions;
	}

	public void setHasConstructions(boolean hasConstructions) {
		this.hasConstructions = hasConstructions;
	}

	public String getConstructionDate() {
		return constructionDate;
	}

	public void setConstructionDate(String constructionDate) {
		this.constructionDate = constructionDate;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getLandProjectCode() {
		return landProjectCode;
	}

	public void setLandProjectCode(String landProjectCode) {
		this.landProjectCode = landProjectCode;
	}
	public String getCommuneCode() {
		return communeCode;
	}

	public void setCommuneCode(String communeCode) {
		this.communeCode = communeCode;
	}

	@SerializedName("issuanceDate")
	String issuanceDate;
	@SerializedName("blockNumber")
	String blockNumber;
	@SerializedName("plotNumber")
	String plotNumber;
	@SerializedName("hasConstructions")
	boolean hasConstructions;
	@SerializedName("constructionDate")
	String constructionDate;
	@SerializedName("neighborhood")
	String neighborhood;
	@SerializedName("landProjectCode")
	String landProjectCode;
	@SerializedName("communeCode")
	String communeCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public List<AdditionalInfo> getAdditionaInfo() {
	// return additionaInfo;
	// }
	//
	// public void setAdditionaInfo(List<AdditionalInfo> additionaInfo) {
	// this.additionaInfo = additionaInfo;
	// }

	public String getNr() {
		return nr;
	}

	public String getLodgementDate() {
		return lodgementDate;
	}

	public void setLodgementDate(String lodgementDate) {
		this.lodgementDate = lodgementDate;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setNr(String nr) {
		this.nr = nr;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getChallengeExpiryDate() {
		return challengeExpiryDate;
	}

	public void setChallengeExpiryDate(String challengeExpiryDate) {
		this.challengeExpiryDate = challengeExpiryDate;
	}

	public String getMappedGeometry() {
		return mappedGeometry;
	}

	public void setMappedGeometry(String mappedGeometry) {
		this.mappedGeometry = mappedGeometry;
	}

	public String getGpsGeometry() {
		return gpsGeometry;
	}

	public void setGpsGeometry(String gpsGeometry) {
		this.gpsGeometry = gpsGeometry;
	}

	public String getChallengedClaimId() {
		return challengedClaimId;
	}

	public void setChallengedClaimId(String challengedClaimId) {
		this.challengedClaimId = challengedClaimId;
	}

	public FormPayload getDynamicForm() {
		return dynamicForm;
	}

	public void setDynamicForm(FormPayload dynamicForm) {
		this.dynamicForm = dynamicForm;
	}

	public Claimant getClaimant() {
		return claimant;
	}

	public void setClaimant(Claimant claimant) {
		this.claimant = claimant;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public List<Share> getShares() {
		return shares;
	}

	public void setShares(List<Share> shares) {
		this.shares = shares;
	}

	public String getLandUseCode() {
		return landUseCode;
	}

	public void setLandUseCode(String landUseCode) {
		this.landUseCode = landUseCode;
	}

	public String getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(String decisionDate) {
		this.decisionDate = decisionDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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

	public String getWestAdjacency() {
		return westAdjacency;
	}

	public void setWestAdjacency(String westAdjacency) {
		this.westAdjacency = westAdjacency;
	}

	public String getEastAdjacency() {
		return eastAdjacency;
	}

	public void setEastAdjacency(String eastAdjacency) {
		this.eastAdjacency = eastAdjacency;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public String getRecorderName() {
		return recorderName;
	}

	public void setRecorderName(String recorderName) {
		this.recorderName = recorderName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getClaimArea() {
		return claimArea;
	}

	public void setClaimArea(long claimArea) {
		this.claimArea = claimArea;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	
	

}
