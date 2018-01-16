package org.librairy.service.nlp.es;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Test
    @Ignore
    public void run() throws InterruptedException {
        Application.main(new String[]{});
        Thread.sleep(Long.MAX_VALUE);
    }

}
