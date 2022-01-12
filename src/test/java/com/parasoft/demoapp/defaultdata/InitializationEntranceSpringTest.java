package com.parasoft.demoapp.defaultdata;

import com.parasoft.demoapp.model.global.DatabaseInitResultEntity;
import com.parasoft.demoapp.repository.global.DatabaseInitResultRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class InitializationEntranceSpringTest {
    @Autowired
    private DatabaseInitResultRepository databaseInitResultRepository;

    @Test
    public void test() throws Exception {
        DatabaseInitResultEntity result = databaseInitResultRepository.findFirstByOrderByCreatedTimeDesc();
        assertTrue(result.isCreated());
        assertNotNull(result.getCreatedTime());
    }
}
