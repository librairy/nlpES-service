package org.librairy.service.nlp.es.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.es.service.IXAService;
import org.librairy.service.nlp.facade.rest.model.ProcessRequest;
import org.librairy.service.nlp.facade.rest.model.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RestController
@RequestMapping("/process")
@Api(tags = "/process", description = "sequence of tokens from a text")
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    IXAService service;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "filter words by PoS and return them in a specific form", nickname = "postProcess", response=ProcessResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProcessResult.class),
            })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ProcessResult analyze(@RequestBody ProcessRequest processRequest)  {
        try {
            String text = service.process(processRequest.getText(), processRequest.getFilter(), processRequest.getForm());
            return new ProcessResult(text);
        } catch (AvroRemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
