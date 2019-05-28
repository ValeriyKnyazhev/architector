package valeriy.knyazhev.architector.domain.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.domain.model.commit.*;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection;
import valeriy.knyazhev.architector.domain.model.commit.projection.Projection.FileProjection;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.addItem;
import static valeriy.knyazhev.architector.domain.model.commit.CommitItem.deleteItem;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommitCombinator.class)
public class CommitCombinatorTests
{

    private final static ProjectId PROJECT_ID = ProjectId.nextId();

    private final static String PROJECT_NAME = "Name";

    private final static String PROJECT_DESCRIPTION = "Description";

    private static FileMetadataChanges FILE_METADATA = FileMetadataChanges.builder()
        .name("File")
        .authors(emptyList())
        .organizations(emptyList())
        .build();

    private static FileDescriptionChanges FILE_DESCRIPTION = FileDescriptionChanges.builder()
        .descriptions(emptyList())
        .build();

    @Test
    public void shouldCombineCommitItemsToProjection()
    {
        // given
        FileId fileId = FileId.nextId();
        List<Commit> commits = singletonList(
            sampleCommit(null, fileId, asList(
                addItem("1", 0),
                addItem("2", 0))
            )
        );

        // when
        Projection projection = CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits);

        // then
        assertThat(projection.files()).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        FileProjection fileProjection = projection.files().get(0);
        softly.assertThat(fileProjection.fileId()).isEqualTo(fileId);
        softly.assertThat(fileProjection.items()).isEqualTo(asList("1", "2"));
        softly.assertAll();
    }

    @Test
    public void shouldCombineFewAdditionCommitsToProjection()
    {
        // given
        FileId fileId = FileId.nextId();
        Commit firstCommit = sampleCommit(null, fileId, singletonList(addItem("1", 0)));
        List<Commit> commits = asList(
            firstCommit,
            sampleCommit(firstCommit.id(), fileId, singletonList(addItem("2", 1)))
        );

        // when
        Projection projection = CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits);

        // then
        assertThat(projection.files()).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        FileProjection fileProjection = projection.files().get(0);
        softly.assertThat(fileProjection.fileId()).isEqualTo(fileId);
        softly.assertThat(fileProjection.items()).isEqualTo(asList("1", "2"));
        softly.assertAll();
    }

    @Test
    public void shouldCombineAdditionAndDeletionCommitsToProjection()
    {
        // given
        FileId fileId = FileId.nextId();
        Commit firstCommit = sampleCommit(null, fileId, singletonList(addItem("1", 0)));
        List<Commit> commits = asList(
            firstCommit,
            sampleCommit(firstCommit.id(), fileId, singletonList(deleteItem("1", 1)))
        );

        // when
        Projection projection = CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits);

        // then
        assertThat(projection.files()).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        FileProjection fileProjection = projection.files().get(0);
        softly.assertThat(fileProjection.fileId()).isEqualTo(fileId);
        softly.assertThat(fileProjection.items()).isEqualTo(emptyList());
        softly.assertAll();
    }

    @Test
    public void shouldNotCombineCommitsToProjectionIfValuesNotMatched()
    {
        // given
        FileId fileId = FileId.nextId();
        Commit firstCommit = sampleCommit(null, fileId, singletonList(addItem("1", 0)));
        List<Commit> commits = asList(
            firstCommit,
            sampleCommit(firstCommit.id(), fileId, singletonList(deleteItem("2", 1)))
        );

        // expect
        assertThatThrownBy(() -> CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Current item does not match with deleted item.");
    }

    @Test
    public void shouldNotCombineCommitsToProjectionIfFoundFewDeletionItemsInOnePosition()
    {
        // given
        FileId fileId = FileId.nextId();
        Commit firstCommit = sampleCommit(null, fileId, asList(
            addItem("1", 0), addItem("2", 0)));
        List<Commit> commits = asList(
            firstCommit,
            sampleCommit(
                firstCommit.id(), fileId, asList(deleteItem("1", 1), deleteItem("2", 1))
            )
        );

        // expect
        assertThatThrownBy(() -> CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Commit must not have a few deletion items in one position.");
    }

    @Test
    public void shouldCombineComplexCommitsToProjection()
    {
        // given
        FileId fileId = FileId.nextId();
        Commit firstCommit = sampleCommit(null, fileId,
            asList(
                addItem("1", 0),
                addItem("2", 0),
                addItem("3", 0),
                addItem("4", 0),
                addItem("5", 0),
                addItem("8", 0),
                addItem("9", 0)
            )
        );
        List<Commit> commits = asList(
            firstCommit,
            sampleCommit(firstCommit.id(), fileId,
                asList(
                    addItem("0", 0),
                    deleteItem("3", 3),
                    deleteItem("4", 4),
                    addItem("6", 5),
                    addItem("7", 5),
                    deleteItem("9", 7)
                )
            )
        );

        // when
        Projection projection = CommitCombinator.combineCommits(PROJECT_NAME, PROJECT_DESCRIPTION, commits);

        // then
        assertThat(projection.files()).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        FileProjection fileProjection = projection.files().get(0);
        softly.assertThat(fileProjection.fileId()).isEqualTo(fileId);
        softly.assertThat(fileProjection.items()).isEqualTo(asList("0", "1", "2", "5", "6", "7", "8"));
        softly.assertAll();
    }

    private static Commit sampleCommit(Long parentId, FileId fileId, List<CommitItem> items)
    {
        CommitDescription data = CommitDescription.builder()
            .files(
                singletonList(
                    CommitFileItem.of(fileId, FILE_METADATA, FILE_DESCRIPTION, items)
                )
            )
            .build();
        return Commit.builder()
            .parentId(parentId)
            .projectId(PROJECT_ID)
            .author("author")
            .data(data)
            .build();
    }

}
