package valeriy.knyazhev.architector.application.project.file.command;

import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileContentCommand
{

    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private String author;

    @Nonnull
    private List<String> content;

    @Nonnull
    private String commitMessage;

    public UpdateFileContentCommand(@Nonnull String projectId,
                                    @Nonnull String fileId,
                                    @Nonnull String author,
                                    @Nonnull String content,
                                    @Nonnull String commitMessage)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.author = author;
        this.content = asList(content.split(System.lineSeparator()));
        this.commitMessage = commitMessage;
    }

    @Nonnull
    public ProjectId projectId()
    {
        return ProjectId.of(this.projectId);
    }

    @Nonnull
    public FileId fileId()
    {
        return FileId.of(this.fileId);
    }

    @Nonnull
    public String author()
    {
        return this.author;
    }

    @Nonnull
    public List<String> content()
    {
        return this.content;
    }

    @Nonnull
    public String commitMessage()
    {
        return this.commitMessage;
    }
}