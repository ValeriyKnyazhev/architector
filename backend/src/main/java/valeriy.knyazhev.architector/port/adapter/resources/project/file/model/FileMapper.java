package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectDescription;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectMetadata;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileModel.DescriptionModel;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileModel.MetadataModel;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper {

    @Nonnull
    public static FileContentModel buildContent(@Nonnull File file) {
        return new FileContentModel(
            file.fileId().id(), file.name(), file.content().items()
        );
    }

    @Nonnull
    public static FileModel buildFile(@Nonnull File file) {
        DescriptionModel description = constructDescription(file.description());
        MetadataModel metadata = constructMetadata(file.metadata());
        return new FileModel(
            file.fileId().id(), file.name(), file.createdDate(), file.updatedDate(),
            file.schema(), description, metadata
        );
    }

    @Nonnull
    private static DescriptionModel constructDescription(@Nonnull ProjectDescription description) {
        return new DescriptionModel(
            description.descriptions(), description.implementationLevel()
        );
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
    private static List<String> checkAndMapList(@Nonnull List<String> items) {
        return items.stream().anyMatch(item -> !item.isEmpty())
            ? items
            : emptyList();
    }


}
