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

            @Override
            public boolean canBeRead()
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

            @Override
            public boolean canBeRead()
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

            @Override
            public boolean canBeRead()
            {
                return true;
            }
        },
    FORBIDDEN
        {
            @Override
            public boolean canBeUpdated()
            {
                return false;
            }

            @Override
            public boolean canBeRead()
            {
                return false;
            }
        };

    public abstract boolean canBeUpdated();

    public abstract boolean canBeRead();

}
