/**d
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fao.sola.clients.android.opentenure.model.AdjacenciesNotes;
import org.fao.sola.clients.android.opentenure.model.Adjacency;
import org.fao.sola.clients.android.opentenure.model.AdjacencyType;
import org.fao.sola.clients.android.opentenure.model.Claim;
import org.fao.sola.clients.android.opentenure.model.LandProject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdjacentClaimsFragment extends ListFragment {

	private View rootView;
	private ClaimDispatcher claimActivity;
	private ModeDispatcher modeActivity;
	private Map<String, String> keyValueAdjacencyTypeMap;
	private Map<String, String> valueKeyAdjacencyTypeMap;

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
	}

	public AdjacentClaimsFragment() {
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

		inflater.inflate(R.menu.adjacencies, menu);

		super.onCreateOptionsMenu(menu, inflater);
		
		Claim claim = Claim.getClaim(claimActivity.getClaimId());
		
		if (claim != null && !claim.isModifiable() ) {
			menu.removeItem(R.id.action_save);
		}
		
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.adjacent_claims_list, container,
				false);

		setHasOptionsMenu(true);
		setRetainInstance(true);
		InputMethodManager imm = (InputMethodManager) rootView.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

		Spinner northAdjacencyType = (Spinner) rootView
				.findViewById(R.id.northAdjacencyTypeSpinner);
		Spinner southAdjacencyType = (Spinner) rootView
				.findViewById(R.id.southAdjacencyTypeSpinner);
		Spinner eastAdjacencyType = (Spinner) rootView
				.findViewById(R.id.eastAdjacencyTypeSpinner);
		Spinner westAdjacencyType = (Spinner) rootView
				.findViewById(R.id.westAdjacencyTypeSpinner);
		AdjacencyType at = new AdjacencyType();
		keyValueAdjacencyTypeMap = at.getKeyValueMap(OpenTenureApplication
				.getInstance().getLocalization(),true);
		valueKeyAdjacencyTypeMap = at.getValueKeyMap(OpenTenureApplication
				.getInstance().getLocalization(),true);

		List<String> atlist = new ArrayList<String>();
		SortedSet<String> keys;
		keys = new TreeSet<String>(keyValueAdjacencyTypeMap.keySet());
		for (String key : keys) {
			String value = keyValueAdjacencyTypeMap.get(key);
			atlist.add(value);
			// do something
		}

		ArrayAdapter<String> northDataAdapterAdjacency = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				atlist) {
		};
		northDataAdapterAdjacency.setDropDownViewResource(R.layout.my_spinner);
		northAdjacencyType.setAdapter(northDataAdapterAdjacency);

		ArrayAdapter<String> southDataAdapterAdjacency = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				atlist) {
		};
		southDataAdapterAdjacency.setDropDownViewResource(R.layout.my_spinner);
		southAdjacencyType.setAdapter(southDataAdapterAdjacency);

		ArrayAdapter<String> eastDataAdapterAdjacency = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				atlist) {
		};
		eastDataAdapterAdjacency.setDropDownViewResource(R.layout.my_spinner);
		eastAdjacencyType.setAdapter(eastDataAdapterAdjacency);

		ArrayAdapter<String> westDataAdapterAdjacency = new ArrayAdapter<String>(
				OpenTenureApplication.getContext(), R.layout.my_spinner,
				atlist) {
		};
		westDataAdapterAdjacency.setDropDownViewResource(R.layout.my_spinner);
		westAdjacencyType.setAdapter(westDataAdapterAdjacency);

		Claim claim = Claim.getClaim(claimActivity.getClaimId());
		if (claim != null)
			load(claim);

		update();

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (modeActivity.getMode().compareTo(ModeDispatcher.Mode.MODE_RW) == 0) {

			Intent intent = new Intent(rootView.getContext(),
					ClaimActivity.class);
			intent.putExtra(ClaimActivity.CLAIM_ID_KEY,
					((TextView) v.findViewById(R.id.claim_id)).getText());
			intent.putExtra(ClaimActivity.MODE_KEY,
					ModeDispatcher.Mode.MODE_RO.toString());
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {

		case R.id.action_save:

			if (AdjacenciesNotes
					.getAdjacenciesNotes(claimActivity.getClaimId()) != null)
				return updateNotes();

			return save();
		}
		return false;
	}

	protected boolean save() {

		Toast toast;
		String claimId = claimActivity.getClaimId();
		
		if(claimId == null){			
			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_save_claim_before_adding_content, Toast.LENGTH_LONG);
			toast.show();
			return true;			
		}

		AdjacenciesNotes adjacenciesNotes = new AdjacenciesNotes();
		adjacenciesNotes.setClaimId(claimId);
		String northAdjacency = ((EditText) rootView
				.findViewById(R.id.north_adjacency)).getText().toString();
		adjacenciesNotes.setNorthAdjacency(northAdjacency);

		String northAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.northAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setNorthAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(northAdjacencyTypeDispValue));

		String eastAdjacency = ((EditText) rootView
				.findViewById(R.id.east_adjacency)).getText().toString();
		adjacenciesNotes.setEastAdjacency(eastAdjacency);

		String eastAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.eastAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setEastAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(eastAdjacencyTypeDispValue));

		String southAdjacency = ((EditText) rootView
				.findViewById(R.id.south_adjacency)).getText().toString();
		adjacenciesNotes.setSouthAdjacency(southAdjacency);

		String southAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.southAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setSouthAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(southAdjacencyTypeDispValue));

		String westAdjacency = ((EditText) rootView
				.findViewById(R.id.west_adjacency)).getText().toString();
		adjacenciesNotes.setWestAdjacency(westAdjacency);

		String westAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.westAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setWestAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(westAdjacencyTypeDispValue));

		if(
				northAdjacency == null
				|| northAdjacency.equalsIgnoreCase("")
				|| northAdjacencyTypeDispValue == null
				|| northAdjacencyTypeDispValue.equalsIgnoreCase("")
				|| southAdjacency == null
				|| southAdjacency.equalsIgnoreCase("")
				|| southAdjacencyTypeDispValue == null
				|| southAdjacencyTypeDispValue.equalsIgnoreCase("")
				|| eastAdjacency == null
				|| eastAdjacency.equalsIgnoreCase("")
				|| eastAdjacencyTypeDispValue == null
				|| eastAdjacencyTypeDispValue.equalsIgnoreCase("")
				|| westAdjacency == null
				|| westAdjacency.equalsIgnoreCase("")
				|| westAdjacencyTypeDispValue == null
				|| westAdjacencyTypeDispValue.equalsIgnoreCase("")

				){
			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_unable_to_save_missing_adjacency, Toast.LENGTH_LONG);
			toast.show();
			return true;

		}
		int result = AdjacenciesNotes.createAdjacenciesNotes(adjacenciesNotes);

		

		if (result == 1) {

			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_adjacencies_saved, Toast.LENGTH_LONG);
			toast.show();

			return true;
		} else {

			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_adjacencies_not_saved, Toast.LENGTH_LONG);
			toast.show();

			return false;
		}
	}

	protected boolean updateNotes() {

		
		Toast toast;
		String claimId = claimActivity.getClaimId();
		
		if(claimId == null){			
			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_save_claim_before_adding_content, Toast.LENGTH_LONG);
			toast.show();
			return true;			
		}

		AdjacenciesNotes adjacenciesNotes = new AdjacenciesNotes();
		adjacenciesNotes.setClaimId(claimId);
		adjacenciesNotes.setNorthAdjacency(((EditText) rootView
				.findViewById(R.id.north_adjacency)).getText().toString());

		String northAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.northAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setNorthAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(northAdjacencyTypeDispValue));

		adjacenciesNotes.setEastAdjacency(((EditText) rootView
				.findViewById(R.id.east_adjacency)).getText().toString());

		String eastAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.eastAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setEastAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(eastAdjacencyTypeDispValue));

		adjacenciesNotes.setSouthAdjacency(((EditText) rootView
				.findViewById(R.id.south_adjacency)).getText().toString());

		String southAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.southAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setSouthAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(southAdjacencyTypeDispValue));

		adjacenciesNotes.setWestAdjacency(((EditText) rootView
				.findViewById(R.id.west_adjacency)).getText().toString());

		String westAdjacencyTypeDispValue = (String) ((Spinner) rootView
				.findViewById(R.id.westAdjacencyTypeSpinner)).getSelectedItem();
		adjacenciesNotes.setWestAdjacencyTypeCode(valueKeyAdjacencyTypeMap.get(westAdjacencyTypeDispValue));

		int result = AdjacenciesNotes.updateAdjacenciesNotes(adjacenciesNotes);

		if (result == 1) {

			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_adjacencies_saved, Toast.LENGTH_LONG);
			toast.show();

			return true;
		} else {

			toast = Toast.makeText(OpenTenureApplication.getContext(),
					R.string.message_adjacencies_not_saved, Toast.LENGTH_LONG);
			toast.show();

			return false;
		}
	}

	protected void update() {

		String claimId = claimActivity.getClaimId();

		if (claimId != null) {

			List<Adjacency> adjacencies = Adjacency.getAdjacencies(claimId);
			List<AdjacentClaimListTO> claimListTOs = new ArrayList<AdjacentClaimListTO>();

			for (Adjacency adjacency : adjacencies) {

				Claim adjacentClaim;
				String direction;

				if (claimActivity.getClaimId().equalsIgnoreCase(
						adjacency.getSourceClaimId())) {
					adjacentClaim = Claim.getClaim(adjacency.getDestClaimId());
					direction = Adjacency.getCardinalDirection(
							rootView.getContext(),
							adjacency.getCardinalDirection());
				} else {
					adjacentClaim = Claim
							.getClaim(adjacency.getSourceClaimId());
					direction = Adjacency.getCardinalDirection(rootView
							.getContext(), Adjacency
							.getReverseCardinalDirection(adjacency
									.getCardinalDirection()));
				}

				AdjacentClaimListTO acto = new AdjacentClaimListTO();
				acto.setSlogan(adjacentClaim.getName() + ", "
						+ getResources().getString(R.string.by) + ": "
						+ adjacentClaim.getPerson().getFirstName() + " "
						+ adjacentClaim.getPerson().getLastName());
				acto.setId(adjacentClaim.getClaimId());
				acto.setCardinalDirection(direction);
				acto.setStatus(adjacentClaim.getStatus());

				claimListTOs.add(acto);
			}
			ArrayAdapter<AdjacentClaimListTO> adapter = new AdjacentClaimsListAdapter(
					rootView.getContext(), claimListTOs);
			setListAdapter(adapter);
			adapter.notifyDataSetChanged();

		}
	}

	public void load(Claim claim) {

		AdjacenciesNotes adNotes = AdjacenciesNotes.getAdjacenciesNotes(claim
				.getClaimId());
		AdjacencyType at = new AdjacencyType();

		if (claim != null && adNotes != null) {

			((EditText) rootView.findViewById(R.id.north_adjacency))
					.setText(adNotes.getNorthAdjacency());

			((EditText) rootView.findViewById(R.id.south_adjacency))
					.setText(adNotes.getSouthAdjacency());

			((EditText) rootView.findViewById(R.id.east_adjacency))
					.setText(adNotes.getEastAdjacency());

			((EditText) rootView.findViewById(R.id.west_adjacency))
					.setText(adNotes.getWestAdjacency());

			Spinner northAdjacencyType = (Spinner) rootView
					.findViewById(R.id.northAdjacencyTypeSpinner);
			Spinner southAdjacencyType = (Spinner) rootView
					.findViewById(R.id.southAdjacencyTypeSpinner);
			Spinner eastAdjacencyType = (Spinner) rootView
					.findViewById(R.id.eastAdjacencyTypeSpinner);
			Spinner westAdjacencyType = (Spinner) rootView
					.findViewById(R.id.westAdjacencyTypeSpinner);

			northAdjacencyType.setSelection(at.getIndexByCodeType(
					adNotes.getNorthAdjacencyTypeCode(), true));

			southAdjacencyType.setSelection(at.getIndexByCodeType(
					adNotes.getSouthAdjacencyTypeCode(), true));

			eastAdjacencyType.setSelection(at.getIndexByCodeType(
					adNotes.getEastAdjacencyTypeCode(), true));

			westAdjacencyType.setSelection(at.getIndexByCodeType(
					adNotes.getWestAdjacencyTypeCode(), true));

			if (modeActivity.getMode().compareTo(ModeDispatcher.Mode.MODE_RO) == 0) {
				((EditText) rootView.findViewById(R.id.north_adjacency))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.north_adjacency))
				.setLongClickable(false);

				((EditText) rootView.findViewById(R.id.south_adjacency))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.south_adjacency))
				.setLongClickable(false);

				((EditText) rootView.findViewById(R.id.east_adjacency))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.east_adjacency))
				.setLongClickable(false);

				((EditText) rootView.findViewById(R.id.west_adjacency))
						.setFocusable(false);
				((EditText) rootView.findViewById(R.id.west_adjacency))
				.setLongClickable(false);
				((Spinner) rootView.findViewById(R.id.northAdjacencyTypeSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.northAdjacencyTypeSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.southAdjacencyTypeSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.southAdjacencyTypeSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.eastAdjacencyTypeSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.eastAdjacencyTypeSpinner))
						.setClickable(false);
				((Spinner) rootView.findViewById(R.id.westAdjacencyTypeSpinner))
						.setFocusable(false);
				((Spinner) rootView.findViewById(R.id.westAdjacencyTypeSpinner))
						.setClickable(false);
			}

		}
	}

}
