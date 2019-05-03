package valeriy.knyazhev.architector.domain.model.project.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@JsonAutoDetect(fieldVisibility = ANY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FileId
{


    @Nonnull
    private String id;

    private FileId(@Nonnull String id)
    {
        this.id = id;
    }

    @Nonnull
    public static FileId of(@Nonnull String id)
    {
        return new FileId(id);
    }

    @Nonnull
    public static FileId nextId()
    {
        return new FileId(UUID.randomUUID().toString());
    }

    @Nonnull
    public String id()
    {
        return this.id;
    }

}
