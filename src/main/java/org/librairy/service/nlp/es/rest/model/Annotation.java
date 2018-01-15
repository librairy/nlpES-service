package org.librairy.service.nlp.es.rest.model;

import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class Annotation {

    private String target;

    private Map<String,String> values;

    public Annotation() {
    }

    public String getTarget() {
        return target;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
