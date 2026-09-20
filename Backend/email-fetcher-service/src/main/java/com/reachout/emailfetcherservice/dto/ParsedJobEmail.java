package com.reachout.emailfetcherservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParsedJobEmail {
    private String company;
    private String role;
    private String status;       // APPLIED, INTERVIEW, OFFER, REJECTED
    @JsonProperty("isJobEmail")
    private boolean isJobEmail;  // false if email is not job related
}