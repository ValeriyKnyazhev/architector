package valeriy.knyazhev.architector.port.adapter.resources.project.file.model;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.ProjectAccessRights;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper
{

    @Nonnull
    public static FileContentModel buildContent(@Nonnull File file)
    {
        return new FileContentModel(
            file.fileId().id(),
            MetadataModel.of(file.metadata()),
            DescriptionModel.of(file.description()),
            file.content().items()
        );
    }

    @Nonnull
    public static FileDescriptorModel buildFile(@Nonnull File file, @Nonnull ProjectAccessRights accessRights)
    {
        MetadataModel metadata = MetadataModel.of(file.metadata());
        DescriptionModel description = DescriptionModel.of(file.description());
        return new FileDescriptorModel(
            file.fileId().id(), file.createdDate(), file.updatedDate(),
            file.schema(), metadata, description, accessRights
        );
    }

}
