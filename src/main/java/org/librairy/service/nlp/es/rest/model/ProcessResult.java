package org.librairy.service.nlp.es.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ProcessResult {

    private final String processedText;

    public ProcessResult(String processedText) {
        this.processedText = processedText;
    }

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The text processed by the service", required = true)
    public String getProcessedText() {
        return processedText;
    }
}
