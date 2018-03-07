package shield.auth.ex1.client.app.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import shield.auth.ex1.client.app.conf.LogRest;

@RestController
public class HomeController {

    //injects the bean of type OAuth2RestTemplate that is configured in SecurityConf
    @Autowired
    @Qualifier("templateTeste")
    OAuth2RestTemplate templateTeste;

    @RequestMapping("/list")
    public Object list() {
        //request to the server for a REST resource
        return templateTeste.getForObject("http://localhost:8080/drive/list", Object.class);
    }

    @RequestMapping("/testesemtoken")
    public Object teste() {
        RestTemplate temp = new RestTemplate();
        return temp.getForObject("http://localhost:8080/drive/list", Object.class);

    }

    private HttpHeaders createHttpHeaders(OAuth2Authentication user) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) user.getDetails();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", details.getTokenType() + " " + details.getTokenValue());
        headers.add("Authorization", details.getTokenType() + " " + "asd");

//        headers.add("SessionId", details.getSessionId());
        System.out.println(details.getTokenType());
        System.out.println(details.getTokenValue());

        return headers;
    }

    @RequestMapping("/obtainString")
    public Object doYourThing(OAuth2Authentication user) {
        String theUrl = "http://localhost:8080/drive/list";

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new LogRest());
        restTemplate.setInterceptors(interceptors);

        try {
            HttpHeaders headers = createHttpHeaders(user);
            HttpEntity<String> entity = new HttpEntity<String>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(theUrl, HttpMethod.GET, entity, Object.class);
            System.out.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
            return response.getBody();
        } catch (Exception eek) {
            System.out.println("** Exception: " + eek.getMessage());
        }

        return null;
    }

    @RequestMapping("/user")
    public Object user(OAuth2Authentication auth) {
        return auth;
    }

}
