package org.librairy.service.nlp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.librairy.service.nlp.annotators.StanfordAnnotatorWrapperES;
import org.librairy.service.nlp.annotators.StanfordPipeAnnotatorES;
import org.librairy.service.nlp.annotators.StanfordWordnetPipeAnnotatorES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    @Value("#{environment['SPOTLIGHT_ENDPOINT']?:'${spotlight.endpoint}'}")
    String endpoint;

    @Value("#{environment['SPOTLIGHT_THRESHOLD']?:${spotlight.threshold}}")
    Double threshold;

    LoadingCache<String, CoreNLPService> coreServices;

    LoadingCache<String, IXAService> ixaModels;

    LoadingCache<String, DBpediaService> dbpediaServices;

    LoadingCache<String, CoreNLPService> wordnetServices;

    @PostConstruct
    public void setup(){
        ixaModels = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, IXAService>() {
                            public IXAService load(String key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing IXA service for thread: " + key);
                                    IXAService ixaService = new IXAService(resourceFolder);
                                    ixaService.setup();
                                    return ixaService;
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });

        coreServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, CoreNLPService>() {
                            public CoreNLPService load(String key) throws ExecutionException {
                                LOG.info("Initializing CoreNLP service for thread: " + key);
                                try{
                                    CoreNLPService coreService = new CoreNLPService();
                                    coreService.setAnnotator(new StanfordPipeAnnotatorES());
                                    coreService.setTokenizer(new StanfordAnnotatorWrapperES());
                                    return coreService;
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });

        wordnetServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, CoreNLPService>() {
                            public CoreNLPService load(String key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing CoreNLP with Wordnet service for thread: " + key);
                                    CoreNLPService coreService = new CoreNLPService();
                                    coreService.setAnnotator(new StanfordWordnetPipeAnnotatorES(resourceFolder));
                                    coreService.setTokenizer(new StanfordAnnotatorWrapperES());
                                    return coreService;
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });


        dbpediaServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, DBpediaService>() {
                            public DBpediaService load(String key) throws ExecutionException {
                                try{
                                    LOG.info("Initializing DBpedia service for thread: " + key);
                                    return new DBpediaService(endpoint, threshold);
                                }catch(Exception e){
                                    LOG.error("Unexpected error",e);
                                    throw new ExecutionException(e);
                                }
                            }
                        });
    }

    public IXAService getIXAService(Thread thread) {
        try {
            return ixaModels.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CoreNLPService getCoreService(Thread thread) {

        try {
            return coreServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public CoreNLPService getWordnetService(Thread thread) {

        try {
            return wordnetServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public DBpediaService getDBpediaService(Thread thread) {

        try {
            return dbpediaServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

}
