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

import com.ipaulpro.afilechooser.utils.FileUtils;

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
	private Map<String, String> keyValueMapIdTypes;
	private Map<String, String> valueKeyMapIdTypes;
	private Map<String, String> keyValueCountryMap;
	private Map<String, String> valueKeyCountryMap;
	private Map<String, String> keyValueProvinceMap;
	private Map<String, String> valueKeyProvinceMap;
	private Map<String, String> keyValueMunicipalityMap;
	private Map<String, String> valueKeyMunicipalityMap;
	private Map<String, String> keyValueCommuneMap;
	private Map<String, String> valueKeyCommuneMap;
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
		Spinner spinnerIdIssuanceCountry = (Spinner) rootView
				.findViewById(R.id.id_issuance_country);
		Spinner spinnerIdIssuanceProvince = (Spinner) rootView
				.findViewById(R.id.id_issuance_province);
		Spinner spinnerIdIssuanceMunicipality = (Spinner) rootView
				.findViewById(R.id.id_issuance_municipality);
		Spinner spinnerIdIssuanceCommune = (Spinner) rootView
				.findViewById(R.id.id_issuance_commune);
		Spinner spinnerResidenceCommune = (Spinner) rootView
				.findViewById(R.id.residence_commune);

		IdType it = new IdType();
		Country co = new Country();
		Province pr = new Province();
		Municipality mu = new Municipality();
		Commune com = new Commune();

		SortedSet<String> keys;

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
				idTypelist) {
		};
		spinnerIT.setAdapter(dataAdapterIT);

		/* Mapping country localization */
		keyValueCountryMap = co.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyCountryMap = co.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> countrylist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueCountryMap.keySet());
		for (String key : keys) {
			String value = keyValueCountryMap.get(key);
			countrylist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterCO = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				countrylist) {
		};
		spinnerIdIssuanceCountry.setAdapter(dataAdapterCO);

		/* Mapping province localization */
		keyValueProvinceMap = pr.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyProvinceMap = pr.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> provincelist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueProvinceMap.keySet());
		for (String key : keys) {
			String value = keyValueProvinceMap.get(key);
			provincelist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterPR = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				provincelist) {
		};
		spinnerIdIssuanceProvince.setAdapter(dataAdapterPR);

		/* Mapping municipality localization */
		keyValueMunicipalityMap = mu.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyMunicipalityMap = mu.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> municipalitylist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueMunicipalityMap.keySet());
		for (String key : keys) {
			String value = keyValueMunicipalityMap.get(key);
			municipalitylist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterMU = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				municipalitylist) {
		};
		spinnerIdIssuanceMunicipality.setAdapter(dataAdapterMU);

		/* Mapping commune localization */
		keyValueCommuneMap = com.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);
		valueKeyCommuneMap = com.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),onlyActive);

		List<String> communelist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueCommuneMap.keySet());
		for (String key : keys) {
			String value = keyValueCommuneMap.get(key);
			communelist.add(value);
			// do something
		}

		ArrayAdapter<String> dataAdapterCOM = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				communelist) {
		};
		spinnerIdIssuanceCommune.setAdapter(dataAdapterCOM);
		spinnerResidenceCommune.setAdapter(dataAdapterCOM);

		List<String> genderList = new ArrayList<String>();

		genderList.add(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_masculine));
		genderList.add(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_feminine));

		ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				genderList) {
		};

		// dataAdapterIT.setDropDownViewResource(R.layout.my_spinner);

		spinnerGender.setAdapter(dataAdapterGender);

	}

	private void load(String personId) {
		
		Person person = Person.getPerson(personId);
		((EditText) rootView.findViewById(R.id.first_name_input_field))
				.setText(person.getFirstName());
		((EditText) rootView.findViewById(R.id.last_name_input_field))
				.setText(person.getLastName());
		((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
				.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
						.format(person.getDateOfBirth()));
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
				.setSelection(new Country().getIndexByCodeType(person
						.getIdIssuanceCountryCode(),onlyActive));
		((Spinner) rootView.findViewById(R.id.id_issuance_province))
				.setSelection(new Province().getIndexByCodeType(person
						.getIdIssuanceProvinceCode(),onlyActive));
		((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
				.setSelection(new Municipality().getIndexByCodeType(person
						.getIdIssuanceMunicipalityCode(),onlyActive));
		((Spinner) rootView.findViewById(R.id.id_issuance_commune))
				.setSelection(new Commune().getIndexByCodeType(person
						.getIdIssuanceCommuneCode(),onlyActive));
		((Spinner) rootView.findViewById(R.id.residence_commune))
				.setSelection(new Commune().getIndexByCodeType(person
						.getResidenceCommuneCode(),onlyActive));

		if (person.getGender().equals("M")) {
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setSelection(0);
		} else
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setSelection(1);

		((EditText) rootView.findViewById(R.id.id_number)).setText(person
				.getIdNumber());

		if (person.hasUploadedClaims()) {
			((EditText) rootView.findViewById(R.id.first_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.last_name_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
					.setFocusable(false);
			((EditText) rootView.findViewById(R.id.date_of_birth_input_field))
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
					.setClickable(false);
			((ImageView) rootView.findViewById(R.id.claimant_picture))
					.setClickable(false);

			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.gender_spinner))
					.setClickable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_country))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_country))
					.setClickable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_province))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_province))
					.setClickable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_municipality))
					.setClickable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_commune))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.id_issuance_commune))
					.setClickable(false);
			((Spinner) rootView.findViewById(R.id.residence_commune))
					.setFocusable(false);
			((Spinner) rootView.findViewById(R.id.residence_commune))
					.setClickable(false);
			((EditText) rootView.findViewById(R.id.id_number))
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
		person.setContactPhoneNumber(((EditText) rootView
				.findViewById(R.id.contact_phone_number_input_field)).getText()
				.toString());

		String idTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_type_spinner)).getSelectedItem();
		person.setIdType(valueKeyMapIdTypes.get(idTypeDispValue));

		String idIssuanceCountryDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country)).getSelectedItem();
		person.setIdIssuanceCountryCode(valueKeyCountryMap.get(idIssuanceCountryDispValue));

		String idIssuanceProvinceDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province)).getSelectedItem();
		person.setIdIssuanceProvinceCode(valueKeyProvinceMap.get(idIssuanceProvinceDispValue));

		String idIssuanceMunicipalityDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality)).getSelectedItem();
		person.setIdIssuanceMunicipalityCode(valueKeyMunicipalityMap.get(idIssuanceMunicipalityDispValue));

		String idIssuanceCommuneDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune)).getSelectedItem();
		person.setIdIssuanceCommuneCode(valueKeyCommuneMap.get(idIssuanceCommuneDispValue));

		String residenceCommuneDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.residence_commune)).getSelectedItem();
		person.setResidenceCommuneCode(valueKeyCommuneMap.get(residenceCommuneDispValue));

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
		// person = new Person();
		// person.setPersonId(personActivity.getPersonId());
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
			// dob = new java.util.Date();
			return 4;
		}

		try {
			java.util.Date iid= new SimpleDateFormat("yyyy-MM-dd", Locale.US)
					.parse(((EditText) rootView
							.findViewById(R.id.id_issuance_date_input_field))
							.getText().toString());
			person.setIdIssuanceDate(new Date(iid.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
			// dob = new java.util.Date();
			return 9;
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

		String idIssuanceCountryDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country)).getSelectedItem();
		person.setIdIssuanceCountryCode(valueKeyCountryMap.get(idIssuanceCountryDispValue));

		String idIssuanceProvinceDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province)).getSelectedItem();
		person.setIdType(valueKeyProvinceMap.get(idIssuanceProvinceDispValue));

		String idIssuanceMunicipalityDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality)).getSelectedItem();
		person.setIdIssuanceMunicipalityCode(valueKeyMunicipalityMap.get(idIssuanceMunicipalityDispValue));

		String idIssuanceCommuneDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune)).getSelectedItem();
		person.setIdIssuanceCommuneCode(valueKeyCommuneMap.get(idIssuanceCommuneDispValue));

		String residenceCommuneDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.residence_commune)).getSelectedItem();
		person.setResidenceCommuneCode(valueKeyCommuneMap.get(residenceCommuneDispValue));

		person.setIdNumber(((EditText) rootView.findViewById(R.id.id_number))
				.getText().toString());

		String gender = (String) ((Spinner) rootView
				.findViewById(R.id.gender_spinner)).getSelectedItem();
		if (gender.equals(OpenTenureApplication.getContext().getResources()
				.getString(R.string.gender_feminine)))
			person.setGender("F");
		else
			person.setGender("M");

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
							R.string.message_unable_to_save_invalid_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (saved == 10) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_date_format,
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
							R.string.message_unable_to_save_invalid_date_format,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 10) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_invalid_date_format,
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

		String idIssuanceCountryCode = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_country))
				.getSelectedItem();

		if ((idIssuanceCountryCode != null && person.getIdIssuanceCountryCode() != null)
				&& !person.getIdIssuanceCountryCode().trim().equals(
				valueKeyCountryMap
						.get(idIssuanceCountryCode).trim()))
			return true;

		String idIssuanceProvinceCode = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_province))
				.getSelectedItem();

		if ((idIssuanceProvinceCode != null && person.getIdIssuanceProvinceCode() != null)
				&& !person.getIdIssuanceProvinceCode().trim().equals(
				valueKeyProvinceMap
						.get(idIssuanceProvinceCode).trim()))
			return true;

		String idIssuanceMunicipalityCode = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_municipality))
				.getSelectedItem();

		if ((idIssuanceMunicipalityCode != null && person.getIdIssuanceMunicipalityCode() != null)
				&& !person.getIdIssuanceMunicipalityCode().trim().equals(
				valueKeyMunicipalityMap
						.get(idIssuanceMunicipalityCode).trim()))
			return true;

		String idIssuanceCommuneCode = (String) ((Spinner) rootView
				.findViewById(R.id.id_issuance_commune))
				.getSelectedItem();

		if ((idIssuanceCommuneCode != null && person.getIdIssuanceCommuneCode() != null)
				&& !person.getIdIssuanceCommuneCode().trim().equals(
				valueKeyCommuneMap
						.get(idIssuanceCommuneCode).trim()))
			return true;

		String residenceCommuneCode = (String) ((Spinner) rootView
				.findViewById(R.id.residence_commune))
				.getSelectedItem();

		if ((residenceCommuneCode != null && person.getResidenceCommuneCode() != null)
				&& !person.getResidenceCommuneCode().trim().equals(
				valueKeyCommuneMap
						.get(residenceCommuneCode).trim()))
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

		if (person.getDateOfBirth() == null
				|| person.getDateOfBirth()
				.equals("")) {

			if (dateOfBirth != null
					&& !dateOfBirth.equals(""))
				return true;
		} else {
			java.util.Date dob = null;

			if (dateOfBirth != null
					&& !dateOfBirth.trim()
					.equals("")) {

				try {
					dob = new SimpleDateFormat(
							"yyyy-MM-dd",
							Locale.US)
							.parse(dateOfBirth);

					Date date = new Date(
							dob.getTime());

					if (person.getDateOfBirth()
							.compareTo(date) != 0)
						return true;

				} catch (ParseException e) {
					e.printStackTrace();
					dob = null;

				}

			}

		}
		String idIssuanceDate = ((EditText) rootView
				.findViewById(R.id.id_issuance_date_input_field))
				.getText().toString();

		if (person.getIdIssuanceDate() == null) {

			if (!idIssuanceDate.equals(""))
				return true;
		} else {
			java.util.Date iid = null;

			if (!idIssuanceDate.trim().equals("")) {

				try {
					iid = new SimpleDateFormat(
							"yyyy-MM-dd",
							Locale.US)
							.parse(idIssuanceDate);

					Date date = new Date(
							iid.getTime());

					if (person.getIdIssuanceDate()
							.compareTo(date) != 0)
						return true;

				} catch (ParseException e) {
					e.printStackTrace();
					iid = null;
				}
			}
		}

		String idExpiryDate = ((EditText) rootView
				.findViewById(R.id.id_expiry_date_input_field))
				.getText().toString();

		if (person.getIdExpiryDate() == null) {

			if (!idExpiryDate.equals(""))
				return true;
		} else {
			java.util.Date ied = null;

			if (!idExpiryDate.trim().equals("")) {

				try {
					ied = new SimpleDateFormat(
							"yyyy-MM-dd",
							Locale.US)
							.parse(idExpiryDate);

					Date date = new Date(
							ied.getTime());

					if (person.getIdExpiryDate()
							.compareTo(date) != 0)
						return true;

				} catch (ParseException e) {
					e.printStackTrace();
					ied = null;
				}
			}
		}
		return false;
	}

	public boolean personHasValues(Person person, View rootView){
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

		return false;
	}

	public boolean checkChangesPerson(PersonActivity personActivity) {

		if (valueKeyMapIdTypes == null)
			valueKeyMapIdTypes = new IdType()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);

		if (valueKeyCountryMap == null)
			valueKeyCountryMap = new Country()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);

		if (valueKeyProvinceMap == null)
			valueKeyProvinceMap = new Province()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);

		if (valueKeyMunicipalityMap == null)
			valueKeyMunicipalityMap = new Municipality()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);

		if (valueKeyCommuneMap == null)
			valueKeyCommuneMap = new Commune()
					.getValueKeyMap(OpenTenureApplication.getInstance()
							.getLocalization(),onlyActive);


		Person person = Person.getPerson(personActivity.getPersonId());
		View rootView = OpenTenureApplication.getPersonsView();

		if ((person != null && isPersonChanged(person, rootView)) || personHasValues(person, rootView)) {

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
}