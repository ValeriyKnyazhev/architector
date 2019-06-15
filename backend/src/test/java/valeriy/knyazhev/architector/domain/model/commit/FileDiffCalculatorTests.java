package valeriy.knyazhev.architector.domain.model.commit;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.FileDiffCalculator;
import valeriy.knyazhev.architector.domain.model.commit.FileMetadataChanges;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.domain.model.commit.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileDiffCalculator.class)
public class FileDiffCalculatorTests
{

    @Autowired
    private FileDiffCalculator diffCalculator;

    @Test
    public void shouldFilesAreEquals()
    {
        // given
        List<String> content = generateContent(asList("1", "2"));
        FileContent oldFile = sampleFileContent(content);
        FileContent newFile = sampleFileContent(content);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(0);
    }

    @Test
    public void shouldAddLineToEndOfFile()
    {
        // given
        List<String> oldContent = generateContent(singletonList("1"));
        List<String> newContent = generateContent(asList("1", "2"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromEndOfFile()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "2"));
        List<String> newContent = generateContent(Collections.singletonList("1"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem.position()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    public void shouldAddLineToTopOfFile()
    {
        // given
        List<String> oldContent = generateContent(singletonList("2"));
        List<String> newContent = generateContent(asList("1", "2"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("1");
        softly.assertThat(changedItem.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem.position()).isEqualTo(0);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromTopOfFile()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "2"));
        List<String> newContent = generateContent(Collections.singletonList("2"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("1");
        softly.assertThat(changedItem.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldAddLineToMiddleOfFile()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "4"));
        List<String> newContent = generateContent(asList("1", "2", "3", "4"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(2);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem0 = diff.get(0);
        softly.assertThat(changedItem0.value()).isEqualTo("2");
        softly.assertThat(changedItem0.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem0.position()).isEqualTo(1);
        CommitItem changedItem1 = diff.get(1);
        softly.assertThat(changedItem1.value()).isEqualTo("3");
        softly.assertThat(changedItem1.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem1.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromMiddleOfFile()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "2", "3", "4"));
        List<String> newContent = generateContent(asList("1", "4"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(2);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem0 = diff.get(0);
        softly.assertThat(changedItem0.value()).isEqualTo("2");
        softly.assertThat(changedItem0.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem0.position()).isEqualTo(2);
        CommitItem changedItem1 = diff.get(1);
        softly.assertThat(changedItem1.value()).isEqualTo("3");
        softly.assertThat(changedItem1.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem1.position()).isEqualTo(3);
        softly.assertAll();
    }

    @Test
    public void shouldChangeLinesInFile()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "2", "3", "4", "5", "8", "9"));
        List<String> newContent = generateContent(asList("0", "1", "2", "5", "6", "7", "8"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(6);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem0 = diff.stream()
            .filter(item -> item.value().equals("0"))
            .findFirst().orElse(null);
        assertThat(changedItem0).isNotNull();
        softly.assertThat(changedItem0.value()).isEqualTo("0");
        softly.assertThat(changedItem0.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem0.position()).isEqualTo(0);
        CommitItem changedItem1 = diff.stream()
            .filter(item -> item.value().equals("3"))
            .findFirst().orElse(null);
        assertThat(changedItem1).isNotNull();
        softly.assertThat(changedItem1.value()).isEqualTo("3");
        softly.assertThat(changedItem1.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem1.position()).isEqualTo(3);
        CommitItem changedItem2 = diff.stream()
            .filter(item -> item.value().equals("4"))
            .findFirst().orElse(null);
        assertThat(changedItem2).isNotNull();
        softly.assertThat(changedItem2.value()).isEqualTo("4");
        softly.assertThat(changedItem2.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem2.position()).isEqualTo(4);
        CommitItem changedItem3 = diff.stream()
            .filter(item -> item.value().equals("6"))
            .findFirst().orElse(null);
        assertThat(changedItem3).isNotNull();
        softly.assertThat(changedItem3.value()).isEqualTo("6");
        softly.assertThat(changedItem3.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem3.position()).isEqualTo(5);
        CommitItem changedItem4 = diff.stream()
            .filter(item -> item.value().equals("7"))
            .findFirst().orElse(null);
        assertThat(changedItem4).isNotNull();
        softly.assertThat(changedItem4.value()).isEqualTo("7");
        softly.assertThat(changedItem4.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem4.position()).isEqualTo(5);
        CommitItem changedItem5 = diff.stream()
            .filter(item -> item.value().equals("9"))
            .findFirst().orElse(null);
        assertThat(changedItem5).isNotNull();
        softly.assertThat(changedItem5.value()).isEqualTo("9");
        softly.assertThat(changedItem5.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem5.position()).isEqualTo(7);
        softly.assertAll();
    }

    @Test
    public void shouldChangedLineReplaceByDeletionAndAddition()
    {
        // given
        List<String> oldContent = generateContent(asList("1", "2", "3"));
        List<String> newContent = generateContent(asList("1", "2 new", "3"));
        FileContent oldFile = sampleFileContent(oldContent);
        FileContent newFile = sampleFileContent(newContent);

        // when
        List<CommitItem> diff = FileDiffCalculator.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(2);
        SoftAssertions softly = new SoftAssertions();
        CommitItem changedItem0 = diff.stream()
            .filter(item -> item.value().equals("2"))
            .findFirst().orElse(null);
        assertThat(changedItem0).isNotNull();
        softly.assertThat(changedItem0.value()).isEqualTo("2");
        softly.assertThat(changedItem0.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem0.position()).isEqualTo(2);
        CommitItem changedItem1 = diff.stream()
            .filter(item -> item.value().equals("2 new"))
            .findFirst().orElse(null);
        assertThat(changedItem1).isNotNull();
        softly.assertThat(changedItem1.value()).isEqualTo("2 new");
        softly.assertThat(changedItem1.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem1.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldDefineNewMetadata()
    {
        // given
        FileMetadata oldMetadata = null;
        FileMetadata newMetadata = FileMetadata.builder()
            .name("new")
            .authors(List.of("new"))
            .organizations(List.of("new"))
            .timestamp(LocalDateTime.MAX)
            .preprocessorVersion("new")
            .originatingSystem("new")
            .authorization("new")
            .build();

        // when
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(oldMetadata, newMetadata);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isFalse();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.name()).isEqualTo(newMetadata.name());
        softly.assertThat(changes.authors()).isEqualTo(newMetadata.authors());
        softly.assertThat(changes.organizations()).isEqualTo(newMetadata.organizations());
        softly.assertThat(changes.timestamp()).isEqualTo(newMetadata.timestamp());
        softly.assertThat(changes.preprocessorVersion()).isEqualTo(newMetadata.preprocessorVersion());
        softly.assertThat(changes.originatingSystem()).isEqualTo(newMetadata.originatingSystem());
        softly.assertThat(changes.authorization()).isEqualTo(newMetadata.authorization());
        softly.assertAll();
    }

    @Test
    public void shouldDefineMetadataChanges()
    {
        // given
        FileMetadata oldMetadata = FileMetadata.builder()
            .name("old")
            .authors(List.of("old"))
            .organizations(List.of("old"))
            .timestamp(LocalDateTime.MIN)
            .preprocessorVersion("old")
            .originatingSystem("old")
            .authorization("old")
            .build();
        FileMetadata newMetadata = FileMetadata.builder()
            .name("new")
            .authors(List.of("new"))
            .organizations(List.of("new"))
            .timestamp(LocalDateTime.MAX)
            .preprocessorVersion("new")
            .originatingSystem("new")
            .authorization("new")
            .build();

        // when
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(oldMetadata, newMetadata);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isFalse();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.name()).isEqualTo(newMetadata.name());
        softly.assertThat(changes.authors()).isEqualTo(newMetadata.authors());
        softly.assertThat(changes.organizations()).isEqualTo(newMetadata.organizations());
        softly.assertThat(changes.timestamp()).isEqualTo(newMetadata.timestamp());
        softly.assertThat(changes.preprocessorVersion()).isEqualTo(newMetadata.preprocessorVersion());
        softly.assertThat(changes.originatingSystem()).isEqualTo(newMetadata.originatingSystem());
        softly.assertThat(changes.authorization()).isEqualTo(newMetadata.authorization());
        softly.assertAll();
    }

    @Test
    public void shouldDefineEmptyMetadataChangesIfEquals()
    {
        // given
        FileMetadata oldMetadata = FileMetadata.builder()
            .name("old")
            .authors(List.of("old"))
            .organizations(List.of("old"))
            .timestamp(LocalDateTime.MIN)
            .preprocessorVersion("old")
            .originatingSystem("old")
            .authorization("old")
            .build();
        FileMetadata newMetadata = oldMetadata;

        // when
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(oldMetadata, newMetadata);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldDefineEmptyMetadataChangesIfNewAbsent()
    {
        // given
        FileMetadata oldMetadata = FileMetadata.builder()
            .name("old")
            .authors(List.of("old"))
            .organizations(List.of("old"))
            .timestamp(LocalDateTime.MIN)
            .preprocessorVersion("old")
            .originatingSystem("old")
            .authorization("old")
            .build();
        FileMetadata newMetadata = null;

        // when
        FileMetadataChanges changes = FileDiffCalculator.defineMetadataChanges(oldMetadata, newMetadata);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNotDefineMetadataChangesIfBothAbsent()
    {
        // given
        FileMetadata oldMetadata = null;
        FileMetadata newMetadata = null;

        // expect
        assertThatThrownBy(() -> FileDiffCalculator.defineMetadataChanges(oldMetadata, newMetadata))
        .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldDefineNewDescription()
    {
        // given
        FileDescription oldDescription = null;
        FileDescription newDescription = FileDescription.of(List.of("new"), "new");

        // when
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(oldDescription, newDescription);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isFalse();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.descriptions()).isEqualTo(newDescription.descriptions());
        softly.assertThat(changes.implementationLevel()).isEqualTo(newDescription.implementationLevel());
        softly.assertAll();
    }

    @Test
    public void shouldDefineDescriptionChanges()
    {
        // given
        FileDescription oldDescription = FileDescription.of(List.of("old"), "old");
        FileDescription newDescription = FileDescription.of(List.of("new"), "new");

        // when
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(oldDescription, newDescription);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isFalse();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.descriptions()).isEqualTo(newDescription.descriptions());
        softly.assertThat(changes.implementationLevel()).isEqualTo(newDescription.implementationLevel());
        softly.assertAll();
    }

    @Test
    public void shouldDefineEmptyDescriptionChangesIfEquals()
    {
        // given
        FileDescription oldDescription = FileDescription.of(List.of("old"), "old");
        FileDescription newDescription = oldDescription;

        // when
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(oldDescription, newDescription);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldDefineEmptyDescriptionChangesIfNewAbsent()
    {
        // given
        FileDescription oldDescription = FileDescription.of(List.of("old"), "old");
        FileDescription newDescription = null;

        // when
        FileDescriptionChanges changes = FileDiffCalculator.defineDescriptionChanges(oldDescription, newDescription);

        // then
        assertThat(changes).isNotNull();
        assertThat(changes.isEmpty()).isTrue();
    }

    @Test
    public void shouldNotDefineDescriptionChangesIfBothAbsent()
    {
        // given
        FileDescription oldDescription = null;
        FileDescription newDescription = null;

        // expect
        assertThatThrownBy(() -> FileDiffCalculator.defineDescriptionChanges(oldDescription, newDescription))
            .isExactlyInstanceOf(IllegalStateException.class);
    }

    private static FileContent sampleFileContent(List<String> content)
    {
        return FileContent.of(content);
    }

    private static List<String> generateContent(List<String> values)
    {
        return values;
    }

}
