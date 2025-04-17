package gov.epa.ghg.invdb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.epa.ghg.invdb.rest.dto.PermissionsAuthTokenDto;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Service
@Log4j2
public class AwsAuthService {
    @Autowired
    private WebClient webClient;
    @Value("${s3.PERMISSIONS_API_KEY}")
    private String permissionsApiKey;

    private static StaticCredentialsProvider credentialsProvider;
    private static PermissionsAuthTokenDto authToken;

    private void refreshAwsCredentials() throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("x-api-key", permissionsApiKey);
            String response = webClient.get().uri("https://data.epa.gov/permissions-service")
                    .headers(h -> h.addAll(headers)).retrieve().bodyToMono(String.class) // Convert response to a String
                    .block();
            if (response != null) {
                ObjectMapper mapper = new ObjectMapper();
                AwsAuthService.authToken = mapper.readValue(response, PermissionsAuthTokenDto.class);
            }
            if (AwsAuthService.authToken != null) {
                AwsAuthService.credentialsProvider = StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(AwsAuthService.authToken.getAccessKeyId(),
                                AwsAuthService.authToken.getSecretAccessKey(),
                                AwsAuthService.authToken.getSessionToken()));
            }
        } catch (Exception e) {
            log.error("An error occured: ", e);
            throw e;
        }
    }

    public StaticCredentialsProvider getCredentials() throws Exception {
        if (AwsAuthService.authToken != null && AwsAuthService.authToken.isExpired() == false) {
            log.info("inside AwsAuthService");
            return AwsAuthService.credentialsProvider;
        } else {
            this.refreshAwsCredentials();
            return this.getCredentials();
        }
    }
}
