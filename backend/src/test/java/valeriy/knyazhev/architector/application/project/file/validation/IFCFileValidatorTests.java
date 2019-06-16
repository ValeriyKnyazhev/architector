package valeriy.knyazhev.architector.application.project.file.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import valeriy.knyazhev.architector.application.project.file.validation.IFCFileValidator.ValidationType;
import valeriy.knyazhev.architector.domain.model.commit.CommitItem;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IFCFileValidator.class)
public class IFCFileValidatorTests
{

    private List<String> content;

    @Autowired
    private IFCFileValidator validator;

    @PostConstruct
    public void setup()
        throws IOException
    {
        Resource resource = new ClassPathResource("test.ifc");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        List<String> readContent = new ArrayList<>();
        String line = reader.readLine();
        while (line != null)
        {
            readContent.add(line);
            line = reader.readLine();
        }
        int startIndex = readContent.indexOf("DATA;") + 1;
        this.content = readContent.subList(startIndex, readContent.size() - 2);
    }

    @Test
    public void shouldNotValidateIFC4()
    {
        // given
        ValidationType validationType = ValidationType.NONE;
        String schema = "IFC4";

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, List.of(), List.of()
        );

        // then
        assertThat(changedEntities).hasSize(0);
    }

    @Test
    public void shouldNotValidateIFC2X3()
    {
        // given
        ValidationType validationType = ValidationType.NONE;
        String schema = "IFC2X3";

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, List.of(), List.of()
        );

        // then
        assertThat(changedEntities).hasSize(0);
    }

    @Test
    public void shouldValidateIFC2X3WithoutChanges()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = sampleContent();

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, content, List.of()
        );

        // then
        assertThat(changedEntities).hasSize(0);
    }

    @Test
    public void shouldValidateIFC2X3WithNotAffectedChanges()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = sampleContent();
        String deletedValue = content.stream()
            .filter(item -> item.startsWith("#149"))
            .findFirst()
            .get();
        int index = content.indexOf(deletedValue) + 1;
        List<CommitItem> changes = List.of(CommitItem.deleteItem(deletedValue, index));
        content = extractElement(index, content);

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, content, changes
        );

        // then
        assertThat(changedEntities).hasSize(0);
    }

    @Test
    public void shouldValidateIFC2X3WithAffectedChanges()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = sampleContent();
        String deletedValue = content.stream()
            .filter(item -> item.startsWith("#127"))
            .findFirst()
            .get();
        int index = content.indexOf(deletedValue) + 1;
        String newValue = "#127 = IFCCARTESIANPOINT((0., 1.100E-1, 0.));";
        List<CommitItem> changes = List.of(
            CommitItem.deleteItem(deletedValue, index),
            CommitItem.addItem(newValue, index - 1)
        );
        content = extractElement(index, content);
        content.add(newValue);

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, content, changes
        );

        // then
        assertThat(changedEntities).hasSize(1);
        assertThat(changedEntities.get(0).id()).isEqualTo(124);
    }

    @Test
    public void shouldReturnInvalidEntitiesAfterDeletion()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = sampleContent();
        List<CommitItem> changes = List.of(CommitItem.deleteItem(content.get(0), 1));

        // expect
        assertThatThrownBy(
            () -> this.validator.validateContent(
                validationType, schema, content.subList(1, content.size()), changes
            )
        )
            .isExactlyInstanceOf(InvalidFileContentException.class);
    }

    @Test
    public void shouldNotDeserializeInvalidContent()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = List.of("#1=;");

        // expect
        assertThatThrownBy(
            () -> this.validator.validateContent(
                validationType, schema, content, List.of()
            )
        ).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldValidateEmptyIFC2X3()
    {
        // given
        ValidationType validationType = ValidationType.REFERENCES;
        String schema = "IFC2X3";
        List<String> content = List.of();

        // when
        List<ChangedEntity> changedEntities = this.validator.validateContent(
            validationType, schema, content, List.of()
        );

        // then
        assertThat(changedEntities).hasSize(0);
    }

    private List<String> sampleContent()
    {
        return List.copyOf(this.content);
    }

    private static List<String> extractElement(int index, List<String> content)
    {
        return Stream.concat(
            content.subList(0, index - 1).stream(),
            content.subList(index, content.size()).stream()
        )
            .collect(toList());
    }

}