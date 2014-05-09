package org.fenixedu.oddjet.test.bean;

import java.io.Serializable;

public class Observation implements Serializable {
    private String id;
    private String text;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Observation(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Observation() {
    }

}
