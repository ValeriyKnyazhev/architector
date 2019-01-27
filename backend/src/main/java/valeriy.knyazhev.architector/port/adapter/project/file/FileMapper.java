package valeriy.knyazhev.architector.port.adapter.project.file;

import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.port.adapter.project.file.model.FileWithContentModel;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class FileMapper {

    @Nonnull
    public static FileWithContentModel mapToModel(@Nonnull File file) {
        FileWithContentModel.FileContentModel content = new FileWithContentModel.FileContentModel(file.content().items());
        return new FileWithContentModel(file.fileId().id(), file.createdDate(), file.updatedDate(), content);
    }


}
