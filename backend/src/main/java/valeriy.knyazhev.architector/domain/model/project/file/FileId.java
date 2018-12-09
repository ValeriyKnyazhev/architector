package valeriy.knyazhev.architector.domain.model.project.file;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
public final class FileId {


    private String id;

    private FileId(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    public static FileId of(@Nonnull String id) {
        return new FileId(id);
    }

    @Nonnull
    public static FileId nextId() {
        return new FileId(UUID.randomUUID().toString());
    }

    @Nonnull
    public String id() {
        return this.id;
    }

}
