package com.parasoft.demoapp.util;

import java.util.Comparator;

import com.parasoft.demoapp.model.global.preferences.RestEndpointEntity;

public class RouteIdSortOfRestEndpoint implements Comparator<RestEndpointEntity> {

	@Override
	public int compare(RestEndpointEntity o1, RestEndpointEntity o2) {

		return o1.getRouteId().compareTo(o2.getRouteId());
	}

}
