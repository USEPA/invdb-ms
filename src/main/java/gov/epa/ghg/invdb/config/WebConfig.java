package gov.epa.ghg.invdb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder().baseUrl(pythonServiceUrl).build();
    }

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder().baseUrl(pythonServiceUrl).build();
    }

}
