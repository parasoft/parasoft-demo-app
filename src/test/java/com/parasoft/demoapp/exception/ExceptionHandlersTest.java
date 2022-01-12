package com.parasoft.demoapp.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.parasoft.demoapp.controller.ResponseResult;

/**
 * test class for ExceptionHandlers
 *
 * @see ExceptionHandlers
 */
public class ExceptionHandlersTest {

	@InjectMocks
	ExceptionHandlers underTest;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for exceptionHandler(Exception)
	 *
	 * @see ExceptionHandlers#exceptionHandler(Exception)
	 */
	@Test
	public void testExceptionHandler() throws Throwable {
		// Given
		String message = "error";
		Exception e = new Exception(message, mock(Throwable.class));
		
		// When
		ResponseResult<Void> result = underTest.exceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());

	}

	/**
	 * test for resourceNotFoundExceptionHandler(Exception)
	 *
	 * @see ExceptionHandlers#resourceNotFoundExceptionHandler(Exception)
	 */
	@Test
	public void testResourceNotFoundExceptionHandler() throws Throwable {
		// Given
		String message = "resource not found";
		Exception e = new ResourceNotFoundException(message, mock(Throwable.class));
		
		// When
		ResponseResult<Void> result = underTest.resourceNotFoundExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * test for resourceExistsAlreadyExceptionHandler(Exception)
	 *
	 * @see ExceptionHandlers#resourceExistsAlreadyExceptionHandler(Exception)
	 */
	@Test
	public void testResourceExistsAlreadyExceptionHandler() throws Throwable {
		// Given
		String message = "resource exists already";
		Exception e = new ResourceExistsAlreadyException(message, mock(Throwable.class));
		
		// When
		ResponseResult<Void> result = underTest.resourceExistsAlreadyExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * test for parameterExceptionHandler(Exception)
	 *
	 * @see ExceptionHandlers#parameterExceptionHandler(Exception)
	 */
	@Test
	public void testParameterExceptionHandler() throws Throwable {
		// Given
		String message = "null parameter";
		Exception e = new ParameterException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.parameterExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * test for categoryHasAtLeastOneItemExceptionHandler(Exception)
	 *
	 * @see ExceptionHandlers#categoryHasAtLeastOneItemExceptionHandler(Exception)
	 */
	@Test
	public void testCategoryHasAtLeastOneItemExceptionHandler() throws Throwable {
		// Given
		String message = "category with id 1 has at least one items, can not delete this category.";
		Exception e = new CategoryHasAtLeastOneItemException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.categoryHasAtLeastOneItemExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * Test for unsupportedOperationInCurrentIndustryExceptionHandler(Exception)
	 *
	 * @see com.parasoft.demoapp.exception.ExceptionHandlers#unsupportedOperationInCurrentIndustryExceptionHandler(Exception)
	 */
	@Test
	public void testUnsupportedOperationInCurrentIndustryExceptionHandler() throws Throwable {
		// Given
		String message = "Current industry DEFENSE does not support this operation.";
		Exception e = new UnsupportedOperationInCurrentIndustryException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.unsupportedOperationInCurrentIndustryExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * Test for noPermissionExceptionHandler(Exception)
	 *
	 * @see com.parasoft.demoapp.exception.ExceptionHandlers#noPermissionExceptionHandler(Exception)
	 */
	@Test
	public void testNoPermissionExceptionHandler() throws Throwable {
		// Given
		String message = "Have no permission to operate.";
		Exception e = new NoPermissionException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.noPermissionExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}
	
	/**
	 * Test for incorrectOperationExcetionHandler(Exception)
	 *
	 * @see com.parasoft.demoapp.exception.ExceptionHandlers#incorrectOperationExceptionHandler(Exception)
	 */
	@Test
	public void testIncorrectOperationExceptionHandler() throws Throwable {
		// Given
		String message = "Incorrect operation.";
		Exception e = new IncorrectOperationException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.incorrectOperationExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}

	/**
	 * Test for endpointInvalidExceptionHandler(Exception)
	 *
	 * @see com.parasoft.demoapp.exception.ExceptionHandlers#endpointInvalidExceptionHandler(Exception)
	 */
	@Test
	public void testEndpointInvalidExceptionHandler() throws Throwable {
		// Given
		String message = "Invalid url.";
		Exception e = new EndpointInvalidException(message, mock(Throwable.class));

		// When
		ResponseResult<Void> result = underTest.endpointInvalidExceptionHandler(e);

		// Then
		assertNotNull(result);
		assertNull(result.getData());
		assertEquals(ResponseResult.STATUS_ERR, result.getStatus());
		assertEquals(message, result.getMessage());
	}
}