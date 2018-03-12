/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shield.auth.ex1.server.app.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

/**
 *
 * @author renan-baisso
 */
@Component
public class ValidToken {

    @Autowired
    TokenStore tokenStore;
    
    String token = null;
    String tokenType = null;
    OAuth2Authentication auth = null;
    
    private String[] splitToken(String token) {
        return token.split(" ");
    }
    //Extracts local token from request header and validates the token.
    //Returns the external token from the database.

    public boolean validToken(String tokenLocal) {
        String[] parts = splitToken(tokenLocal);
        this.tokenType = parts[0];
        this.token = parts[1];
        
        if (!tokenLocal.equals("empty")) {
            if (parts[0].equalsIgnoreCase("bearer")) {
                return (this.auth = tokenStore.readAuthentication(parts[1])) != null;
            }
        }
        return false;
    }
    private OAuth2Authentication getAuthentication(){
        return this.auth;
    }
    
    public String getToken(String token) {
        if(validToken(token)){
            return ((OAuth2AuthenticationDetails) this.auth.getUserAuthentication()
                    .getDetails()).getTokenValue();
        }
        return null;
    }
}
