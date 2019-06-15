package valeriy.knyazhev.architector.domain.model.commit;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Valeriy Knyazhev
 */
public class FileDescriptionChangesTests
{

    @Test
    public void shouldCreateEmptyChanges()
    {
        // when
        FileDescriptionChanges changes = FileDescriptionChanges.empty();

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.descriptions()).isNull();
        softly.assertThat(changes.implementationLevel()).isNull();
        softly.assertAll();
    }

    @Test
    public void shouldCheckChanges()
    {
        // given
        FileDescriptionChanges changes = sampleChanges();

        // expect
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.descriptions()).isNotNull();
        softly.assertThat(changes.implementationLevel()).isNotNull();
        softly.assertAll();
    }

    @Test
    public void shouldReturnNewDescriptions()
    {
        // given
        FileDescriptionChanges changes = sampleChanges();

        // when
        List<String> result = changes.newDescriptions(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("new");
    }

    @Test
    public void shouldReturnOldDescriptions()
    {
        // given
        FileDescriptionChanges changes = FileDescriptionChanges.empty();

        // when
        List<String> result = changes.newDescriptions(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("old");
    }

    @Test
    public void shouldReturnNewImplementationLevel()
    {
        // given
        FileDescriptionChanges changes = sampleChanges();

        // when
        String result = changes.newImplementationLevel("old");

        // then
        assertThat(result).isEqualTo("new");
    }

    @Test
    public void shouldReturnOldImplementationLevel()
    {
        // given
        FileDescriptionChanges changes = FileDescriptionChanges.empty();

        // when
        String result = changes.newImplementationLevel("old");

        // then
        assertThat(result).isEqualTo("old");
    }

    @Test
    public void shouldEmptyIfNotChanged()
    {
        // given
        FileDescriptionChanges changes = FileDescriptionChanges.empty();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotEmptyIfOrganizationsChanged()
    {
        // given
        FileDescriptionChanges changes = FileDescriptionChanges.builder().descriptions(List.of("")).build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfImplementationLevelChanged()
    {
        // given
        FileDescriptionChanges changes = FileDescriptionChanges.builder().implementationLevel("").build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    private static FileDescriptionChanges sampleChanges()
    {
        return FileDescriptionChanges.builder()
            .descriptions(List.of("new"))
            .implementationLevel("new")
            .build();
    }

}