package valeriy.knyazhev.architector.factory;

import valeriy.knyazhev.architector.domain.model.project.Project;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public final class ProjectObjectFactory
{

    private ProjectObjectFactory()
    {
        // nop
    }

    public static Project emptyProject(String author)
    {
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor(author)
            .construct();
        enrichProject(project);
        return project;
    }

    public static Project projectWithFiles(String author)
    {
        Project project = Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor(author)
            .withFile(createFile(FileId.nextId()))
            .construct();
        enrichProject(project);
        return project;
    }

    private static void enrichProject(Project project)
    {
        LocalDateTime time = LocalDateTime.now();
        project.setCreatedDate(time);
        project.setUpdatedDate(time);
    }

    public static File sampleFile()
    {
        return createFile(FileId.nextId());
    }

    public static FileDescription sampleDescription(String value)
    {
        return createDescription(value);
    }

    public static FileMetadata sampleMetadata(String value)
    {
        return createMetadata(value);
    }

    private static File createFile(FileId fileId)
    {
        return File.constructor()
            .withFileId(fileId)
            .withIsoId("ISO-10303-21")
            .withSchema("IFC4")
            .withDescription(createDescription("tmp"))
            .withMetadata(createMetadata("tmp"))
            .withContent(FileContent.of(List.of()))
            .construct();
    }

    private static FileDescription createDescription(String value)
    {
        return FileDescription.of(List.of(value), value);
    }

    private static FileMetadata createMetadata(String value)
    {
        return FileMetadata.builder()
            .name(value)
            .authors(List.of(value))
            .organizations(List.of(value))
            .timestamp(LocalDateTime.now())
            .preprocessorVersion(value)
            .originatingSystem(value)
            .authorization(value)
            .build();
    }

}
