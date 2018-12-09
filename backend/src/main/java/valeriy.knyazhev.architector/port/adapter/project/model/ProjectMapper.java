package valeriy.knyazhev.architector.port.adapter.project.model;

import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.port.adapter.project.model.FileModel.FileDescriptionModel;
import valeriy.knyazhev.architector.port.adapter.project.model.FileModel.FileMetadataModel;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class ProjectMapper {

    @Nonnull
    public static ProjectModel mapToModel(@Nonnull Project project) {
        List<FileModel> files = project.files().stream()
                .map(ProjectMapper::constructFile)
                .collect(toList());
        return new ProjectModel(project.projectId().id(), project.createdDate(), project.updatedDate(), files);
    }

    @Nonnull
    private static FileModel constructFile(@Nonnull File file) {
        FileDescriptionModel description = constructFileDescription(file.description());
        FileMetadataModel metadata = constructFileMetadata(file.metadata());
        return new FileModel(file.fileId().id(), file.createdDate(), file.updatedDate(), description, metadata);
    }

    @Nonnull
    private static FileDescriptionModel constructFileDescription(@Nonnull FileDescription description) {
        return new FileDescriptionModel(description.description(), description.implementationLevel());
    }

    @Nonnull
    private static FileMetadataModel constructFileMetadata(@Nonnull FileMetadata metadata) {
        return FileMetadataModel.builder()
                .name(metadata.name())
                .timestamp(metadata.timestamp())
                .author(metadata.author())
                .organization(metadata.organization())
                .preprocessorVersion(metadata.preprocessorVersion())
                .originatingSystem(metadata.originatingSystem())
                .authorisation(metadata.authorisation())
                .build();
    }
}
