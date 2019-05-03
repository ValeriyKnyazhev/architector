package valeriy.knyazhev.architector.domain.model.commit.projection;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.file.FileDescription;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;
import valeriy.knyazhev.architector.domain.model.project.file.FileMetadata;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Projection
{

    @Nonnull
    private String name;

    @Nonnull
    private String description;

    @Nonnull
    private List<FileProjection> files;

    private Projection(@Nonnull String name,
                       @Nonnull String description,
                       @Nonnull List<FileProjection> files)
    {
        this.name = Args.notBlank(name, "Project name is required.");
        this.description = Args.notNull(description, "Project description is required.");
        this.files = Args.notNull(files, "Project files are required.");
    }

    public boolean addNewFile(@Nonnull FileProjection fileProjection)
    {
        Args.notNull(fileProjection, "File projection is required.");
        if (this.files.stream().anyMatch(file -> fileProjection.fileId.equals(file.fileId)))
        {
            return false;
        } else
        {
            this.files = Stream.concat(this.files.stream(), Stream.of(fileProjection)).collect(Collectors.toList());
            return true;
        }
    }

    @Nonnull
    public String name()
    {
        return this.name;
    }

    @Nonnull
    public String description()
    {
        return this.description;
    }

    @Nonnull
    public List<FileProjection> files()
    {
        return this.files;
    }

    public static Projection empty()
    {
        // FIXME initial projection for combinator
        return new Projection("INIT", "", ImmutableList.of());
    }

    @Nonnull
    public static Projection of(@Nonnull String name,
                                @Nonnull String description,
                                @Nonnull List<FileProjection> files)
    {
        return new Projection(name, description, files);
    }

    public static class FileProjection
    {

        @Nonnull
        private FileId fileId;

        @Nonnull
        private FileMetadata metadata;

        @Nonnull
        private FileDescription description;

        @Nonnull
        private List<String> items;

        private FileProjection(@Nonnull FileId fileId,
                               @Nonnull FileMetadata metadata,
                               @Nonnull FileDescription description,
                               @Nonnull List<String> items)
        {
            this.fileId = Args.notNull(fileId, "File identifier is required.");
            this.metadata = Args.notNull(metadata, "File metadata is required.");
            this.description = Args.notNull(description, "File description is required.");
            this.items = Args.notNull(items, "File items are required.");
        }

        @Nonnull
        public FileId fileId()
        {
            return this.fileId;
        }

        @Nonnull
        public FileMetadata metadata()
        {
            return this.metadata;
        }

        @Nonnull
        public FileDescription description()
        {
            return this.description;
        }

        @Nonnull
        public List<String> items()
        {
            return Collections.unmodifiableList(this.items);
        }

        public void update(@Nonnull FileMetadata metadata,
                           @Nonnull FileDescription description,
                           @Nonnull List<String> items)
        {
            this.metadata = Args.notNull(metadata, "File metadata is required.");
            this.description = Args.notNull(description, "File description is required.");
            this.items = Args.notNull(items, "File projection items are required.");
        }

        @Nonnull
        public static FileProjection of(@Nonnull FileId fileId,
                                        @Nonnull FileMetadata metadata,
                                        @Nonnull FileDescription description,
                                        @Nonnull List<String> items)
        {
            return new FileProjection(fileId, metadata, description, items);
        }

    }

}
