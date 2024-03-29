package cz.cvut.fit.bap.parser.controller.fetcher;

/**
 * Unchecked exception signalizing failed fetching
 */
public class FailedFetchException extends RuntimeException{
    public FailedFetchException(String s){
        super(s);
    }

    public FailedFetchException(Exception e){
        super(e);
    }

    public FailedFetchException(Exception e, String url) {
        super("Failed to fetch url: " + url + "  " + e.getMessage());
    }
}
