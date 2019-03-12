package org.librairy.service.nlp.annotators;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import edu.mit.jmwe.data.IMWEDesc;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.JMWEAnnotator;
import edu.stanford.nlp.util.CoreMap;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.librairy.service.nlp.facade.utils.AnnotationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorWrapperES {


    public List<org.librairy.service.nlp.facade.model.Annotation> tokenize(Annotation annotation)
    {

        List<CoreMap> sentenceAnnotation = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterate over all of the sentences found
        List<org.librairy.service.nlp.facade.model.Annotation> unigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.get(CoreAnnotations.TokensAnnotation.class).stream())
                .map(coreLabel -> {
                    Token token = new Token();
                    String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    PoS posValue = translateFrom(pos.toLowerCase());
                    token.setPos(posValue);

                    String raw = coreLabel.originalText();
                    token.setTarget(raw);

                    String lemma = coreLabel.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                    token.setLemma(posValue.equals(PoS.PROPER_NOUN)? raw : lemma);

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(Long.valueOf(coreLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)));

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma()) && a.getToken().getLemma().length()>1))
                .collect(Collectors.toList());

        List<org.librairy.service.nlp.facade.model.Annotation> multigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.containsKey(JMWEAnnotator.JMWEAnnotation.class) ? sentence.get(JMWEAnnotator.JMWEAnnotation.class).stream() : null)
                .filter(t -> t != null)
                .map(coreLabel -> {

                    IMWEDesc entry = coreLabel.getEntry();
                    Token token = new Token();

                    PoS pos = translateFrom(entry.getID().getPOS().name().toLowerCase());
                    token.setPos(pos);

                    String raw = coreLabel.getForm();
                    token.setTarget(raw);

                    String lemma = entry.getID().getForm().toLowerCase();
                    token.setLemma(pos.equals(PoS.PROPER_NOUN)? raw : lemma);

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(coreLabel.getOffset());

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma())))
                .collect(Collectors.toList());


        List<org.librairy.service.nlp.facade.model.Annotation> annotations = multigramAnnotations.isEmpty()? unigramAnnotations : AnnotationUtils.merge(unigramAnnotations, multigramAnnotations);

        return annotations;
    }

    /**
     * ADJECTIVE('J', new String[]{"JJ"}),
     NOUN('N', new String[]{"NN"}),
     OTHER('O', (String[])null),
     PROPER_NOUN('P', new String[]{"NNP"}),
     ADVERB('R', new String[]{"RB", "WRB"}),
     VERB('V', new String[]{"VB"});
     * @param posTag
     * @return
     */
    private PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            default:
                // Ancora annotation
                if (posTag.startsWith("aq")) return PoS.ADJECTIVE;
                if (posTag.startsWith("ao")) return PoS.ADJECTIVE;

                if (posTag.startsWith("rg")) return PoS.ADVERB;
                if (posTag.startsWith("rn")) return PoS.ADVERB;

                if (posTag.startsWith("dd")) return PoS.ARTICLE;
                if (posTag.startsWith("dp")) return PoS.ARTICLE;
                if (posTag.startsWith("dt")) return PoS.ARTICLE;
                if (posTag.startsWith("de")) return PoS.ARTICLE;
                if (posTag.startsWith("di")) return PoS.ARTICLE;
                if (posTag.startsWith("da")) return PoS.ARTICLE;

                if (posTag.startsWith("nc")) return PoS.NOUN;
                if (posTag.startsWith("np")) return PoS.PROPER_NOUN;

                if (posTag.startsWith("vm")) return PoS.VERB;
                if (posTag.startsWith("va")) return PoS.VERB;
                if (posTag.startsWith("vs")) return PoS.VERB;

                if (posTag.startsWith("pp")) return PoS.PRONOUN;
                if (posTag.startsWith("pd")) return PoS.PRONOUN;
                if (posTag.startsWith("px")) return PoS.PRONOUN;
                if (posTag.startsWith("pi")) return PoS.PRONOUN;
                if (posTag.startsWith("pt")) return PoS.PRONOUN;
                if (posTag.startsWith("pr")) return PoS.PRONOUN;
                if (posTag.startsWith("pe")) return PoS.PRONOUN;

                if (posTag.startsWith("cc")) return PoS.CONJUNCTION;
                if (posTag.startsWith("cs")) return PoS.CONJUNCTION;

                if (posTag.startsWith("i")) return PoS.INTERJECTION;

                if (posTag.startsWith("sp")) return PoS.PREPOSITION;

                if (posTag.startsWith("f")) return PoS.PUNCTUATION_MARK;

                if (posTag.startsWith("z")) return PoS.NUMBER;

                if (posTag.startsWith("w")) return PoS.DATE;
                return PoS.SYMBOL;
        }
    }
}
