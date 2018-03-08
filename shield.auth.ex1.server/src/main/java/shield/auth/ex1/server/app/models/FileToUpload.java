/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shield.auth.ex1.server.app.models;

/**
 *
 * @author rafael-a-mendoza
 */
public class FileToUpload {
    private String fileName;
    private String fileSufix;
    private byte[] fileInByteArray;
    
    public FileToUpload() {}

    public FileToUpload(String fileName, String fileSufix, byte[] fileInByteArray) {
        this.fileName = fileName;
        this.fileSufix = fileSufix;
        this.fileInByteArray = fileInByteArray;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSufix() {
        return fileSufix;
    }

    public void setFileSufix(String fileSufix) {
        this.fileSufix = fileSufix;
    }

    public byte[] getFileInByteArray() {
        return fileInByteArray;
    }

    public void setFileInByteArray(byte[] fileInByteArray) {
        this.fileInByteArray = fileInByteArray;
    }
}
