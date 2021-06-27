package com.restream.api.service.dto;

public class MailDTO {
    private String mail;
    private String username;
    private String content;

    public MailDTO() {

    }    

    public MailDTO(String mail, String username, String content) {
        this.mail = mail;
        this.username = username;
        this.content = content;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

