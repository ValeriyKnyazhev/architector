package valeriy.knyazhev.architector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author valeriy.knyazhev@yandex.ru
 */
@EnableWebSecurity
@SpringBootApplication
public class ArchitectorSpringApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ArchitectorSpringApplication.class, args);
    }

}
