package de.jpaw.batch.api;


/** Process input of type E to produce output of type F. */
public interface BatchProcessorFactory<E,F> extends Contributor {
    BatchProcessor<E,F> getProcessor(int threadNo) throws Exception;
}
