package org.librairy.service.nlp.es.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.apache.avro.Schema;
import org.springframework.beans.BeanUtils;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Annotation extends org.librairy.service.nlp.facade.model.Annotation {

    public Annotation(org.librairy.service.nlp.facade.model.Annotation annotation){
        BeanUtils.copyProperties(annotation,this);
    }

    @Override
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    public Schema getSchema() {
        return super.getSchema();
    }
}
