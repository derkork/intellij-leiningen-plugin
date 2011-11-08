package de.janthomae.leiningenplugin.project;

/**
 * Date: 08.11.11
 * Time: 21:49
 *
 * @author Vladimir Matveev
 */
public class LeiningenProjectException extends Exception {
    public LeiningenProjectException(String message) {
        super(message);
    }

    public LeiningenProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeiningenProjectException(Throwable cause) {
        super(cause);
    }
}
