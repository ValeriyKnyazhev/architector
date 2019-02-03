package valeriy.knyazhev.architector.application.project;

import lombok.RequiredArgsConstructor;
import org.bimserver.models.store.IfcHeader;
import org.springframework.stereotype.Service;
import valeriy.knyazhev.architector.domain.model.project.ProjectDescription;
import valeriy.knyazhev.architector.domain.model.project.ProjectMetadata;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author Valeriy Knyazhev
 */
@Service
@RequiredArgsConstructor
public final class ProjectInfoExtractor {

    @Nonnull
    public static ProjectDescription extractDescription(@Nonnull IfcHeader header) {
        return ProjectDescription.of(header.getDescription(), header.getImplementationLevel());
    }

    @Nonnull
    public static ProjectMetadata extractMetadata(@Nonnull IfcHeader header) {

        LocalDate date = header.getTimeStamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ProjectMetadata.builder()
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
