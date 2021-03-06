package shield.auth.ex1.server.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.google.api.drive.DriveFile;
import org.springframework.social.google.api.drive.DriveFilesPage;
import org.springframework.social.google.api.drive.UploadParameters;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shield.auth.ex1.server.app.models.FileDetails;
import shield.auth.ex1.server.app.models.FileToUpload;
import shield.auth.ex1.server.app.resources.ValidToken;

@RestController
public class DriveController {

    @Autowired
    TokenStore tokenStore;
    @Autowired
    ValidToken validToken;

    //Obtains a list of Google Drive files based on an Auth Token.
    @RequestMapping(value = "/drive/list", method = RequestMethod.GET)
    public Object list(@RequestHeader(value = "Authorization", defaultValue = "empty") String tokenLocal) {
        String tokenGoogle = validToken.getToken(tokenLocal);
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
        String tokenGoogle = validToken.getToken(tokenLocal);
        if (tokenGoogle != null) {
            GoogleTemplate google = new GoogleTemplate(tokenGoogle);
            DriveFilesPage files = google.driveOperations().getFiles("root", null);

            return files.getItems();
        }
        return new Exception("Erro ao extrair token.");
    }

    @PostMapping("drive/upload")
    public String handleFileUpload(@RequestHeader(value = "Authorization", defaultValue = "empty") String tokenLocal, @RequestBody FileToUpload file) throws IOException {
        String tokenGoogle = validToken.getToken(tokenLocal);
        File driveFile = File.createTempFile(file.getFileName(), "." + file.getFileSufix(), null);
        FileOutputStream fos = new FileOutputStream(driveFile);

        try {
            fos.write(file.getFileInByteArray());
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }

        if (tokenGoogle != null) {
            GoogleTemplate google = new GoogleTemplate(tokenGoogle);
            Resource resource = new FileSystemResource(driveFile);  // any Resource implementation can be used
            DriveFile metadata = DriveFile.builder() // use this builder to set metadata
                    .setTitle(file.getFileName())
                    .build();
            UploadParameters parameters = new UploadParameters();  // call setters to modify upload parameters
            DriveFile fileDrive = google.driveOperations().upload(resource, metadata, parameters);

            driveFile.delete();
        }
        return "Erro: invalid or empty token";
    }

}
