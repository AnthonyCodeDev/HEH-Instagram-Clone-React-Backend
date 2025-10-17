package be.heh.stragram;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import javax.sql.DataSource;

@SpringBootTest(
    excludeAutoConfiguration = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
    }
)
@ActiveProfiles("test")
class StragramApplicationTests {

    // Mock les dépendances de base de données pour éviter la connexion à PostgreSQL
    @MockBean
    private DataSource dataSource;
    
    @MockBean
    private Flyway flyway;
    
    @MockBean
    private FlywayMigrationInitializer flywayInitializer;

    @Test
    void contextLoads() {
        System.out.println("✅ TEST: contextLoads");
        System.out.println("📝 Testing if Spring context loads correctly");
        System.out.println("📝 Expected: Spring context should load without database connection");
        
        // Le test est considéré comme réussi si le contexte Spring se charge correctement
        
        System.out.println("✅ TEST PASSED: contextLoads");
    }

}