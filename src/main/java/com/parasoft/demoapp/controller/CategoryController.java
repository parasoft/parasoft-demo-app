package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.CategoryDTO;
import com.parasoft.demoapp.exception.CategoryHasAtLeastOneItemException;
import com.parasoft.demoapp.exception.CategoryNameExistsAlreadyException;
import com.parasoft.demoapp.exception.CategoryNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "assets/categories")
@Controller
@RequestMapping(value = {"/v1/assets", "/proxy/v1/assets"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(description = "Obtain category by id.")
    @ApiResponse(responseCode = "200", description = "Category with corresponding id was returned.")
    @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "404", description = "No category with corresponding id.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @GetMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseResult<CategoryEntity> getCategoryById(@PathVariable Long categoryId)
            throws CategoryNotFoundException, ParameterException {

        ResponseResult<CategoryEntity> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(categoryService.getByCategoryId(categoryId));

        return response;
    }

    @Operation(description = "Obtain category by name.")
    @ApiResponse(responseCode = "200", description = "Category with corresponding name was returned.")
    @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
                content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "404", description = "No category with corresponding name.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @GetMapping("/categories/name/{categoryName}")
    @ResponseBody
    public ResponseResult<CategoryEntity> getCategoryByName(@PathVariable String categoryName)
            throws CategoryNotFoundException, ParameterException {

        ResponseResult<CategoryEntity> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(categoryService.getByCategoryName(categoryName));

        return response;
    }

    @Operation(description = "Obtain all categories under the current conditions.")
    @ApiResponse(responseCode = "200", description = "All categories under the current conditions were returned.")
    @GetMapping("/categories")
    @ResponseBody
    public ResponseResult<PageInfo<CategoryEntity>> getCategories(
            @RequestParam(required = false) String searchString,
            @ParameterObject
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {

        ResponseResult<PageInfo<CategoryEntity>> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        PageInfo<CategoryEntity> pageInfo =
                new PageInfo<>(categoryService.getCategories(searchString, pageable));

        response.setData(pageInfo);

        return response;
    }

    @Operation(description = "Add a new category.")
    @ApiResponse(responseCode = "200", description = "New category successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "401",
                 description = "You are not authorized to add a new category.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @PostMapping("/categories")
    @ResponseBody
    public ResponseResult<CategoryEntity> addNewCategory(@RequestBody CategoryDTO categoryDto)
            throws CategoryNameExistsAlreadyException, ParameterException {

        ResponseResult<CategoryEntity> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(categoryService.addNewCategory(
                categoryDto.getName(), categoryDto.getDescription(), categoryDto.getImagePath()));

        return response;
    }

    @Operation(description = "Update the category by id.")
    @ApiResponse(responseCode = "200", description = "Update successful.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "401",
                 description = "You are not authorized to update the category.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "404", description = "No category with corresponding id.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @PutMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseResult<CategoryEntity> updateCategory(
            @PathVariable Long categoryId, @RequestBody CategoryDTO categoryDto)
            throws CategoryNotFoundException, ParameterException, CategoryNameExistsAlreadyException {

        ResponseResult<CategoryEntity> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        response.setData(categoryService.updateCategory(
                categoryId, categoryDto.getName(), categoryDto.getDescription(), categoryDto.getImagePath()));

        return response;
    }

    @Operation(description = "Delete the category by id.")
    @ApiResponse(responseCode = "200", description = "Category with the corresponding id successfully deleted.")
    @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "401",
                 description = "You are not authorized to delete the category.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @ApiResponse(responseCode = "404", description = "No category with corresponding id.",
                 content = {@Content(schema = @Schema(hidden = true))})
    @DeleteMapping("/categories/{categoryId}")
    @ResponseBody
    public ResponseResult<Long> deleteCategory(@PathVariable Long categoryId)
            throws CategoryNotFoundException, ParameterException, CategoryHasAtLeastOneItemException {

        ResponseResult<Long> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        categoryService.removeCategory(categoryId);

        response.setData(categoryId);

        return response;
    }
}
