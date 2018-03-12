/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shield.auth.ex1.server.app.controller;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shield.auth.ex1.server.app.models.UserDetails;
import shield.auth.ex1.server.app.resources.ValidToken;

/**
 *
 * @author vitor-s-silva
 */
@RestController
public class UserController {

    @Autowired
    TokenStore tokenStore;
    @Autowired
    ValidToken validToken;

    @RequestMapping("/profile")
    public Object profile(OAuth2Authentication auth) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map = (LinkedHashMap<String, String>) auth.getUserAuthentication().getDetails();
//        map.remove("sub");
//        map.remove("locale");
        return map;
    }

    @RequestMapping(value = "/usuario", method = RequestMethod.GET)
    public Object userDetails(@RequestHeader(value = "Authorization", defaultValue = "empty") String tokenLocal) {
        String tokenGoogle = validToken.getToken(tokenLocal);
        if (tokenGoogle != null) {
            GoogleTemplate google = new GoogleTemplate(tokenGoogle);
            UserDetails user = new UserDetails();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
            String[] imgUrl = new String[2];
            String imageUrl;
            String givenName;
            String familyName;

            if ((imageUrl = google.plusOperations().getGoogleProfile().getImageUrl()) != null) {
                imgUrl = imageUrl.split("[?]");
                user.setImg(imgUrl[0]);
            } else {
                user.setImg("null");
            }
            if ((givenName = google.plusOperations().getGoogleProfile().getGivenName()) != null) {
                user.setNome(givenName);
            } else {
                user.setNome("null");
            }
            if ((familyName = google.plusOperations().getGoogleProfile().getFamilyName()) != null) {
                user.setSobrenome(familyName);
            } else {
                user.setSobrenome("null");
            }
//            user.setEmail();
            if (google.plusOperations().getGoogleProfile().getGender().equals("male")) {
                user.setGenero("Masculino");
            } else if (google.plusOperations().getGoogleProfile().getGender().equals("female")) {
                user.setGenero("Feminino");
            } else {
                user.setGenero("Outro");
            }
            if (google.plusOperations().getGoogleProfile().getBirthday() != null) {
                user.setNiver(dt1.format(google.plusOperations().getGoogleProfile().getBirthday()));
            } else {
                user.setNiver("null");
            }

            return user;
        }
        return new Exception("Erro ao extrair token.");
    }

    @RequestMapping(value = "/usuarioteste", method = RequestMethod.GET)
    public Object userDetails(OAuth2Authentication principal) {

        GoogleTemplate google = new GoogleTemplate(((OAuth2AuthenticationDetails) principal.getDetails()).getTokenValue());
        UserDetails user = new UserDetails();
        String[] imgUrl = new String[2];
        imgUrl = google.plusOperations().getGoogleProfile().getImageUrl().split("?");
//        Set<String> map = google.plusOperations().getGoogleProfile().getEmailAddresses();

        user.setNome(google.plusOperations().getGoogleProfile().getGivenName());
        user.setSobrenome(google.plusOperations().getGoogleProfile().getFamilyName());
//            user.setEmail();
        user.setGenero(google.plusOperations().getGoogleProfile().getGender());
        user.setNiver(google.plusOperations().getGoogleProfile().getBirthday().toString());
        user.setImg(imgUrl[0]);
//        System.out.println("Este é o MAP: "+map);
        return google.plusOperations().getGoogleProfile();

    }

}
