package org.librairy.service.nlp.es;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@SpringBootApplication
@ComponentScan({
        "org.librairy.service.nlp.es"
})
public class Application  {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);


        String restPort = System.getenv("REST_PORT");

        if (Strings.isNullOrEmpty(restPort)) restPort = "7777";

        Map<String, Object> map = new HashMap<>();
        map.put("SERVER_PORT", restPort);
        application.setDefaultProperties(map);
        application.run(args);

        LOG.info("Http-REST listening at 0.0.0.0:" + restPort + "/nlp-es/");
    }

}
