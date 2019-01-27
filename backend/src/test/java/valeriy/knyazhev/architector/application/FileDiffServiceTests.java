package valeriy.knyazhev.architector.application;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.FileDiffService.ChangedItem;
import valeriy.knyazhev.architector.domain.model.project.ProjectDescription;
import valeriy.knyazhev.architector.domain.model.project.ProjectMetadata;
import valeriy.knyazhev.architector.domain.model.project.file.File;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static valeriy.knyazhev.architector.application.FileDiffService.ChangedItem.ChangeType.ADDITION;
import static valeriy.knyazhev.architector.application.FileDiffService.ChangedItem.ChangeType.DELETION;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileDiffService.class)
public class FileDiffServiceTests {

    @Autowired
    private FileDiffService fileDiffService;

    private static File sampleFile(List<String> content) {
        ProjectDescription description = ProjectDescription.of(emptyList(), "");
        ProjectMetadata metadata = ProjectMetadata.builder()
                .name("File name")
                .timestamp(LocalDate.now())
                .authors(emptyList())
                .organizations(emptyList())
                .preprocessorVersion("")
                .originatingSystem("")
                .authorisation("")
                .build();
        return File.builder()
                .fileId(FileId.nextId())
                .description(description)
                .metadata(metadata)
                .content(FileContent.of(content))
                .build();
    }

    private static List<String> generateContent(List<String> values) {
        return values;
    }

    @Test
    public void shouldFilesAreEquals() {
        // given
        List<String> content = generateContent(asList("1", "2"));
        File oldFile = sampleFile(content);
        File newFile = sampleFile(content);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(0);
    }

    @Test
    public void shouldAddLineToEndOfFile() {
        // given
        List<String> oldContent = generateContent(singletonList("1"));
        List<String> newContent = generateContent(asList("1", "2"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem.position()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromEndOfFile() {
        // given
        List<String> oldContent = generateContent(asList("1", "2"));
        List<String> newContent = generateContent(Collections.singletonList("1"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem.position()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    public void shouldAddLineToTopOfFile() {
        // given
        List<String> oldContent = generateContent(singletonList("2"));
        List<String> newContent = generateContent(asList("1", "2"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("1");
        softly.assertThat(changedItem.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromTopOfFile() {
        // given
        List<String> oldContent = generateContent(asList("1", "2"));
        List<String> newContent = generateContent(Collections.singletonList("2"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("1");
        softly.assertThat(changedItem.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem.position()).isEqualTo(1);
        softly.assertAll();
    }

    @Test
    public void shouldAddLineToMiddleOfFile() {
        // given
        List<String> oldContent = generateContent(asList("1", "3"));
        List<String> newContent = generateContent(asList("1", "2", "3"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem.position()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    public void shouldRemoveLineFromMiddleOfFile() {
        // given
        List<String> oldContent = generateContent(asList("1", "2", "3"));
        List<String> newContent = generateContent(asList("1", "3"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(1);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem = diff.get(0);
        softly.assertThat(changedItem.value()).isEqualTo("2");
        softly.assertThat(changedItem.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem.position()).isEqualTo(2);
        softly.assertAll();
    }

    @Test
    public void shouldChangeLinesInFile() {
        // given
        List<String> oldContent = generateContent(asList("1", "2", "3", "5"));
        List<String> newContent = generateContent(asList("1", "3", "4"));
        File oldFile = sampleFile(oldContent);
        File newFile = sampleFile(newContent);

        // when
        List<ChangedItem> diff = this.fileDiffService.calculateDiff(oldFile, newFile);

        // then
        assertThat(diff).size().isEqualTo(3);
        SoftAssertions softly = new SoftAssertions();
        ChangedItem changedItem1 = diff.stream().filter(item -> item.value().equals("2")).findFirst().orElse(null);
        assertThat(changedItem1).isNotNull();
        softly.assertThat(changedItem1.value()).isEqualTo("2");
        softly.assertThat(changedItem1.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem1.position()).isEqualTo(2);
        ChangedItem changedItem2 = diff.stream().filter(item -> item.value().equals("4")).findFirst().orElse(null);
        assertThat(changedItem2).isNotNull();
        softly.assertThat(changedItem2.value()).isEqualTo("4");
        softly.assertThat(changedItem2.type()).isEqualTo(ADDITION);
        softly.assertThat(changedItem2.position()).isEqualTo(4);
        ChangedItem changedItem3 = diff.stream().filter(item -> item.value().equals("5")).findFirst().orElse(null);
        assertThat(changedItem3).isNotNull();
        softly.assertThat(changedItem3.value()).isEqualTo("5");
        softly.assertThat(changedItem3.type()).isEqualTo(DELETION);
        softly.assertThat(changedItem3.position()).isEqualTo(4);
        softly.assertAll();
    }

}
