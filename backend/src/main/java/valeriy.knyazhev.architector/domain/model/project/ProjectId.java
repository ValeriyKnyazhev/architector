package valeriy.knyazhev.architector.domain.model.project;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
public final class ProjectId {


    private String id;

    private ProjectId(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    public static ProjectId of(@Nonnull String id) {
        return new ProjectId(id);
    }

    @Nonnull
    public static ProjectId nextId() {
        return new ProjectId(UUID.randomUUID().toString());
    }

    @Nonnull
    public String id() {
        return this.id;
    }

}
