package com.parasoft.demoapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.parasoft.demoapp.dto.CategoryDTO;
import com.parasoft.demoapp.exception.CategoryHasAtLeastOneItemException;
import com.parasoft.demoapp.exception.CategoryNameExistsAlreadyException;
import com.parasoft.demoapp.exception.CategoryNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.service.CategoryService;

/**
 * test class for CategoryController
 *
 * @see CategoryController
 */
public class CategoryControllerTest {

    @InjectMocks
    CategoryController underTest;

    @Mock
    CategoryService categoryService;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * test for getByCategoryId
     *
     * @see CategoryController#getCategoryById(Long)
     */
    @Test
    public void testGetCategoryById_normal() throws Throwable {
        // Given
        CategoryEntity getByCategoryIdResult = new CategoryEntity();
        doReturn(getByCategoryIdResult).when(categoryService).getByCategoryId(anyLong());

        //  When
        Long id = 0L;
        ResponseResult<CategoryEntity> result = underTest.getCategoryById(id);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * test for getCategoryById
     *
     * @see CategoryController#getCategoryById(Long)
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testGetCategoryById_categoryNotFoundException() throws Throwable {
        // Given
        doThrow(CategoryNotFoundException.class).when(categoryService).getByCategoryId(anyLong());

        //  When
        Long id = -1L;
        underTest.getCategoryById(id);

    }

    /**
     * test for getCategoryById
     *
     * @see CategoryController#getCategoryById(Long)
     */
    @Test(expected = ParameterException.class)
    public void testGetCategoryById_parameterException() throws Throwable {
        // Given
        doThrow(ParameterException.class).when(categoryService).getByCategoryId(anyLong());

        //  When
        Long id = -1L;
        underTest.getCategoryById(id);

    }

    /**
     * test for getByCategoryId
     *
     * @see CategoryController#getCategoryById(Long)
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testGetByCategoryId_categoryNotFoundException() throws Throwable {
        // Given
        when(categoryService.getByCategoryId(anyLong())).thenThrow(CategoryNotFoundException.class);

        // When
        Long categoryId = -1L;
        underTest.getCategoryById(categoryId);

    }

    /**
     * test for getCategoryByName
     *
     * @see CategoryController#getCategoryByName(String)
     */
    @Test
    public void testGetCategoryByName_normal() throws Throwable {
        // Given
        CategoryEntity getByCategoryNameResult = new CategoryEntity();
        doReturn(getByCategoryNameResult).when(categoryService).getByCategoryName(anyString());

        //  When
        ResponseResult<CategoryEntity> result = underTest.getCategoryByName(anyString());

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * test for getCategoryByName
     *
     * @see CategoryController#getCategoryByName(String)
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testGetCategoryByName_categoryNotFoundException() throws Throwable {
        // Given
        when(categoryService.getByCategoryName(anyString())).thenThrow(CategoryNotFoundException.class);

        //  When
        String categoryName = "category name not exists";
        underTest.getCategoryByName(categoryName);

    }

    /**
     * test for getCategoryByName
     *
     * @see CategoryController#getCategoryByName(String)
     */
    @Test(expected = ParameterException.class)
    public void testGetCategoryByName_parameterException() throws Throwable {
        // Given
        when(categoryService.getByCategoryName(anyString())).thenThrow(ParameterException.class);

        //  When
        String categoryName = " ";
        underTest.getCategoryByName(categoryName);

    }

    /**
     * test for getAllCategories
     *
     * @see CategoryController#getCategories(String, Pageable) 
     */
    @Test
    public void testGetCategories_normal() throws Throwable {
        // Given
        Page<CategoryEntity> page = new PageImpl<>(new ArrayList<>());
        String searchString = "";
        when(categoryService.getCategories(searchString, Pageable.unpaged())).thenReturn(page);

        //  When
        ResponseResult<PageInfo<CategoryEntity>> result = underTest.getCategories(searchString, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * test for addNewCategory(CategoryDTO)
     *
     * @see com.parasoft.demoapp.controller.CategoryController#addNewCategory(CategoryDTO)
     */
    @Test
    public void testAddNewCategory_normal() throws Throwable {
        // Given
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("Tanks");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");
        CategoryEntity addNewCategoryResult = mock(CategoryEntity.class);
        doReturn(addNewCategoryResult).when(categoryService).addNewCategory(anyString(), anyString(), anyString());

        //  When
        ResponseResult<CategoryEntity> result = underTest.addNewCategory(categoryDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * test for addNewCategory
     *
     * @see com.parasoft.demoapp.controller.CategoryController#addNewCategory(CategoryDTO)
     */
    @Test(expected = CategoryNameExistsAlreadyException.class)
    public void testAddNewCategory_categoryNameExistsAlreadyException() throws Throwable {
        // Given
        when(categoryService.addNewCategory(anyString(), anyString(), anyString()))
                .thenThrow(CategoryNameExistsAlreadyException.class);

        //  When
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("category name exists already");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");
        underTest.addNewCategory(categoryDto);
    }

    /**
     * test for addNewCategory
     *
     * @see com.parasoft.demoapp.controller.CategoryController#addNewCategory(CategoryDTO)
     */
    @Test(expected = ParameterException.class)
    public void testAddNewCategory_parameterException() throws Throwable {
        // Given
        when(categoryService.addNewCategory(anyString(), anyString(), anyString()))
                .thenThrow(ParameterException.class);

        //  When
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName(" ");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");
        underTest.addNewCategory(categoryDto);
    }

    /**
     * test for updateCategory
     *
     * @see com.parasoft.demoapp.controller.CategoryController#updateCategory(Long, CategoryDTO)
     */
    @Test
    public void testUpdateCategory_normal() throws Throwable {
        // Given
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("Tanks");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");
        CategoryEntity updateCategoryResult = mock(CategoryEntity.class);
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn(updateCategoryResult);

        //  When
        Long categoryId = 0L;

        ResponseResult<CategoryEntity> result =
                underTest.updateCategory(categoryId, categoryDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
    }

    /**
     * test for updateCategory(Long, Category)
     *
     * @see com.parasoft.demoapp.controller.CategoryController#updateCategory(Long, CategoryDTO)
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testUpdateCategory_categoryNotFoundException() throws Throwable {
        // Given
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(CategoryNotFoundException.class);

        //  When
        Long categoryId = -1L;
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("category not exist");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");

        underTest.updateCategory(categoryId, categoryDto);

    }

    /**
     * test for updateCategory(Long, Category)
     *
     * @see com.parasoft.demoapp.controller.CategoryController#updateCategory(Long, CategoryDTO)
     */
    @Test(expected = CategoryNameExistsAlreadyException.class)
    public void testUpdateCategory_categoryNameExistsAlreadyException() throws Throwable {
        // Given
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(CategoryNameExistsAlreadyException.class);

        //  When
        Long categoryId = 0L;
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("category exists already");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");

        underTest.updateCategory(categoryId, categoryDto);

    }

    /**
     * test for updateCategory(Long, Category)
     *
     * @see com.parasoft.demoapp.controller.CategoryController#updateCategory(Long, CategoryDTO)
     */
    @Test(expected = ParameterException.class)
    public void testUpdateCategory_parameterException() throws Throwable {
        // Given
        when(categoryService.updateCategory(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(ParameterException.class);

        //  When
        Long categoryId = 0L;
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setName("  ");
        categoryDto.setDescription("about tanks");
        categoryDto.setImagePath("/image/path");

        underTest.updateCategory(categoryId, categoryDto);

    }

    /**
     * test for deleteCategory(Long)
     *
     * @see CategoryController#deleteCategory(Long)
     */
    @Test
    public void testDeleteCategory_normal() throws Throwable {
        // Given
        doNothing().when(categoryService).removeCategory(anyLong());

        //  When
        Long id = 0L;
        ResponseResult<Long> result = underTest.deleteCategory(id);

        // Then
        assertNotNull(result);
        assertEquals(ResponseResult.STATUS_OK, result.getStatus());
        assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
        assertEquals(id, result.getData());
    }

    /**
     * test for deleteCategory(Long)
     *
     * @see CategoryController#deleteCategory(Long)
     */
    @Test(expected = CategoryNotFoundException.class)
    public void testDeleteCategory_categoryNotFoundException() throws Throwable {
        // Given
        doThrow(CategoryNotFoundException.class).when(categoryService).removeCategory(anyLong());

        //  When
        Long id = -1L;
        underTest.deleteCategory(id);
    }

    /**
     * test for deleteCategory(Long)
     *
     * @see CategoryController#deleteCategory(Long)
     */
    @Test(expected = ParameterException.class)
    public void testDeleteCategory_parameterException() throws Throwable {
        // Given
        doThrow(ParameterException.class).when(categoryService).removeCategory(anyLong());

        //  When
        Long id = -1L;
        underTest.deleteCategory(id);
    }

    /**
     * test for deleteCategory(Long)
     *
     * @see CategoryController#deleteCategory(Long)
     */
    @Test(expected = CategoryHasAtLeastOneItemException.class)
    public void testDeleteCategory_categoryHasAtLeastOneItemException() throws Throwable {
        // Given
        doThrow(CategoryHasAtLeastOneItemException.class).when(categoryService).removeCategory(anyLong());

        //  When
        Long id = 1L;
        underTest.deleteCategory(id);
    }

}
