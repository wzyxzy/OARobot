package com.zgty.oarobot.bean;

/**
 * Created by zy on 2017/11/28.
 */

public class MessageBuilder {
    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private int agentid;
    private int safe;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    private FileBean file;

    public FileBean getFile() {
        return file;
    }

    public void setFile(FileBean file) {
        this.file = file;
    }

    public static class FileBean {
        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }


    }

    private TextBean text;

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public static class TextBean {
        /**
         * content : 你的快递已到，请携带工卡前往邮件中心领取。
         出发前可查看<a href="http://work.weixin.qq.com">邮件中心视频实况</a>，聪明避开排队。
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

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
