package valeriy.knyazhev.architector.application.project.file.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class DeleteFileCommand {

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private String author;

    public DeleteFileCommand(@Nonnull String projectId,
                             @Nonnull String fileId,
                             @Nonnull String author) {
        this.projectId = projectId;
        this.fileId = fileId;
        this.author = author;
    }

    @Nonnull
    public ProjectId projectId() {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId() {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public String author() {
        return this.author;
    }

}
