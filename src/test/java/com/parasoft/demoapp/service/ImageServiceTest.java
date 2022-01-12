/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.parasoft.demoapp.config.WebConfig;
import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.ImageMessages;
import com.parasoft.demoapp.model.global.preferences.IndustryType;

/**
 * Test class for ImageService
 *
 * @see ImageService
 */
public class ImageServiceTest {

	//  Object under test
	@InjectMocks
	ImageService underTest;

	@Mock
	WebConfig webConfig;

	@Mock
	private ItemService itemService;

	@Mock
	private CategoryService categoryService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for handleImageAndReturnStaticLocation(MultipartFile)
	 *
	 * @see ImageService#handleImageAndReturnStaticLocation(MultipartFile)
	 */
	@Test
	public void testHandleImageAndReturnStaticLocation_normal() throws Throwable {

		// Given
		String industrySubDirName = IndustryRoutingDataSource.currentIndustry.toString().toLowerCase();
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "tmp";
		when(webConfig.getUploadedImagesStorePath()).thenReturn(tmpDirPath);
		
		String multipartFileName = "anyMultipartName";
		String imageOriginalName = "1234.jpg"; // test point: .jpg
		String contentType = "image/jpg";
		byte[] content = "bytes of image".getBytes();
		MultipartFile image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		
		// When
		String result = underTest.handleImageAndReturnStaticLocation(image);

		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);
		
		// When
		imageOriginalName = "1234.jpeg"; // test point: .jpeg
		image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		result = underTest.handleImageAndReturnStaticLocation(image);

		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);
		
		// When
		imageOriginalName = "1234.gif"; // test point: .gif
		image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		result = underTest.handleImageAndReturnStaticLocation(image);

		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);
		
		// When
		imageOriginalName = "1234.png"; // test point: .png
		image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		result = underTest.handleImageAndReturnStaticLocation(image);

		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);
		
		// When
		imageOriginalName = "1234.bmp"; // test point: .bmp format
		image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		result = underTest.handleImageAndReturnStaticLocation(image);

		File savedFile1 = new File(tmpDirPath + "/" + industrySubDirName+ "/" + imageOriginalName);
		long lastModified1 = savedFile1.lastModified();
		long length1 = savedFile1.length();

		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);

		// When
		imageOriginalName = "1234.bmp"; // test point: same name, file will be rewrote.
		image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		result = underTest.handleImageAndReturnStaticLocation(image);

		File savedFile2 = new File(tmpDirPath + "/" + industrySubDirName+ "/" + imageOriginalName);
		long lastModified2 = savedFile2.lastModified();
		long length2 = savedFile2.length();
		System.out.println(lastModified1);
		System.out.println(lastModified2);
		// Then
		assertEquals(WebConfig.UPLOADED_IMAGES_SUB_LOCATION + industrySubDirName+ "/" + imageOriginalName, result);
		//assertTrue(lastModified1 < lastModified2);
		assertEquals(length1, length2);
		
		// Finally
		// Delete all temporary files
		File tmpDir = new File(tmpDirPath);
		System.out.println(tmpDir.getAbsolutePath());
		for(File industryDir : tmpDir.listFiles()) {
			for(File imageFile : industryDir.listFiles()) {
				imageFile.delete();
			}
			industryDir.delete();
		}
		tmpDir.delete();
		assertFalse(tmpDir.exists());
	}
	
	/**
	 * Test for handleImageAndReturnStaticLocation(MultipartFile)
	 *
	 * @see ImageService#handleImageAndReturnStaticLocation(MultipartFile)
	 */
	@Test
	public void testHandleImageAndReturnStaticLocation_nullImage() throws Throwable {

		// Given
		MultipartFile image = null; // test point
		
		// When
		String message = "";
		try {
			underTest.handleImageAndReturnStaticLocation(image);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(ImageMessages.IMAGE_FILE_IS_EMPTY_OR_NOT_EXISTING, message);
	}
	
	/**
	 * Test for handleImageAndReturnStaticLocation(MultipartFile)
	 *
	 * @see ImageService#handleImageAndReturnStaticLocation(MultipartFile)
	 */
	@Test
	public void testHandleImageAndReturnStaticLocation_emptyImage() throws Throwable {

		// Given
		String multipartFileName = "anyMultipartName";
		String imageOriginalName = "1234.jpg";
		String contentType = "image/jpg";
		byte[] content = new byte[0]; // test point
		MultipartFile image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		
		// When
		String message = "";
		try {
			underTest.handleImageAndReturnStaticLocation(image);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(ImageMessages.IMAGE_FILE_IS_EMPTY_OR_NOT_EXISTING, message);
	}
	
	/**
	 * Test for handleImageAndReturnStaticLocation(MultipartFile)
	 *
	 * @see ImageService#handleImageAndReturnStaticLocation(MultipartFile)
	 */
	@Test
	public void testHandleImageAndReturnStaticLocation_originalFileNameWithoutSuffixName() throws Throwable {

		// Given
		String multipartFileName = "anyMultipartName";
		String imageOriginalName = "noSuffixName"; // test point
		String contentType = "image/jpg";
		byte[] content = "bytes of image".getBytes();
		MultipartFile image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		
		// When
		String message = "";
		try {
			underTest.handleImageAndReturnStaticLocation(image);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(ImageMessages.IMAGE_WITH_NO_SUFFIX_NAME, message);
	}
	
	/**
	 * Test for handleImageAndReturnStaticLocation(MultipartFile)
	 *
	 * @see ImageService#handleImageAndReturnStaticLocation(MultipartFile)
	 */
	@Test
	public void testHandleImageAndReturnStaticLocation_unsupportedSuffixName() throws Throwable {

		// Given
		String multipartFileName = "anyMultipartName";
		String suffixName = ".unsupportedSuffixName"; // test point
		String imageOriginalName = "123" + suffixName;
		String contentType = "image/jpg";
		byte[] content = "bytes of image".getBytes();
		MultipartFile image = new MockMultipartFile(multipartFileName, imageOriginalName, contentType, content);
		
		// When
		String message = "";
		try {
			underTest.handleImageAndReturnStaticLocation(image);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(ImageMessages.IMAGE_SUFFIX_NAME_IS_NOT_SUPPORTED, suffixName.toLowerCase()), message);
	}

	/**
	 * Test for deleteUploadedImageByPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#deleteUploadedImageByPath(String)
	 */
	@Test
	public void testDeleteUploadedImageByPath_normal() throws Throwable {
		// Given
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "tmp";
		when(webConfig.getUploadedImagesStorePath()).thenReturn(tmpDirPath);
		File tmpDir = new File(tmpDirPath);
		if(!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		
		String testFileName = "testFile.jpg";
		File testFile = Paths.get(tmpDirPath, testFileName).toFile();
		testFile.createNewFile();
		assertTrue(testFile.exists());

		// When
		String imagePath = WebConfig.UPLOADED_IMAGES_SUB_LOCATION + testFileName;
		underTest.deleteUploadedImageByPath(imagePath);
		
		// Then
		assertFalse(testFile.exists());

		// Finally
		tmpDir.delete();
	}
	
	/**
	 * Test for deleteUploadedImageByPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#deleteUploadedImageByPath(String)
	 */
	@Test
	public void testDeleteUploadedImageByPath_nullImagePath() throws Throwable {

		// Given
		String imagePath = null;
		
		// When
		String message = "";
		try {
			underTest.deleteUploadedImageByPath(imagePath);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(ImageMessages.IMAGE_NOT_FOUND, imagePath), message);
	}
	
	/**
	 * Test for deleteUploadedImageByPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#deleteUploadedImageByPath(String)
	 */
	@Test
	public void testDeleteUploadedImageByPath_invalidImagePath() throws Throwable {

		// Given
		String imagePath = "/image/path"; // not start with /uploaded_images/
		
		// When
		String message = "";
		try {
			underTest.deleteUploadedImageByPath(imagePath);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(ImageMessages.IMAGE_NOT_FOUND, imagePath), message);
	}
	
	/**
	 * Test for deleteUploadedImageByPath(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#deleteUploadedImageByPath(String)
	 */
	@Test
	public void testDeleteUploadedImageByPath_uploadedImageCanNotDeleteException() throws Throwable {
		// Given
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "tmp";
		when(webConfig.getUploadedImagesStorePath()).thenReturn(tmpDirPath);
		File tmpDir = new File(tmpDirPath);
		if(!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		
		String testFileName = "testFile.jpg";
		File testFile = Paths.get(tmpDirPath, testFileName).toFile();
		testFile.createNewFile();
		assertTrue(testFile.exists());
		
		FileInputStream fis = new FileInputStream(testFile); // make test file can not be deleted

		// When
		String message = "";
		String imagePath = WebConfig.UPLOADED_IMAGES_SUB_LOCATION + testFileName;
		try {
			underTest.deleteUploadedImageByPath(imagePath);
		}catch(Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(ImageMessages.IMAGE_FAILED_TO_DELETE, imagePath), message);
		assertTrue(testFile.exists());

		// Finally
		fis.close();
		testFile.delete();
		tmpDir.delete();
	}

	/**
	 * Test for removeAllIndustriesUploadedImages()
	 *
	 * @see com.parasoft.demoapp.service.ImageService#removeAllIndustriesUploadedImages()
	 */
	@Test
	public void testRemoveAllIndustriesUploadedImages_normal() throws Throwable {
		// Given
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "tmp";
		File tmpDir = new File(tmpDirPath);
		tmpDir.mkdir();
		assertTrue(tmpDir.exists());

		// Path of uploaded images is like xxx/{industry}/image.jpg, the sub path "/{industry}/" is fixed.
		File uploadedImage = new File(tmpDir.getCanonicalPath() + "/" +
				IndustryType.DEFENSE.toString().toLowerCase() + "/image.jpg");
		File industryDir = uploadedImage.getParentFile();
		industryDir.mkdirs();
		uploadedImage.createNewFile();
		assertTrue(uploadedImage.exists());

		String getUploadedImagesStorePathResult = tmpDirPath;
		when(webConfig.getUploadedImagesStorePath()).thenReturn(getUploadedImagesStorePathResult);

		// When
		boolean result = underTest.removeAllIndustriesUploadedImages();

		// Then
		assertTrue(result);
		assertFalse(uploadedImage.exists());

		// Finally
		industryDir.delete();
		assertFalse(industryDir.exists());
		tmpDir.delete();
		assertFalse(tmpDir.exists());
	}

	/**
	 * Test for removeAllIndustriesUploadedImages()
	 *
	 * @see com.parasoft.demoapp.service.ImageService#removeAllIndustriesUploadedImages()
	 */
	@Test
	public void testRemoveAllIndustriesUploadedImages_someImageRemoveUnsuccessfully() throws Throwable {
		// Given
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "tmp";
		File tmpDir = new File(tmpDirPath);
		tmpDir.mkdir();
		assertTrue(tmpDir.exists());

		// Path of uploaded images is like xxx/{industry}/image.jpg, the sub path "/{industry}/" is fixed.
		File uploadedImage = new File(tmpDir.getCanonicalPath() + "/" +
				IndustryType.DEFENSE.toString().toLowerCase() + "/image.jpg");
		File industryDir = uploadedImage.getParentFile();
		industryDir.mkdirs();
		uploadedImage.createNewFile();
		assertTrue(uploadedImage.exists());
		FileInputStream fis = new FileInputStream(uploadedImage); // make uploaded image can not be removed

		String getUploadedImagesStorePathResult = tmpDirPath;
		when(webConfig.getUploadedImagesStorePath()).thenReturn(getUploadedImagesStorePathResult);

		// When
		boolean result = underTest.removeAllIndustriesUploadedImages();

		// Then
		assertFalse(result);
		assertTrue(uploadedImage.exists());

		// Finally
		fis.close(); // make uploaded image can be removed
		uploadedImage.delete();
		assertFalse(uploadedImage.exists());
		industryDir.delete();
		assertFalse(industryDir.exists());
		tmpDir.delete();
		assertFalse(tmpDir.exists());
	}

	/**
	 * Test for removeAllIndustriesUploadedImages()
	 *
	 * @see com.parasoft.demoapp.service.ImageService#removeAllIndustriesUploadedImages()
	 */
	@Test
	public void testRemoveAllIndustriesUploadedImages_uploadedImagesDirNotExists() throws Throwable {
		// Given
		String tmpDirPath = System.getProperty("java.io.tmpdir") + "notExist";
		String getUploadedImagesStorePathResult = tmpDirPath;
		when(webConfig.getUploadedImagesStorePath()).thenReturn(getUploadedImagesStorePathResult);

		// When
		boolean result = underTest.removeAllIndustriesUploadedImages();

		// Then
		assertTrue(result);
	}

	/**
	 * Test for numberOfImageUsed(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#numberOfImageUsed(String)
	 */
	@Test
	public void testNumberOfImageUsed_normal() throws Throwable {
		// Given
		when(itemService.numberOfImageUsedInItems(anyString())).thenReturn(1L);
		when(categoryService.numberOfImageUsedInCategories(anyString())).thenReturn(1L);

		// When
		String imagePath = "/uploaded_images/defense/123.jpg";
		long result = underTest.numberOfImageUsed(imagePath);

		// Then
		assertEquals(2l, result);
	}

	/**
	 * Test for numberOfImageUsed(String)
	 *
	 * @see com.parasoft.demoapp.service.ImageService#numberOfImageUsed(String)
	 */
	@Test
	public void testNumberOfImageUsed_nullImagePath() throws Throwable {

		// When
		String imagePath = null;
		long result = underTest.numberOfImageUsed(imagePath);

		// Then
		assertEquals(0, result);
	}

}