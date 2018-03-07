package shield.auth.ex1.client.app.conf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
@EnableOAuth2Sso
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    //injects the context that it'll store the information about the requested service
    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login**", "/webjars/**",
                "/css/**", "/font-awesome/**", "/js/**", "/img/**", "/vendor/simple-line-icons**")
                .permitAll()
                .anyRequest().authenticated();
    }

    //configures a protected resource server that we want to access
    @Bean
    public OAuth2ProtectedResourceDetails resourceOauth() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setId("gas");
        details.setClientId("acme2");
        details.setClientSecret("acmesecret2");
        details.setAccessTokenUri("http://localhost:8080/oauth/token");
        details.setUserAuthorizationUri("http://localhost:8080/oauth/authorize");
        details.setTokenName("oauth_token");
        details.setScope(Arrays.asList("read,write"));
        details.setPreEstablishedRedirectUri("http://localhost:8080/me");
        details.setUseCurrentUri(false);
        return details;
    }

    //creates a OAuth2RestTemplate that configure the requests to resource auth server
    @Bean
    public OAuth2RestTemplate templateTeste() {
//        this commented code configure an interceptor which catch the request and reponse http operations between server and this client
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
//        interceptors.add(new LogRest());
        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceOauth(), oauth2ClientContext);
//        template.setInterceptors(interceptors);
        return template;
    }
}
