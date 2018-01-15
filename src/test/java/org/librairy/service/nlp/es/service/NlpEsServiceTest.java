package org.librairy.service.nlp.es.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NlpESService.class)
@WebAppConfiguration
public class NlpEsServiceTest {


    @Autowired
    NlpESService service;

    @Test
    public void annotation() throws IOException {

        String text = "Este es mi primer ejemplo";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = service.annotate(text, filter);

        Assert.assertEquals(5, annotations.size());

        annotations.forEach(annotation -> System.out.println("Annotation: " + annotation));

        List<String> pos = annotations.stream().map(a -> a.getValue().get("pos")).collect(Collectors.toList());

        Assert.assertArrayEquals(new String[]{PoS.PRONOUN.name(),PoS.VERB.name(),PoS.ARTICLE.name(),PoS.ADJECTIVE.name(),PoS.NOUN.name()}, pos.toArray());

    }
}
