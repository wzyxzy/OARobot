package com.zgty.oarobot.bean;

public class TextMessageBuilder extends MessageBuilder {


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
}
