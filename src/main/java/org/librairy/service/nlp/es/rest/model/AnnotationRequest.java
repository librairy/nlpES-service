package org.librairy.service.nlp.es.rest.model;

import io.swagger.annotations.ApiModelProperty;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class AnnotationRequest {

    @ApiModelProperty(notes="Unstructured text")
    private String text;

    @ApiModelProperty(notes="List of PoS to be considered. All when empty")
    private List<PoS> filter;

    public AnnotationRequest(String text, List<PoS> filter) {
        this.text = text;
        this.filter = filter;
    }

    public AnnotationRequest(){};

    public String getText() {
        return text;
    }

    public List<PoS> getFilter() {
        return filter;
    }
}
