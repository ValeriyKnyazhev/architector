package valeriy.knyazhev.architector.application.project;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class AccessGrantedInfo
{

    @Nonnull
    private List<String> readAccess;

    @Nonnull
    private List<String> writeAccess;

    public AccessGrantedInfo(@Nonnull List<String> readAccess, @Nonnull List<String> writeAccess)
    {
        this.readAccess = Args.notNull(readAccess, "Users with read access rights are required.");
        this.writeAccess = Args.notNull(writeAccess, "Users with write access rights are required.");
    }

    @Nonnull
    public List<String> readAccess()
    {
        return this.readAccess;
    }

    @Nonnull
    public List<String> writeAccess()
    {
        return this.writeAccess;
    }

}