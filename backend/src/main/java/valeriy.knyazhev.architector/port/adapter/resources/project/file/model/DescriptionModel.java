package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class DescriptionModel
{

    @Nonnull
    private List<String> descriptions;

    @Nonnull
    private String implementationLevel;

    private DescriptionModel(@Nonnull List<String> descriptions,
                             @Nonnull String implementationLevel)
    {
        this.descriptions = descriptions;
        this.implementationLevel = implementationLevel;
    }

    @Nonnull
    public static DescriptionModel of(@Nonnull FileDescription description)
    {
        return new DescriptionModel(
            description.descriptions(), description.implementationLevel()
        );
    }

}
