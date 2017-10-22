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
package org.fao.sola.clients.android.opentenure;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


import org.fao.sola.clients.android.opentenure.button.listener.ConfirmExit;
import org.fao.sola.clients.android.opentenure.filesystem.FileSystemUtilities;
import org.fao.sola.clients.android.opentenure.model.Commune;
import org.fao.sola.clients.android.opentenure.model.Country;
import org.fao.sola.clients.android.opentenure.model.IdType;
import org.fao.sola.clients.android.opentenure.model.Municipality;
import org.fao.sola.clients.android.opentenure.model.Person;
import org.fao.sola.clients.android.opentenure.model.Province;
import org.fao.sola.clients.android.opentenure.model.MaritalStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class PersonFragment extends Fragment {

	private View rootView;
	private PersonDispatcher personActivity;
	private ModeDispatcher mainActivity;
	private final Calendar localCalendar = Calendar.getInstance();
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private File personPictureFile;
	private ImageView claimantImageView;
	private Button changeButton;
	private boolean allowSave = true;
	private Map<String, String> keyValueMaritalStatusMap;
	private Map<String, String> valueKeyMaritalStatusMap;
	private Map<String, String> keyValueMapIdTypes;
	private Map<String, String> valueKeyMapIdTypes;
	private List<Country> countriesList;
	private List<Province> provincesList;
	private List<Municipality> municipalitiesList;
	private List<Commune> communesList;
	boolean isPerson = true;
	boolean onlyActive = true;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			personActivity = (PersonDispatcher) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement PersonDispatcher");
		}
		try {
			mainActivity = (ModeDispatcher) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ModeDispatcher");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.person, menu);

		if (!allowSave) {
			menu.removeItem(R.id.action_save);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	// @Override
	// public void onPause() {
	//
	// checkChanges();
	//
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		if(personActivity.getPersonId() == null)
			onlyActive = true;
		else 
			onlyActive = false;
		

		if ((personActivity.getEntityType() != null && personActivity
				.getEntityType().equalsIgnoreCase("group"))
				|| (personActivity.getPersonId() != null && Person
						.getPerson(personActivity.getPersonId())
						.getPersonType().equals(Person._GROUP))) {

			rootView = inflater.inflate(R.layout.fragment_group, container,
					false);
			setHasOptionsMenu(true);

			OpenTenureApplication.setPersonsView(rootView);

			InputMethodManager imm = (InputMethodManager) rootView.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

			EditText dateOfEstablishment = (EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field);

			final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					localCalendar.set(Calendar.YEAR, year);
					localCalendar.set(Calendar.MONTH, monthOfYear);
					localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateDoE();
				}

			};

			dateOfEstablishment
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							new DatePickerDialog(rootView.getContext(), date,
									localCalendar.get(Calendar.YEAR),
									localCalendar.get(Calendar.MONTH),
									localCalendar.get(Calendar.DAY_OF_MONTH))
									.show();
							return true;
						}
					});

			claimantImageView = (ImageView) rootView
					.findViewById(R.id.claimant_picture);
			claimantImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (personPictureFile != null) {

						Person.deleteAllBmp(personActivity.getPersonId());
						
						//*****************************************//						
						
						
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(personPictureFile));
						startActivityForResult(intent,
								CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					} else {
						Toast toast = Toast.makeText(
								rootView.getContext(),
								R.string.message_save_person_before_adding_content,
								Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			});
			
			
			if (personActivity.getPersonId() != null
					&& Person.getPerson(personActivity.getPersonId())
							.getPersonType().equalsIgnoreCase(Person._PHYSICAL)) {
				load(personActivity.getPersonId());
			} else if (personActivity.getPersonId() != null
					&& Person.getPerson(personActivity.getPersonId())
							.getPersonType().equalsIgnoreCase(Person._GROUP)) {
				loadGroup(personActivity.getPersonId());
			}

			return rootView;

		} else {

			rootView = inflater.inflate(R.layout.fragment_person, container,
					false);
			setHasOptionsMenu(true);
			InputMethodManager imm = (InputMethodManager) rootView.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

			OpenTenureApplication.setPersonsView(rootView);

			preload();

			EditText dateOfBirth = (EditText) rootView
					.findViewById(R.id.date_of_birth_input_field);

			final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
									  int monthOfYear, int dayOfMonth) {
					localCalendar.set(Calendar.YEAR, year);
					localCalendar.set(Calendar.MONTH, monthOfYear);
					localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateDoB();
				}

			};

			dateOfBirth.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					new DatePickerDialog(rootView.getContext(), date,
							localCalendar.get(Calendar.YEAR), localCalendar
							.get(Calendar.MONTH), localCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
					return true;
				}
			});

			EditText idIssuanceDate = (EditText) rootView
					.findViewById(R.id.id_issuance_date_input_field);

			final DatePickerDialog.OnDateSetListener issuanceDate = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
									  int monthOfYear, int dayOfMonth) {
					localCalendar.set(Calendar.YEAR, year);
					localCalendar.set(Calendar.MONTH, monthOfYear);
					localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateIssuanceDate();
				}

			};

			idIssuanceDate.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					new DatePickerDialog(rootView.getContext(), issuanceDate,
							localCalendar.get(Calendar.YEAR), localCalendar
							.get(Calendar.MONTH), localCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
					return true;
				}
			});

			EditText idExpiryDate = (EditText) rootView
					.findViewById(R.id.id_expiry_date_input_field);

			final DatePickerDialog.OnDateSetListener expiryDate = new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
									  int monthOfYear, int dayOfMonth) {
					localCalendar.set(Calendar.YEAR, year);
					localCalendar.set(Calendar.MONTH, monthOfYear);
					localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					updateExpiryDate();
				}

			};

			idExpiryDate.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					new DatePickerDialog(rootView.getContext(), expiryDate,
							localCalendar.get(Calendar.YEAR), localCalendar
							.get(Calendar.MONTH), localCalendar
							.get(Calendar.DAY_OF_MONTH)).show();
					return true;
				}
			});

			claimantImageView = (ImageView) rootView
					.findViewById(R.id.claimant_picture);
			claimantImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (personPictureFile != null) {
						
						Person.deleteAllBmp(personActivity.getPersonId());
						

						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(personPictureFile));
						startActivityForResult(intent,
								CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					} else {
						Toast toast = Toast.makeText(
								rootView.getContext(),
								R.string.message_save_person_before_adding_content,
								Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			});
			if (personActivity.getPersonId() != null) {
				load(personActivity.getPersonId());
			}

			return rootView;
		}
	}

	private void preload() {
		// ID TYPE Spinner
		Spinner spinnerIT = (Spinner) rootView
				.findViewById(R.id.id_type_spinner);
		Spinner spinnerGender = (Spinner) rootView
				.findViewById(R.id.gender_spinner);
		Spinner spinnerMaritalStatus = (Spinner) rootView
				.findViewById(R.id.marital_status_spinner);
		final Spinner spinnerBirthCountry = (Spinner) rootView
				.findViewById(R.id.birth_country);
		final Spinner spinnerIdIssuanceCountry = (Spinner) rootView
				.findViewById(R.id.id_issuance_country);

		IdType it = new IdType();
		MaritalStatus ms = new MaritalStatus();
		Country co = new Country();
		Province pr = new Province();
		Municipality mu = new Municipality();
		Commune com = new Commune();

		SortedSet<String> keys;

		/* Mapping marital status localization */
		keyValueMaritalStatusMap = ms.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyMaritalStatusMap = ms.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> maritalStatuseslist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueMaritalStatusMap.keySet());
		for (String key : keys) {
			String value = keyValueMaritalStatusMap.get(key);
			maritalStatuseslist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterMS = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				maritalStatuseslist);
		spinnerMaritalStatus.setAdapter(dataAdapterMS);

		/* Mapping id type localization */
		keyValueMapIdTypes = it.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyMapIdTypes = it.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> idTypelist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueMapIdTypes.keySet());
		for (String key : keys) {
			String value = keyValueMapIdTypes.get(key);
			idTypelist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterIT = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				idTypelist);
		spinnerIT.setAdapter(dataAdapterIT);

		/* Mapping country localization */
		countriesList = new ArrayList<Country>(new TreeSet<Country>(onlyActive ? Country.getActiveCountries():Country.getCountries()));
		final ArrayAdapter<Country> dataAdapterIdIssuanceCountry = new ArrayAdapter<Country>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				countriesList);
		spinnerIdIssuanceCountry.setAdapter(dataAdapterIdIssuanceCountry);
		final ArrayAdapter<Country> dataAdapterBirthCountry = new ArrayAdapter<Country>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				countriesList);
		spinnerBirthCountry.setAdapter(dataAdapterBirthCountry);
		/* Mapping province localization */
		provincesList = new ArrayList<Province>(new TreeSet<Province>(onlyActive ? Province.getActiveProvinces():Province.getProvinces()));

		ArrayAdapter<Province> dataAdapterPR = new ArrayAdapter<Province>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Province>(provincesList));
		final Spinner spinnerIdIssuanceProvince = (Spinner) rootView
				.findViewById(R.id.id_issuance_province);

		spinnerIdIssuanceProvince.setAdapter(dataAdapterPR);

		/* Mapping municipality localization */
		municipalitiesList = new ArrayList<Municipality>(new TreeSet<Municipality>(onlyActive ? Municipality.getActiveMunicipalities():Municipality.getMunicipalities()));

		ArrayAdapter<Municipality> dataAdapterMU = new ArrayAdapter<Municipality>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Municipality>(municipalitiesList));
		final Spinner spinnerIdIssuanceMunicipality = (Spinner) rootView
				.findViewById(R.id.id_issuance_municipality);
		spinnerIdIssuanceMunicipality.setAdapter(dataAdapterMU);
		/* Mapping commune localization */
		communesList = new ArrayList<Commune>(new TreeSet<Commune>(onlyActive ? Commune.getActiveCommunes():Commune.getCommunes()));

		ArrayAdapter<Commune> dataAdapterIdIssuanceCommune = new ArrayAdapter<Commune>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Commune>(communesList));
		ArrayAdapter<Commune> dataAdapterResidenceCommune = new ArrayAdapter<Commune>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Commune>(communesList));
		ArrayAdapter<Commune> dataAdapterBirthCommune = new ArrayAdapter<Commune>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Commune>(communesList));

		final Spinner spinnerBirthCommune = (Spinner) rootView
				.findViewById(R.id.birth_commune);
		final Spinner spinnerResidenceCommune = (Spinner) rootView
				.findViewById(R.id.residence_commune);
		final Spinner spinnerIdIssuanceCommune = (Spinner) rootView
				.findViewById(R.id.id_issuance_commune);

		spinnerIdIssuanceCommune.setAdapter(dataAdapterIdIssuanceCommune);
		spinnerResidenceCommune.setAdapter(dataAdapterResidenceCommune);
		spinnerBirthCommune.setAdapter(dataAdapterBirthCommune);

		List<String> genderList = new ArrayList<String>();

		genderList.add(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_masculine));
		genderList.add(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_feminine));

		ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				genderList);

		spinnerGender.setAdapter(dataAdapterGender);
		// Setup listeners
		spinnerIdIssuanceCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of id issuance provinces based on selected country
				Country selectedCountry = (Country)spinnerIdIssuanceCountry.getSelectedItem();

				ArrayAdapter<Province> dataAdapterPR = (ArrayAdapter<Province>)spinnerIdIssuanceProvince.getAdapter();
				dataAdapterPR.clear();
				List<Province> provinces = filterProvincesByCountry(provincesList, selectedCountry.getCode());
				dataAdapterPR.addAll(provinces);
				dataAdapterPR.notifyDataSetChanged();
				spinnerIdIssuanceProvince.setSelection(0,true);

				Province selectedProvince = provinces.get(0);
				ArrayAdapter<Municipality> dataAdapterMU = (ArrayAdapter<Municipality>)spinnerIdIssuanceMunicipality.getAdapter();
				dataAdapterMU.clear();
				List<Municipality> municipalities = filterMunicipalitiesByProvince(municipalitiesList, selectedProvince.getCode());
				dataAdapterMU.addAll(municipalities);
				dataAdapterMU.notifyDataSetChanged();
				spinnerIdIssuanceMunicipality.setSelection(0,true);

				Municipality selectedMunicipality = municipalities.get(0);
				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerIdIssuanceCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerIdIssuanceCommune.setSelection(0,true);

				spinnerIdIssuanceCountry.requestFocusFromTouch();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinnerBirthCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of birth communes based on selected country
				Country selectedCountry = (Country)spinnerBirthCountry.getSelectedItem();

				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerBirthCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(filterCommunesByCountry(communesList, selectedCountry.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerBirthCommune.setSelection(0,true);

				spinnerBirthCountry.requestFocusFromTouch();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinnerIdIssuanceProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of issuance municipalities based on selected province
				Province selectedProvince = (Province)spinnerIdIssuanceProvince.getSelectedItem();

				ArrayAdapter<Municipality> dataAdapterMU = (ArrayAdapter<Municipality>)spinnerIdIssuanceMunicipality.getAdapter();
				dataAdapterMU.clear();
				List<Municipality> municipalities = filterMunicipalitiesByProvince(municipalitiesList, selectedProvince.getCode());
				dataAdapterMU.addAll(municipalities);
				dataAdapterMU.notifyDataSetChanged();
				spinnerIdIssuanceMunicipality.setSelection(0,true);

				Municipality selectedMunicipality = municipalities.get(0);
				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerIdIssuanceCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerIdIssuanceCommune.setSelection(0,true);

				spinnerIdIssuanceProvince.requestFocusFromTouch();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinnerIdIssuanceMunicipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of issuance communes based on selected municipality

				Municipality selectedMunicipality = (Municipality)spinnerIdIssuanceMunicipality.getSelectedItem();

				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerIdIssuanceCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerIdIssuanceCommune.setSelection(0,true);

				spinnerIdIssuanceMunicipality.requestFocusFromTouch();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		// set default choice
		spinnerBirthCountry.setSelection(countryIndex(Country.DEFAULT_COUNTRY_CODE),true);
		spinnerIdIssuanceCountry.setSelection(countryIndex(Country.DEFAULT_COUNTRY_CODE),true);

	}

	private void load(String personId) {
		
		Person person = Person.getPerson(personId);
		((EditText) rootView.findViewById(R.id.first_name_input_field))
				.setText(person.getFirstName());
		((EditText) rootView.findViewById(R.id.last_name_input_field))
				.setText(person.getLastName());
		((EditText) rootView.findViewById(R.id.other_name_input_field))
				.setText(person.getOtherName());
		((EditText) rootView.findViewById(R.id.father_name_input_field))
				.setText(person.getFatherName());
		((EditText) rootView.findViewById(R.id.mother_name_input_field))
				.setText(person.getMotherName());
		((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
				.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
						.format(person.getDateOfBirth()));
		if(person.getIdExpiryDate() != null){
			((EditText) rootView.findViewById(R.id.id_expiry_date_input_field))
					.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
							.format(person.getIdExpiryDate()));
		}
		if(person.getIdIssuanceDate() != null){
			((EditText) rootView.findViewById(R.id.id_issuance_date_input_field))
					.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
							.format(person.getIdIssuanceDate()));
		}
		((EditText) rootView.findViewById(R.id.postal_address_input_field))
				.setText(person.getPostalAddress());
		((EditText) rootView.findViewById(R.id.email_address_input_field))
				.setText(person.getEmailAddress());
		((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field))
				.setText(person.getContactPhoneNumber());
		((Spinner) rootView.findViewById(R.id.id_type_spinner))
				.setSelection(new IdType().getIndexByCodeType(person
						.getIdType(),onlyActive));

		((Spinner) rootView.findViewById(R.id.id_issuance_country))
				.setSelection(countryIndex(person.getIdIssuanceCountryCode()));
		((Spinner) rootView.findViewById(R.id.id_issuance_province))
				.setSelection(provinceIndex(person.getIdIssuanceProvinceCode()));
		((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
				.setSelection(municipalityIndex(person
						.getIdIssuanceMunicipalityCode()));
		((Spinner) rootView.findViewById(R.id.id_issuance_commune))
				.setSelection(Commune.communeIndex(communesList, person.getIdIssuanceCommuneCode()));
		((Spinner) rootView.findViewById(R.id.residence_commune))
				.setSelection(Commune.communeIndex(communesList, person.getResidenceCommuneCode()));
		((Spinner) rootView.findViewById(R.id.marital_status_spinner))
				.setSelection(new MaritalStatus().getIndexByCodeType(person
						.getMaritalStatusCode(),onlyActive));
		((Spinner) rootView.findViewById(R.id.birth_country))
				.setSelection(countryIndex(person.getBirthCountryCode()));
		((Spinner) rootView.findViewById(R.id.birth_commune))
				.setSelection(Commune.communeIndex(communesList, person
						.getBirthCommuneCode()));

		if (person.getGender().equals("M")) {
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setSelection(0);
		} else
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setSelection(1);

		((EditText) rootView.findViewById(R.id.id_number)).setText(person
				.getIdNumber());
		((EditText) rootView.findViewById(R.id.beneficiary_name_input_field)).setText(person
				.getBeneficiaryName());
		((EditText) rootView.findViewById(R.id.beneficiary_id_number_input_field)).setText(person
				.getBeneficiaryIdNumber());

		if (person.hasUploadedClaims()) {
			((EditText) rootView.findViewById(R.id.first_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.last_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.other_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.mother_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.father_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
					.setOnLongClickListener(null);
			((EditText) rootView.findViewById(R.id.id_expiry_date_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.id_expiry_date_input_field))
					.setOnLongClickListener(null);
			((EditText) rootView.findViewById(R.id.id_issuance_date_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.id_issuance_date_input_field))
					.setOnLongClickListener(null);
			((EditText) rootView.findViewById(R.id.postal_address_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.email_address_input_field))
					.setFocusable(false);
			((EditText) rootView
					.findViewById(R.id.contact_phone_number_input_field))
					.setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_type_spinner))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_type_spinner))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.id_type_spinner))
					.setClickable(false);
			((ImageView) rootView.findViewById(R.id.claimant_picture))
					.setClickable(false);

            ((Spinner) rootView.findViewById(R.id.gender_spinner))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.gender_spinner))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.marital_status_spinner))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.marital_status_spinner))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.marital_status_spinner))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_country))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_country))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_country))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.birth_country))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.birth_country))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.birth_country))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_province))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_province))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_province))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_commune))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.id_issuance_commune))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_commune))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.residence_commune))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.residence_commune))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.residence_commune))
					.setClickable(false);
            ((Spinner) rootView.findViewById(R.id.birth_commune))
                    .setFocusable(false);
            ((Spinner) rootView.findViewById(R.id.birth_commune))
                    .setFocusableInTouchMode(false);
			((Spinner) rootView.findViewById(R.id.birth_commune))
					.setClickable(false);
			((EditText) rootView.findViewById(R.id.id_number))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.beneficiary_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.beneficiary_id_number_input_field))
					.setFocusable(false);
			allowSave = false;
			getActivity().invalidateOptionsMenu();
		}

		personPictureFile = Person.getPersonPictureFile(person.getPersonId());

		claimantImageView.setImageBitmap(Person.getPersonPicture(
				rootView.getContext(), person.getPersonId(), 128));
	}

	private void loadGroup(String personId) {
		Person person = Person.getPerson(personId);
		((EditText) rootView.findViewById(R.id.first_name_input_field))
				.setText(person.getFirstName());

		if (person.getDateOfBirth() != null)
			((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
							.format(person.getDateOfBirth()));

		((EditText) rootView.findViewById(R.id.id_number)).setText(person
				.getIdNumber());

		((EditText) rootView.findViewById(R.id.postal_address_input_field))
				.setText(person.getPostalAddress());

		((EditText) rootView.findViewById(R.id.email_address_input_field))
				.setText(person.getEmailAddress());

		((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field))
				.setText(person.getContactPhoneNumber());
		if (person.hasUploadedClaims()) {

			((EditText) rootView.findViewById(R.id.first_name_input_field))
					.setFocusable(false);

			((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.setFocusable(false);

			((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.setOnLongClickListener(null);

			((EditText) rootView.findViewById(R.id.id_number))
					.setFocusable(false);

			((EditText) rootView.findViewById(R.id.postal_address_input_field))
					.setFocusable(false);

			((EditText) rootView.findViewById(R.id.email_address_input_field))
					.setFocusable(false);

			((ImageView) rootView.findViewById(R.id.claimant_picture))
					.setClickable(false);

			((EditText) rootView
					.findViewById(R.id.contact_phone_number_input_field))
					.setFocusable(false);

		}

		personPictureFile = Person.getPersonPictureFile(person.getPersonId());

		claimantImageView.setImageBitmap(Person.getPersonPicture(
				rootView.getContext(), person.getPersonId(), 128));

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				File original = Person.getPersonPictureFile(personActivity.getPersonId());
				File copy = null;
				Log.d(this.getClass().getName(), "Attachment size : " + original.length());
				
				if(original.length() > 800000){

					copy = FileSystemUtilities.reduceJpeg(original);

					if(copy != null){
						
						Log.d(this.getClass().getName(), "Reduced size to : " + copy.length());
						original.delete();
					
						if(copy.renameTo(Person.getPersonPictureFile(personActivity.getPersonId()))){
							Log.d(this.getClass().getName(), "Renamed : " + copy.getName() + " to "
									+ Person.getPersonPictureFile(personActivity.getPersonId()).getName());
						} else {
							Log.e(this.getClass().getName(), "Can't rename : " + copy.getName() + " to "
									+ Person.getPersonPictureFile(personActivity.getPersonId()).getName());
						}
					}else{
						
					}
				}else{
					copy = original;
				}
				
				
				try {
					claimantImageView.setImageBitmap(Person.getPersonPicture(
							rootView.getContext(),
							personActivity.getPersonId(), 128));
				} catch (Exception e) {
					claimantImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.ic_contact_picture));
				}
			}
		}
	}

	private void updateDoB() {

		EditText dateOfBirth = (EditText) getView().findViewById(
				R.id.date_of_birth_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		dateOfBirth.setText(sdf.format(localCalendar.getTime()));
	}

	private void updateIssuanceDate() {

		EditText issuanceDate = (EditText) getView().findViewById(
				R.id.id_issuance_date_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		issuanceDate.setText(sdf.format(localCalendar.getTime()));
	}

	private void updateExpiryDate() {

		EditText expiryDate = (EditText) getView().findViewById(
				R.id.id_expiry_date_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		expiryDate.setText(sdf.format(localCalendar.getTime()));
	}

	private void updateDoE() {

		EditText date = (EditText) getView().findViewById(
				R.id.date_of_establishment_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		date.setText(sdf.format(localCalendar.getTime()));
	}

	public int savePerson() {
		Person person = new Person();
		person.setFirstName(((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString());
		person.setLastName(((EditText) rootView
				.findViewById(R.id.last_name_input_field)).getText().toString());
		person.setOtherName(((EditText) rootView
				.findViewById(R.id.other_name_input_field)).getText().toString());
		person.setFatherName(((EditText) rootView
				.findViewById(R.id.father_name_input_field)).getText().toString());
		person.setMotherName(((EditText) rootView
				.findViewById(R.id.mother_name_input_field)).getText().toString());
		try {
			java.util.Date dob;
			dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
					.parse(((EditText) rootView
							.findViewById(R.id.date_of_birth_input_field))
							.getText().toString());
			person.setDateOfBirth(new Date(dob.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			return 4;
		}

		if(!((EditText) rootView
				.findViewById(R.id.id_issuance_date_input_field))
				.getText().toString().trim().equalsIgnoreCase("")){
			try {
				java.util.Date dob;
				dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
						.parse(((EditText) rootView
								.findViewById(R.id.id_issuance_date_input_field))
								.getText().toString());
				person.setIdIssuanceDate(new Date(dob.getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
				return 9;
			}
		}

		try {
			java.util.Date dob;
			dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
					.parse(((EditText) rootView
							.findViewById(R.id.id_expiry_date_input_field))
							.getText().toString());
			person.setIdExpiryDate(new Date(dob.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			return 10;
		}

		person.setPostalAddress(((EditText) rootView
				.findViewById(R.id.postal_address_input_field)).getText()
				.toString());
		person.setEmailAddress(((EditText) rootView
				.findViewById(R.id.email_address_input_field)).getText()
				.toString());

		person.setContactPhoneNumber(((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field)).getText()
				.toString());

		String idTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_type_spinner)).getSelectedItem();
		person.setIdType(valueKeyMapIdTypes.get(idTypeDispValue));

		Country idIssuanceCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country)).getSelectedItem();
		person.setIdIssuanceCountryCode(idIssuanceCountry.getCode());

		Country birthCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.birth_country)).getSelectedItem();
		person.setBirthCountryCode(birthCountry.getCode());

		Province idIssuanceProvince = (Province) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province)).getSelectedItem();
		person.setIdIssuanceProvinceCode(idIssuanceProvince.getCode());

		Municipality idIssuanceMunicipality = (Municipality) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality)).getSelectedItem();
		person.setIdIssuanceMunicipalityCode(idIssuanceMunicipality.getCode());

		Commune idIssuanceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune)).getSelectedItem();
		person.setIdIssuanceCommuneCode(idIssuanceCommune.getCode());

		Commune residenceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.residence_commune)).getSelectedItem();
		person.setResidenceCommuneCode(residenceCommune.getCode());

		Commune birthCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.birth_commune)).getSelectedItem();
		person.setBirthCommuneCode(birthCommune.getCode());

		person.setIdNumber(((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString());

		String gender = (String) ((Spinner) rootView
				.findViewById(R.id.gender_spinner)).getSelectedItem();
		if (gender.equals(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_feminine)))
			person.setGender("F");
		else
			person.setGender("M");
		// if (((RadioButton) rootView
		// .findViewById(R.id.gender_feminine_input_field)).isChecked())
		// person.setGender("F");
		// if (((RadioButton) rootView
		// .findViewById(R.id.gender_masculine_input_field)).isChecked())
		// person.setGender("M");

		String maritalStatusDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.marital_status_spinner)).getSelectedItem();
		person.setMaritalStatusCode(valueKeyMaritalStatusMap.get(maritalStatusDispValue));

		person.setBeneficiaryName(((EditText) rootView
				.findViewById(R.id.beneficiary_name_input_field)).getText().toString());
		person.setBeneficiaryIdNumber(((EditText) rootView
				.findViewById(R.id.beneficiary_id_number_input_field)).getText().toString());

		person.setPersonType(Person._PHYSICAL);

		if (person.getFirstName() == null
				|| person.getFirstName().trim().equals(""))
			return 2;

		if (person.getLastName() == null
				|| person.getLastName().trim().equals(""))
			return 3;

		if (person.getGender() == null)
			return 5;

		if (person.getFatherName() == null
				|| person.getFatherName().trim().equals(""))
			return 6;

		if (person.getMotherName() == null
				|| person.getMotherName().trim().equals(""))
			return 7;

		if (person.getResidenceCommuneCode() == null
				|| person.getResidenceCommuneCode().trim().equals(""))
			return 8;

		if (person.getPostalAddress() == null
				|| person.getPostalAddress().trim().equals(""))
			return 11;

		if (person.create() == 1) {

			personActivity.setPersonId(person.getPersonId());
			personActivity.setEntityType(person.getPersonType());
			personPictureFile = Person.getPersonPictureFile(person
					.getPersonId());

			return 1;
		}
		return 0;

	}

	public int saveGroup() {
		Person person = new Person();
		person.setFirstName(((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString());
		person.setLastName("");

		java.util.Date doe = null;
		try {

			String date = ((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.getText().toString();
			if (date != null && !date.trim().equals(""))
				doe = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return 4;
		}
		if (doe != null)
			person.setDateOfBirth(new Date(doe.getTime()));

		person.setPostalAddress(((EditText) rootView
				.findViewById(R.id.postal_address_input_field)).getText()
				.toString());
		person.setEmailAddress(((EditText) rootView
				.findViewById(R.id.email_address_input_field)).getText()
				.toString());
		person.setContactPhoneNumber(((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field)).getText()
				.toString());

		person.setIdNumber(((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString());

		person.setPersonType(Person._GROUP);

		if (person.getFirstName() == null
				|| person.getFirstName().trim().equals(""))
			return 2;

		if (person.create() == 1) {

			personActivity.setPersonId(person.getPersonId());
			personActivity.setEntityType(person.getPersonType());
			personPictureFile = Person.getPersonPictureFile(person
					.getPersonId());

			return 1;
		}
		return 0;

	}

	public int updateGroup(PersonActivity personActivity) {

		if (rootView == null)
			rootView = OpenTenureApplication.getPersonsView();
		Person person;

		if (this.personActivity == null)
			this.personActivity = personActivity;

		person = Person.getPerson(this.personActivity.getPersonId());

		person.setFirstName(((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString());
		person.setLastName("");

		java.util.Date doe = null;
		try {

			String date = ((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.getText().toString();
			if (date != null && !date.trim().equals(""))
				doe = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return 4;
		}
		if (doe != null)
			person.setDateOfBirth(new Date(doe.getTime()));

		person.setPostalAddress(((EditText) rootView
				.findViewById(R.id.postal_address_input_field)).getText()
				.toString());
		person.setEmailAddress(((EditText) rootView
				.findViewById(R.id.email_address_input_field)).getText()
				.toString());
		person.setContactPhoneNumber(((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field)).getText()
				.toString());

		person.setIdNumber(((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString());

		person.setPersonType(Person._GROUP);

		if (person.getFirstName() == null
				|| person.getFirstName().trim().equals(""))
			return 2;

		if (person.update() == 1) {

			this.personActivity.setPersonId(person.getPersonId());
			this.personActivity.setEntityType(person.getPersonType());
			personPictureFile = Person.getPersonPictureFile(person
					.getPersonId());

			return 1;
		}
		return 0;

	}

	public int updatePerson(String personId) {

		if (rootView == null)
			rootView = OpenTenureApplication.getPersonsView();

		Person person = Person.getPerson(personId);
		person.setFirstName(((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString());
		person.setLastName(((EditText) rootView
				.findViewById(R.id.last_name_input_field)).getText().toString());
		person.setOtherName(((EditText) rootView
				.findViewById(R.id.other_name_input_field)).getText().toString());
		person.setFatherName(((EditText) rootView
				.findViewById(R.id.father_name_input_field)).getText().toString());
		person.setMotherName(((EditText) rootView
				.findViewById(R.id.mother_name_input_field)).getText().toString());
		try {
			java.util.Date dob;

			dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
					.parse(((EditText) rootView
							.findViewById(R.id.date_of_birth_input_field))
							.getText().toString());
			person.setDateOfBirth(new Date(dob.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			return 4;
		}

		if(!((EditText) rootView
                .findViewById(R.id.id_issuance_date_input_field))
                .getText().toString().trim().equalsIgnoreCase("")){
            try {
                java.util.Date iid= new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(((EditText) rootView
                                .findViewById(R.id.id_issuance_date_input_field))
                                .getText().toString());
                person.setIdIssuanceDate(new Date(iid.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                return 9;
            }
        }

		try {
			java.util.Date ied= new SimpleDateFormat("yyyy-MM-dd", Locale.US)
					.parse(((EditText) rootView
							.findViewById(R.id.id_expiry_date_input_field))
							.getText().toString());
			person.setIdExpiryDate(new Date(ied.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			// dob = new java.util.Date();
			return 10;
		}

		person.setPostalAddress(((EditText) rootView
				.findViewById(R.id.postal_address_input_field)).getText()
				.toString());
		person.setEmailAddress(((EditText) rootView
				.findViewById(R.id.email_address_input_field)).getText()
				.toString());
		person.setContactPhoneNumber(((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field)).getText()
				.toString());
		person.setPersonType(Person._PHYSICAL);

		String idTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_type_spinner)).getSelectedItem();
		person.setIdType(valueKeyMapIdTypes.get(idTypeDispValue));

		Country idIssuanceCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country)).getSelectedItem();
		person.setIdIssuanceCountryCode(idIssuanceCountry.getCode());

		Country birthCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.birth_country)).getSelectedItem();
		person.setBirthCountryCode(birthCountry.getCode());

		Province idIssuanceProvince = (Province) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province)).getSelectedItem();
		person.setIdIssuanceProvinceCode(idIssuanceProvince.getCode());

		Municipality idIssuanceMunicipality = (Municipality) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality)).getSelectedItem();
		person.setIdIssuanceMunicipalityCode(idIssuanceMunicipality.getCode());

		Commune idIssuanceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune)).getSelectedItem();
		person.setIdIssuanceCommuneCode(idIssuanceCommune.getCode());

		Commune birthCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.birth_commune)).getSelectedItem();
		person.setBirthCommuneCode(birthCommune.getCode());

		Commune residenceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.residence_commune)).getSelectedItem();
		person.setResidenceCommuneCode(residenceCommune.getCode());

		person.setIdNumber(((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString());

		person.setBeneficiaryName(((EditText) rootView.findViewById(R.id.beneficiary_name_input_field))
				.getText().toString());

		person.setBeneficiaryIdNumber(((EditText) rootView.findViewById(R.id.beneficiary_id_number_input_field))
				.getText().toString());

		String gender = (String) ((Spinner) rootView
				.findViewById(R.id.gender_spinner)).getSelectedItem();
		if (gender.equals(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_feminine)))
			person.setGender("F");
		else
			person.setGender("M");

		String maritalStatusDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.marital_status_spinner)).getSelectedItem();
		person.setMaritalStatusCode(valueKeyMaritalStatusMap.get(maritalStatusDispValue));

		if (person.getFirstName() == null
				|| person.getFirstName().trim().equals(""))
			return 2;

		if (person.getLastName() == null
				|| person.getLastName().trim().equals(""))
			return 3;

		if (person.getGender() == null)
			return 5;

		if (person.getFatherName() == null
				|| person.getFatherName().trim().equals(""))
			return 6;

		if (person.getMotherName() == null
				|| person.getMotherName().trim().equals(""))
			return 7;

		if (person.getResidenceCommuneCode() == null
				|| person.getResidenceCommuneCode().trim().equals(""))
			return 8;

		if (person.getPostalAddress() == null
				|| person.getPostalAddress().trim().equals(""))
			return 11;

		return person.update();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Toast toast;
		switch (item.getItemId()) {

		case R.id.action_save:

			if (personActivity.getPersonId() == null
					&& !(personActivity.getEntityType() != null && personActivity
							.getEntityType().equalsIgnoreCase("group"))) {

				int saved = savePerson();
				if (saved == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();

					Intent resultIntent = new Intent();

					resultIntent.putExtra(PersonActivity.PERSON_ID_KEY,
							personActivity.getPersonId());
					// Set The Result in Intent
					((PersonActivity) getActivity()).setResult(2, resultIntent);

				} else if (saved == 2) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_first_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 3) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_last_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 4) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_birthdate,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 5) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_gender,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 6) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_father_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 7) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_mother_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 8) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_residence_commune,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 9) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_id_issuance_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 10) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_id_expiry_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 11) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_address,
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast
							.makeText(rootView.getContext(),
									R.string.message_unable_to_save,
									Toast.LENGTH_SHORT);
					toast.show();
				}
			} else if (personActivity.getPersonId() == null
					&& (personActivity.getEntityType() != null && personActivity
							.getEntityType().equalsIgnoreCase("group"))) {

				int saved = saveGroup();
				if (saved == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();

					Intent resultIntent = new Intent();

					resultIntent.putExtra(PersonActivity.PERSON_ID_KEY,
							personActivity.getPersonId());
					// Set The Result in Intent
					((PersonActivity) getActivity()).setResult(2, resultIntent);

				} else {
					toast = Toast
							.makeText(rootView.getContext(),
									R.string.message_unable_to_save,
									Toast.LENGTH_SHORT);
					toast.show();
				}

			} else if (personActivity.getPersonId() != null
					&& (Person.getPerson(personActivity.getPersonId())
							.getPersonType().equalsIgnoreCase("group"))) {

				int saved = updateGroup(null);
				if (saved == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();

					Intent resultIntent = new Intent();

					resultIntent.putExtra(PersonActivity.PERSON_ID_KEY,
							personActivity.getPersonId());
					// Set The Result in Intent
					((PersonActivity) getActivity()).setResult(2, resultIntent);

				} else {
					toast = Toast
							.makeText(rootView.getContext(),
									R.string.message_unable_to_save,
									Toast.LENGTH_SHORT);
					toast.show();
				}

			} else {

				int updated = updatePerson(personActivity.getPersonId());

				if (updated == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();

					Intent resultIntent = new Intent();

					resultIntent.putExtra(PersonActivity.PERSON_ID_KEY,
							personActivity.getPersonId());
					// Set The Result in Intent
					((PersonActivity) getActivity()).setResult(2, resultIntent);

				} else if (updated == 2) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_first_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 3) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_last_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 4) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_birthdate,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 5) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_mandatory_field_gender,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 6) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_father_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 7) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_mother_name,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 8) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_residence_commune,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 9) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_id_issuance_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 10) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_id_expiry_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast
							.makeText(rootView.getContext(),
									R.string.message_unable_to_save,
									Toast.LENGTH_SHORT);
					toast.show();
				}
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean checkChanges(PersonActivity personActivity) {

		String entityType = personActivity.getEntityType();

		if (entityType == null)

			entityType = Person.getPerson(personActivity.getPersonId())
					.getPersonType();

		if (entityType.equalsIgnoreCase(Person._GROUP))
			return checkChangesGroup(personActivity);
		else
			return checkChangesPerson(personActivity);

	}

	public boolean checkChangesGroup(PersonActivity personActivity) {

		View rootView = null;

		boolean changed = false;
		Person person = Person.getPerson(personActivity.getPersonId());
		rootView = OpenTenureApplication.getPersonsView();

		if (person != null) {

			if (person.getPersonType().equalsIgnoreCase(Person._GROUP)) {

				String name = ((EditText) rootView
						.findViewById(R.id.first_name_input_field)).getText()
						.toString();

				if (!person.getFirstName().equals(name))
					changed = true;

				else {

					String postal_address = ((EditText) rootView
							.findViewById(R.id.postal_address_input_field))
							.getText().toString();

					if ((postal_address == null || postal_address
							.equalsIgnoreCase(""))
							&& (person.getPostalAddress() != null && !person
									.getPostalAddress().equals("")))

						changed = true;

					else if ((postal_address != null && !postal_address
							.equalsIgnoreCase(""))
							&& (person.getPostalAddress() == null || person
									.getPostalAddress().equals("")))
						changed = true;

					else if ((postal_address != null && person
							.getPostalAddress() != null)
							&& !postal_address
									.equals(person.getPostalAddress()))
						changed = true;

					else {

						String email = ((EditText) rootView
								.findViewById(R.id.email_address_input_field))
								.getText().toString();

						if ((email == null || email.equals(""))
								&& (person.getEmailAddress() != null && !person
										.getEmailAddress().equals("")))

							changed = true;

						else if ((email != null && !email.equals(""))
								&& (person.getEmailAddress() == null || person
										.getEmailAddress().equals("")))
							changed = true;
						else if ((person.getEmailAddress() != null && email != null)
								&& !person.getEmailAddress().equalsIgnoreCase(
										email))
							changed = true;
						else {
							String numberId = ((EditText) rootView
									.findViewById(R.id.id_number)).getText()
									.toString();

							if ((numberId == null || numberId.equals(""))
									&& (person.getIdNumber() != null && !person
											.getIdNumber().equals("")))

								changed = true;
							else if ((numberId != null && !numberId.equals(""))
									&& (person.getIdNumber() == null || person
											.getIdNumber().equals("")))

								changed = true;

							else if ((numberId != null && person.getIdNumber() != null)
									&& !person.getIdNumber().equals(numberId))
								changed = true;

							else {

								String contact = ((EditText) rootView
										.findViewById(R.id.contact_phone_number_input_field))
										.getText().toString();

								if ((contact == null || contact.equals(""))
										&& (person.getContactPhoneNumber() != null && !person
												.getContactPhoneNumber()
												.equals("")))

									changed = true;

								else if ((contact != null && !contact
										.equals(""))
										&& (person.getContactPhoneNumber() == null || person
												.getContactPhoneNumber()
												.equals("")))
									changed = true;
								else if ((contact != null && person
										.getContactPhoneNumber() != null)
										&& !contact.equals(person
												.getContactPhoneNumber()))
									changed = true;

								else {

									String dateEstablishment = ((EditText) rootView
											.findViewById(R.id.date_of_establishment_input_field))
											.getText().toString();

									if (person.getDateOfBirth() == null
											|| person.getDateOfBirth().equals(
													"")) {

										if (dateEstablishment != null
												&& !dateEstablishment
														.equals(""))
											changed = true;
									} else {
										java.util.Date dob = null;

										if (dateEstablishment != null
												&& !dateEstablishment.trim()
														.equals("")) {

											try {
												dob = new SimpleDateFormat(
														"yyyy-MM-dd", Locale.US)
														.parse(dateEstablishment);

												Date date = new Date(
														dob.getTime());

												if (person.getDateOfBirth()
														.compareTo(date) != 0)
													changed = true;

											} catch (ParseException e) {
												e.printStackTrace();
												dob = null;

											}

										}

									}

								}

							}

						}

					}

				}

			}

		} else {

			String name = ((EditText) rootView
					.findViewById(R.id.first_name_input_field)).getText()
					.toString();
			if (name != null && !name.trim().equals(""))
				changed = true;

			String postal_address = ((EditText) rootView
					.findViewById(R.id.postal_address_input_field)).getText()
					.toString();

			if (postal_address != null && !postal_address.trim().equals(""))
				changed = true;

			String email = ((EditText) rootView
					.findViewById(R.id.email_address_input_field)).getText()
					.toString();
			if (email != null && !email.trim().equals(""))
				changed = true;

			String numberId = ((EditText) rootView.findViewById(R.id.id_number))
					.getText().toString();

			if (numberId != null && !numberId.trim().equals(""))
				changed = true;

			String contact = ((EditText) rootView
					.findViewById(R.id.contact_phone_number_input_field))
					.getText().toString();
			if (contact != null && !contact.trim().equals(""))
				changed = true;

			String dateEstablishment = ((EditText) rootView
					.findViewById(R.id.date_of_establishment_input_field))
					.getText().toString();
			if (dateEstablishment != null
					&& !dateEstablishment.trim().equals(""))
				changed = true;

		}

		if (changed) {

			AlertDialog.Builder saveChangesDialog = new AlertDialog.Builder(
					rootView.getContext());
			saveChangesDialog.setTitle(R.string.title_save_person_dialog);
			String dialogMessage = OpenTenureApplication.getContext()
					.getString(R.string.message_discard_changes);

			saveChangesDialog.setMessage(dialogMessage);

			saveChangesDialog.setPositiveButton(R.string.confirm,
					new ConfirmExit(personActivity));

			saveChangesDialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			saveChangesDialog.show();

		}

		return false;

	}

	public boolean isPersonChanged(Person person, View rootView){

		String name = ((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString();

		if (!person.getFirstName().equals(name))
			return true;

		String lastName = ((EditText) rootView
				.findViewById(R.id.last_name_input_field)).getText()
				.toString();

		if (!person.getLastName().equals(lastName))
			return true;

		String otherName = ((EditText) rootView
				.findViewById(R.id.other_name_input_field)).getText()
				.toString();

		if (person.getOtherName()!= null && !person.getOtherName().equals(otherName))
			return true;

		String motherName = ((EditText) rootView
				.findViewById(R.id.mother_name_input_field)).getText()
				.toString();

		if (person.getMotherName() != null && !person.getMotherName().equals(motherName))
			return true;

		String beneficiaryName = ((EditText) rootView
				.findViewById(R.id.beneficiary_name_input_field)).getText()
				.toString();

		if (person.getBeneficiaryName() != null && !person.getBeneficiaryName().equals(beneficiaryName))
			return true;

		String beneficiaryIdNumber = ((EditText) rootView
				.findViewById(R.id.beneficiary_id_number_input_field)).getText()
				.toString();

		if (person.getBeneficiaryIdNumber() != null && !person.getBeneficiaryIdNumber().equals(beneficiaryIdNumber))
			return true;

		String fatherName = ((EditText) rootView
				.findViewById(R.id.father_name_input_field)).getText()
				.toString();

		if (person.getFatherName()!= null && !person.getFatherName().equals(fatherName))
			return true;

		String postal_address = ((EditText) rootView
				.findViewById(R.id.postal_address_input_field))
				.getText().toString();

		if ((postal_address == null || postal_address
				.equalsIgnoreCase(""))
				&& (person.getPostalAddress() != null && !person
				.getPostalAddress().equals("")))
			return true;

		else if ((postal_address != null && !postal_address
				.equalsIgnoreCase(""))
				&& (person.getPostalAddress() == null || person
				.getPostalAddress().equals("")))
			return true;

		else if (!postal_address.equals(person.getPostalAddress()))
			return true;

		String email = ((EditText) rootView
				.findViewById(R.id.email_address_input_field))
				.getText().toString();

		if ((email == null || email.equals(""))
				&& (person.getEmailAddress() != null && !person
				.getEmailAddress().equals("")))

			return true;

		else if ((email != null && !email.equals(""))
				&& (person.getEmailAddress() == null || person
				.getEmailAddress().equals("")))
			return true;
		else if ((email != null && person.getEmailAddress() != null)
				&& !person.getEmailAddress().equalsIgnoreCase(
				email))
			return true;

		String numberId = ((EditText) rootView
				.findViewById(R.id.id_number)).getText()
				.toString();

		if ((numberId == null || numberId.equals(""))
				&& (person.getIdNumber() != null && !person
				.getIdNumber().equals("")))

			return true;
		else if ((numberId != null && !numberId.equals(""))
				&& (person.getIdNumber() == null || person
				.getIdNumber().equals("")))

			return true;

		else if ((person.getIdNumber() != null && numberId != null)
				&& !person.getIdNumber().equals(numberId))
			return true;

		String idType = (String) ((Spinner) rootView
				.findViewById(R.id.id_type_spinner))
				.getSelectedItem();


		if ((idType != null && person.getIdType() != null)
				&& !person.getIdType().trim().equals(
				valueKeyMapIdTypes
						.get(idType).trim()))
			return true;

		Country idIssuanceCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country))
				.getSelectedItem();

		if ((idIssuanceCountry.getCode() != null && person.getIdIssuanceCountryCode() != null)
				&& !person.getIdIssuanceCountryCode().trim().equals(
				idIssuanceCountry.getCode().trim()))
			return true;

		Country birthCountry = (Country) ((Spinner) rootView
				.findViewById(R.id.birth_country))
				.getSelectedItem();

		if ((birthCountry.getCode() != null && person.getBirthCountryCode() != null)
				&& !person.getBirthCountryCode().trim().equals(
				birthCountry.getCode().trim()))
			return true;

		Province idIssuanceProvince = (Province) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province))
				.getSelectedItem();

		if ((idIssuanceProvince.getCode() != null && person.getIdIssuanceProvinceCode() != null)
				&& !person.getIdIssuanceProvinceCode().trim().equals(
				idIssuanceProvince.getCode().trim()))
			return true;

		Municipality idIssuanceMunicipality = (Municipality) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality))
				.getSelectedItem();

		if ((idIssuanceMunicipality != null && person.getIdIssuanceMunicipalityCode() != null)
				&& !person.getIdIssuanceMunicipalityCode().trim().equals(
				idIssuanceMunicipality.getCode().trim()))
			return true;

		Commune idIssuanceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune))
				.getSelectedItem();

		if ((idIssuanceCommune.getCode() != null && person.getIdIssuanceCommuneCode() != null)
				&& !person.getIdIssuanceCommuneCode().trim().equals(
				idIssuanceCommune.getCode().trim()))
			return true;

		Commune residenceCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.residence_commune))
				.getSelectedItem();

		if ((residenceCommune.getCode() != null && person.getResidenceCommuneCode() != null)
				&& !person.getResidenceCommuneCode().trim().equals(
				residenceCommune.getCode().trim()))
			return true;

		Commune birthCommune = (Commune) ((Spinner) rootView
				.findViewById(R.id.birth_commune))
				.getSelectedItem();

		if ((birthCommune.getCode() != null && person.getBirthCommuneCode() != null)
				&& !person.getBirthCommuneCode().trim().equals(
				birthCommune.getCode().trim()))
			return true;

		String contact = ((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field))
				.getText().toString();

		if ((contact == null || contact.equals(""))
				&& (person.getContactPhoneNumber() != null && !person
				.getContactPhoneNumber()
				.equals("")))

			return true;

		else if ((contact != null && !contact
				.equals(""))
				&& (person.getContactPhoneNumber() == null || person
				.getContactPhoneNumber()
				.equals("")))
			return true;
		else if ((contact != null && person
				.getContactPhoneNumber() != null)
				&& !contact.equals(person
				.getContactPhoneNumber()))
			return true;

		String dateOfBirth = ((EditText) rootView
				.findViewById(R.id.date_of_birth_input_field))
				.getText().toString();

		if (person.getDateOfBirth() == null ^ dateOfBirth.trim().equalsIgnoreCase("")) {

			Log.d(this.getClass().getName(), "Date of birth has changed");
			return true;

		}
		if (!dateOfBirth.trim().equalsIgnoreCase("")) {

			try {
				java.util.Date dob = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.US)
						.parse(dateOfBirth);

				Date date = new Date(dob.getTime());

				if (person.getDateOfBirth().compareTo(date) != 0) {
					Log.d(this.getClass().getName(), "Date of birth has changed");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}
		}

		String idIssuanceDate = ((EditText) rootView
				.findViewById(R.id.id_issuance_date_input_field))
				.getText().toString();

		if (person.getIdIssuanceDate() == null ^ idIssuanceDate.trim().equalsIgnoreCase("")) {

			Log.d(this.getClass().getName(), "ID issuance date has changed");
			return true;

		}
		if (!idIssuanceDate.trim().equalsIgnoreCase("")) {

			try {
				java.util.Date iid = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.US)
						.parse(idIssuanceDate);

				Date date = new Date(iid.getTime());

				if (person.getIdIssuanceDate().compareTo(date) != 0) {
					Log.d(this.getClass().getName(), "ID issuance date has changed");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}
		}

		String idExpiryDate = ((EditText) rootView
				.findViewById(R.id.id_expiry_date_input_field))
				.getText().toString();

		if (person.getIdExpiryDate() == null ^ idExpiryDate.trim().equalsIgnoreCase("")) {

			Log.d(this.getClass().getName(), "ID expiry date has changed");
			return true;

		}
		if (!idExpiryDate.trim().equalsIgnoreCase("")) {

			try {
				java.util.Date ied = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.US)
						.parse(idExpiryDate);

				Date date = new Date(ied.getTime());

				if (person.getIdExpiryDate().compareTo(date) != 0) {
					Log.d(this.getClass().getName(), "ID expiry date has changed");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}
		}
		return false;
	}

	public boolean hasFragmentValues(View rootView){
		String name = ((EditText) rootView
				.findViewById(R.id.first_name_input_field)).getText()
				.toString();
		if (name != null && !name.trim().equals(""))
			return true;

		String lastName = ((EditText) rootView
				.findViewById(R.id.last_name_input_field)).getText()
				.toString();
		if (lastName != null && !lastName.trim().equals(""))
			return true;

		String otherName = ((EditText) rootView
				.findViewById(R.id.other_name_input_field)).getText()
				.toString();
		if (otherName != null && !otherName.trim().equals(""))
			return true;

		String fatherName = ((EditText) rootView
				.findViewById(R.id.father_name_input_field)).getText()
				.toString();
		if (fatherName != null && !fatherName.trim().equals(""))
			return true;

		String motherName = ((EditText) rootView
				.findViewById(R.id.mother_name_input_field)).getText()
				.toString();
		if (motherName != null && !motherName.trim().equals(""))
			return true;

		String postal_address = ((EditText) rootView
				.findViewById(R.id.postal_address_input_field)).getText()
				.toString();

		if (postal_address != null && !postal_address.trim().equals(""))
			return true;

		String email = ((EditText) rootView
				.findViewById(R.id.email_address_input_field)).getText()
				.toString();
		if (email != null && !email.trim().equals(""))
			return true;

		String numberId = ((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString();

		if (numberId != null && !numberId.trim().equals(""))
			return true;

		String contact = ((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field))
				.getText().toString();
		if (contact != null && !contact.trim().equals(""))
			return true;

		String dateOfBirth = ((EditText) rootView
				.findViewById(R.id.date_of_birth_input_field)).getText()
				.toString();
		if (dateOfBirth != null && !dateOfBirth.trim().equals(""))
			return true;

		String idExpiryDate = ((EditText) rootView
				.findViewById(R.id.id_expiry_date_input_field)).getText()
				.toString();
		if (idExpiryDate != null && !idExpiryDate.trim().equals(""))
			return true;

		String idIssuanceDate = ((EditText) rootView
				.findViewById(R.id.id_issuance_date_input_field)).getText()
				.toString();
		if (idIssuanceDate != null && !idIssuanceDate.trim().equals(""))
			return true;

		return false;
	}

	public boolean checkChangesPerson(PersonActivity personActivity) {

		if (valueKeyMapIdTypes == null)
			valueKeyMapIdTypes = new IdType()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);

		if (countriesList == null)
			countriesList = new ArrayList<Country>(new TreeSet<Country>(onlyActive ? Country.getActiveCountries():Country.getCountries()));

		if (provincesList == null)
			provincesList = new ArrayList<Province>(new TreeSet<Province>(onlyActive ? Province.getActiveProvinces():Province.getProvinces()));

		if (municipalitiesList == null)
			municipalitiesList = new ArrayList<Municipality>(new TreeSet<Municipality>(onlyActive ? Municipality.getActiveMunicipalities():Municipality.getMunicipalities()));

		if (communesList == null)
			communesList = new ArrayList<Commune>(new TreeSet<Commune>(onlyActive ? Commune.getActiveCommunes():Commune.getCommunes()));


		Person person = Person.getPerson(personActivity.getPersonId());
		View rootView = OpenTenureApplication.getPersonsView();

		if ((person != null && isPersonChanged(person, rootView)) || (person == null && hasFragmentValues(rootView))) {

			AlertDialog.Builder saveChangesDialog = new AlertDialog.Builder(
					rootView.getContext());
			saveChangesDialog.setTitle(R.string.title_save_person_dialog);
			String dialogMessage = OpenTenureApplication.getContext()
					.getString(R.string.message_discard_changes);

			saveChangesDialog.setMessage(dialogMessage);

			saveChangesDialog.setPositiveButton(R.string.confirm,
					new ConfirmExit(personActivity));

			saveChangesDialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});

			saveChangesDialog.show();

		}

		return false;

	}
	private int countryIndex(String countryCode){
		int i = 0;
		for(Country country:countriesList){
			if(country.getCode().trim().equalsIgnoreCase(countryCode.trim())){
				return i;
			}else{
				i++;
			}
		}
		return -1;
	}
	private int provinceIndex(String provinceCode){
		int i = 0;
		for(Province province:provincesList){
			if(province.getCode().trim().equalsIgnoreCase(provinceCode.trim())){
				return i;
			}else{
				i++;
			}
		}
		return -1;
	}
	private int municipalityIndex(String municipalityCode){
		int i = 0;
		for(Municipality municipality:municipalitiesList){
			if(municipality.getCode().trim().equalsIgnoreCase(municipalityCode.trim())){
				return i;
			}else{
				i++;
			}
		}
		return -1;
	}
	private List<Province> filterProvincesByCountry(List<Province> provinces, String countryCode){
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
	private List<Commune> filterCommunesByCountry(List<Commune> communes, String countryCode){
		List<Commune> filteredCommunes = new ArrayList<Commune>();
		for(Commune commune:communes){
			if(commune.getCountryCode().equalsIgnoreCase(countryCode)){
				filteredCommunes.add(commune);
			}
		}
		if(filteredCommunes.size() <= 0){
			// To account for countries without communes
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
	private List<Commune> filterCommunesByMunicipality(List<Commune> communes, String municipalityCode){
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
	private List<Municipality> filterMunicipalitiesByProvince(List<Municipality> municipalities, String provinceCode){
		List<Municipality> filteredMunicipalities = new ArrayList<Municipality>();
		for(Municipality municipality:municipalities){
			if(municipality.getProvinceCode().equalsIgnoreCase(provinceCode)){
				filteredMunicipalities.add(municipality);
			}
		}
		if(filteredMunicipalities.size() <= 0){
			// To account for provinces without municipalities
			Municipality municipality = new Municipality();
			municipality.setCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setDisplayValue(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			municipality.setProvinceCode(OpenTenureApplication.getActivity().getResources().getString(R.string.na));
			filteredMunicipalities.add(municipality);
			return filteredMunicipalities;

		}
		return filteredMunicipalities;
	}
}