package org.talend.dataprep.api.service.mail;

import java.io.Serializable;

/**
 * This class contains all the data needed to send feedback to Talend.
 */
public class MailDetails implements Serializable {

    private String title;

    private String mail;

    private String severity;

    private String type;

    private String description;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override public String toString() {
        return "MailDetails{" +
                "title='" + title + '\'' +
                ", mail='" + mail + '\'' +
                ", severity='" + severity + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}