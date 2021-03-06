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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fao.sola.clients.android.opentenure.button.listener.SaveDetailsListener;
import org.fao.sola.clients.android.opentenure.button.listener.SaveDetailsNegativeListener;
import org.fao.sola.clients.android.opentenure.filesystem.FileSystemUtilities;
import org.fao.sola.clients.android.opentenure.form.FieldConstraint;
import org.fao.sola.clients.android.opentenure.form.FormPayload;
import org.fao.sola.clients.android.opentenure.form.FormTemplate;
import org.fao.sola.clients.android.opentenure.maps.EditablePropertyBoundary;
import org.fao.sola.clients.android.opentenure.model.Attachment;
import org.fao.sola.clients.android.opentenure.model.Claim;
import org.fao.sola.clients.android.opentenure.model.ClaimStatus;
import org.fao.sola.clients.android.opentenure.model.ClaimType;
import org.fao.sola.clients.android.opentenure.model.Commune;
import org.fao.sola.clients.android.opentenure.model.Country;
import org.fao.sola.clients.android.opentenure.model.HoleVertex;
import org.fao.sola.clients.android.opentenure.model.LandProject;
import org.fao.sola.clients.android.opentenure.model.LandUse;
import org.fao.sola.clients.android.opentenure.model.Municipality;
import org.fao.sola.clients.android.opentenure.model.Owner;
import org.fao.sola.clients.android.opentenure.model.Person;
import org.fao.sola.clients.android.opentenure.model.Province;
import org.fao.sola.clients.android.opentenure.model.ShareProperty;
import org.fao.sola.clients.android.opentenure.model.Vertex;
import org.fao.sola.clients.android.opentenure.print.PDFClaimExporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ClaimDetailsFragment extends Fragment {

	View rootView;
	private ClaimDispatcher claimActivity;
	private ModeDispatcher modeActivity;
	private FormDispatcher formDispatcher;
	private ClaimListener claimListener;
	private Map<String, String> keyValueMapLandUse;
	private Map<String, String> valueKeyMapLandUse;
	private Map<String, String> keyValueClaimTypesMap;
	private Map<String, String> valueKeyClaimTypesMap;
	private List<Country> countriesList;
	private List<Province> provincesList;
	private List<Municipality> municipalitiesList;
	private List<Commune> communesList;
	private Map<String, String> keyValueLandProjectsMap;
	private Map<String, String> valueKeyLandProjectsMap;
	private boolean challengedJustLoaded = false;
	private final Calendar localCalendar = Calendar.getInstance();

	private static final int PERSON_RESULT = 100;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception

		try {
			claimActivity = (ClaimDispatcher) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ClaimDispatcher");
		}
		try {
			modeActivity = (ModeDispatcher) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ModeDispatcher");
		}
		try {
			claimListener = (ClaimListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ClaimListener");
		}
		try {
			formDispatcher = (FormDispatcher) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FormDispatcher");
		}
	}

	public ClaimDetailsFragment() {
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		super.onPrepareOptionsMenu(menu);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();

		inflater.inflate(R.menu.claim_details, menu);

		super.onCreateOptionsMenu(menu, inflater);

		Claim claim = Claim.getClaim(claimActivity.getClaimId());
		if (claim != null && !claim.isModifiable()) {
			menu.removeItem(R.id.action_save);
		}

		setHasOptionsMenu(true);
		// setRetainInstance(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null) { // No selection has been done

			switch (requestCode) {
			case PERSON_RESULT:
				String personId = data
						.getStringExtra(PersonActivity.PERSON_ID_KEY);
				Log.d(this.getClass().getName(), "Created claimant with id: " + personId);

				Person claimant = Person.getPerson(personId);
				loadClaimant(claimant);
				break;
			case SelectClaimActivity.SELECT_CLAIM_ACTIVITY_RESULT:
				String claimId = data
						.getStringExtra(ClaimActivity.CLAIM_ID_KEY);
				Claim challengedClaim = Claim.getClaim(claimId);

				loadChallengedClaim(challengedClaim);
				challengedJustLoaded = true;
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		OpenTenureApplication.setClaimId(claimActivity.getClaimId());
		OpenTenureApplication.setDetailsFragment(this);

		rootView = inflater.inflate(R.layout.fragment_claim_details, container,
				false);
		setHasOptionsMenu(true);

		// setRetainInstance(true);
		InputMethodManager imm = (InputMethodManager) rootView.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

		Claim claim = Claim.getClaim(claimActivity.getClaimId());
		preload(claim);

		load(claim);

		ProgressBar bar = (ProgressBar) rootView
				.findViewById(R.id.progress_bar);
		TextView status = (TextView) rootView.findViewById(R.id.claim_status);

		if (claim != null) {

			if (!claim.getStatus().equals(ClaimStatus._UPLOADING)
					&& !claim.getStatus()
							.equals(ClaimStatus._UPDATE_INCOMPLETE)
					&& !claim.getStatus()
							.equals(ClaimStatus._UPLOAD_INCOMPLETE)
					&& !claim.getStatus().equals(ClaimStatus._UPDATING)) {
				bar.setVisibility(View.GONE);
				status.setVisibility(View.GONE);

			} else {

				status = (TextView) rootView.findViewById(R.id.claim_status);

				int progress = FileSystemUtilities.getUploadProgress(
						claim.getClaimId(), claim.getStatus());

				// Setting the update value in the progress bar
				bar.setVisibility(View.VISIBLE);
				bar.setProgress(progress);
				status.setVisibility(View.VISIBLE);
				status.setText(claim.getStatus() + " " + progress + " %");

			}
		}

		String claimantId = ((TextView) rootView.findViewById(R.id.claimant_id))
				.getText().toString();

		if (OpenTenureApplication.getInstance().getLocalization()
				.startsWith("ar")) {
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setTextDirection(View.TEXT_DIRECTION_LOCALE);
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
		}
		if (claimantId != null && !claimantId.trim().equals(""))
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setVisibility(View.VISIBLE);

		((View) rootView.findViewById(R.id.claimant_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ////////

						// Intent intent = new Intent(rootView.getContext(),
						// SelectPersonActivity.class);
						//
						// // SOLA DB cannot store the same person twice
						//
						// ArrayList<String> idsWithSharesOrClaims = Person
						// .getIdsWithSharesOrClaims();
						//
						// intent.putStringArrayListExtra(
						// SelectPersonActivity.EXCLUDE_PERSON_IDS_KEY,
						// idsWithSharesOrClaims);
						//
						// startActivityForResult(
						// intent,
						// SelectPersonActivity.SELECT_PERSON_ACTIVITY_RESULT);

						String claimantId = ((TextView) rootView
								.findViewById(R.id.claimant_id)).getText()
								.toString();

						if (claimantId != null && !claimantId.trim().equals("")) {

							Intent intent = new Intent(rootView.getContext(),
									PersonActivity.class);
							intent.putExtra(PersonActivity.PERSON_ID_KEY,
									((TextView) rootView
											.findViewById(R.id.claimant_id))
											.getText());
							intent.putExtra(PersonActivity.MODE_KEY,
									modeActivity.getMode().toString());
							startActivityForResult(intent, PERSON_RESULT);

						} else {

							AlertDialog.Builder dialog = new AlertDialog.Builder(
									rootView.getContext());

							dialog.setTitle(R.string.new_entity);
							dialog.setMessage(R.string.message_entity_type);

							dialog.setPositiveButton(R.string.person,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(rootView
													.getContext(),
													PersonActivity.class);
											intent.putExtra(
													PersonActivity.PERSON_ID_KEY,
													PersonActivity.CREATE_PERSON_ID);
											intent.putExtra(
													PersonActivity.ENTIY_TYPE,
													PersonActivity.TYPE_PERSON);
											intent.putExtra(
													PersonActivity.MODE_KEY,
													modeActivity.getMode()
															.toString());
											startActivityForResult(intent,
													PERSON_RESULT);
										}
									});

							dialog.setNegativeButton(R.string.group,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(rootView
													.getContext(),
													PersonActivity.class);
											intent.putExtra(
													PersonActivity.PERSON_ID_KEY,
													PersonActivity.CREATE_PERSON_ID);
											intent.putExtra(
													PersonActivity.ENTIY_TYPE,
													PersonActivity.TYPE_GROUP);
											intent.putExtra(
													PersonActivity.MODE_KEY,
													modeActivity.getMode()
															.toString());
											startActivityForResult(intent,
													PERSON_RESULT);

										}
									});

							dialog.show();

						}

					}
				});

		if (modeActivity.getMode().compareTo(ModeDispatcher.Mode.MODE_RW) == 0) {
			((View) rootView.findViewById(R.id.challenge_button))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(rootView.getContext(),
									SelectClaimActivity.class);
							// Excluding from the list of claims that can be
							// challenged
							ArrayList<String> excludeList = new ArrayList<String>();
							List<Claim> claims = Claim.getSimplifiedClaims();
							for (Claim claim : claims) {
								// Challenges and local claims not yet
								// uploaded
								if (claim.getChallengedClaim() != null
										|| claim.getStatus()
												.equalsIgnoreCase(
														Claim.Status.created
																.toString())
										|| claim.getStatus().equalsIgnoreCase(
												Claim.Status.uploading
														.toString())
										|| !claim.isUploadable()) {
									excludeList.add(claim.getClaimId());
								}
							}
							intent.putStringArrayListExtra(
									SelectClaimActivity.EXCLUDE_CLAIM_IDS_KEY,
									excludeList);
							startActivityForResult(
									intent,
									SelectClaimActivity.SELECT_CLAIM_ACTIVITY_RESULT);
						}
					});
		}

		return rootView;
	}

	private void preload(Claim claim) {

		boolean onlyActiveValues;
		if (claim == null)
			onlyActiveValues = true;
		else
			onlyActiveValues = (claim.getStatus()
					.equals(ClaimStatus._MODERATED)
					|| claim.getStatus().equals(ClaimStatus._REJECTED)
					|| claim.getStatus().equals(ClaimStatus._REVIEWED) || claim
					.getStatus().equals(ClaimStatus._REJECTED));

		// Claim Types Spinner
		Spinner spinner = (Spinner) rootView
				.findViewById(R.id.claimTypesSpinner);

		ClaimType ct = new ClaimType();

		keyValueClaimTypesMap = ct.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);
		valueKeyClaimTypesMap = ct.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);
		List<String> list = new ArrayList<String>();

		SortedSet<String> keys = new TreeSet<String>(
				keyValueClaimTypesMap.keySet());
		for (String key : keys) {
			String value = keyValueClaimTypesMap.get(key);
			list.add(value);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner, list) {
		};
		dataAdapter.setDropDownViewResource(R.layout.my_spinner);

		spinner.setAdapter(dataAdapter);

		// Land Uses Spinner
		Spinner spinnerLU = (Spinner) rootView
				.findViewById(R.id.landUseSpinner);

		LandUse lu = new LandUse();
		keyValueMapLandUse = lu.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);
		valueKeyMapLandUse = lu.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);

		List<String> landUseslist = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueMapLandUse.keySet());
		for (String key : keys) {
			String value = keyValueMapLandUse.get(key);
			landUseslist.add(value);
		}

		ArrayAdapter<String> dataAdapterLU = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				landUseslist) {
		};
		dataAdapterLU.setDropDownViewResource(R.layout.my_spinner);

		spinnerLU.setAdapter(dataAdapterLU);

		// Land Projects Spinner
		Spinner spinnerLP = (Spinner) rootView
				.findViewById(R.id.landProjectSpinner);

		LandProject lp = new LandProject();
		keyValueLandProjectsMap = lp.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);
		valueKeyLandProjectsMap = lp.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(), onlyActiveValues);

		List<String> landProjectsList = new ArrayList<String>();
		keys = new TreeSet<String>(keyValueLandProjectsMap.keySet());
		for (String key : keys) {
			String value = keyValueLandProjectsMap.get(key);
			landProjectsList.add(value);
		}

		ArrayAdapter<String> dataAdapterLP = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				landProjectsList) {
		};
		dataAdapterLP.setDropDownViewResource(R.layout.my_spinner);

		spinnerLP.setAdapter(dataAdapterLP);

		countriesList = new ArrayList<Country>(new TreeSet<Country>(onlyActiveValues ? Country.getActiveCountries():Country.getCountries()));

		final Spinner spinnerCountry = (Spinner) rootView
				.findViewById(R.id.countrySpinner);
		final ArrayAdapter<Country> dataAdapterCountry = new ArrayAdapter<Country>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				countriesList);
		spinnerCountry.setAdapter(dataAdapterCountry);

		provincesList = new ArrayList<Province>(new TreeSet<Province>(onlyActiveValues ? Province.getActiveProvinces():Province.getProvinces()));

		ArrayAdapter<Province> dataAdapterProvince = new ArrayAdapter<Province>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Province>(provincesList));
		final Spinner spinnerProvince = (Spinner) rootView
				.findViewById(R.id.provinceSpinner);
		spinnerProvince.setAdapter(dataAdapterProvince);

		municipalitiesList = new ArrayList<Municipality>(new TreeSet<Municipality>(onlyActiveValues ? Municipality.getActiveMunicipalities():Municipality.getMunicipalities()));

		ArrayAdapter<Municipality> dataAdapterMunicipality = new ArrayAdapter<Municipality>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Municipality>(municipalitiesList));
		final Spinner spinnerMunicipality = (Spinner) rootView
				.findViewById(R.id.municipalitySpinner);
		spinnerMunicipality.setAdapter(dataAdapterMunicipality);

		communesList = new ArrayList<Commune>(new TreeSet<Commune>(onlyActiveValues ? Commune.getActiveCommunes():Commune.getCommunes()));

		ArrayAdapter<Commune> dataAdapterCommune = new ArrayAdapter<Commune>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				new ArrayList<Commune>(communesList));
		final Spinner spinnerCommune = (Spinner) rootView
				.findViewById(R.id.communeSpinner);
		spinnerCommune.setAdapter(dataAdapterCommune);

		spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of birth communes based on selected country
				Country selectedCountry = (Country)spinnerCountry.getSelectedItem();

				ArrayAdapter<Province> dataAdapterPR = (ArrayAdapter<Province>)spinnerProvince.getAdapter();
				dataAdapterPR.clear();
				List<Province> provinces = Province.filterProvincesByCountry(provincesList, selectedCountry.getCode());
				dataAdapterPR.addAll(provinces);
				dataAdapterPR.notifyDataSetChanged();
				spinnerProvince.setSelection(0,true);

				Province selectedProvince = provinces.get(0);
				ArrayAdapter<Municipality> dataAdapterMU = (ArrayAdapter<Municipality>)spinnerMunicipality.getAdapter();
				dataAdapterMU.clear();
				List<Municipality> municipalities = Municipality.filterMunicipalitiesByProvince(municipalitiesList, selectedProvince.getCode());
				dataAdapterMU.addAll(municipalities);
				dataAdapterMU.notifyDataSetChanged();
				spinnerMunicipality.setSelection(0,true);

				Municipality selectedMunicipality = municipalities.get(0);
				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(Commune.filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerCommune.setSelection(0,true);

				((EditText) rootView.findViewById(R.id.claim_name_input_field)).requestFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of issuance municipalities based on selected province
				Province selectedProvince = (Province)spinnerProvince.getSelectedItem();

				ArrayAdapter<Municipality> dataAdapterMU = (ArrayAdapter<Municipality>)spinnerMunicipality.getAdapter();
				dataAdapterMU.clear();
				List<Municipality> municipalities = Municipality.filterMunicipalitiesByProvince(municipalitiesList, selectedProvince.getCode());
				dataAdapterMU.addAll(municipalities);
				dataAdapterMU.notifyDataSetChanged();
				spinnerMunicipality.setSelection(0,true);

				Municipality selectedMunicipality = municipalities.get(0);
				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(Commune.filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerCommune.setSelection(0,true);

				((EditText) rootView.findViewById(R.id.claim_name_input_field)).requestFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spinnerMunicipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Reload list of issuance communes based on selected municipality

				Municipality selectedMunicipality = (Municipality)spinnerMunicipality.getSelectedItem();

				ArrayAdapter<Commune> dataAdapterCO = (ArrayAdapter<Commune>)spinnerCommune.getAdapter();
				dataAdapterCO.clear();
				dataAdapterCO.addAll(Commune.filterCommunesByMunicipality(communesList, selectedMunicipality.getCode()));
				dataAdapterCO.notifyDataSetChanged();
				spinnerCommune.setSelection(0,true);

				((EditText) rootView.findViewById(R.id.claim_name_input_field)).requestFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
		spinnerCountry.setSelection(Country.countryIndex(Country.DEFAULT_COUNTRY_CODE, countriesList),true);

		// Claimant
		((TextView) rootView.findViewById(R.id.claimant_id)).setTextSize(8);
		((TextView) rootView.findViewById(R.id.claimant_id)).setText("");
		ImageView claimantImageView = (ImageView) rootView
				.findViewById(R.id.claimant_picture);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_contact_picture);

		claimantImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 128,
				128, true));

		// Challenged claim
		((TextView) rootView.findViewById(R.id.challenge_to_claim_id))
				.setTextSize(8);
		((TextView) rootView.findViewById(R.id.challenge_to_claim_id))
				.setText("");

		// Challenged claimant
		ImageView challengedClaimantImageView = (ImageView) rootView
				.findViewById(R.id.challenge_to_claimant_picture);

		challengedClaimantImageView.setImageBitmap(Bitmap.createScaledBitmap(
				bitmap, 128, 128, true));

		EditText dateOfStart = (EditText) rootView
				.findViewById(R.id.date_of_start_input_field);

		final DatePickerDialog.OnDateSetListener dateOfStartListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				localCalendar.set(Calendar.YEAR, year);
				localCalendar.set(Calendar.MONTH, monthOfYear);
				localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDoB();
			}

		};

		dateOfStart.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				new DatePickerDialog(rootView.getContext(), dateOfStartListener, localCalendar
						.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH),
						localCalendar.get(Calendar.DAY_OF_MONTH)).show();
				return true;
			}
		});
		EditText constructionDate = (EditText) rootView
				.findViewById(R.id.construction_date_input_field);

		final DatePickerDialog.OnDateSetListener constructionDateListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {
				localCalendar.set(Calendar.YEAR, year);
				localCalendar.set(Calendar.MONTH, monthOfYear);
				localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateCD();
			}

		};

		constructionDate.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				new DatePickerDialog(rootView.getContext(), constructionDateListener, localCalendar
						.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH),
						localCalendar.get(Calendar.DAY_OF_MONTH)).show();
				return true;
			}
		});
	}

	private void loadChallengedClaim(Claim challengedClaim) {

		if (challengedClaim != null) {

			Person challengedPerson = challengedClaim.getPerson();
			((TextView) rootView.findViewById(R.id.challenge_to_claim_id))
					.setTextSize(8);
			((TextView) rootView.findViewById(R.id.challenge_to_claim_id))
					.setText(challengedClaim.getClaimId());
			((TextView) rootView.findViewById(R.id.challenge_to_claim_slogan))
					.setBackgroundColor(getResources().getColor(
							R.color.light_background_opentenure));
			((TextView) rootView.findViewById(R.id.challenge_to_claim_slogan))
					.setText(getResources().getString(
							R.string.title_challenged_claims)
							+ " "
							+ challengedClaim.getName()
							+ ", "
							+ getResources().getString(R.string.by)
							+ ": "
							+ challengedPerson.getFirstName()
							+ " "
							+ challengedPerson.getLastName()
							+ ", "
							+ getResources().getString(R.string.status)
							+ challengedClaim.getStatus());
			((TextView) rootView.findViewById(R.id.challenge_to_claim_slogan))
					.setVisibility(View.VISIBLE);
			ImageView challengedClaimantImageView = (ImageView) rootView
					.findViewById(R.id.challenge_to_claimant_picture);

			((View) rootView.findViewById(R.id.challenge_button))
					.setEnabled(false);

			// File challengedPersonPictureFile = Person
			// .getPersonPictureFile(challengedPerson.getPersonId());
			challengedClaimantImageView
					.setImageBitmap(Person.getPersonPicture(
							rootView.getContext(),
							challengedPerson.getPersonId(), 128));

			ImageView challengedClaimantRemoveButton = (ImageView) rootView
					.findViewById(R.id.action_remove_challenge);

			if (modeActivity.getMode().compareTo(ModeDispatcher.Mode.MODE_RO) != 0)
				challengedClaimantRemoveButton.setVisibility(View.VISIBLE);

			challengedClaimantRemoveButton
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							((TextView) rootView
									.findViewById(R.id.challenge_to_claim_id))
									.setText("");

							((TextView) rootView
									.findViewById(R.id.challenge_to_claim_slogan))
									.setVisibility(View.GONE);

							((ImageView) rootView
									.findViewById(R.id.action_remove_challenge))
									.setVisibility(View.INVISIBLE);
							((View) rootView
									.findViewById(R.id.challenge_button))
									.setEnabled(true);

						}
					});

		} else {

			((TextView) rootView.findViewById(R.id.challenge_to_claim_slogan))
					.setVisibility(View.GONE);
			((TextView) rootView.findViewById(R.id.challenge_to_claim_slogan))
					.setVisibility(View.GONE);
		}
	}

	private void loadClaimant(Person claimant) {
		Log.d(this.getClass().getName(), "Loading claimant: " + claimant.toString());

		if (claimant != null) {
			if (OpenTenureApplication.getInstance().getLocale().toString()
					.startsWith("ar")) {
				((View) rootView.findViewById(R.id.claimant_slogan))
						.setTextDirection(View.TEXT_DIRECTION_LOCALE);
				((View) rootView.findViewById(R.id.claimant_slogan))
						.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
			}
			((TextView) rootView.findViewById(R.id.claimant_button))
					.setText(getResources().getText(
							R.string.action_modify_claimant));

			((TextView) rootView.findViewById(R.id.claimant_id)).setTextSize(8);
			((TextView) rootView.findViewById(R.id.claimant_id))
					.setText(claimant.getPersonId());
			((TextView) rootView.findViewById(R.id.claimant_slogan))
					.setBackgroundColor(getResources().getColor(
							R.color.light_background_opentenure));
			((TextView) rootView.findViewById(R.id.claimant_slogan))
					.setVisibility(View.VISIBLE);
			((TextView) rootView.findViewById(R.id.claimant_slogan))
					.setText(claimant.getFirstName() + " "
							+ claimant.getLastName());
			ImageView claimantImageView = (ImageView) rootView
					.findViewById(R.id.claimant_picture);
			// File personPictureFile = Person.getPersonPictureFile(claimant
			// .getPersonId());
			claimantImageView.setImageBitmap(Person.getPersonPicture(
					rootView.getContext(), claimant.getPersonId(), 128));

			ImageView claimantRemove = (ImageView) rootView
					.findViewById(R.id.action_remove_person);
			claimantRemove.setVisibility(View.INVISIBLE);

		} else {

			((TextView) rootView.findViewById(R.id.claimant_slogan))
					.setVisibility(View.GONE);
		}
	}

	public void reloadArea(Claim claim) {

		((TextView) rootView.findViewById(R.id.claim_area_label))
				.setText(R.string.claim_area_label);

		((TextView) rootView.findViewById(R.id.claim_area_label))
				.setVisibility(View.VISIBLE);

		((TextView) rootView.findViewById(R.id.claim_area)).setText(claim
				.getClaimArea()
				+ " "
				+ OpenTenureApplication.getContext().getString(
						R.string.square_meters));

		((TextView) rootView.findViewById(R.id.claim_area))
				.setVisibility(View.VISIBLE);

	}

	public void load(Claim claim) {

		if (claim != null) {

			boolean onlyActiveValues = (!claim.getStatus().equals(
					ClaimStatus._MODERATED)
					&& claim.getStatus().equals(ClaimStatus._REJECTED)
					&& !claim.getStatus().equals(ClaimStatus._REVIEWED)
					&& !claim.getStatus().equals(ClaimStatus._UNMODERATED)
					&& !claim.getStatus().equals(ClaimStatus._UPLOAD_ERROR) && !claim
					.getStatus().equals(ClaimStatus._UPLOAD_INCOMPLETE));

			if (OpenTenureApplication.getInstance().getLocale().toString()
					.startsWith("ar")) {
				((EditText) rootView.findViewById(R.id.claim_name_input_field))
						.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
				((EditText) rootView.findViewById(R.id.claim_name_input_field))
						.setTextDirection(View.TEXT_DIRECTION_LOCALE);
				((EditText) rootView.findViewById(R.id.block_number_input_field))
						.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
				((EditText) rootView.findViewById(R.id.block_number_input_field))
						.setTextDirection(View.TEXT_DIRECTION_LOCALE);
				((EditText) rootView.findViewById(R.id.plot_number_input_field))
						.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
				((EditText) rootView.findViewById(R.id.plot_number_input_field))
						.setTextDirection(View.TEXT_DIRECTION_LOCALE);
				((EditText) rootView.findViewById(R.id.neighborhood_input_field))
						.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
				((EditText) rootView.findViewById(R.id.neighborhood_input_field))
						.setTextDirection(View.TEXT_DIRECTION_LOCALE);
			}
			((EditText) rootView.findViewById(R.id.claim_name_input_field))
					.setText(claim.getName());
			((EditText) rootView.findViewById(R.id.block_number_input_field))
					.setText(claim.getBlockNumber());
			((EditText) rootView.findViewById(R.id.plot_number_input_field))
					.setText(claim.getPlotNumber());
			((EditText) rootView.findViewById(R.id.neighborhood_input_field))
					.setText(claim.getNeighborhood());

			((Spinner) rootView.findViewById(R.id.claimTypesSpinner))
					.setSelection(new ClaimType().getIndexByCodeType(
							claim.getType(), onlyActiveValues));

			((Spinner) rootView.findViewById(R.id.landUseSpinner))
					.setSelection(new LandUse().getIndexByCodeType(
							claim.getLandUse(), onlyActiveValues));

			((Spinner) rootView.findViewById(R.id.landProjectSpinner))
					.setSelection(new LandProject().getIndexByCodeType(
							claim.getLandProjectCode(), onlyActiveValues));

			Commune commune = Commune.getCommune(claim.getCommuneCode());

			((Spinner) rootView.findViewById(R.id.countrySpinner))
					.setSelection(Country.countryIndex(commune.getCountryCode(), countriesList));

			((Spinner) rootView.findViewById(R.id.provinceSpinner))
					.setSelection(Province.provinceIndex(commune.getProvinceCode(), Province.filterProvincesByCountry(provincesList, commune.getCountryCode())));

			((Spinner) rootView.findViewById(R.id.municipalitySpinner))
					.setSelection(Municipality.municipalityIndex(commune.getMunicipalityCode(), Municipality.filterMunicipalitiesByProvince(municipalitiesList, commune.getProvinceCode())));

			((Spinner) rootView.findViewById(R.id.communeSpinner))
					.setSelection(Commune.communeIndex(Commune.filterCommunesByMunicipality(communesList, commune.getMunicipalityCode()), claim.getCommuneCode()));

			((EditText) rootView.findViewById(R.id.claim_notes_input_field))
					.setText(claim.getNotes());

			((Switch) rootView.findViewById(R.id.has_constructions_switch))
					.setSelected(claim.isHasConstructions());

			if (claim.getClaimArea() > 0) {

				((TextView) rootView.findViewById(R.id.claim_area_label))
						.setText(R.string.claim_area_label);

				((TextView) rootView.findViewById(R.id.claim_area_label))
						.setVisibility(View.VISIBLE);

				((TextView) rootView.findViewById(R.id.claim_area))
						.setText(claim.getClaimArea()
								+ " "
								+ OpenTenureApplication.getContext().getString(
										R.string.square_meters));

				((TextView) rootView.findViewById(R.id.claim_area))
						.setVisibility(View.VISIBLE);
			}

			if (claim.getDateOfStart() != null) {

				((EditText) rootView
						.findViewById(R.id.date_of_start_input_field))
						.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
								.format(claim.getDateOfStart()));
			} else {
				((EditText) rootView
						.findViewById(R.id.date_of_start_input_field))
						.setText("");
			}
			if (claim.getConstructionDate() != null) {

				((EditText) rootView
						.findViewById(R.id.construction_date_input_field))
						.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
								.format(claim.getConstructionDate()));
			} else {
				((EditText) rootView
						.findViewById(R.id.construction_date_input_field))
						.setText("");
			}
			if (modeActivity.getMode().compareTo(ModeDispatcher.Mode.MODE_RO) == 0) {
				((EditText) rootView.findViewById(R.id.claim_name_input_field))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.block_number_input_field))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.plot_number_input_field))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.neighborhood_input_field))
						.setFocusable(false);

				((Spinner) rootView.findViewById(R.id.claimTypesSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.claimTypesSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.claimTypesSpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.claimTypesSpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.landUseSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.landUseSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.landUseSpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.landUseSpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.landProjectSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.landProjectSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.landProjectSpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.landProjectSpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.countrySpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.countrySpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.countrySpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.countrySpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.provinceSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.provinceSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.provinceSpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.provinceSpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.municipalitySpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.municipalitySpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.municipalitySpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.municipalitySpinner))
						.setEnabled(false);

				((Spinner) rootView.findViewById(R.id.communeSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.communeSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.communeSpinner))
						.setFocusableInTouchMode(false);
				((Spinner) rootView.findViewById(R.id.communeSpinner))
						.setEnabled(false);

				((Switch) rootView.findViewById(R.id.has_constructions_switch))
						.setFocusable(false);
				((Switch) rootView.findViewById(R.id.has_constructions_switch))
						.setClickable(false);

				((EditText) rootView
						.findViewById(R.id.date_of_start_input_field))
						.setFocusable(false);
				((EditText) rootView
						.findViewById(R.id.date_of_start_input_field))
						.setLongClickable(false);
				((EditText) rootView
						.findViewById(R.id.construction_date_input_field))
						.setFocusable(false);
				((EditText) rootView
						.findViewById(R.id.construction_date_input_field))
						.setLongClickable(false);
				((EditText) rootView.findViewById(R.id.claim_notes_input_field))
						.setFocusable(false);

			}
			Person claimant = null;
			String claimantId = ((TextView) rootView
					.findViewById(R.id.claimant_id)).getText().toString();

			if (claimantId == null || claimantId.trim().equals(""))
				claimant = claim.getPerson();
			else
				claimant = Person.getPerson(claimantId);
			loadClaimant(claimant);

			if (challengedJustLoaded) {
				challengedJustLoaded = false;
			} else
				loadChallengedClaim(claim.getChallengedClaim());
		}
	}

	public int saveClaim() {

		String personId = ((TextView) rootView
				.findViewById(R.id.claimant_id)).getText().toString();
		Log.d(this.getClass().getName(), "Retrieving claimant with id: " + personId);
		Person person = Person.getPerson(personId);

		Claim challengedClaim = Claim
				.getClaim(((TextView) rootView
						.findViewById(R.id.challenge_to_claim_id)).getText()
						.toString());

		Claim claim = new Claim();
        String claimName = ((EditText) rootView
                .findViewById(R.id.claim_name_input_field)).getText()
                .toString();

        if (claimName == null || claimName.trim().equals(""))
            return 3;
		claim.setName(claimName);

        String plotNumber = ((EditText) rootView
                .findViewById(R.id.plot_number_input_field)).getText()
                .toString();

        if (plotNumber == null || plotNumber.trim().equals(""))
            return 6;
        claim.setPlotNumber(plotNumber);

        String blockNumber = ((EditText) rootView
                .findViewById(R.id.block_number_input_field)).getText()
                .toString();
        claim.setBlockNumber(blockNumber);

        String neighborhood = ((EditText) rootView
                .findViewById(R.id.neighborhood_input_field)).getText()
                .toString();
        claim.setNeighborhood(neighborhood);

        boolean hasConstructions = ((Switch)rootView
                .findViewById(R.id.has_constructions_switch)).isSelected();
        claim.setHasConstructions(hasConstructions);

        String displayValue = (String) ((Spinner) rootView
				.findViewById(R.id.claimTypesSpinner)).getSelectedItem();
		claim.setType(valueKeyClaimTypesMap.get(displayValue));

		String landUseDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landUseSpinner)).getSelectedItem();
		claim.setLandUse(valueKeyMapLandUse.get(landUseDispValue));

		String landProjectDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landProjectSpinner)).getSelectedItem();
		claim.setLandProjectCode(valueKeyLandProjectsMap.get(landProjectDispValue));

		Commune commune = (Commune) ((Spinner) rootView
				.findViewById(R.id.communeSpinner)).getSelectedItem();
		claim.setCommuneCode(commune.getCode());

		String notes = ((EditText) rootView
				.findViewById(R.id.claim_notes_input_field)).getText()
				.toString();

		claim.setNotes(notes);

		String startDate = ((EditText) rootView
				.findViewById(R.id.date_of_start_input_field)).getText()
				.toString();

        java.util.Date dob = null;

        if (startDate != null && !startDate.trim().equals("")) {
            try {

                dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(startDate);

                if (dob != null)
                    claim.setDateOfStart(new Date(dob.getTime()));

            } catch (ParseException e) {
                e.printStackTrace();
                dob = null;
                return 2;
            }

        }

        String constructionDate = ((EditText) rootView
                .findViewById(R.id.construction_date_input_field)).getText()
                .toString();

        if (constructionDate != null && !constructionDate.trim().equals("")) {
            try {

                java.util.Date cd = null;
                cd = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .parse(constructionDate);

                if (cd != null)
                    claim.setConstructionDate(new Date(cd.getTime()));

            } catch (ParseException e) {
            }

        }

        if (person == null)
			return 4;

		claim.setPerson(person);
		claim.setChallengedClaim(challengedClaim);
		// Still allow saving the claim if the dynamic part contains errors
		isFormValid();
		claim.setDynamicForm(formDispatcher.getEditedFormPayload());
		claim.setVersion("0");

		if (claim.create() == 1) {
			List<Vertex> vertices = Vertex.getVertices(claim.getClaimId());
			if (challengedClaim != null
					&& (vertices == null || vertices.size() == 0)) {
				copyVerticesFromChallengedClaim(challengedClaim.getClaimId(),
						claim.getClaimId());
			}

			OpenTenureApplication.setClaimId(claim.getClaimId());

			FileSystemUtilities.createClaimFileSystem(claim.getClaimId());
			claimActivity.setClaimId(claim.getClaimId());

			if (createPersonAsOwner(person) == 0)
				return 0;

			claimListener.onClaimSaved();
			return 1;

		} else
			return 5;

	}

	private void copyVerticesFromChallengedClaim(String challengedClaimId,
			String challengingClaimId) {
		Log.d(this.getClass().getName(), "copying vertices from "
				+ challengedClaimId + " to " + challengingClaimId);
		// delete eventually existing vertices
		Vertex.deleteVertices(challengingClaimId);
		// get vertices from the challenged claim
		List<Vertex> vertices = Vertex.getVertices(challengedClaimId);
		List<Vertex> copiedVertices = new ArrayList<Vertex>();
		for (Vertex vertex : vertices) {
			Vertex copiedVertex = new Vertex(vertex);
			// associate it to the challenging claim id
			copiedVertex.setClaimId(challengingClaimId);
			copiedVertices.add(copiedVertex);
		}
		// save them again
		Vertex.createVertices(copiedVertices);

		// delete eventually existing holes vertices
		HoleVertex.deleteVertices(challengingClaimId);
		// get vertices from the challenged claim
		List<List<HoleVertex>> holes = HoleVertex.getHoles(challengedClaimId);
		List<List<HoleVertex>> copiedHoles = new ArrayList<List<HoleVertex>>();
		for(List<HoleVertex> hole:holes){
			List<HoleVertex> copiedHole = new ArrayList<HoleVertex>();
			for (HoleVertex vertex : hole) {
				HoleVertex copiedVertex = new HoleVertex(vertex);
				// associate it to the challenging claim id
				copiedVertex.setClaimId(challengingClaimId);
				copiedHole.add(copiedVertex);
			}
			copiedHoles.add(copiedHole);
		}
		// save them again
		HoleVertex.createVertices(copiedHoles);
	}

	public int updateClaim() {

		Person person = Person.getPerson(((TextView) rootView
				.findViewById(R.id.claimant_id)).getText().toString());

		if (OpenTenureApplication.getInstance().getLocale().toString()
				.startsWith("ar")) {
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setTextDirection(View.TEXT_DIRECTION_LOCALE);
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
		}
		if (person != null)
			((View) rootView.findViewById(R.id.claimant_slogan))
					.setVisibility(View.VISIBLE);

		Claim challengedClaim = Claim
				.getClaim(((TextView) rootView
						.findViewById(R.id.challenge_to_claim_id)).getText()
						.toString());

		// Claim claim = Claim.getClaim(claimActivity.getClaimId());
		Claim claim = Claim.getClaim(claimActivity.getClaimId());
		claim.setClaimId(claimActivity.getClaimId());
		claim.setName(((EditText) rootView
				.findViewById(R.id.claim_name_input_field)).getText()
				.toString());

		if (claim.getName() == null || claim.getName().trim().equals(""))
			return 0;

		claim.setBlockNumber(((EditText) rootView
				.findViewById(R.id.block_number_input_field)).getText()
				.toString());
		String plotNumber = ((EditText) rootView
				.findViewById(R.id.plot_number_input_field)).getText()
				.toString();

		if (plotNumber == null || plotNumber.trim().equals(""))
			return 6;
		claim.setPlotNumber(plotNumber);
		claim.setNeighborhood(((EditText) rootView
				.findViewById(R.id.neighborhood_input_field)).getText()
				.toString());

		String displayValue = (String) ((Spinner) rootView
				.findViewById(R.id.claimTypesSpinner)).getSelectedItem();
		claim.setType(valueKeyClaimTypesMap.get(displayValue));

		String landUseDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landUseSpinner)).getSelectedItem();
		claim.setLandUse(valueKeyMapLandUse.get(landUseDispValue));

		String landProjectDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landProjectSpinner)).getSelectedItem();
		claim.setLandProjectCode(valueKeyLandProjectsMap.get(landProjectDispValue));

		Commune commune = (Commune) ((Spinner) rootView
				.findViewById(R.id.communeSpinner)).getSelectedItem();
		claim.setCommuneCode(commune.getCode());

		String notes = ((EditText) rootView
				.findViewById(R.id.claim_notes_input_field)).getText()
				.toString();

		claim.setNotes(notes);

		String startDate = ((EditText) rootView
				.findViewById(R.id.date_of_start_input_field)).getText()
				.toString();

		java.util.Date dob = null;

		if (startDate != null && !startDate.trim().equals("")) {
			try {

				dob = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
						.parse(startDate);

				if (dob != null)
					claim.setDateOfStart(new Date(dob.getTime()));

			} catch (ParseException e) {
				e.printStackTrace();
				dob = null;
				return 2;
			}

		}

		String constructionDate = ((EditText) rootView
				.findViewById(R.id.construction_date_input_field)).getText()
				.toString();

		java.util.Date cd = null;

		if (constructionDate != null && !constructionDate.trim().equals("")) {
			try {

				cd = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
						.parse(constructionDate);

				if (cd != null)
					claim.setConstructionDate(new Date(cd.getTime()));

			} catch (ParseException e) {
				e.printStackTrace();
				cd = null;
				return 2;
			}

		}
		// Still allow saving the claim if the dynamic part contains errors
		isFormValid();

		if (createPersonAsOwner(person) == 0)
			return 0;

		claim.setPerson(person);
		claim.setChallengedClaim(challengedClaim);
		claim.setDynamicForm(formDispatcher.getEditedFormPayload());

		int result = claim.update();

		if (challengedClaim != null) {
			List<Vertex> vertices = Vertex.getVertices(claim.getClaimId());
			if (vertices == null || vertices.size() == 0) {
				copyVerticesFromChallengedClaim(challengedClaim.getClaimId(),
						claim.getClaimId());
			}
		}

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		Toast toast;
		switch (item.getItemId()) {

		case R.id.action_save:

			if (claimActivity.getClaimId() == null) {
				int resultSave = saveClaim();
				if (resultSave == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();
				}
				if (resultSave == 2) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_startdate,
							Toast.LENGTH_SHORT);
					toast.show();
                } else if (resultSave == 3) {
                    toast = Toast.makeText(rootView.getContext(),
                            R.string.message_unable_to_save_missing_claim_name,
                            Toast.LENGTH_SHORT);
                    toast.show();
				} else if (resultSave == 4) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_person,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (resultSave == 5) {
					toast = Toast
							.makeText(rootView.getContext(),
									R.string.message_unable_to_save,
									Toast.LENGTH_SHORT);
					toast.show();
				} else if (resultSave == 6) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_plot_number,
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				int updated = updateClaim();

				if (updated == 1) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_saved, Toast.LENGTH_SHORT);
					toast.show();

				} else if (updated == 2) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_error_startdate,
							Toast.LENGTH_SHORT);
					toast.show();
				} else if (updated == 6) {
					toast = Toast.makeText(rootView.getContext(),
							R.string.message_unable_to_save_missing_plot_number,
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

		case R.id.action_print:
			Claim claim = Claim.getClaim(claimActivity.getClaimId());
			boolean mapPresent = false;
			boolean mapToDownload = false;
			String path = null;

			if (claim == null) {
				toast = Toast.makeText(rootView.getContext(),
						R.string.message_save_snapshot_before_printing,
						Toast.LENGTH_LONG);
				toast.show();
				return true;
			}

			for (Attachment attachment : claim.getAttachments()) {
				if (EditablePropertyBoundary.DEFAULT_MAP_FILE_NAME
						.equalsIgnoreCase(attachment.getFileName())
						&& EditablePropertyBoundary.DEFAULT_MAP_FILE_TYPE
								.equalsIgnoreCase(attachment.getFileType())
						&& EditablePropertyBoundary.DEFAULT_MAP_MIME_TYPE
								.equalsIgnoreCase(attachment.getMimeType())) {
					mapPresent = true;
					path = attachment.getPath();
					mapToDownload = !(new File(path).exists());
				}
			}
			if (!mapPresent) {
				toast = Toast.makeText(rootView.getContext(),
						R.string.message_save_snapshot_before_printing,
						Toast.LENGTH_LONG);
				toast.show();
				return true;
			}
			if (mapToDownload) {

				toast = Toast.makeText(rootView.getContext(),
						R.string.message_download_snapshot_before_printing,
						Toast.LENGTH_LONG);
				toast.show();
				return true;
			}
			try {
				PDFClaimExporter pdf = new PDFClaimExporter(
						rootView.getContext(), claim, false);

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + pdf.getFilePath()),
						"application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivity(intent);

			} catch (Error e) {
				toast = Toast.makeText(rootView.getContext(),
						R.string.message_not_supported_on_this_device,
						Toast.LENGTH_SHORT);
				toast.show();
			}

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean isClaimChanged(Claim claim){

		if (!claim.getName().equalsIgnoreCase(
				((EditText) rootView
						.findViewById(R.id.claim_name_input_field))
						.getText().toString())){
			Log.d(this.getClass().getName(), "Claim name has changed");
			return true;
		}

		String blockNumber = ((EditText) rootView
				.findViewById(R.id.block_number_input_field))
				.getText().toString();

		if (((claim.getBlockNumber() == null || claim.getBlockNumber().equalsIgnoreCase("")) ^ blockNumber.equalsIgnoreCase(""))
				|| (!blockNumber.equalsIgnoreCase(claim.getBlockNumber()))){
			Log.d(this.getClass().getName(), "Block number has changed");
			return true;
		}

		String plotNumber = ((EditText) rootView
				.findViewById(R.id.plot_number_input_field))
				.getText().toString();

		if (((claim.getPlotNumber() == null || claim.getPlotNumber().equalsIgnoreCase("")) ^ plotNumber.equalsIgnoreCase(""))
				|| (!plotNumber.equalsIgnoreCase(claim.getPlotNumber()))){
			Log.d(this.getClass().getName(), "Plot number has changed");
			return true;
		}

		String neighborhood = ((EditText) rootView
				.findViewById(R.id.neighborhood_input_field))
				.getText().toString();

		if (((claim.getNeighborhood() == null || claim.getNeighborhood().equalsIgnoreCase("")) ^ neighborhood.equalsIgnoreCase(""))
				|| (!neighborhood.equalsIgnoreCase(claim.getNeighborhood()))){
			Log.d(this.getClass().getName(), "Neighborhood has changed");
			return true;
		}

		Person person = Person.getPerson(((TextView) rootView
				.findViewById(R.id.claimant_id)).getText().toString());
		if (!claim.getPerson().getPersonId().equalsIgnoreCase(person.getPersonId())){
			Log.d(this.getClass().getName(), "Claimant has changed");
			return true;
		}

		Claim challengedClaim = Claim.getClaim(((TextView) rootView
				.findViewById(R.id.challenge_to_claim_id))
				.getText().toString());
		if ((challengedClaim == null && claim.getChallengedClaim() != null)
				|| (challengedClaim != null && claim.getChallengedClaim() == null)
				|| (challengedClaim != null && claim.getChallengedClaim() != null
					&& !claim.getChallengedClaim().getClaimId().equalsIgnoreCase(challengedClaim.getClaimId()))){
			Log.d(this.getClass().getName(), "Challenged claim has changed");
			return true;
		}

		String claimType = (String) ((Spinner) rootView
				.findViewById(R.id.claimTypesSpinner))
				.getSelectedItem();

		if (!claim.getType().equalsIgnoreCase(valueKeyClaimTypesMap.get(claimType))){
			Log.d(this.getClass().getName(), "Claim type has changed");
			return true;
		}

		String landUseDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landUseSpinner))
				.getSelectedItem();
		if ((claim.getLandUse() == null && valueKeyMapLandUse.get(landUseDispValue) != null)
				|| (!claim.getLandUse().equals(valueKeyMapLandUse.get(landUseDispValue)))){
			Log.d(this.getClass().getName(), "Land use has changed");
			return true;
		}
		String landProjectDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.landProjectSpinner))
				.getSelectedItem();
		if ((claim.getLandProjectCode() == null && valueKeyLandProjectsMap.get(landProjectDispValue) != null)
				|| (!claim.getLandProjectCode().equals(valueKeyLandProjectsMap.get(landProjectDispValue)))){
			Log.d(this.getClass().getName(), "Land project has changed");
			return true;
		}
		Commune commune = (Commune) ((Spinner) rootView
				.findViewById(R.id.communeSpinner))
				.getSelectedItem();
		if (!areEqual(claim.getCommuneCode(), commune.getCode())){
			Log.d(this.getClass().getName(), "Commune has changed");
			return true;
		}
		Country country = (Country) ((Spinner) rootView
				.findViewById(R.id.countrySpinner))
				.getSelectedItem();
		if (!areEqual(commune.getCountryCode(), country.getCode())){
			Log.d(this.getClass().getName(), "Country has changed");
			return true;
		}
		Province province = (Province) ((Spinner) rootView
				.findViewById(R.id.provinceSpinner))
				.getSelectedItem();
		if (!areEqual(commune.getProvinceCode(), province.getCode())){
			Log.d(this.getClass().getName(), "Province has changed");
			return true;
		}
		Municipality municipality = (Municipality) ((Spinner) rootView
				.findViewById(R.id.municipalitySpinner))
				.getSelectedItem();
		if (!areEqual(commune.getMunicipalityCode(), municipality.getCode())){
			Log.d(this.getClass().getName(), "Municipality has changed");
			return true;
		}
		String notes = ((EditText) rootView
				.findViewById(R.id.claim_notes_input_field))
				.getText().toString();

		if (claim.getNotes() != null && !claim.getNotes().equals(notes)){
			Log.d(this.getClass().getName(), "Claim notes have changed");
			return true;
		}
		String startDate = ((EditText) rootView
				.findViewById(R.id.date_of_start_input_field))
				.getText().toString();

		if (claim.getDateOfStart() == null ^ startDate.trim().equalsIgnoreCase("")) {

			Log.d(this.getClass().getName(), "Rights start date has changed");
			return true;

		}
		if (!startDate.trim().equalsIgnoreCase("")) {

			try {
				java.util.Date dos = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.US)
						.parse(startDate);

				Date date = new Date(
						dos.getTime());

				if (claim.getDateOfStart()
						.compareTo(date) != 0) {
					Log.d(this.getClass().getName(), "Rights start date has changed");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}
		}

		String constructionDate = ((EditText) rootView
				.findViewById(R.id.construction_date_input_field))
				.getText().toString();

		if (claim.getConstructionDate() == null ^ constructionDate.trim().equalsIgnoreCase("")) {

			Log.d(this.getClass().getName(), "Construction date has changed");
			return true;

		}
		if (!constructionDate.trim().equalsIgnoreCase("")) {

			try {
				java.util.Date cd = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.US)
						.parse(constructionDate);

				Date date = new Date(
						cd.getTime());

				if (claim.getConstructionDate()
						.compareTo(date) != 0) {
					Log.d(this.getClass().getName(), "Construction date has changed");
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return true;
			}
		}

		if(((Switch) rootView.findViewById(R.id.has_constructions_switch)).isSelected() ^ claim.isHasConstructions()){
			Log.d(this.getClass().getName(), "Has constructions has changed");
			return true;
		}

		return isFormChanged();
	}

	public boolean hasFragmentValues(){

		if (!((EditText) rootView
				.findViewById(R.id.claim_name_input_field)).getText()
				.toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Claim name has value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.block_number_input_field)).getText()
				.toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Block number has value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.plot_number_input_field)).getText()
				.toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Plot number has value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.neighborhood_input_field)).getText()
				.toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Neighborhood has value");
			return true;
		}

		String person = ((TextView) rootView
				.findViewById(R.id.claimant_id)).getText().toString();
		if (person != null && !person.trim().equals("")){
			Log.d(this.getClass().getName(), "Claimant has value");
			return true;
		}

		String challengedClaim = ((TextView) rootView
				.findViewById(R.id.challenge_to_claim_id))
				.getText().toString();
		if (challengedClaim != null
				&& !challengedClaim.trim().equals("")){
			Log.d(this.getClass().getName(), "Challenged claim has value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.claim_notes_input_field))
				.getText().toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Claim notes have value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.date_of_start_input_field))
				.getText().toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Rights start date has value");
			return true;
		}

		if (!((EditText) rootView
				.findViewById(R.id.construction_date_input_field))
				.getText().toString().trim().equals("")){
			Log.d(this.getClass().getName(), "Construction date has value");
			return true;
		}

		return isFormChanged();

	}

	public boolean checkChanges() {

		Claim claim = Claim.getClaim(claimActivity.getClaimId());

			if ((claim != null && isClaimChanged(claim)) || (claim == null && hasFragmentValues())) {

				AlertDialog.Builder saveChangesDialog = new AlertDialog.Builder(
						this.getActivity());
				saveChangesDialog.setTitle(R.string.title_save_claim_dialog);
				String dialogMessage = OpenTenureApplication.getContext()
						.getString(R.string.message_discard_changes);

				saveChangesDialog.setMessage(dialogMessage);

				saveChangesDialog.setPositiveButton(R.string.confirm,
						new SaveDetailsNegativeListener(this));

				saveChangesDialog.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								return;
							}
						});
				saveChangesDialog.show();
				return true;
			}
		return false;
	}

	@Override
	public void onResume() {
//		Claim claim = Claim.getClaim(claimActivity.getClaimId());
//		preload(claim);
//		load(claim);

		super.onResume();

	};

	public int createPersonAsOwner(Person claimant) {
		try {
			List<ShareProperty> shares = ShareProperty.getShares(claimActivity
					.getClaimId());

			int value = 0;

			for (Iterator<ShareProperty> iterator = shares.iterator(); iterator
					.hasNext();) {
				ShareProperty shareProperty = (ShareProperty) iterator.next();
				value = value + shareProperty.getShares();
			}

			int shareValue = 100 - value;

			if (shareValue > 0) {
				ShareProperty share = new ShareProperty();

				share.setClaimId(claimActivity.getClaimId());
				share.setShares(shareValue);

				share.create();

				Person claimantCopy = claimant.copy();
				claimantCopy.create();

				File personImg = new File(
						FileSystemUtilities.getClaimantFolder(claimant
								.getPersonId())
								+ File.separator
								+ claimant.getPersonId() + ".jpg");

				if (personImg != null)
					FileSystemUtilities.copyFileInClaimantFolder(
							claimantCopy.getPersonId(), personImg);

				Owner owner = new Owner();
				owner.setPersonId(claimantCopy.getPersonId());
				owner.setShareId(share.getId());

				owner.create();

				OpenTenureApplication.getOwnersFragment().update();
			}
			return 1;

		} catch (Exception e) {
			Log.d("Details", "An error " + e.getMessage());

			e.printStackTrace();

			return 0;
		}

	}

	private boolean isFormValid() {
		FormPayload formPayload = formDispatcher.getEditedFormPayload();
		FormTemplate formTemplate = formDispatcher.getFormTemplate();
		FieldConstraint constraint = null;
		DisplayNameLocalizer dnl = new DisplayNameLocalizer(
				OpenTenureApplication.getInstance().getLocalization());

		if ((constraint = formTemplate.getFailedConstraint(formPayload, dnl)) != null) {
			Toast.makeText(rootView.getContext(), dnl.getLocalizedDisplayName(constraint.displayErrorMsg()),
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}

	private boolean isFormChanged() {
		FormPayload editedFormPayload = formDispatcher.getEditedFormPayload();
		FormPayload originalFormPayload = formDispatcher
				.getOriginalFormPayload();

		if (((editedFormPayload != null) && (originalFormPayload == null))
				|| ((editedFormPayload == null) && (originalFormPayload != null))
				|| !editedFormPayload.toJson().equalsIgnoreCase(
						originalFormPayload.toJson())) {
			Log.d(this.getClass().getName(), "Dynamic form has changed");
			return true;
		} else {
			return false;
		}
	}

	private void updateDoB() {

		EditText dateOfBirth = (EditText) getView().findViewById(
				R.id.date_of_start_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		dateOfBirth.setText(sdf.format(localCalendar.getTime()));
	}

	private void updateCD() {

		EditText dateOfBirth = (EditText) getView().findViewById(
				R.id.construction_date_input_field);
		String myFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		dateOfBirth.setText(sdf.format(localCalendar.getTime()));
	}

	private boolean areEqual(String codeA, String codeB){
		Log.d(this.getClass().getName(), "comparing " + codeA + " and " + codeB);
		if((codeA == null && codeB == null)
				|| (codeA == null && codeB != null && codeB.equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)))
				|| (codeB == null && codeA != null && codeA.equalsIgnoreCase(OpenTenureApplication.getActivity().getResources().getString(R.string.na)))
				|| (codeA != null && codeB != null && codeA.equals(codeB))){

			return true;
		}else{
			return false;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
