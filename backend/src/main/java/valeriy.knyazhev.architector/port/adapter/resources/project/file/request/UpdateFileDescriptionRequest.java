package valeriy.knyazhev.architector.port.adapter.resources.project.file.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.http.util.Args;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class UpdateFileDescriptionRequest
{

    @Nonnull
    private List<String> descriptions;

    @Nonnull
    private String implementationLevel;

    @Nonnull
    private Long headCommitId;

    public void setDescriptions(@Nonnull List<String> descriptions)
    {
        this.descriptions = Args.notNull(descriptions, "Descriptions are required.");
    }

    public void setImplementationLevel(@Nonnull String implementationLevel)
    {
        this.implementationLevel = Args.notNull(implementationLevel, "Implementation level is required.");
    }

    public void setHeadCommitId(@Nonnull Long headCommitId)
    {
        this.headCommitId = Args.notNull(headCommitId, "Head commit identifier is required.");
    }

    @Nonnull
    public List<String> descriptions()
    {
        return this.descriptions;
    }

    @Nonnull
    public String implementationLevel()
    {
        return this.implementationLevel;
    }

    public long headCommitId()
    {
        return this.headCommitId;
    }

}
