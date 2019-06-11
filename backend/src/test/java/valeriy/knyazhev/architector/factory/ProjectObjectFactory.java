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

    public static Project emptyProject()
    {
        return Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor("author")
            .construct();
    }

    public static Project projectWithFiles()
    {
        return Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor("author")
            .withFile(createFile(FileId.nextId()))
            .construct();
    }

    public static File sampleFile()
    {
        return createFile(FileId.nextId());
    }

    private static File createFile(FileId fileId)
    {
        FileDescription description = FileDescription.of(List.of(), "");
        FileMetadata metadata = FileMetadata.builder()
            .name("name")
            .authors(List.of("author"))
            .organizations(List.of("organization"))
            .timestamp(LocalDateTime.now())
            .preprocessorVersion("version")
            .originatingSystem("system")
            .authorization("authorization")
            .build();
        return File.constructor()
            .withFileId(fileId)
            .withIsoId("ISO-10303-21")
            .withSchema("IFC4")
            .withDescription(description)
            .withMetadata(metadata)
            .withContent(FileContent.of(List.of()))
            .construct();
    }

    private ProjectObjectFactory()
    {
        // nop
    }

}
