/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shield.auth.ex1.server.app.controller;

import java.util.LinkedHashMap;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vitor-s-silva
 */
@RestController
public class TokenController {
    //Obtains the token for an authenticated user.
    @RequestMapping({"/user", "/me"})
    public Object user(OAuth2Authentication principal) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("tokenValue", ((OAuth2AuthenticationDetails) principal.getDetails()).getTokenValue());
        map.put("tokenType", ((OAuth2AuthenticationDetails) principal.getDetails()).getTokenType());

        return map;
    }
}
