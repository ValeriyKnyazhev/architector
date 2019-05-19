package valeriy.knyazhev.architector.domain.model;

/**
 * @author Valeriy Knyazhev
 */
public class AccessRightsNotFoundException extends IllegalStateException
{

    public AccessRightsNotFoundException()
    {
        super("Access rights not found.");
    }

}
