package org.librairy.service.nlp.es;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@SpringBootApplication
@ComponentScan({"org.librairy.service"})
public class Application  {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        new SpringApplication(Application.class).run(args);
    }

}
