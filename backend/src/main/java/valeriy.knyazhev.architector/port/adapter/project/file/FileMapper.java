package valeriy.knyazhev.architector.port.adapter.project.file;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileWithContentModel;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileWithContentModel.FileDescriptionModel;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileWithContentModel.FileMetadataModel;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper {

    @Nonnull
    public static FileWithContentModel mapToModel(@Nonnull File file) {
        FileDescriptionModel description = constructFileDescription(file.description());
        FileMetadataModel metadata = constructFileMetadata(file.metadata());
        FileWithContentModel.FileContentModel content = new FileWithContentModel.FileContentModel(file.content().items());
        return new FileWithContentModel(file.fileId().id(), file.createdDate(), file.updatedDate(), description, metadata, content);
    }

    @Nonnull
    private static FileDescriptionModel constructFileDescription(@Nonnull FileDescription description) {
        return new FileDescriptionModel(description.descriptions(), description.implementationLevel());
    }

    @Nonnull
    private static FileMetadataModel constructFileMetadata(@Nonnull FileMetadata metadata) {
        return FileMetadataModel.builder()
                .name(metadata.name())
                .timestamp(metadata.timestamp())
                .authors(metadata.authors())
                .organizations(metadata.organizations())
                .preprocessorVersion(metadata.preprocessorVersion())
                .originatingSystem(metadata.originatingSystem())
                .authorisation(metadata.authorisation())
                .build();
    }
}
