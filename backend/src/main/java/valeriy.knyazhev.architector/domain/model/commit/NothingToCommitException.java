package valeriy.knyazhev.architector.domain.model.commit;

/**
 * @author Valeriy Knyazhev
 */
public class NothingToCommitException extends IllegalStateException
{

    public NothingToCommitException()
    {
        super("Nothing to commit: empty changes.");
    }

}
