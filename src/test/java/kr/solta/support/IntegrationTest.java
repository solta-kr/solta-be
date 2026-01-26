package kr.solta.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class IntegrationTest {
}
