package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "search")
@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Operation(description = "Search items by name or description, fuzzy search and ignore case.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "All items under the current conditions were returned."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
                    content = {@Content(schema = @Schema(hidden = true))})
    })
    @GetMapping("/v1/search/items")
    @ResponseBody
    public ResponseResult<PageInfo<ItemEntity>> searchItems(
         @Parameter(description = "Search string.") String key,
         @ParameterObject
         @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable)
         throws ParameterException {

        ResponseResult<PageInfo<ItemEntity>> response = ResponseResult.getInstance(
                ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        PageInfo<ItemEntity> searchResult = new PageInfo<>(itemService.searchItemsByNameOrDescription(key, pageable));
        response.setData(searchResult);

        return response;
    }

    @Operation(description = "Search categories by name or description, fuzzy search and ignore case.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "All categories under the current conditions were returned."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
                    content = {@Content(schema = @Schema(hidden = true))})
    })
    @GetMapping("/v1/search/categories")
    @ResponseBody
    public ResponseResult<PageInfo<CategoryEntity>> searchCategories(
            @RequestParam String key,
            @ParameterObject
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable)
            throws ParameterException {

        ResponseResult<PageInfo<CategoryEntity>> response =
                ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

        PageInfo<CategoryEntity> searchResult =
                new PageInfo<>(categoryService.searchCategoriesByNameOrDescription(key, pageable));

        response.setData(searchResult);

        return response;
    }
}
