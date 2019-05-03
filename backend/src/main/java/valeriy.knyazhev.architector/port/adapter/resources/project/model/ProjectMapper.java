package valeriy.knyazhev.architector.port.adapter.resources.project.model;

import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.port.adapter.resources.project.file.model.FileBriefModel;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public final class ProjectMapper
{

    @Nonnull
    public static ProjectDescriptorModel buildProject(@Nonnull Project project)
    {
        List<FileBriefModel> files = project.files().stream()
            .map(ProjectMapper::constructFile)
            .collect(toList());
        return new ProjectDescriptorModel(
            project.projectId().id(),
            project.createdDate(),
            project.updatedDate(),
            project.name(),
            project.author(),
            project.description(),
            files
        );
    }

    @Nonnull
    private static FileBriefModel constructFile(@Nonnull File file)
    {
        return new FileBriefModel(
            file.fileId().id(),
            file.metadata().name(),
            file.createdDate(),
            file.updatedDate(),
            file.schema()
        );
    }


}
