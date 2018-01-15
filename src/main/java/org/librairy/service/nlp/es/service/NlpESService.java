package org.librairy.service.nlp.es.service;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import eus.ixa.ixa.pipe.pos.Annotate;
import eus.ixa.ixa.pipe.pos.CLI;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class NlpESService implements NlpService {

    private static final Logger LOG = LoggerFactory.getLogger(NlpESService.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    String model              ;
    String lemmatizerModel    ;
    String language           ;
    String multiwords         ;
    String dictag             ;
    String kafVersion         ;
    String normalize          ;
    String untokenizable      ;
    String hardParagraph      ;
    String noseg              ;


    @PostConstruct
    public void setup() throws IOException {

        model              = Paths.get(resourceFolder,"morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin").toFile().getAbsolutePath();
        lemmatizerModel    = Paths.get(resourceFolder,"morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin").toFile().getAbsolutePath();
        language           = "es";
        multiwords         = "true";
        dictag             = Paths.get(resourceFolder,"tag").toFile().getAbsolutePath();
        kafVersion         = "1.5.0";
        normalize          = "true"; // false
        untokenizable      = "false";
        hardParagraph      = "false";
        noseg              = "false";


    }

    @Override
    public String process(String text, List<PoS> filter, Form form) throws AvroRemoteException {

        return analyze(text,filter).stream()
                    .map(term-> {
                        switch (form){
                            case LEMMA: return Strings.isNullOrEmpty(term.getLemma())? term.getStr() : term.getLemma().toLowerCase();
                            case STEM:  return Strings.isNullOrEmpty(term.getStr())? term.getStr() : term.getStr();
                            default: return term.getStr().toLowerCase();
                        }
                    })
                    .collect(Collectors.joining(" "));
    }

    @Override
    public List<Annotation> annotate(String text, List<PoS> filter) throws AvroRemoteException {
        return analyze(text,filter).stream()
                .map(term -> {
                    Annotation annotation = new Annotation();
                    annotation.setTarget(term.getStr());

                    Map<String,String> values = new HashMap<>();

                    if (!Strings.isNullOrEmpty(term.getCase()))
                        values.put("case",term.getCase());
                    if (!Strings.isNullOrEmpty(term.getLemma()))
                        values.put("lemma",term.getLemma());
                    if (!Strings.isNullOrEmpty(term.getForm()))
                        values.put("form",term.getForm());
                    if (!Strings.isNullOrEmpty(term.getMorphofeat()))
                        values.put("morphoFeat",term.getMorphofeat());
                    if (!Strings.isNullOrEmpty(term.getPos()))
                        values.put("pos",PoSTranslator.toPoSTag(term.getPos()).name());
                    if (!Strings.isNullOrEmpty(term.getType()))
                        values.put("type",term.getType());
                    if (term.getSentiment() != null){
                        Term.Sentiment sentiment = term.getSentiment();
                        values.put("sent.polarity",sentiment.getPolarity());
                    }
                    annotation.setValue(values);
                    return annotation;
                })
                .collect(Collectors.toList());
    }

    private List<Term> analyze(String text, List<PoS> filter){
        List<Term> terms = Collections.emptyList();

        final KAFDocument kaf;
        try {

            InputStream is = new ByteArrayInputStream(text.getBytes());

            BufferedReader breader = new BufferedReader(new InputStreamReader(is));

            kaf = new KAFDocument(language, kafVersion);

            final Properties annotateProperties = new Properties();

            annotateProperties.setProperty("normalize", normalize);
            annotateProperties.setProperty("untokenizable", untokenizable);
            annotateProperties.setProperty("hardParagraph", hardParagraph);
            annotateProperties.setProperty("noseg",noseg);
            annotateProperties.setProperty("model", model);
            annotateProperties.setProperty("lemmatizerModel", lemmatizerModel);
            annotateProperties.setProperty("language", language);
            annotateProperties.setProperty("multiwords", multiwords);
            annotateProperties.setProperty("dictTag", dictag);
            annotateProperties.setProperty("dictPath", dictag);
            annotateProperties.setProperty("ruleBasedOption", dictag);

//            annotateProperties.setProperty("resourcesDirectory","src/main/resources");


            final String version        = CLI.class.getPackage().getImplementationVersion();
            final String commit         = CLI.class.getPackage().getSpecificationVersion();

            final eus.ixa.ixa.pipe.tok.Annotate tokAnnotator = new eus.ixa.ixa.pipe.tok.Annotate(breader, annotateProperties);

            // Tokenization
            tokAnnotator.tokenizeToKAF(kaf);


            // PosTagging
            final Annotate posAnnotator    = new Annotate(annotateProperties);
            final KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + Files.getNameWithoutExtension(model), version + "-" + commit);
            newLp.setBeginTimestamp();
            posAnnotator.annotatePOSToKAF(kaf);


            // Debug
//            kaf.getAnnotations(KAFDocument.AnnotationType.TERM).stream().map( annotation -> (Term) annotation).forEach(term -> System.out.println(term.getStr() + "\t " + term.getLemma() +"\t " + term.getPos()));
            //System.out.println(posAnnotator.annotatePOSToCoNLL(kaf));

//            // Named-Entity Annotator
//            final eus.ixa.ixa.pipe.nerc.Annotate neAnnotator = new eus.ixa.ixa.pipe.nerc.Annotate(annotateProperties);
//            neAnnotator.annotateNEsToKAF(kaf);
//
//            for(KAFDocument.AnnotationType aType : KAFDocument.AnnotationType.values()){
//                List<Annotation> out = kaf.getAnnotations(aType);
//                System.out.println("Annotations '" + aType.name()+"' found: " + out.size());
//            }




            // Filtering
            List<String> postags = filter.stream().flatMap( type -> PoSTranslator.toTermPoS(type).stream()).collect(Collectors.toList());

            terms = kaf.getAnnotations(KAFDocument.AnnotationType.TERM).stream()
                    .map(annotation -> (Term) annotation)
                    .filter(term -> postags.isEmpty() || postags.contains(term.getPos()))
                    .collect(Collectors.toList());


            breader.close();
        } catch (IOException e) {
            LOG.error("Error analyzing text", e);
        }

        return terms;
    }
}
