package valeriy.knyazhev.architector.port.adapter.project.model;

import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectDescription;
import valeriy.knyazhev.architector.domain.model.project.ProjectMetadata;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileModel;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectModel.DescriptionModel;
import static valeriy.knyazhev.architector.port.adapter.project.model.ProjectModel.MetadataModel;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class ProjectMapper {

    @Nonnull
    public static ProjectModel buildProject(@Nonnull Project project) {
        List<FileModel> files = project.files().stream()
                .map(ProjectMapper::constructFile)
                .collect(toList());
        DescriptionModel description = constructDescription(project.description());
        MetadataModel metadata = constructMetadata(project.metadata());
        return new ProjectModel(project.projectId().id(), project.createdDate(), project.updatedDate(),
                project.schema(), description, metadata, files);
    }

    @Nonnull
    private static DescriptionModel constructDescription(@Nonnull ProjectDescription description) {
        return new DescriptionModel(description.descriptions(), description.implementationLevel());
    }

    @Nonnull
    private static MetadataModel constructMetadata(@Nonnull ProjectMetadata metadata) {
        return MetadataModel.builder()
                .name(metadata.name())
                .timestamp(metadata.timestamp())
                .authors(checkAndMapList(metadata.authors()))
                .organizations(checkAndMapList(metadata.organizations()))
                .preprocessorVersion(metadata.preprocessorVersion())
                .originatingSystem(metadata.originatingSystem())
                .authorisation(metadata.authorisation())
                .build();
    }

    @Nonnull
    private static FileModel constructFile(@Nonnull File file) {

        return new FileModel(file.fileId().id(), file.createdDate(), file.updatedDate());
    }

    @Nonnull
    private static List<String> checkAndMapList(@Nonnull List<String> items)
    {
        return items.stream().anyMatch(item -> !item.isEmpty())
                ? items
                : emptyList();
    }

}
