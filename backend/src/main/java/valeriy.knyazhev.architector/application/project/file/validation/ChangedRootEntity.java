package valeriy.knyazhev.architector.application.project.file.validation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChangedRootEntity
{

    private int id;

    @Nonnull
    private String name;

    public ChangedRootEntity(int id,
                             @Nonnull String name)
    {
        this.id = id;
        this.name = name;
    }

    public int id()
    {
        return this.id;
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

}
