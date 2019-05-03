package valeriy.knyazhev.architector.domain.model.project.file;

import javax.annotation.Nonnull;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class FileEntityListener
{

    @PrePersist
    public void setCreatedAndUpdatedDate(@Nonnull File file)
    {
        file.setCreatedDate(LocalDateTime.now());
        file.setUpdatedDate(LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdatedDate(@Nonnull File file)
    {
        file.setUpdatedDate(LocalDateTime.now());
    }

}
