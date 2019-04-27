package valeriy.knyazhev.architector.domain.model.project.commit.projection;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.http.util.Args;
import valeriy.knyazhev.architector.domain.model.project.file.FileId;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDataProjection {

    @Nonnull
    private List<FileDataProjection> files;

    private ProjectDataProjection(@Nonnull List<FileDataProjection> files) {
        this.files = Args.notNull(files, "Project files are required.");
    }

    public static ProjectDataProjection empty() {
        return new ProjectDataProjection(ImmutableList.of());
    }

    @Nonnull
    public static ProjectDataProjection of(@Nonnull List<FileDataProjection> files) {
        return new ProjectDataProjection(files);
    }

    public boolean addNewFile(@Nonnull FileDataProjection fileProjection) {
        Args.notNull(fileProjection, "File projection is required.");
        if (this.files.stream().anyMatch(file -> fileProjection.fileId.equals(file.fileId))) {
            return false;
        } else {
            this.files = Stream.concat(this.files.stream(), Stream.of(fileProjection)).collect(Collectors.toList());
            return true;
        }
    }

    @Nonnull
    public List<FileDataProjection> files() {
        return this.files;
    }

    public static class FileDataProjection {

        @Nonnull
        private FileId fileId;

        @Nonnull
        private String name;

        @Nonnull
        private List<String> items;

        private FileDataProjection(@Nonnull FileId fileId,
                                   @Nonnull String name,
                                   @Nonnull List<String> items) {
            this.fileId = Args.notNull(fileId, "File identifier is required.");
            this.name = Args.notNull(name, "File name is required.");
            this.items = Args.notNull(items, "File items are required.");
        }

        @Nonnull
        public static FileDataProjection of(@Nonnull FileId fileId,
                                            @Nonnull String name,
                                            @Nonnull List<String> items) {
            return new FileDataProjection(fileId, name, items);
        }

        @Nonnull
        public FileId fileId() {
            return this.fileId;
        }

        @Nonnull
        public String name() {
            return this.name;
        }

        @Nonnull
        public List<String> items() {
            return Collections.unmodifiableList(this.items);
        }

        public void updateItems(@Nonnull List<String> items) {
            this.items = Args.notNull(items, "File projection items are required.");
        }

    }

}
