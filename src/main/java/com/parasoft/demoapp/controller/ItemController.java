package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.UnsupportedOperationInCurrentIndustryException;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.exception.CategoryNotFoundException;
import com.parasoft.demoapp.exception.GlobalPreferencesMoreThanOneException;
import com.parasoft.demoapp.exception.GlobalPreferencesNotFoundException;
import com.parasoft.demoapp.exception.ItemNameExistsAlreadyException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "assets/items")
@Controller
@RequestMapping(value = {"/v1/assets/items", "/proxy/v1/assets/items"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemController {
	@Autowired
	private ItemService itemService;

	@Operation(description = "Obtain all items under current conditions.")
	@ApiResponse(responseCode = "200", description = "All items under current conditions were returned.")
	@GetMapping
	@ResponseBody
	public ResponseResult<PageInfo<ItemEntity>> getItems(
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) RegionType[] regions,
			@RequestParam(required = false) String searchString,
			@ParameterObject
			@PageableDefault(sort = "name", direction = Direction.ASC, size = Integer.MAX_VALUE) Pageable pageable) {
		ResponseResult<PageInfo<ItemEntity>> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		PageInfo<ItemEntity> pageInfo = new PageInfo<ItemEntity>(
				itemService.getItems(categoryId, regions, searchString, pageable));

		response.setData(pageInfo);

		return response;
	}

	@Operation(description = "Obtain item by item id.")
	@ApiResponse(responseCode = "200", description = "Item with corresponding id was returned.")
    @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
    			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "404", description = "No item with corresponding item id.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@GetMapping("/{itemId}")
	@ResponseBody
	public ResponseResult<ItemEntity> getItemById(@PathVariable Long itemId)
			throws ItemNotFoundException, ParameterException {

		ResponseResult<ItemEntity> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		response.setData(itemService.getItemById(itemId));

		return response;
	}

	@Operation(description = "Obtain item by item name.")
	@ApiResponse(responseCode = "200", description = "Item with corresponding name was returned.")
	@ApiResponse(responseCode = "400", description = "Invalid request parameter.",
	content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "404", description = "No item with corresponding item name.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@GetMapping("/name/{itemName}")
	@ResponseBody
	public ResponseResult<ItemEntity> getItemByName(@PathVariable String itemName)
			throws ItemNotFoundException, ParameterException {

		ResponseResult<ItemEntity> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		response.setData(itemService.getItemByName(itemName));

		return response;
	}

	@Operation(description = "Add a new item in this category.")
	@ApiResponse(responseCode = "200", description = "New item successfully created in this category.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
    content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to add a new item.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No category with corresponding category id.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@PostMapping
	@ResponseBody
	public ResponseResult<ItemEntity> addNewItem(@RequestBody ItemsDTO itemsDto)
			throws ItemNameExistsAlreadyException, CategoryNotFoundException, ParameterException, 
					GlobalPreferencesNotFoundException, GlobalPreferencesMoreThanOneException, 
					UnsupportedOperationInCurrentIndustryException {

		ResponseResult<ItemEntity> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		response.setData(itemService.addNewItem(itemsDto.getName(), itemsDto.getDescription(), itemsDto.getCategoryId(),
				itemsDto.getInStock(), itemsDto.getImagePath(), itemsDto.getRegion()));

		return response;
	}

	@Operation(description = "Update the item by item id.")
	@ApiResponse(responseCode = "200", description = "Item updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
    content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to update the item.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No item with corresponding item or category id.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@PutMapping("/{itemId}")
	@ResponseBody
	public ResponseResult<ItemEntity> updateItemById(@PathVariable Long itemId, @RequestBody ItemsDTO itemsDto)
			throws CategoryNotFoundException, ItemNameExistsAlreadyException, ItemNotFoundException, ParameterException,
			UnsupportedOperationInCurrentIndustryException {

		ResponseResult<ItemEntity> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		response.setData(itemService.updateItem(itemId, itemsDto.getName(), itemsDto.getDescription(),
				itemsDto.getCategoryId(), itemsDto.getInStock(), itemsDto.getImagePath(), itemsDto.getRegion()));

		return response;
	}

	@Operation(description = "Update the inStock of item by item id.")
	@ApiResponse(responseCode = "200", description = "Item updated successfully.")
	@ApiResponse(responseCode = "400", description = "Invalid request payload.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to update the item.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No item with corresponding item or category id.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@PutMapping("/inStock/{itemId}")
	@ResponseBody
	public ResponseResult<ItemEntity> updateItemInStock(@PathVariable Long itemId, @RequestParam Integer newInStock)
			throws ParameterException, ItemNotFoundException {
		ResponseResult<ItemEntity> response =
				ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		response.setData(itemService.updateItemInStock(itemId, newInStock));

		return response;
	}

	@Operation(description = "Delete the item by item id.")
	@ApiResponse(responseCode = "200", description = "Item with corresponding id successfully deleted.")
    @ApiResponse(responseCode = "400", description = "Invalid request parameter.",
    content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to delete the item.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No item with corresponding item id.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@DeleteMapping("/{itemId}")
	@ResponseBody
	public ResponseResult<Long> deleteItemById(@PathVariable Long itemId)
			throws ItemNotFoundException, ParameterException {

		ResponseResult<Long> response = ResponseResult.getInstance(ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		itemService.removeItemById(itemId);
		response.setData(itemId);

		return response;
	}

	@Operation(description = "Delete the item by item name.")
	@ApiResponse(responseCode = "200", description = "Item with corresponding name successfully deleted.")
	@ApiResponse(responseCode = "400", description = "Invalid request parameter.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to delete the item.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No item with corresponding item name.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@DeleteMapping("/name/{itemName}")
	@ResponseBody
	public ResponseResult<String> deleteItemByName(@PathVariable String itemName)
			throws ItemNotFoundException, ParameterException {

		ResponseResult<String> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		itemService.removeItemByName(itemName);
		response.setData(itemName);

		return response;
	}
}
