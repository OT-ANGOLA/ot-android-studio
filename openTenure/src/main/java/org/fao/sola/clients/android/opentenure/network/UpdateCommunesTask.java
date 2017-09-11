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
package org.fao.sola.clients.android.opentenure.network;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import org.fao.sola.clients.android.opentenure.OpenTenureApplication;
import org.fao.sola.clients.android.opentenure.maps.MainMapFragment;
import org.fao.sola.clients.android.opentenure.model.Configuration;
import org.fao.sola.clients.android.opentenure.network.API.CommunityServerAPI;
import org.fao.sola.clients.android.opentenure.network.response.Commune;

import java.util.Iterator;
import java.util.List;

public class UpdateCommunesTask extends AsyncTask<String, Void, List<Commune>> {

	@Override
	protected List<Commune> doInBackground(String... params) {
		List<Commune> communes = CommunityServerAPI.getCommunes();
		return communes;
	}

	@Override
	protected void onPostExecute(List<Commune> communes) {

		if (communes != null && (communes.size() > 0)) {

			org.fao.sola.clients.android.opentenure.model.Commune.setAllCommunesInactive();
			for (Iterator<Commune> iterator = communes.iterator(); iterator
					.hasNext();) {
				Commune networkCommune = (Commune) iterator.next();

				org.fao.sola.clients.android.opentenure.model.Commune modelCommune = new org.fao.sola.clients.android.opentenure.model.Commune();

				modelCommune.setDescription(networkCommune.getDescription());
				modelCommune.setCode(networkCommune.getCode());
				modelCommune.setDisplayValue(networkCommune.getDisplayValue());
				modelCommune.setMunicipalityCode(networkCommune.getMunicipalityCode());
				if (org.fao.sola.clients.android.opentenure.model.Commune
						.getCommune(networkCommune.getCode()) == null)

					modelCommune.add();
				else
					modelCommune.updateCommune();

			}

			OpenTenureApplication.getInstance().setCheckedCommunes(true);

			synchronized (OpenTenureApplication.getInstance()) {

				if (OpenTenureApplication.getInstance().isCheckedCommunityArea()
						&& OpenTenureApplication.getInstance().isCheckedTypes()
						&& OpenTenureApplication.getInstance().isCheckedDocTypes()
						&& OpenTenureApplication.getInstance().isCheckedLandUses()
						&& OpenTenureApplication.getInstance().isCheckedLanguages()
						&& OpenTenureApplication.getInstance().isCheckedForm()
						&& OpenTenureApplication.getInstance().isCheckedGeometryRequired()
						&& OpenTenureApplication.getInstance().isCheckedIdTypes()
						// Angola specific
						&& OpenTenureApplication.getInstance().isCheckedCountries()
						&& OpenTenureApplication.getInstance().isCheckedMunicipalities()
						&& OpenTenureApplication.getInstance().isCheckedProvinces()
						&& OpenTenureApplication.getInstance().isCheckedLandProjects()
						&& OpenTenureApplication.getInstance().isCheckedMaritalStatuses()
						&& OpenTenureApplication.getInstance().isCheckedAdjacencyTypes()

				)

				{

					OpenTenureApplication.getInstance().setInitialized(true);

					Configuration conf = Configuration
							.getConfigurationByName("isInitialized");
					conf.setValue("true");
					conf.update();

					FragmentActivity fa = (FragmentActivity) OpenTenureApplication
							.getNewsFragment();
					if (fa != null)
						fa.invalidateOptionsMenu();

					Configuration latitude = Configuration
							.getConfigurationByName(MainMapFragment.MAIN_MAP_LATITUDE);
					if (latitude != null)
						latitude.delete();

					MainMapFragment mapFrag = OpenTenureApplication
							.getMapFragment();

					mapFrag.boundCameraToInterestArea();
				}
			}

		}

	}

}
