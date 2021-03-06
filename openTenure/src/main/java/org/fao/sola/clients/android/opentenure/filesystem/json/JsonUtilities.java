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
package org.fao.sola.clients.android.opentenure.filesystem.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.lang.reflect.Modifier;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.fao.sola.clients.android.opentenure.OpenTenureApplication;
import org.fao.sola.clients.android.opentenure.OpenTenurePreferencesActivity;
import org.fao.sola.clients.android.opentenure.R;
import org.fao.sola.clients.android.opentenure.filesystem.FileSystemUtilities;
import org.fao.sola.clients.android.opentenure.filesystem.json.model.Claimant;
import org.fao.sola.clients.android.opentenure.filesystem.json.model.Location;
import org.fao.sola.clients.android.opentenure.filesystem.json.model.Person;
import org.fao.sola.clients.android.opentenure.filesystem.json.model.Share;
import org.fao.sola.clients.android.opentenure.model.AdjacenciesNotes;
import org.fao.sola.clients.android.opentenure.model.Attachment;
import org.fao.sola.clients.android.opentenure.model.Claim;
import org.fao.sola.clients.android.opentenure.model.ClaimStatus;
import org.fao.sola.clients.android.opentenure.model.Owner;
import org.fao.sola.clients.android.opentenure.model.ShareProperty;
import org.fao.sola.clients.android.opentenure.model.PropertyLocation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtilities {

	public static boolean createClaimJson(String claimID) {

		Log.d("CreateClaimJson", "Calling data2json");

		String json = data2Json(claimID);
		writeJsonTofile(claimID, json);
		return true;
	}

	private static String data2Json(String claimId) {

		org.fao.sola.clients.android.opentenure.filesystem.json.model.Claim tempClaim = new org.fao.sola.clients.android.opentenure.filesystem.json.model.Claim();

		try {
			String[] splittpedServerUrl = null;

			Claim claim = Claim.getClaim(claimId);

			if (claim != null) {

				TimeZone tz = TimeZone.getTimeZone("UTC");
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
				sdf.setTimeZone(tz);

				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				// number of days to add
				String lodgementDate = sdf.format(c.getTime());

				String challengeExpiryDate = null;
				if (claim.getStatus().equals(ClaimStatus._CREATED)
						|| claim.getStatus().equals(ClaimStatus._UPLOAD_ERROR)
						|| claim.getStatus().equals(
								ClaimStatus._UPLOAD_INCOMPLETE)) {
					c.add(Calendar.MONTH, 1);
					// number of days to add
					challengeExpiryDate = null;
				} else
					challengeExpiryDate = sdf.format(claim
							.getChallengeExpiryDate());

				// tempClaim.setChallengedClaim(null);
				tempClaim.setDescription(claim.getName());
				tempClaim
						.setChallengedClaimId(claim.getChallengedClaim() != null ? claim
								.getChallengedClaim().getClaimId() : null);
				tempClaim.setId(claimId);

				// Server ulr
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(OpenTenureApplication
								.getContext());

				String serverUrl = preferences.getString(
						OpenTenurePreferencesActivity.CS_URL_PREF,
						OpenTenureApplication._DEFAULT_COMMUNITY_SERVER);

				if (serverUrl.contains("//")) {
					splittpedServerUrl = serverUrl.split("//");
					tempClaim.setServerUrl(splittpedServerUrl[1]);
				} else
					tempClaim.setServerUrl(serverUrl);

				if (claim.getStatus().equals(ClaimStatus._CREATED)
						|| claim.getStatus().equals(ClaimStatus._UPLOAD_ERROR)
						|| claim.getStatus().equals(
								ClaimStatus._UPLOAD_INCOMPLETE))
				tempClaim.setStatusCode(ClaimStatus._CREATED);
				else
					tempClaim.setStatusCode(claim.getStatus());
				tempClaim.setLandUseCode(claim.getLandUse());
				tempClaim.setNotes(claim.getNotes());
				tempClaim.setClaimArea(claim.getClaimArea());
				if (claim.getRecorderName() != null
						&& !claim.getRecorderName().equals(""))
					tempClaim.setRecorderName(claim.getRecorderName());
				else
					tempClaim.setRecorderName(OpenTenureApplication
							.getUsername());

				AdjacenciesNotes adjacenciesNotes = AdjacenciesNotes
						.getAdjacenciesNotes(claimId);

				if (adjacenciesNotes != null) {
					tempClaim.setNorthAdjacency(adjacenciesNotes
							.getNorthAdjacency());
					tempClaim.setSouthAdjacency(adjacenciesNotes
							.getSouthAdjacency());
					tempClaim.setWestAdjacency(adjacenciesNotes
							.getWestAdjacency());
					tempClaim.setEastAdjacency(adjacenciesNotes
							.getEastAdjacency());
					// Angola specific
					tempClaim.setNorthAdjacencyTypeCode(adjacenciesNotes
							.getNorthAdjacencyTypeCode());
					tempClaim.setSouthAdjacencyTypeCode(adjacenciesNotes
							.getSouthAdjacencyTypeCode());
					tempClaim.setWestAdjacencyTypeCode(adjacenciesNotes
							.getWestAdjacencyTypeCode());
					tempClaim.setEastAdjacencyTypeCode(adjacenciesNotes
							.getEastAdjacencyTypeCode());
				}
				tempClaim.setTypeCode(claim.getType());
				if (claim.getDateOfStart() != null)
					tempClaim.setStartDate(sdf.format(claim.getDateOfStart()));

				if (claim.getStatus().equals(ClaimStatus._CREATED)
						|| claim.getStatus().equals(ClaimStatus._UPLOAD_ERROR)
						|| claim.getStatus().equals(
								ClaimStatus._UPLOAD_INCOMPLETE))
					tempClaim.setNr(null);
				else
					tempClaim.setNr(claim.getClaimNumber());

				tempClaim.setLodgementDate(lodgementDate);
				tempClaim.setChallengeExpiryDate(challengeExpiryDate);

				Claimant person = new Claimant();

				person.setPhone(claim.getPerson().getContactPhoneNumber());
				Date bDate = claim.getPerson().getDateOfBirth();
				if (bDate != null)
					person.setBirthDate(sdf.format(bDate));
				person.setEmail(claim.getPerson().getEmailAddress());
				person.setName(claim.getPerson().getFirstName());
				person.setId(claim.getPerson().getPersonId());
				person.setLastName(claim.getPerson().getLastName());
				person.setMobilePhone(claim.getPerson().getMobilePhoneNumber());
				person.setAddress(claim.getPerson().getPostalAddress());
				person.setGenderCode(claim.getPerson().getGender());
				person.setIdTypeCode(claim.getPerson().getIdType());
				person.setIdNumber(claim.getPerson().getIdNumber());
				if (claim
						.getPerson()
						.getPersonType()
						.equals(org.fao.sola.clients.android.opentenure.model.Person._PHYSICAL))
					person.setPhysicalPerson(true);
				else
					person.setPhysicalPerson(false);

				// Angola specific
				person.setOtherName(claim.getPerson().getOtherName());
				person.setFatherName(claim.getPerson().getFatherName());
				person.setMotherName(claim.getPerson().getMotherName());
                if(claim.getPerson().getIdIssuanceDate() != null){
                    person.setIdIssuanceDate(sdf.format(claim.getPerson().getIdIssuanceDate()));
                }
				person.setIdIssuanceCountryCode(claim.getPerson().getIdIssuanceCountryCode());
				person.setIdIssuanceProvinceCode(claim.getPerson().getIdIssuanceProvinceCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : claim.getPerson().getIdIssuanceProvinceCode());
				person.setIdIssuanceMunicipalityCode(claim.getPerson().getIdIssuanceMunicipalityCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : claim.getPerson().getIdIssuanceMunicipalityCode());
				person.setIdIssuanceCommuneCode(claim.getPerson().getIdIssuanceCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : claim.getPerson().getIdIssuanceCommuneCode());

				person.setBirthCountryCode(claim.getPerson().getBirthCountryCode());
				person.setBirthCommuneCode(claim.getPerson().getBirthCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : claim.getPerson().getBirthCommuneCode());

				person.setResidenceCommuneCode(claim.getPerson().getResidenceCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : claim.getPerson().getResidenceCommuneCode());

				person.setBeneficiaryName(claim.getPerson().getBeneficiaryName());
				person.setBeneficiaryIdNumber(claim.getPerson().getBeneficiaryIdNumber());
				person.setMaritalStatusCode(claim.getPerson().getMaritalStatusCode());

				tempClaim.setClaimant(person);

				if (claim.getDynamicForm() != null
						&& claim.getDynamicForm().getSectionPayloadList() != null
						&& claim.getDynamicForm().getSectionPayloadList()
								.size() > 0) {
					tempClaim.setDynamicForm(claim.getDynamicForm());
				}

				List<org.fao.sola.clients.android.opentenure.filesystem.json.model.Attachment> attachments = new ArrayList<org.fao.sola.clients.android.opentenure.filesystem.json.model.Attachment>();

				for (Iterator<Attachment> iterator = claim.getAttachments()
						.iterator(); iterator.hasNext();) {
					Attachment attachment = (Attachment) iterator.next();

					org.fao.sola.clients.android.opentenure.filesystem.json.model.Attachment attach = new org.fao.sola.clients.android.opentenure.filesystem.json.model.Attachment();

					attach.setId(attachment.getAttachmentId());
					attach.setDescription(attachment.getDescription());
					attach.setFileName(attachment.getFileName());

					String extension = "";
					int i = attachment.getPath().lastIndexOf('.');
					if (i > 0) {
						extension = attachment.getPath().substring(i + 1);
					}

					attach.setFileExtension(extension);
					attach.setTypeCode(attachment.getFileType());
					// attach.setFileType(attachment.getFileType());
					attach.setMd5(attachment.getMD5Sum());
					attach.setSize(attachment.getSize());
					attach.setMimeType(attachment.getMimeType());

					attachments.add(attach);
				}

				List<Location> locations = new ArrayList<Location>();

				for (Iterator<PropertyLocation> iterator = claim
						.getPropertyLocations().iterator(); iterator.hasNext();) {

					PropertyLocation propertyLocation = (PropertyLocation) iterator
							.next();

					Location location = new Location();

					location.setClaimId(propertyLocation.getClaimId());
					location.setDescription(propertyLocation.getDescription());
					location.setGpsLocation(PropertyLocation
							.gpsWKTFromPropertyLocation(propertyLocation));
					location.setMappedLocation(PropertyLocation
							.mapWKTFromPropertyLocation(propertyLocation));
					location.setId(propertyLocation.getPropertyLocationId());

					locations.add(location);
				}

				List<org.fao.sola.clients.android.opentenure.filesystem.json.model.Share> shares = new ArrayList<org.fao.sola.clients.android.opentenure.filesystem.json.model.Share>();

				List<ShareProperty> sharesDB = ShareProperty.getShares(claimId);

				for (Iterator<ShareProperty> iterator = sharesDB.iterator(); iterator
						.hasNext();) {
					ShareProperty shareDB = (ShareProperty) iterator.next();

					Share share = new Share();

					List<Owner> owners = Owner.getOwners(shareDB.getId());

					List<Person> ownersJson = new ArrayList<Person>();

					for (Iterator<Owner> iterator2 = owners.iterator(); iterator2
							.hasNext();) {

						Person personJson = new Person();
						Owner ownerDB = (Owner) iterator2.next();

						org.fao.sola.clients.android.opentenure.model.Person personDB = org.fao.sola.clients.android.opentenure.model.Person
								.getPerson(ownerDB.getPersonId());

						personJson.setAddress(personDB.getPostalAddress());
						Date aDate = personDB.getDateOfBirth();
						if (aDate != null)
							personJson.setBirthDate(sdf.format(aDate));
						personJson.setEmail(personDB.getEmailAddress());
						personJson.setGenderCode(personDB.getGender());
						personJson
								.setPhysicalPerson(personDB
										.getPersonType()
										.equals(org.fao.sola.clients.android.opentenure.model.Person._PHYSICAL));

						if (claim.getPerson().getPersonId()
								.equals(personDB.getPersonId()))
							personJson.setId(UUID.randomUUID().toString()); // This
																			// trick
						// is
						// to
						// permit a
						// claimant
						// to be
						// also
						// owner
						// . When
						// the
						// issue
						// will be
						// resolved
						// on SOLA
						// ----->
						// personJson.setId(personDB.getPersonId());

						else
							personJson.setId(personDB.getPersonId());
						personJson.setMobilePhone(personDB
								.getMobilePhoneNumber());
						personJson.setLastName(personDB.getLastName());
						personJson.setName(personDB.getFirstName());
						personJson.setPhone(personDB.getContactPhoneNumber());
						personJson.setIdNumber(personDB.getIdNumber());
						personJson.setIdTypeCode(personDB.getIdType());
						// Angola specific
						personJson.setOtherName(personDB.getOtherName());
						personJson.setFatherName(personDB.getFatherName());
						personJson.setMotherName(personDB.getMotherName());
                        if(personDB.getIdIssuanceDate() != null){
                            personJson.setIdIssuanceDate(sdf.format(personDB.getIdIssuanceDate()));
                        }
						personJson.setIdIssuanceCountryCode(personDB.getIdIssuanceCountryCode());
						personJson.setIdIssuanceProvinceCode(personDB.getIdIssuanceProvinceCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : personDB.getIdIssuanceProvinceCode());
						personJson.setIdIssuanceMunicipalityCode(personDB.getIdIssuanceMunicipalityCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : personDB.getIdIssuanceMunicipalityCode());
						personJson.setIdIssuanceCommuneCode(personDB.getIdIssuanceCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : personDB.getIdIssuanceCommuneCode());

						personJson.setBirthCountryCode(personDB.getBirthCountryCode());
						personJson.setBirthCommuneCode(personDB.getBirthCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : personDB.getBirthCommuneCode());

						personJson.setResidenceCommuneCode(personDB.getResidenceCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)) ? null : personDB.getResidenceCommuneCode());

						personJson.setBeneficiaryName(personDB.getBeneficiaryName());
						personJson.setBeneficiaryIdNumber(personDB.getBeneficiaryIdNumber());
						personJson.setMaritalStatusCode(personDB.getMaritalStatusCode());

						ownersJson.add(personJson);

					}

					share.setOwners(ownersJson);
					share.setId("" + shareDB.getId());

					share.setPercentage(shareDB.getShares());

					shares.add(share);

				}

				/*
				 * TEmporary off the additional info on the claim submission
				 */

				// List<AdditionalInfo> xMetadata = new
				// ArrayList<AdditionalInfo>();
				//
				// for (Iterator iterator = claim.getMetadata().iterator();
				// iterator.hasNext();) {
				// Metadata metadataO = (Metadata) iterator.next();
				//
				// AdditionalInfo xm = new AdditionalInfo();
				//
				// xm.setMetadataId(metadataO.getMetadataId());
				// xm.setName(metadataO.getName());
				// xm.setValue(metadataO.getValue());
				//
				// xMetadata.add(xm);
				// }

				tempClaim
						.setGpsGeometry(claim.getGPSWKT());
				tempClaim
						.setMappedGeometry(claim.getMapWKT());
				tempClaim.setAttachments(attachments);

				tempClaim.setLocations(locations);

				tempClaim.setShares(shares);
				// tempClaim.setAdditionaInfo(xMetadata);
				// Angola specific
				tempClaim.setBlockNumber(claim.getBlockNumber());
				tempClaim.setPlotNumber(claim.getPlotNumber());
				tempClaim.setHasConstructions(claim.isHasConstructions());
                if(claim.getConstructionDate() != null){
                    tempClaim.setConstructionDate(sdf.format(claim.getConstructionDate()));
                }
				tempClaim.setNeighborhood(claim.getNeighborhood());
				tempClaim.setLandProjectCode(claim.getLandProjectCode());
				tempClaim.setCommuneCode(claim.getCommuneCode().equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na))?null:claim.getCommuneCode());

				try {
					Gson gson = new GsonBuilder()
							.setPrettyPrinting()
							.serializeNulls()
							// .setFieldNamingPolicy(
							// FieldNamingPolicy.UPPER_CAMEL_CASE)
							.excludeFieldsWithModifiers(Modifier.TRANSIENT)
							.create();

					String g = gson.toJson(tempClaim);
					Log.d("CreateClaimJson", g);

					return g;

				} catch (Throwable e) {

					Log.d("CreateClaimJson",
							"An error has occurred" + e.getMessage());
					e.printStackTrace();

					return null;
				}
			} else {

				Log.d("CreateClaimJson", "The claim is null");
				return null;
			}
		} catch (Exception e) {
			Log.d("CreateClaimJson", "Error : " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	private static boolean writeJsonTofile(String claimID, String json) {

		try {

			// convert String into InputStream
			InputStream is = new ByteArrayInputStream(json.getBytes());

			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			File jFile = new File(FileSystemUtilities.getClaimFolder(claimID),
					"claim.json");

			if (jFile.exists())
				jFile.delete();

			jFile.createNewFile();

			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(jFile));

			char[] buffer = new char[1024];
			int x;
			while ((x = (br.read(buffer))) != -1) {

				writer.write(buffer, 0, x);
				writer.flush();

			}

			writer.flush();
			writer.close();
			br.close();

		} catch (Exception e) {
			Log.d("CreateClaimJson", "An error has occurred" + e.getMessage());
		}

		return false;

	}

	/** Transform Calendar to ISO 8601 string. */
	public static String fromCalendar(final Calendar calendar) {
		Date date = calendar.getTime();
		String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ",
				Locale.US).format(date);
		return formatted.substring(0, 22) + ":" + formatted.substring(22);
	}

	/** Get current date and time formatted as ISO 8601 string. */
	public static String now() {
		return fromCalendar(GregorianCalendar.getInstance());
	}

	/** Transform ISO 8601 string to Calendar. */
	public static Calendar toCalendar(final String iso8601string)
			throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance();
		String s = iso8601string.replace("Z", "+00:00");
		try {
			s = s.substring(0, 22) + s.substring(23); // to get rid of the ":"
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid length", 0);
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
				.parse(s);
		calendar.setTime(date);
		return calendar;
	}

	public static int remainingDays(java.sql.Date challengeExpiryDate) {
		if (challengeExpiryDate == null)
			return 0;
		return (int) ((challengeExpiryDate.getTime() - new java.util.Date()
				.getTime()) / (1000 * 60 * 60 * 24)) + 1;
	}

}
