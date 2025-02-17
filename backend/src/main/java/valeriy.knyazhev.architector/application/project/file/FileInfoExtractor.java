package valeriy.knyazhev.architector.application.project.file;

import lombok.RequiredArgsConstructor;
import org.bimserver.emf.Schema;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class FileInfoExtractor
{

    @Nonnull
    public static String extractSchemaVersion(@Nonnull IfcHeader header)
    {
        return header.getIfcSchemaVersion();
    }

    @Nonnull
    public static FileDescription extractDescription(@Nonnull IfcHeader header)
    {
        return FileDescription.of(header.getDescription(), header.getImplementationLevel());
    }

    @Nonnull
    public static FileMetadata extractMetadata(@Nonnull IfcHeader header)
    {

        LocalDateTime date = header.getTimeStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return FileMetadata.builder()
            .name(header.getFilename())
            .timestamp(date)
            .authors(header.getAuthor())
            .organizations(header.getOrganization())
            .preprocessorVersion(header.getPreProcessorVersion())
            .originatingSystem(header.getOriginatingSystem())
            .authorization(header.getAuthorization())
            .build();
    }

}
