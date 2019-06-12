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
        return Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor(author)
            .construct();
    }

    public static Project projectWithFiles(String author)
    {
        return Project.constructor()
            .projectId(ProjectId.nextId())
            .withName("Project")
            .withDescription("Description")
            .withAuthor(author)
            .withFile(createFile(FileId.nextId()))
            .construct();
    }

    public static File sampleFile()
    {
        return createFile(FileId.nextId());
    }

    public static FileDescription sampleDescription()
    {
        return createDescription();
    }

    public static FileMetadata sampleMetadata()
    {
        return createMetadata();
    }

    private static File createFile(FileId fileId)
    {
        return File.constructor()
            .withFileId(fileId)
            .withIsoId("ISO-10303-21")
            .withSchema("IFC4")
            .withDescription(createDescription())
            .withMetadata(createMetadata())
            .withContent(FileContent.of(List.of()))
            .construct();
    }

    private static FileDescription createDescription()
    {
        return FileDescription.of(List.of(), "");
    }

    private static FileMetadata createMetadata()
    {
        return FileMetadata.builder()
            .name("name")
            .authors(List.of("author"))
            .organizations(List.of("organization"))
            .timestamp(LocalDateTime.now())
            .preprocessorVersion("version")
            .originatingSystem("system")
            .authorization("authorization")
            .build();
    }

}
