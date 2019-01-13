package valeriy.knyazhev.architector.application;

import lombok.RequiredArgsConstructor;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class FileBuilder {

    @Nonnull
    public static File buildFile(@Nonnull IfcHeader header, @Nonnull List<String> content) {
        return File.builder()
                .fileId(FileId.nextId())
                .description(extractDescription(header))
                .metadata(extractMetadata(header))
                .content(FileContent.of(content))
                .build();
    }

    @Nonnull
    private static FileDescription extractDescription(@Nonnull IfcHeader header) {
        return FileDescription.of(header.getDescription(), header.getImplementationLevel());
    }

    @Nonnull
    private static FileMetadata extractMetadata(@Nonnull IfcHeader header) {

        LocalDate date = header.getTimeStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return FileMetadata.builder()
                .name(header.getFilename())
                .timestamp(date)
                .authors(header.getAuthor())
                .organizations(header.getOrganization())
                .preprocessorVersion(header.getPreProcessorVersion())
                .originatingSystem(header.getOriginatingSystem())
                .authorisation(header.getAuthorization())
                .build();
    }

}
