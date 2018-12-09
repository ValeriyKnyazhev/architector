package valeriy.knyazhev.architector.application;

import lombok.RequiredArgsConstructor;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class ProjectBuilder {

    @Nonnull
    public static Project buildProject(@Nonnull IfcModelInterface ifcModel) {

        return Project.constructor()
                .projectId(ProjectId.nextId())
                .withFile(buildFile(ifcModel))
                .construct();
    }

    @Nonnull
    private static File buildFile(@Nonnull IfcModelInterface ifcModel) {
        IfcHeader headerSummary = ifcModel.getModelMetaData().getIfcHeader();
        return File.builder()
                .fileId(FileId.nextId())
                .description(extractDescription(headerSummary))
                .metadata(extractMetadata(headerSummary))
                .build();
    }

    @Nonnull
    private static FileDescription extractDescription(@Nonnull IfcHeader header) {
        String fullDescription = String.join(" ", header.getDescription());
        return FileDescription.of(fullDescription, header.getImplementationLevel());
    }

    @Nonnull
    private static FileMetadata extractMetadata(@Nonnull IfcHeader header) {

        LocalDate date = header.getTimeStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return FileMetadata.builder()
                .name(header.getFilename())
                .timestamp(date)
//                .author(header.getAuthor())
//                .organization(header.getOrganization())
                .preprocessorVersion(header.getPreProcessorVersion())
                .originatingSystem(header.getOriginatingSystem())
                .authorisation(header.getAuthorization())
                .build();
    }

}
