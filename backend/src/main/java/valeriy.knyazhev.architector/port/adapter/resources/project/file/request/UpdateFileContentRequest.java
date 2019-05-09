package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import org.apache.http.util.Args;

import javax.annotation.Nonnull;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileContentRequest
{

    @Nonnull
    private String content;

    @Nonnull
    private String commitMessage;

    public void setContent(@Nonnull String content)
    {
        this.content = Args.notNull(content, "Content is required.");
    }

    public void setCommitMessage(@Nonnull String commitMessage)
    {
        this.commitMessage = Args.notNull(commitMessage, "Commit message is required.");
    }

    @Nonnull
    public String content()
    {
        return this.content;
    }

    @Nonnull
    public String commitMessage()
    {
        return this.commitMessage;
    }

}
