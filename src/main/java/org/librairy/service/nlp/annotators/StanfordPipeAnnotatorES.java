package org.librairy.service.nlp.annotators;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordPipeAnnotatorES implements StanfordAnnotator {
    /**
     *
     CC Coordinating conjunction
     CD Cardinal number
     DT Determiner
     EX Existential there
     FW Foreign word
     IN Preposition or subordinating conjunction
     JJ Adjective
     JJR Adjective, comparative
     JJS Adjective, superlative
     LS List item marker
     MD Modal
     NN Noun, singular or mass
     NNS Noun, plural
     NNP Proper noun, singular
     NNPS Proper noun, plural
     PDT Predeterminer
     POS Possessive ending
     PRP Personal pronoun
     PRP$ Possessive pronoun
     RB Adverb
     RBR Adverb, comparative
     RBS Adverb, superlative
     RP Particle
     SYM Symbol
     TO to
     UH Interjection
     VB Verb, base form
     VBD Verb, past tense
     VBG Verb, gerund or present participle
     VBN Verb, past participle
     VBP Verb, non­3rd person singular present
     VBZ Verb, 3rd person singular present
     WDT Whdeterminer
     WP Whpronoun
     WP$ Possessive whpronoun
     WRB Whadverb
     */

    private static final Logger LOG = LoggerFactory.getLogger(StanfordPipeAnnotatorES.class);

    //adding extra terms to standard lucene listByExtension
    public static final String customStopWordList = "" +
            ".,a,also,an,and,any,are,as,at," +
            "be,become,both,bring,but,by," +
            "can,come," +
            "do," +
            "e.g.,et_al,et.al,example,extend,enough,enhance," +
            "for,from," +
            "give,get,greatly," +
            "have,highly,high," +
            "if,i.e.,in,into,is,it,its," +
            "keyword,keywords," +
            "more,most,my," +
            "no,not," +
            "of,on,or,only,onto" +
            "paper,provide," +
            "same,show,such," +
            "take,that,than,the,their,then,there,thereby,these,they,this,to,tool," +
            "use,up,"+
            "was,we,where,which,widely,will,with,yet,your";
    public List<String> customStopWord = Arrays.asList(customStopWordList.split(","));
    private final Escaper escaper = Escapers.builder()
            .addEscape('\'',"_")
            .addEscape('('," ")
            .addEscape(')'," ")
            .addEscape('['," ")
            .addEscape(']'," ")
            .build();

    private StanfordCoreNLP pipeline;

    public StanfordPipeAnnotatorES() {

        Properties props;
        props = new Properties();
        //props.put("annotators", "tokenize, cleanxml, ssplit, pos, lemma, stopword"); //"tokenize, ssplit, pos, lemma, ner, parse, dcoref"
        //props.put("annotators", "tokenize, ssplit, pos, lemma, stopword, ner"); //"tokenize, ssplit, pos,
        //props.put("annotators", "tokenize, ssplit, pos, lemma"); //"tokenize, ssplit, pos,

        props.put("annotators", "tokenize, ssplit, pos, lemma, ner"); // depparse, kbp
        props.put("tokenize.language","es");

        props.put("pos.model","edu/stanford/nlp/models/pos-tagger/spanish/spanish.tagger");
//        props.put("pos.maxlen","100");

        props.put("ner.model","edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
        props.put("ner.applyNumericClassifiers","true");
        props.put("ner.useSUTime","true");
        props.put("ner.language","es");
        props.put("sutime.language","spanish");
        props.put("parse.model","edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz");

        props.put("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_Spanish.gz");
        props.put("depparse.language","spanish");

        props.put("ner.fine.regexner.mapping","edu/stanford/nlp/models/kbp/spanish/gazetteers/kbp_regexner_mapping_sp.tag");
        props.put("ner.fine.regexner.validpospattern","^(NOUN|ADJ|PROPN).*");
        props.put("ner.fine.regexner.ignorecase","true");
        props.put("ner.fine.regexner.noDefaultOverwriteLabels","CITY,COUNTRY,STATE_OR_PROVINCE");

        props.put("kbp.semgrex","edu/stanford/nlp/models/kbp/spanish/semgrex");
        props.put("kbp.tokensregex","edu/stanford/nlp/models/kbp/spanish/tokensregex");
        props.put("kbp.model","none");
        props.put("kbp.language","es");

        props.put("entitylink.caseless","true");
        props.put("entitylink.wikidict","edu/stanford/nlp/models/kbp/spanish/wikidict_spanish.tsv");


        // Max length
        props.setProperty("parse.maxlen","100");

        // The rule-based SUTime and tokensregex NER is actually considerably slower than the statistical CRF NER.
        props.setProperty("ner.useSUTime", "false");
        props.setProperty("ner.applyNumericClassifiers", "false");


        // Custom sentence split
        props.setProperty("ssplit.boundaryTokenRegex", "[.]|[!?]+|[。]|[！？]+");

        // Custom tokenize
        //props.setProperty("tokenize.options","untokenizable=allDelete,normalizeOtherBrackets=false," +
//                "normalizeParentheses=false");
        props.setProperty("tokenize.options","untokenizable=noneDelete,normalizeOtherBrackets=false,normalizeParentheses=false");

        // Custom stopwords
//        props.setProperty("customAnnotatorClass.stopword", "intoxicant.analytics.coreNlp.StopwordAnnotator");
        props.setProperty("customAnnotatorClass.stopword", StopWordAnnotatorWrapperES.class.getCanonicalName());
        props.setProperty(StopWordAnnotatorWrapperES.STOPWORDS_LIST, customStopWordList);

        // Parallel
//        props.put("threads", ""+Runtime.getRuntime().availableProcessors());

        pipeline = new StanfordCoreNLP(props);

        LOG.info("Stanford Annotator EN ready");
    }

    public Annotation annotate(String text){
        // Create an empty Annotation just with the given text
        Annotation annotation = new Annotation(text);

        // run all Annotators on this text
        Instant start = Instant.now();
        pipeline.annotate(annotation);
        Instant end = Instant.now();
        LOG.debug("parsing elapsed time: " + Duration.between(start,end).toMillis() + "msecs");

        return annotation;
    }
}