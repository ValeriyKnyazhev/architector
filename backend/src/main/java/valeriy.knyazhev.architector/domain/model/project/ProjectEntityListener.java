package valeriy.knyazhev.architector.domain.model.project;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
public class ProjectEntityListener {

    @PrePersist
    public void setCreatedAndUpdatedDate(Project project) {
        project.setCreatedDate(LocalDateTime.now());
        project.setUpdatedDate(LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdatedDate(Project project) {
        project.setUpdatedDate(LocalDateTime.now());
    }

}
