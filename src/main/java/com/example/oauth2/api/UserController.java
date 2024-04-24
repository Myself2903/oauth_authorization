package com.example.oauth2.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    
    @GetMapping()
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
    
    @GetMapping("/token")
    public Map<String, Object> getToken(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal);
    }
    @GetMapping("/repositories")
    public List<String> getUserRepositories(@AuthenticationPrincipal OAuth2User principal) {
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String registrationId = authentication.getAuthorizedClientRegistrationId();
        String accessToken = authorizedClientService.loadAuthorizedClient(registrationId, authentication.getName()).getAccessToken().getTokenValue();

        //api Call
        String apiUrl = "https://api.github.com/user/repos";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = new RestTemplate().exchange(
            apiUrl,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        List<Map<String, Object>> repositories = response.getBody();
        
        List<String> repositoryNames = new ArrayList<>();
        for (Map<String, Object> repo : repositories) {
            repositoryNames.add((String) repo.get("name"));
        }

        return repositoryNames;
    }


}
