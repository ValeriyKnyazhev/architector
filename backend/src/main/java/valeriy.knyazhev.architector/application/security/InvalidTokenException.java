package valeriy.knyazhev.architector.application.security;

/**
 * @author Valeriy Knyazhev
 */
public class InvalidTokenException extends IllegalStateException
{

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public InvalidTokenException(String msg, Throwable t) {
        super(msg, t);
    }

}