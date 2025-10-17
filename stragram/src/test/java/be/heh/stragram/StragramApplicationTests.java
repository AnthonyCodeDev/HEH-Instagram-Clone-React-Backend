package be.heh.stragram;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties =
    "spring.autoconfigure.exclude=" +
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
    "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
)
@ActiveProfiles("test")
class StragramApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("✅ TEST PASSED: contextLoads");
    }

}