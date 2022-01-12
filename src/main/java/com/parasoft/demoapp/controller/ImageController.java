package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.UploadedImageCanNotDeleteException;
import com.parasoft.demoapp.service.ImageService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "images")
@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

	@Autowired
	private ImageService imageService;
	
	@Operation(description = "Upload an image, the size of this image must equal or less than 1MB.")
	@ApiResponse(responseCode = "200", description = "Image uploaded successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request payload.",
    content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "403", description = "You do not have permission to add a new item.",
				 content = {@Content(schema = @Schema(hidden = true)) })
	@PostMapping(value = "/v1/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseResult<String> imageUpload(@RequestParam(value = "image", required = true) MultipartFile image)
																				throws IOException, ParameterException {

		ResponseResult<String> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);
		
        response.setData(imageService.handleImageAndReturnStaticLocation(image));
        
		return response;
	}

	@Operation(description = "Delete an uploaded image.")
	@ApiResponse(responseCode = "200", description = "Image deleted successfully.")
	@ApiResponse(responseCode = "400", description = "Invalid request payload.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "403", description = "You do not have permission to add a new item.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@DeleteMapping(value = "/v1/images")
	@ResponseBody
	@Hidden
	public ResponseResult<String> imageDelete(@RequestParam(required = true) String imagePath)
			throws ParameterException, UploadedImageCanNotDeleteException {

		ResponseResult<String> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		imageService.deleteUploadedImageByPath(imagePath);

		return response;
	}
}
