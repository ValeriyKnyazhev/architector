package valeriy.knyazhev.architector.application.project.file;

import org.bimserver.emf.Schema;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;

import static org.bimserver.emf.Schema.IFC2X3TC1;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FileData {

    @Nonnull
    private Schema schema = IFC2X3TC1;

    @Nonnull
    private String isoId;

    @Nonnull
    private FileDescription description;

    @Nonnull
    private FileMetadata metadata;

    @Nonnull
    private FileContent content;

    public FileData(@Nonnull String isoId,
                    @Nonnull FileDescription description,
                    @Nonnull FileMetadata metadata,
                    @Nonnull FileContent content) {
        this.isoId = isoId;
        this.description = description;
        this.metadata = metadata;
        this.content = content;
    }

    @Nonnull
    public Schema schema() {
        return this.schema;
    }

    @Nonnull
    public String isoId() {
        return this.isoId;
    }

    @Nonnull
    public FileDescription description() {
        return this.description;
    }

    @Nonnull
    public FileMetadata metadata() {
        return this.metadata;
    }

    @Nonnull
    public FileContent content() {
        return this.content;
    }

}
