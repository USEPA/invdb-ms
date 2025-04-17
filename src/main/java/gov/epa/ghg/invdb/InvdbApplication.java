package gov.epa.ghg.invdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import gov.epa.ghg.invdb.config.RsaKeyConfigProperties;

@EnableConfigurationProperties(RsaKeyConfigProperties.class)
@SpringBootApplication(scanBasePackages = { "gov.epa.ghg.invdb" })
@PropertySource("classpath:invdb.properties")
public class InvdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvdbApplication.class, args);
	}

}
