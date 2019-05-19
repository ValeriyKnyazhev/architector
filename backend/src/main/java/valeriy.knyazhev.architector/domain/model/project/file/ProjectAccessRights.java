package valeriy.knyazhev.architector.domain.model.project.file;

/**
 * @author Valeriy Knyazhev
 */
public enum ProjectAccessRights
{

    OWNER
        {
            @Override
            public boolean canBeUpdated()
            {
                return true;
            }
        },
    WRITE
        {
            @Override
            public boolean canBeUpdated()
            {
                return true;
            }
        },
    READ
        {
            @Override
            public boolean canBeUpdated()
            {
                return false;
            }
        },
    FORBIDDEN
        {
            @Override
            public boolean canBeUpdated()
            {
                return false;
            }
        };

    public abstract boolean canBeUpdated();

}
