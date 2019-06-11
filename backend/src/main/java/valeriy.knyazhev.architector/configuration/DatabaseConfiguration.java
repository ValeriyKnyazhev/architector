package valeriy.knyazhev.architector.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * @author Valeriy Knyazhev
 */
@Configuration
@EnableJdbcHttpSession
@EnableTransactionManagement
public class DatabaseConfiguration
{

    @Bean
    public PlatformTransactionManager transactionManager(@Nonnull DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}