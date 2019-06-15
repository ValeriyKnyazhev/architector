package valeriy.knyazhev.architector.domain.model.commit;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Valeriy Knyazhev
 */
public class FileMetadataChangesTests
{

    @Test
    public void shouldCreateEmptyChanges()
    {
        // when
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // then
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.name()).isNull();
        softly.assertThat(changes.authors()).isNull();
        softly.assertThat(changes.organizations()).isNull();
        softly.assertThat(changes.timestamp()).isNull();
        softly.assertThat(changes.preprocessorVersion()).isNull();
        softly.assertThat(changes.originatingSystem()).isNull();
        softly.assertThat(changes.authorization()).isNull();
        softly.assertAll();
    }

    @Test
    public void shouldCheckChanges()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // expect
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(changes.name()).isNotNull();
        softly.assertThat(changes.authors()).isNotNull();
        softly.assertThat(changes.organizations()).isNotNull();
        softly.assertThat(changes.timestamp()).isNotNull();
        softly.assertThat(changes.preprocessorVersion()).isNotNull();
        softly.assertThat(changes.originatingSystem()).isNotNull();
        softly.assertThat(changes.authorization()).isNotNull();
        softly.assertAll();
    }

    @Test
    public void shouldReturnNewName()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        String result = changes.newName("old");

        // then
        assertThat(result).isEqualTo("new");
    }

    @Test
    public void shouldReturnOldName()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        String result = changes.newName("old");

        // then
        assertThat(result).isEqualTo("old");
    }

    @Test
    public void shouldReturnNewAuthors()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        List<String> result = changes.newAuthors(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("new");
    }

    @Test
    public void shouldReturnOldAuthors()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        List<String> result = changes.newAuthors(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("old");
    }

    @Test
    public void shouldReturnNewOrganizations()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        List<String> result = changes.newOrganizations(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("new");
    }

    @Test
    public void shouldReturnOldOrganizations()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        List<String> result = changes.newOrganizations(List.of("old"));

        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsOnly("old");
    }

    @Test
    public void shouldReturnNewTimestamp()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        LocalDateTime result = changes.newTimestamp(LocalDateTime.MIN);

        // then
        assertThat(result).isEqualTo(LocalDateTime.MAX);
    }

    @Test
    public void shouldReturnOldTimestamp()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        LocalDateTime result = changes.newTimestamp(LocalDateTime.MIN);

        // then
        assertThat(result).isEqualTo(LocalDateTime.MIN);
    }

    @Test
    public void shouldReturnNewPreprocessorVersion()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        String result = changes.newPreprocessorVersion("old");

        // then
        assertThat(result).isEqualTo("new");
    }

    @Test
    public void shouldReturnOldPreprocessorVersion()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        String result = changes.newPreprocessorVersion("old");

        // then
        assertThat(result).isEqualTo("old");
    }

    @Test
    public void shouldReturnNewOriginatingSystem()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        String result = changes.newOriginatingSystem("old");

        // then
        assertThat(result).isEqualTo("new");
    }

    @Test
    public void shouldReturnOldOriginatingSystem()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        String result = changes.newOriginatingSystem("old");

        // then
        assertThat(result).isEqualTo("old");
    }

    @Test
    public void shouldReturnNewAuthorization()
    {
        // given
        FileMetadataChanges changes = sampleChanges();

        // when
        String result = changes.newAuthorization("old");

        // then
        assertThat(result).isEqualTo("new");
    }

    @Test
    public void shouldReturnOldAuthorization()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        String result = changes.newAuthorization("old");

        // then
        assertThat(result).isEqualTo("old");
    }

    @Test
    public void shouldEmptyIfNotChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.empty();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotEmptyIfNameChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().name("").build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfAuthorsChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().authors(List.of("")).build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfOrganizationsChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().organizations(List.of("")).build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfTimestampChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().timestamp(LocalDateTime.now()).build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfPreprocessorVersionChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().preprocessorVersion("").build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfOriginatingSystemChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().originatingSystem("").build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldNotEmptyIfAuthorizationChanged()
    {
        // given
        FileMetadataChanges changes = FileMetadataChanges.builder().authorization("").build();

        // when
        boolean result = changes.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    private static FileMetadataChanges sampleChanges()
    {
        return FileMetadataChanges.builder()
            .name("new")
            .authors(List.of("new"))
            .organizations(List.of("new"))
            .timestamp(LocalDateTime.MAX)
            .preprocessorVersion("new")
            .originatingSystem("new")
            .authorization("new")
            .build();
    }

}