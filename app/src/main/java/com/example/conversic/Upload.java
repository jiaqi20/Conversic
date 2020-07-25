package com.example.conversic;

public class Upload {
    private String fileName;
    private String fileUrl;

    public Upload() {

    }

    /**
     * Constructor to create upload object.
     * @param fileName Name of image.
     * @param fileUrl
     */
    public Upload(String fileName, String fileUrl) {
        if(fileName.trim().equals("")) {
            fileName = "No name";
        }
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String imgName) {
        this.fileName = imgName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String imgUrl) {
        this.fileUrl = imgUrl;
    }
}
