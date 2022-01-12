package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.config.ParasoftJDBCProxyConfig;
import com.parasoft.demoapp.config.datasource.IndustryDataSourceConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;

/**
 * Test class for ParasoftJDBCProxyService
 *
 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class ParasoftJDBCProxyServiceSpringTest {

	// Component under test
	@Autowired
	ParasoftJDBCProxyService service;

	@Autowired
	private IndustryDataSourceConfig industryDataSourceConfig;

	@Autowired
	private IndustryRoutingDataSource industryRoutingDataSource;

	/**
	 * Test for refreshParasoftJDBCProxyDataSource()
	 *
	 * @see com.parasoft.demoapp.service.ParasoftJDBCProxyService#refreshParasoftJDBCProxyDataSource()
	 */
	@Test
	public void testRefreshParasoftJDBCProxyDataSource() throws Throwable {
		// Given
		// Switch to proxy first time
		String parasoftVirtualizeServerUrl = "server url";
		String parasoftVirtualizeServerPath = "/myVirtualDB";
		String parasoftVirtualizeGroupId = "pda-group";

		String currentIndustry = IndustryRoutingDataSource.currentIndustry.getValue();
		IndustryRoutingDataSource.useParasoftJDBCProxy = true;
		IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = true;
		IndustryRoutingDataSource.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;
		IndustryRoutingDataSource.parasoftVirtualizeServerPath = parasoftVirtualizeServerPath;
		IndustryRoutingDataSource.parasoftVirtualizeGroupId = parasoftVirtualizeGroupId;

		// When
		service.refreshParasoftJDBCProxyDataSource();

		// Then
		Map<Object, Object> industryDatasources = industryDataSourceConfig.getIndustryDataSources();
		String proxyDatasourceKey = currentIndustry + IndustryRoutingDataSource.PARASOFT_JDBC_PROXY_DATA_SOURCE_SUFFIX;
		assertEquals(proxyDatasourceKey, industryRoutingDataSource.determineCurrentLookupKey());
		assertTrue(industryDatasources.containsKey(proxyDatasourceKey));
		Object proxyDatasource1 = industryDatasources.get(proxyDatasourceKey);

		assertEquals(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_DIRECT_DEFAULT_VALUE,
						System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_DIRECT_KEY));
		assertEquals(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_DEFAULT_VALUE,
						System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_KEY));
		assertEquals(parasoftVirtualizeGroupId,
						System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_KEY));
		assertEquals(parasoftVirtualizeServerUrl,
				System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_KEY));
		assertEquals(parasoftVirtualizeServerPath,
				System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_KEY));

		// When
		service.refreshParasoftJDBCProxyDataSource();

		// Then
		industryDatasources = industryDataSourceConfig.getIndustryDataSources();
		assertEquals(proxyDatasourceKey, industryRoutingDataSource.determineCurrentLookupKey());
		assertTrue(industryDatasources.containsKey(proxyDatasourceKey));
		Object proxyDatasource2 = industryDatasources.get(proxyDatasourceKey);

		assertNotEquals(proxyDatasource1, proxyDatasource2);

		// Given
		// Switch to original datasource
		IndustryRoutingDataSource.useParasoftJDBCProxy = false;
		IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected = true;
		IndustryRoutingDataSource.parasoftVirtualizeServerUrl = parasoftVirtualizeServerUrl;

		// When
		service.refreshParasoftJDBCProxyDataSource();

		// Then
		industryDatasources = industryDataSourceConfig.getIndustryDataSources();
		assertEquals(currentIndustry, industryRoutingDataSource.determineCurrentLookupKey());
		assertFalse(industryDatasources.containsKey(proxyDatasourceKey));

		assertEquals(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_DIRECT_DEFAULT_VALUE,
						System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_DIRECT_KEY));
		assertEquals(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_DEFAULT_VALUE,
						System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_REGISTER_JDBCPROXYDRIVER_IN_DRIVERMANAGER_KEY));
		assertEquals(parasoftVirtualizeGroupId,
				System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_GROUP_ID_KEY));
		assertEquals(parasoftVirtualizeServerUrl,
				System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_URL_KEY));
		assertEquals(parasoftVirtualizeServerPath,
				System.getProperty(ParasoftJDBCProxyConfig.PARASOFT_JDBC_PROXY_VIRTUALIZE_SERVER_PATH_KEY));

		// Finally
		IndustryRoutingDataSource.useParasoftJDBCProxy = false;
	}
}