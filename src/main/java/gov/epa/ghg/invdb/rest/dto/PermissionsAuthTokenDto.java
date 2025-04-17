package gov.epa.ghg.invdb.rest.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PermissionsAuthTokenDto {
    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private long expiration;

    @JsonCreator
    public PermissionsAuthTokenDto(
            @JsonProperty("AccessKeyId") String accessKeyId,
            @JsonProperty("SecretAccessKey") String secretAccessKey,
            @JsonProperty("SessionToken") String sessionToken,
            @JsonProperty("Expiration") long expiration) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.expiration = expiration * 1000;
    }

    public Boolean isExpired() {
        return this.expiration < Instant.now().toEpochMilli();
    }
}