package valeriy.knyazhev.architector.application.commit;

import org.apache.http.util.Args;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import valeriy.knyazhev.architector.application.commit.command.FetchChangesHistoryCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeFileProjectionCommand;
import valeriy.knyazhev.architector.application.commit.command.MakeProjectProjectionCommand;
import valeriy.knyazhev.architector.application.commit.data.history.AbstractHistoryData;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.Commit;
import valeriy.knyazhev.architector.domain.model.commit.CommitRepository;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@Service
@Transactional
public class CommitQueryService
{

    private final CommitRepository commitRepository;

    private final ProjectionConstructService projectionConstructService;

    public CommitQueryService(@Nonnull CommitRepository commitRepository,
                              @Nonnull ProjectionConstructService projectionConstructService)
    {
        this.commitRepository = Args.notNull(commitRepository, "Commit repository is required.");
        this.projectionConstructService = Args.notNull(projectionConstructService,
            "Project construct service is required.");
    }

    @Nonnull
    public AbstractHistoryData fetchProjectHistory(@Nonnull FetchChangesHistoryCommand command)
    {
        Args.notNull(command, "Find commit command is required.");
        List<Commit> commits = this.commitRepository.findByProjectIdOrderByIdDesc(command.projectId());
        return command.constructHistory(commits);
    }

    @Nonnull
    public Projection fetchProjection(@Nonnull MakeProjectProjectionCommand command)
    {
        return this.projectionConstructService.makeProjection(command.projectId(), command.commitId());
    }

    @Nonnull
    public FileProjection fetchProjection(@Nonnull MakeFileProjectionCommand command)
    {
        ProjectId projectId = command.projectId();
        Projection projection = this.projectionConstructService.makeProjection(projectId, command.commitId());
        FileId fileId = command.fileId();
        return projection.files().stream()
            .filter(file -> fileId.equals(file.fileId()))
            .findFirst()
            .orElseThrow(() -> new FileNotFoundException(projectId, fileId));
    }

}

