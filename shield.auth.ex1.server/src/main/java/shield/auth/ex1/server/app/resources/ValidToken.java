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

   //Extracts local token from request header and validates the token.
    //Returns the external token from the database.
    public String validToken(String tokenLocal) {
        String[] parts = tokenLocal.split(" ");
        if (!tokenLocal.equals("empty")) {
            if (parts[0].equalsIgnoreCase("bearer")) {
                OAuth2Authentication authen = tokenStore.readAuthentication(parts[1]);
                if (authen != null) {
                    return ((OAuth2AuthenticationDetails) authen.getUserAuthentication().getDetails()).getTokenValue();
                } else {
                    return null;
                }
            }
        }

        return null;
    }
}
