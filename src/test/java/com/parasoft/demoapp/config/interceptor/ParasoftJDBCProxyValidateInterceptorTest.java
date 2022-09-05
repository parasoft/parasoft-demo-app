package com.parasoft.demoapp.config.interceptor;

import com.parasoft.demoapp.config.datasource.IndustryDataSourceConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.exception.VirtualizeServerUrlException;
import com.parasoft.demoapp.service.ParasoftJDBCProxyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ParasoftJDBCProxyValidateInterceptorTest {

    @InjectMocks
    ParasoftJDBCProxyValidateInterceptor underTest;

    @Mock
    IndustryDataSourceConfig industryDataSourceConfig;

    @Mock
    ParasoftJDBCProxyService parasoftJDBCProxyService;

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    public void tearDown() {
        IndustryRoutingDataSource.useParasoftJDBCProxy = false;
    }

    /**
     * Test for preHandle(HttpServletRequest, HttpServletResponse, Object)
     * <br/>
     * If Parasoft JDBC Proxy disabled, http requests do not need to be validated. The request will pass through.
     *
     * @see com.parasoft.demoapp.config.interceptor.ParasoftJDBCProxyValidateInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
     */
    @Test
    void preHandle_notUseParasoftJDBCProxy() throws Exception {
        // Given
        IndustryRoutingDataSource.useParasoftJDBCProxy = false;

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        Object handler = null;

        // When
        boolean result = underTest.preHandle(httpServletRequest, httpServletResponse, handler);

        // Then
        assertTrue(result);
        verify(parasoftJDBCProxyService, times(0)).validateVirtualizeServerUrl(anyString());
        verify(parasoftJDBCProxyService, times(1)).refreshParasoftJDBCProxyDataSource();
    }

    /**
     * Test for preHandle(HttpServletRequest, HttpServletResponse, Object)
     * <br/>
     * If Parasoft JDBC Proxy enabled, every http request needs to be validated. The request will be intercepted if proxy can be connected.
     *
     * @see com.parasoft.demoapp.config.interceptor.ParasoftJDBCProxyValidateInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
     */
    @Test
    void preHandle_useParasoftJDBCProxy_failToConnect() throws Exception {
        // Given
        IndustryRoutingDataSource.useParasoftJDBCProxy = true;
        doThrow(new VirtualizeServerUrlException("Refuse to connect")).when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        Object handler = null;

        // When
        boolean result = underTest.preHandle(httpServletRequest, httpServletResponse, handler);

        // Then
        assertFalse(result);
        assertEquals("{\"status\":0,\"message\":\"Refuse to connect\",\"data\":\"http://localhost:9080\"}", httpServletResponse.getContentAsString());
        assertEquals(500, httpServletResponse.getStatus());
        verify(parasoftJDBCProxyService, times(1)).validateVirtualizeServerUrl(anyString());
        verify(parasoftJDBCProxyService, times(0)).refreshParasoftJDBCProxyDataSource();
    }

    /**
     * Test for preHandle(HttpServletRequest, HttpServletResponse, Object)
     * <br/>
     * If Parasoft JDBC Proxy enabled, every http request needs to be validated. The request pass through only proxy can be connected.
     *
     * @see com.parasoft.demoapp.config.interceptor.ParasoftJDBCProxyValidateInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
     */
    @Test
    void preHandle_useParasoftJDBCProxy_successToConnect_needToRefreshProxyDataSource() throws Exception {
        // Given
        IndustryRoutingDataSource.useParasoftJDBCProxy = true;
        doNothing().when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());
        doNothing().when(parasoftJDBCProxyService).refreshParasoftJDBCProxyDataSource();

        Map<String, Object> industryDataSources = new HashMap<>();
        industryDataSources.put("defense", null); // No defense_virtualize datasource(for Parasoft JDBC Proxy use), need to refresh datasource
        doReturn(industryDataSources).when(industryDataSourceConfig).getIndustryDataSources();
        doReturn("defense_virtualize").when(parasoftJDBCProxyService).getProxyKeyOfCurrentIndustry();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        Object handler = null;

        // When
        boolean result = underTest.preHandle(httpServletRequest, httpServletResponse, handler);

        // Then
        assertTrue(IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected);
        assertTrue(result);
        assertEquals("", httpServletResponse.getContentAsString());
        assertEquals(200, httpServletResponse.getStatus());
        verify(parasoftJDBCProxyService, times(1)).validateVirtualizeServerUrl(anyString());
        verify(parasoftJDBCProxyService, times(1)).refreshParasoftJDBCProxyDataSource();
    }

    /**
     * Test for preHandle(HttpServletRequest, HttpServletResponse, Object)
     * <br/>
     * If Parasoft JDBC Proxy enabled, every http request needs to be validated. The request pass through only proxy can be connected.
     *
     * @see com.parasoft.demoapp.config.interceptor.ParasoftJDBCProxyValidateInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
     */
    @Test
    void preHandle_useParasoftJDBCProxy_successToConnect_noNeedToRefreshProxyDataSource() throws Exception {
        // Given
        IndustryRoutingDataSource.useParasoftJDBCProxy = true;
        doNothing().when(parasoftJDBCProxyService).validateVirtualizeServerUrl(anyString());
        doNothing().when(parasoftJDBCProxyService).refreshParasoftJDBCProxyDataSource();

        Map<String, Object> industryDataSources = new HashMap<>();
        industryDataSources.put("defense_virtualize", null); // Has defense_virtualize datasource(for Parasoft JDBC Proxy use), no need to refresh datasource
        doReturn(industryDataSources).when(industryDataSourceConfig).getIndustryDataSources();
        doReturn("defense_virtualize").when(parasoftJDBCProxyService).getProxyKeyOfCurrentIndustry();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        Object handler = null;

        // When
        boolean result = underTest.preHandle(httpServletRequest, httpServletResponse, handler);

        // Then
        assertTrue(IndustryRoutingDataSource.isParasoftVirtualizeServerUrlConnected);
        assertTrue(result);
        assertEquals("", httpServletResponse.getContentAsString());
        assertEquals(200, httpServletResponse.getStatus());
        verify(parasoftJDBCProxyService, times(1)).validateVirtualizeServerUrl(anyString());
        verify(parasoftJDBCProxyService, times(0)).refreshParasoftJDBCProxyDataSource();
    }
}