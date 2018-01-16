package org.librairy.service.nlp.es.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.librairy.service.nlp.es.Application;
import org.librairy.service.nlp.es.rest.model.ProcessRequest;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class RestIntTest {

    private static final Logger LOG = LoggerFactory.getLogger(RestIntTest.class);

    @Before
    public void setup(){
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    @Ignore
    public void post() throws UnirestException {

        List<PoS> types = Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB});
        String text = "este es el texto que va a ser analizado";
        Form form = Form.STEM;
        ProcessRequest req = new ProcessRequest(text,types,form);

        HttpResponse<JsonNode> response = Unirest.post("http://localhost:8081/nlp-es/process")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(req)
                .asJson();

        LOG.info("Response: " + response.getBody());


    }
}