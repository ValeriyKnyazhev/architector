package valeriy.knyazhev.architector.port.adapter.project.file;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileContentModel;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileModel;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper {

    @Nonnull
    public static FileContentModel buildContent(@Nonnull File file) {
        FileContentModel.ContentModel content = new FileContentModel.ContentModel(file.content().items());
        return new FileContentModel(file.fileId().id(), content);
    }

    @Nonnull
    public static FileModel buildFile(@Nonnull File file) {
        return new FileModel(file.fileId().id(), file.createdDate(), file.updatedDate());
    }

}
