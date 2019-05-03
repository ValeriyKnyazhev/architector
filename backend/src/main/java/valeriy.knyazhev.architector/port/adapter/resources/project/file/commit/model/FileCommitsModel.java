package valeriy.knyazhev.architector.port.adapter.resources.project.file.commit.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileCommitsModel
{


    @Nonnull
    private String projectId;

    @Nonnull
    private String fileId;

    @Nonnull
    private String name;

    @Nonnull
    private List<FileCommitBriefModel> commits;

    public FileCommitsModel(@Nonnull String projectId,
                            @Nonnull String fileId,
                            @Nonnull String name,
                            @Nonnull List<FileCommitBriefModel> commits)
    {
        this.projectId = projectId;
        this.fileId = fileId;
        this.name = name;
        this.commits = commits;
    }

}