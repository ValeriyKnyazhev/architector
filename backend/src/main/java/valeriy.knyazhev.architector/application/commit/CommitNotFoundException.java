package valeriy.knyazhev.architector.application.commit;

/**
 * @author Valeriy Knyazhev
 */
public class CommitNotFoundException extends IllegalStateException
{

    public CommitNotFoundException(long commitId)
    {
        super(
            "Unable to find commit with identifier " + commitId + "."
        );
    }

}
