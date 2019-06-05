package valeriy.knyazhev.architector.application.project.file.conflict.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DescriptionConflictChanges
{

    @Nonnull
    private ConflictChange descriptions;

    @Nonnull
    private ConflictChange implementationLevel;

    @Builder
    private DescriptionConflictChanges(@Nonnull ConflictChange descriptions,
                                       @Nonnull ConflictChange implementationLevel)
    {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

    @JsonIgnore
    public boolean isEmpty()
    {
        return !this.descriptions.hasConflict() && !this.implementationLevel.hasConflict();
    }

    @Nonnull
    public ConflictChange descriptions()
    {
        return this.descriptions;
    }

    @Nonnull
    public ConflictChange implementationLevel()
    {
        return this.implementationLevel;
    }

}
