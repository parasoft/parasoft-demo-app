package com.parasoft.demoapp.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsMessagingTemplate;

import com.parasoft.demoapp.dto.IndustryChangeMQMessageDTO;
import com.parasoft.demoapp.model.global.preferences.IndustryType;

/**
 * Test for class GlobalPreferencesMQService
 *
 * @see com.parasoft.demoapp.service.GlobalPreferencesMQService
 */
public class GlobalPreferencesMQServiceTest {
	
	// Object under test
	@InjectMocks
	GlobalPreferencesMQService underTest;

	@Mock
	JmsMessagingTemplate jmsMessagingTemplate;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for sendToIndustryChangeTopic(IndustryChangeMQMessageDTO)
	 *
	 * @see com.parasoft.demoapp.service.GlobalPreferencesMQService#sendToIndustryChangeTopic(IndustryChangeMQMessageDTO)
	 */
	@Test
	public void testSendToIndustryChangeTopic() throws Throwable {
		// Given
		IndustryType previousIndustry = IndustryType.DEFENSE;
		IndustryType currentIndustry = IndustryType.AEROSPACE;
		IndustryChangeMQMessageDTO messageDto = new IndustryChangeMQMessageDTO(previousIndustry, currentIndustry);
		
		// When
		underTest.sendToIndustryChangeTopic(messageDto);
	}
}