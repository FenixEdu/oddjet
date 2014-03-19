package org.fenixedu.oddjet.test.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class localeObject implements Serializable {
    private Map<Locale, Object> content = new HashMap<Locale, Object>();

    public Map<Locale, Object> getContent() {
        return content;
    }

    public void setContent(Map<Locale, Object> content) {
        this.content = content;
    }

    public Object getContent(Locale l) {
        return content.get(l);
    };

    public Object addContent(Locale l, Object o) {
        return content.put(l, o);
    };
}
