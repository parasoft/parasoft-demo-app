package com.parasoft.demoapp.util;

import java.util.Comparator;

import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;

public class BugsTypeSortOfDemoBugs implements Comparator<DemoBugEntity> {

	@Override
	public int compare(DemoBugEntity o1, DemoBugEntity o2) {

		return o1.getDemoBugsType().getValue().compareTo(o2.getDemoBugsType().getValue());
	}

}
