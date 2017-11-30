package com.zgty.oarobot.bean;

public class ImageMessageBuilder extends MessageBuilder {


    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    private ImageBean image;



    public static class ImageBean {
        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }


    }
}
