package valeriy.knyazhev.architector.port.adapter.resources.project.file.commit.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class FileChangesModel {


    @Nonnull
    private String projectId;

    @Nonnull
    private String name;

    @Nonnull
    private List<FileCommitBriefModel> changes;

    public FileChangesModel(@Nonnull String projectId,
                            @Nonnull String name,
                            @Nonnull List<FileCommitBriefModel> changes) {
        this.projectId = projectId;
        this.name = name;
        this.changes = changes;
    }

}