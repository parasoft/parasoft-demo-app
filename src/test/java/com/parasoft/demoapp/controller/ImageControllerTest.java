package com.parasoft.demoapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.exception.UploadedImageCanNotDeleteException;
import com.parasoft.demoapp.service.ImageService;

/**
 * Test class for ImageController
 *
 * @see com.parasoft.demoapp.controller.ImageController
 */
public class ImageControllerTest {

	@InjectMocks
	ImageController underTest;

	@Mock
	ImageService imageService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for imageUpload(MultipartFile)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageUpload(MultipartFile)
	 */
	@Test
	public void testImageUpload_normal() throws Throwable {
		// Given
		String savedImagePath = "/uploaded_images/uuid.jpg";
		when(imageService.handleImageAndReturnStaticLocation(any(MultipartFile.class))).thenReturn(savedImagePath);

		// When
		MultipartFile image = mock(MultipartFile.class);
		ResponseResult<String> result = underTest.imageUpload(image);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(savedImagePath, result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for imageUpload(MultipartFile)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageUpload(MultipartFile)
	 */
	@Test(expected = ParameterException.class)
	public void testImageUpload_parameterException() throws Throwable {
		// Given
		when(imageService.handleImageAndReturnStaticLocation(any(MultipartFile.class)))
				.thenThrow(ParameterException.class);
		// When
		MultipartFile image = mock(MultipartFile.class);
		underTest.imageUpload(image);
	}

	/**
	 * Test for imageUpload(MultipartFile)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageUpload(MultipartFile)
	 */
	@Test(expected = IOException.class)
	public void testImageUpload_iOException() throws Throwable {
		// Given
		when(imageService.handleImageAndReturnStaticLocation(any(MultipartFile.class))).thenThrow(IOException.class);

		// When
		MultipartFile image = mock(MultipartFile.class);
		underTest.imageUpload(image);
	}

	/**
	 * Test for imageDelete(String)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageDelete(String)
	 */
	@Test
	public void testImageDelete_normal() throws Throwable {
		// Given
		doNothing().when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		String imagePath = "/uploaded_images/e52d7ae2-c912-4e7b-89db-3df32873c322.jpg";
		ResponseResult<String> result = underTest.imageDelete(imagePath);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}
	
	/**
	 * Test for imageDelete(String)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageDelete(String)
	 */
	@Test(expected = ParameterException.class)
	public void testImageDelete_parameterException() throws Throwable {
		// Given
		doThrow(ParameterException.class).when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		String imagePath = "/uploaded_images/e52d7ae2-c912-4e7b-89db-3df32873c322.jpg";
		underTest.imageDelete(imagePath);
	}
	
	/**
	 * Test for imageDelete(String)
	 *
	 * @see com.parasoft.demoapp.controller.ImageController#imageDelete(String)
	 */
	@Test(expected = UploadedImageCanNotDeleteException.class)
	public void testImageDelete_uploadedImageCanNotDeleteException() throws Throwable {
		// Given
		doThrow(UploadedImageCanNotDeleteException.class).when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		String imagePath = "/uploaded_images/e52d7ae2-c912-4e7b-89db-3df32873c322.jpg";
		underTest.imageDelete(imagePath);
	}
}