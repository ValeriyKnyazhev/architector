package valeriy.knyazhev.architector.application.commit.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

/**
 * @author Valeriy Knyazhev
 */
@JsonAutoDetect(fieldVisibility = ANY)
public class CommitDescriptionData
{

    private long id;

    @Nullable
    private Long parentId;

    @Nonnull
    private String author;

    @Nonnull
    private String message;

    @Nonnull
    private LocalDateTime timestamp;

    public CommitDescriptionData(long id, @Nullable Long parentId, @Nonnull String author,
                                 @Nonnull String message, @Nonnull LocalDateTime timestamp)
    {
        this.id = id;
        this.parentId = parentId;
        this.author = author;
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
    }

}