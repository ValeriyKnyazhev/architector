package valeriy.knyazhev.architector.domain.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;
import valeriy.knyazhev.architector.domain.model.commit.FileDiffCalculator;
import valeriy.knyazhev.architector.domain.model.project.file.FileContent;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
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
        FileContent oldFile = sampleFile(content);
        FileContent newFile = sampleFile(content);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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
        FileContent oldFile = sampleFile(oldContent);
        FileContent newFile = sampleFile(newContent);

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

    private static FileContent sampleFile(List<String> content)
    {
        return FileContent.of(content);
    }

    private static List<String> generateContent(List<String> values)
    {
        return values;
    }

}
