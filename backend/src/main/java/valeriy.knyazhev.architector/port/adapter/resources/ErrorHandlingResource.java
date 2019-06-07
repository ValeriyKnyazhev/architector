package valeriy.knyazhev.architector.port.adapter.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.commit.CommitNotFoundException;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.application.security.InvalidTokenException;
import valeriy.knyazhev.architector.domain.model.AccessRightsNotFoundException;
import valeriy.knyazhev.architector.domain.model.commit.NothingToCommitException;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@ControllerAdvice(annotations = RestController.class)
public class ErrorHandlingResource
{

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchProjectNotFoundException(ProjectNotFoundException ex)
    {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(responseMessage);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchFileNotFoundException(FileNotFoundException ex)
    {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(responseMessage);
    }

    @ExceptionHandler(CommitNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchCommitNotFoundException(CommitNotFoundException ex)
    {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(responseMessage);
    }

    @ExceptionHandler(NothingToCommitException.class)
    public ResponseEntity<ResponseMessage> catchNothingToCommitException(NothingToCommitException ex)
    {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(responseMessage);
    }

    @ExceptionHandler(AccessRightsNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchAccessRightsNotFoundException(AccessRightsNotFoundException ex)
    {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(responseMessage);
    }

}
