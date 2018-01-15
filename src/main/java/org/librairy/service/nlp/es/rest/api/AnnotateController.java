package org.librairy.service.nlp.es.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.es.rest.model.*;
import org.librairy.service.nlp.es.service.NlpESService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RestController
@RequestMapping("/annotate")
@Api(tags="/annotate", description="list of annotations from a text")
public class AnnotateController {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotateController.class);

    @Autowired
    NlpESService service;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "filter words by PoS and return their annotations", nickname = "postAnnotate", response=AnnotationResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = AnnotationResult.class),
            })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public AnnotationResult analyze(@RequestBody AnnotationRequest annotationRequest)  {
        try {
            List<org.librairy.service.nlp.facade.model.Annotation> annotations = service.annotate(annotationRequest.getText(), annotationRequest.getFilter());

            List<Annotation> annotationsRest = annotations.stream().map(a -> {
                Annotation a2 = new Annotation();
                a2.setTarget(a.getTarget());
                a2.setValues(a.getValue());
                return a2;
            }).collect(Collectors.toList());

            return new AnnotationResult(annotationsRest);
        } catch (AvroRemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
