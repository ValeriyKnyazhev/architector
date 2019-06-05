package valeriy.knyazhev.architector.application.project.file;

import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public class IFCFileWriter
{

    @Nonnull
    public static byte[] write(@Nonnull File file)
    {
        return writeFileData(
            file.isoId(),
            file.schema(),
            file.metadata(),
            file.description(),
            file.content().items()
        );
    }

    @Nonnull
    public static byte[] write(@Nonnull Projection.FileProjection file)
    {
        return writeFileData(
            file.isoId(),
            file.schema(),
            file.metadata(),
            file.description(),
            file.items()
        );
    }


    @Nonnull
    private static byte[] writeFileData(@Nonnull String isoId,
                                 @Nonnull String schema,
                                 @Nonnull FileMetadata metadata,
                                 @Nonnull FileDescription description,
                                 @Nonnull List<String> items)
    {
        String separator = System.lineSeparator();
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(isoId).append(";").append(separator)
            .append("HEADER;")
            .append(separator)
            .append(separator);
        contentBuilder.append("FILE_DESCRIPTION(")
            .append(separator)
            .append("('").append(String.join("','", description.descriptions())).append("'),")
            .append(separator)
            .append("'").append(description.implementationLevel()).append("'")
            .append(separator)
            .append(");")
            .append(separator)
            .append(separator);
        contentBuilder.append("FILE_NAME(")
            .append(separator)
            .append("'").append(metadata.name()).append("'")
            .append(separator)
            .append("'").append(metadata.timestamp()).append("'")
            .append(separator)
            .append("('").append(String.join("','", metadata.authors())).append("'),")
            .append(separator)
            .append("('").append(String.join("','", metadata.organizations())).append("'),")
            .append(separator)
            .append("'").append(metadata.preprocessorVersion()).append("'")
            .append(separator)
            .append("'").append(metadata.originatingSystem()).append("'")
            .append(separator)
            .append("'").append(metadata.authorization()).append("'")
            .append(separator)
            .append(");")
            .append(separator)
            .append(separator);
        contentBuilder.append("FILE_SCHEMA(('").append(schema).append("'));")
            .append(separator);
        contentBuilder.append("ENDSEC;")
            .append(separator)
            .append(separator);
        contentBuilder.append("DATA;")
            .append(separator);
        items.forEach(item -> contentBuilder.append(item).append(separator));
        contentBuilder.append("ENDSEC;")
            .append(separator);
        contentBuilder.append("END-")
            .append(isoId)
            .append(";");
        return contentBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

}
