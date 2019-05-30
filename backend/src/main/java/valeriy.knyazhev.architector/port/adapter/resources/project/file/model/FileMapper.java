package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper
{

    @Nonnull
    public static FileDescriptorModel buildFile(@Nonnull File file,
                                                @Nonnull ProjectAccessRights accessRights,
                                                @Nonnull Long commitId)
    {
        MetadataModel metadata = MetadataModel.of(file.metadata());
        DescriptionModel description = DescriptionModel.of(file.description());
        return new FileDescriptorModel(
            file.fileId().id(),
            file.createdDate(),
            file.updatedDate(),
            accessRights,
            file.schema(),
            metadata,
            description,
            commitId
        );
    }

}
