package valeriy.knyazhev.architector.factory;

import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public final class CommitObjectFactory
{

    private CommitObjectFactory()
    {
        // nop
    }

    public static Commit commitWithMetadataChanges(ProjectId projectId,
                                                   FileId fileId,
                                                   long commitId,
                                                   Long parentId,
                                                   FileMetadataChanges metadataChanges)
    {
        Commit commit = Commit.builder()
            .projectId(projectId)
            .author("author")
            .message("message")
            .parentId(parentId)
            .data(CommitDescription.builder().files(List.of(createFile(fileId, metadataChanges))).build())
            .build();
        commit.setId(commitId);
        return commit;
    }

    public static Commit commitWithDescriptionChanges(ProjectId projectId,
                                                      FileId fileId,
                                                      long commitId,
                                                      Long parentId,
                                                      FileDescriptionChanges descriptionChanges)
    {
        Commit commit = Commit.builder()
            .projectId(projectId)
            .author("author")
            .message("message")
            .parentId(parentId)
            .data(CommitDescription.builder().files(List.of(createFile(fileId, descriptionChanges))).build())
            .build();
        commit.setId(commitId);
        return commit;
    }

    public static Commit commitWithContentChanges(ProjectId projectId,
                                                  FileId fileId,
                                                  long commitId,
                                                  Long parentId,
                                                  List<CommitItem> items)
    {
        Commit commit = Commit.builder()
            .projectId(projectId)
            .author("author")
            .message("message")
            .parentId(parentId)
            .data(CommitDescription.builder().files(List.of(createFile(fileId, items))).build())
            .build();
        commit.setId(commitId);
        return commit;
    }

    public static Commit commitWithData(ProjectId projectId,
                                        FileId fileId,
                                        long commitId,
                                        Long parentId,
                                        FileMetadataChanges metadataChanges,
                                        FileDescriptionChanges descriptionChanges,
                                        List<CommitItem> items)
    {
        Commit commit = Commit.builder()
            .projectId(projectId)
            .author("author")
            .message("message")
            .parentId(parentId)
            .data(CommitDescription.builder()
                .files(List.of(createFile(fileId, metadataChanges, descriptionChanges, items)))
                .build())
            .build();
        commit.setId(commitId);
        return commit;
    }

    private static CommitFileItem createFile(FileId fileId,
                                             FileMetadataChanges metadataChanges,
                                             FileDescriptionChanges descriptionChanges,
                                             List<CommitItem> items)
    {
        return CommitFileItem.of(
            fileId, "ISO", "IFC4", metadataChanges, descriptionChanges, items
        );
    }

    private static CommitFileItem createFile(FileId fileId, FileMetadataChanges metadataChanges)
    {
        return CommitFileItem.of(
            fileId, "ISO", "IFC4", metadataChanges, FileDescriptionChanges.empty(), List.of()
        );
    }

    private static CommitFileItem createFile(FileId fileId, FileDescriptionChanges descriptionChanges)
    {
        return CommitFileItem.of(
            fileId, "ISO", "IFC4", FileMetadataChanges.empty(), descriptionChanges, List.of()
        );
    }

    private static CommitFileItem createFile(FileId fileId, List<CommitItem> items)
    {
        return CommitFileItem.of(
            fileId, "ISO", "IFC4", FileMetadataChanges.empty(), FileDescriptionChanges.empty(), items
        );
    }

}
