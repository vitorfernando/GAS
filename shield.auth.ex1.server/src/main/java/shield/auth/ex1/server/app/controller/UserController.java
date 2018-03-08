/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shield.auth.ex1.server.app.controller;

import java.util.LinkedHashMap;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author vitor-s-silva
 */
@RestController
public class UserController {
    
    @RequestMapping("/profile")
    public Object profile(OAuth2Authentication auth){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        
        map =  (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
        map.remove("sub");
        map.remove("locale");
        return map;
    }
}
