package valeriy.knyazhev.architector.application.commit.command;

import lombok.Builder;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.application.commit.data.history.AbstractHistoryData;
import valeriy.knyazhev.architector.application.commit.data.history.FileHistoryData;
import valeriy.knyazhev.architector.application.commit.data.history.ProjectHistoryData;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FindCommitsCommand
{

    @Nonnull
    private ProjectId projectId;

    @Nullable
    private FileId fileId;

    @Builder
    private FindCommitsCommand(@Nonnull String projectId,
                               @Nullable String fileId)
    {
        this.projectId = ProjectId.of(Args.notBlank(projectId, "Project identifier is required."));
        this.fileId = Optional.ofNullable(fileId)
            .map(FileId::of)
            .orElse(null);
    }

    @Nonnull
    public ProjectId projectId()
    {
        return this.projectId;
    }

    @Nonnull
    public AbstractHistoryData constructHistory(@Nonnull Project project,
                                                @Nonnull List<Commit> commits)
    {
        Args.notNull(commits, "Commits are required.");
        Stream<Commit> commitsStream = commits.stream();
        if (this.fileId != null)
        {
            commitsStream = commitsStream
                .filter(commit -> commitRelatedToFile(commit, fileId));
        }
        List<Commit> filteredCommits = commitsStream.collect(Collectors.toList());
        return this.fileId == null
               ? new ProjectHistoryData(
            this.projectId.id(), filteredCommits
        )
               : new FileHistoryData(
                   this.fileId.id(), filteredCommits
               );
    }

    private static boolean commitRelatedToFile(@Nonnull Commit commit, @Nonnull FileId fileId)
    {
        return commit.data()
            .changedFiles()
            .stream()
            .anyMatch(file -> fileId.equals(file.fileId()));
    }

}
