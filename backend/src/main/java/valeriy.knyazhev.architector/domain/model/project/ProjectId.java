package valeriy.knyazhev.architector.domain.model.project;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProjectId
{

    @Nonnull
    private String id;

    private ProjectId(@Nonnull String id)
    {
        this.id = id;
    }

    @Nonnull
    public String id()
    {
        return this.id;
    }

    @Nonnull
    public static ProjectId of(@Nonnull String id)
    {
        return new ProjectId(id);
    }

    @Nonnull
    public static ProjectId nextId()
    {
        return new ProjectId(UUID.randomUUID().toString());
    }

}
