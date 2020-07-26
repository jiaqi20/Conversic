package com.example.conversic;

public class Upload {
    private String imgName;
    private String imgUrl;

    public Upload() {

    }

    /**
     * Constructor to create upload object.
     * @param imgName Name of image.
     * @param imgUrl Uri of image.
     */
    public Upload(String imgName, String imgUrl) {
        if(imgName.trim().equals("")) {
            imgName = "No name";
        }
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
