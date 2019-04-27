package valeriy.knyazhev.architector.port.adapter.resources.project.commit.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class ProjectChangesModel {


    @Nonnull
    private String projectId;

    @Nonnull
    private String name;

    @Nonnull
    private List<CommitBriefModel> changes;

    public ProjectChangesModel(@Nonnull String projectId,
                               @Nonnull String name,
                               @Nonnull List<CommitBriefModel> changes) {
        this.projectId = projectId;
        this.name = name;
        this.changes = changes;
    }

}