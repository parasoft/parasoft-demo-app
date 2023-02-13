package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.global.preferences.WebServiceMode;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.util.AuthenticationUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for PageController
 *
 * @see PageController
 */
@PrepareForTest({ AuthenticationUtil.class})
@RunWith(PowerMockRunner.class)
public class PageControllerTest {

    @InjectMocks
    PageController underTest;

    @Mock
    GlobalPreferencesService globalPreferencesService;

    @Mock
    CategoryService categoryService;

    @Mock
    ItemService itemService;

    @Mock
    GlobalPreferencesEntity globalPreferencesEntity;

    @Before
    public void setupMocks()  throws Throwable {
        MockitoAnnotations.initMocks(this);
        when(globalPreferencesService.getCurrentGlobalPreferences()).thenReturn(globalPreferencesEntity);
        when(globalPreferencesEntity.getWebServiceMode()).thenReturn(WebServiceMode.GRAPHQL);
    }

    /**
     * Test for showHomePage(Authentication, ModelMap)
     *
     * @see PageController#showHomePage(Authentication, ModelMap)
     */
    @Test
    public void testShowHomePage_approver() throws Throwable {
        // Given
        String roleType = RoleType.ROLE_APPROVER.toString();
        mockAuthenticationUtil(RoleType.ROLE_APPROVER.toString());
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showHomePage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);
        String role = roleType.toLowerCase().substring(5);

        // Then
        assertEquals(role, result);
    }

    /**
     * Test for showHomePage(Authentication, ModelMap)
     *
     * @see PageController#showHomePage(Authentication, ModelMap)
     */
    @Test
    public void testShowHomePage_purchaser() throws Throwable {
        // Given
        String roleType = RoleType.ROLE_PURCHASER.toString();
        mockAuthenticationUtil(roleType);
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showHomePage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);
        String role = roleType.toLowerCase().substring(5);

        // Then
        assertEquals(role, result);
    }

    /**
     * Test for showHomePage(Authentication, ModelMap)
     *
     * @see PageController#showHomePage(Authentication, ModelMap)
     */
    @Test
    public void testShowHomePage_unsupportedRole() throws Throwable {
        // Given
        String roleType = "UNSUPPORTED_ROLE";
        mockAuthenticationUtil(roleType);
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showHomePage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showHomePage(Authentication, ModelMap)
     *
     * @see PageController#showHomePage(Authentication, ModelMap)
     */
    @Test
    public void testShowHomePage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        String roleType = RoleType.ROLE_PURCHASER.toString();
        mockAuthenticationUtil(roleType);
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showHomePage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showHomePage(Authentication, ModelMap)
     *
     * @see PageController#showHomePage(Authentication, ModelMap)
     */
    @Test
    public void testShowHomePage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        String roleType = RoleType.ROLE_PURCHASER.toString();
        mockAuthenticationUtil(roleType);
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showHomePage(new TestingAuthenticationToken(new Object(), new Object()), modelMap);

        // Then
        assertEquals("error/500", result);
    }

    private void mockAuthenticationUtil(String roleToReturn) throws Exception {
        PowerMockito.mockStatic(AuthenticationUtil.class);
        PowerMockito.doReturn(roleToReturn).when(AuthenticationUtil.class, "getUserRoleNameInAuthentication", any(Authentication.class));
    }

    /**
     * test for showDemoAdminPage(ModelMap)
     *
     * @see PageController#showDemoAdminPage(ModelMap)
     */
    @Test
    public void testShowDemoAdminPage_normal() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showDemoAdminPage(modelMap);

        // Then
        assertEquals("demoAdmin", result);
    }

    /**
     * test for showDemoAdminPage(ModelMap)
     *
     * @see PageController#showDemoAdminPage(ModelMap)
     */
    @Test
    public void testShowDemoAdminPage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showDemoAdminPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * test for showDemoAdminPage(ModelMap)
     *
     * @see PageController#showDemoAdminPage(ModelMap)
     */
    @Test
    public void testShowDemoAdminPage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showDemoAdminPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showOrderWizardPage(ModelMap)
     *
     * @see PageController#showOrderWizardPage(ModelMap)
     */
    @Test
    public void testShowOrderWizardPage_normal() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrderWizardPage(modelMap);

        // Then
        assertEquals("orderWizard", result);
    }

    /**
     * Test for showOrderWizardPage(ModelMap)
     *
     * @see PageController#showOrderWizardPage(ModelMap)
     */
    @Test
    public void testShowOrderWizardPage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrderWizardPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showOrderWizardPage(ModelMap)
     *
     * @see PageController#showOrderWizardPage(ModelMap)
     */
    @Test
    public void testShowOrderWizardPage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrderWizardPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }


    /**
     * Test for showOrdersPage(ModelMap)
     *
     * @see PageController#showOrdersPage(ModelMap)
     */
    @Test
    public void testShowOrdersPage_normal() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrdersPage(modelMap);

        // Then
        assertEquals("orders", result);
    }

    /**
     * Test for showOrdersPage(ModelMap)
     *
     * @see PageController#showOrdersPage(ModelMap)
     */
    @Test
    public void testShowOrdersPage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrdersPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showOrdersPage(ModelMap)
     *
     * @see PageController#showOrdersPage(ModelMap)
     */
    @Test
    public void testShowOrdersPage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showOrdersPage(modelMap);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showItemDetailsPage(ModelMap, itemId)
     *
     * @see PageController#showItemDetailsPage(ModelMap, Long)
     */
    @Test
    public void testShowItemDetailsPage_normal() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);
        doReturn(true).when(itemService).existsByItemId(anyLong());

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemDetailsPage(modelMap, 1L);

        // Then
        assertEquals("item", result);
    }

    /**
     * Test for showItemDetailsPage(ModelMap, itemId)
     *
     * @see PageController#showItemDetailsPage(ModelMap, Long)
     */
    @Test
    public void testShowItemDetailsPage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemDetailsPage(modelMap, 1L);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showItemDetailsPage(ModelMap, itemId)
     *
     * @see PageController#showItemDetailsPage(ModelMap, Long)
     */
    @Test
    public void testShowItemDetailsPage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemDetailsPage(modelMap, 1L);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showItemDetailsPage(ModelMap, itemId)
     *
     * @see PageController#showItemDetailsPage(ModelMap, Long)
     */
    @Test
    public void testShowItemDetailsPage_error_notFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);
        doReturn(false).when(itemService).existsByItemId(anyLong());

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemDetailsPage(modelMap, 100L);

        // Then
        assertEquals("error/404", result);
    }

    /**
     * Test for showItemsListPage(ModelMap, categoryId)
     *
     * @see PageController#showItemsListPage(ModelMap, Long)
     */
    @Test
    public void testShowItemsListPage_normal() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);
        doReturn(true).when(categoryService).existsByCategoryId(anyLong());

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemsListPage(modelMap, 1L);

        // Then
        assertEquals("category", result);
    }

    /**
     * Test for showItemsListPage(ModelMap, categoryId)
     *
     * @see PageController#showItemsListPage(ModelMap, Long)
     */
    @Test
    public void testShowItemsListPage_error_globalPreferencesMoreThanOneException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesMoreThanOneException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemsListPage(modelMap, 1L);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showItemsListPage(ModelMap, categoryId)
     *
     * @see PageController#showItemsListPage(ModelMap, Long)
     */
    @Test
    public void testShowItemsListPage_error_globalPreferencesNotFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenThrow(GlobalPreferencesNotFoundException.class);

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemsListPage(modelMap, 1L);

        // Then
        assertEquals("error/500", result);
    }

    /**
     * Test for showItemsListPage(ModelMap, categoryId)
     *
     * @see PageController#showItemsListPage(ModelMap, Long)
     */
    @Test
    public void testShowItemsListPage_error_notFoundException() throws Throwable {
        // Given
        when(globalPreferencesService.getCurrentIndustry()).thenReturn(IndustryType.DEFENSE);
        doReturn(false).when(categoryService).existsByCategoryId(anyLong());

        // When
        ModelMap modelMap = new ModelMap();
        String result = underTest.showItemsListPage(modelMap, 1L);

        // Then
        assertEquals("error/404", result);
    }
}