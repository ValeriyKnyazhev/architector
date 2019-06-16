package valeriy.knyazhev.architector.port.adapter.resources.project.file.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.commit.CommitNotFoundException;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.project.file.validation.ChangedEntity;
import valeriy.knyazhev.architector.application.project.file.validation.InvalidFileContentException;
import valeriy.knyazhev.architector.application.user.ArchitectorAlreadyExistException;
import valeriy.knyazhev.architector.application.user.ArchitectorNotFoundException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.NothingToCommitException;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@ControllerAdvice(annotations = RestController.class)
public class ValidationErrorHandlingResource
{

    @ExceptionHandler(InvalidFileContentException.class)
    public ResponseEntity<Object> catchInvalidFileContentException(InvalidFileContentException ex)
    {
        Map<String, List<ChangedEntity>> result = Map.of("invalidEntities", ex.entities());
        return ResponseEntity.ok().body(result);
    }

}
