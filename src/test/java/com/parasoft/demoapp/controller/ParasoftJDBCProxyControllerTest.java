package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.dto.ParasoftJDBCProxyStatusResponseDTO;

/**
 * Test class for ParasoftJDBCProxyController
 *
 * @see com.parasoft.demoapp.controller.ParasoftJDBCProxyController
 */
public class ParasoftJDBCProxyControllerTest {

	/**
	 * Test for getParasoftJDBCProxyStatus()
	 *
	 * @see com.parasoft.demoapp.controller.ParasoftJDBCProxyController#getParasoftJDBCProxyStatus()
	 */
	@Test
	public void testGetParasoftJDBCProxyStatus() throws Throwable {
		// Given
		boolean useParasoftJDBCProxy = true;
		String parasoftVirtualizeServerUrl = "http://localhost:9080";
		boolean isParasoftVirtualizeServerUrlConnected = true;
		
		IndustryRoutingDataSource.useParasoftJDBCProxy = useParasoftJDBCProxy;
        IndustryRoutingDataSource.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;
        IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = isParasoftVirtualizeServerUrlConnected;
		
		// Given
		ParasoftJDBCProxyController underTest = new ParasoftJDBCProxyController();

		// When
		ResponseResult<ParasoftJDBCProxyStatusResponseDTO> result = underTest.getParasoftJDBCProxyStatus();

		// Then
		assertNotNull(result);
		assertEquals(ResponseResult.STATUS_OK, (int)result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertNotNull(result.getData());
		assertEquals(useParasoftJDBCProxy, result.getData().getUseParasoftJDBCProxy());
		assertEquals(parasoftVirtualizeServerUrl, result.getData().getParasoftVirtualizeServerUrl());
		assertEquals(isParasoftVirtualizeServerUrlConnected, result.getData().getIsParasoftVirtualizeServerUrlConnected());

		// Finally
		IndustryRoutingDataSource.useParasoftJDBCProxy = false;
	}
}