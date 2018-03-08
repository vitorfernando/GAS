package shield.auth.ex1.server.app.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.google.api.drive.DriveFile;
import org.springframework.social.google.api.drive.DriveFilesPage;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shield.auth.ex1.server.app.models.FileDetails;

@RestController
public class HomeController {

    @Autowired
    TokenStore tokenStore;

    //Obtains the token for an authenticated user.
    @RequestMapping({"/user", "/me"})
    public Object user(OAuth2Authentication principal) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("tokenValue", ((OAuth2AuthenticationDetails) principal.getDetails()).getTokenValue());
        map.put("tokenType", ((OAuth2AuthenticationDetails) principal.getDetails()).getTokenType());

        return map;
    }

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

    //Obtains a list of Google Drive files based on an Auth Token.
    @RequestMapping(value = "/drive/list", method = RequestMethod.GET)
    public Object list(@RequestHeader(value = "Authorization", defaultValue = "empty") String tokenLocal) {
        String tokenGoogle = validToken(tokenLocal);
        if (tokenGoogle != null) {
            GoogleTemplate google = new GoogleTemplate(tokenGoogle);
            DriveFilesPage files = google.driveOperations().getFiles("root", null);
            List<FileDetails> lista = new ArrayList<FileDetails>();
            List<DriveFile> fileList = files.getItems();
            //populate drivedetails object end save in a list
            for (DriveFile driveFile : fileList) {
                FileDetails fileDetails = new FileDetails();
                fileDetails.setFileName(driveFile.getTitle());
                fileDetails.setFileType(driveFile.getMimeType());
                fileDetails.setIconUrl(driveFile.getIconLink());
                fileDetails.setExportedLinks(driveFile.getExportLinks());
                //Validate if download url is empty and set.
                if (driveFile.getDownloadUrl() != null) {
                    //Removes the end of Url for download links.
                    //This is for Image files, otherwise the generated download link doesn't work.
                    String downloadUrl = driveFile.getDownloadUrl().replace("&gd=true", "");
                    downloadUrl = downloadUrl.replace("&gd=false", "");

                    fileDetails.setDownloadUrl(downloadUrl);
                }
                lista.add(fileDetails);
            }
            return lista;
        }
        return new Exception("Erro ao extrair token.");
    }

    @RequestMapping(value = "/drive/listFull", method = RequestMethod.GET)
    public Object listFull(@RequestHeader(value = "Authorization", defaultValue = "empty") String tokenLocal) {
        String tokenGoogle = validToken(tokenLocal);
        if (tokenGoogle != null) {
            GoogleTemplate google = new GoogleTemplate(tokenGoogle);
            DriveFilesPage files = google.driveOperations().getFiles("root", null);

            return files.getItems();
        }
        return new Exception("Erro ao extrair token.");
    }

}
