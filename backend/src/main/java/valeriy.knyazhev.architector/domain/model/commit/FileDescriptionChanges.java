package valeriy.knyazhev.architector.domain.model.commit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FileDescriptionChanges
{

    private List<String> descriptions;

    private String implementationLevel;

    @Builder
    private FileDescriptionChanges(List<String> descriptions, String implementationLevel)
    {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

    protected FileDescriptionChanges()
    {
        // empty
    }

    @JsonIgnore
    public boolean isEmpty()
    {
        return this.descriptions == null && this.implementationLevel == null;
    }

    public List<String> descriptions()
    {
        return this.descriptions;
    }

    @Nonnull
    public List<String> newDescriptions(@Nonnull List<String> oldValue)
    {
        return this.descriptions != null ? this.descriptions : oldValue;
    }

    public String implementationLevel()
    {
        return this.implementationLevel;
    }

    @Nonnull
    public String newImplementationLevel(@Nonnull String oldValue)
    {
        return this.implementationLevel != null ? this.implementationLevel : oldValue;
    }

    @Nonnull
    public static FileDescriptionChanges empty()
    {
        return new FileDescriptionChanges();
    }

}
