package valeriy.knyazhev.architector.port.adapter.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.project.ProjectNotFoundException;
import valeriy.knyazhev.architector.application.project.file.FileNotFoundException;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Valeriy Knyazhev <valeriy.knyazhev@yandex.ru>
 */
@ControllerAdvice(annotations = RestController.class)
public class ErrorHandlingResource {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchProjectNotFoundException(ProjectNotFoundException ex) {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(responseMessage);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseMessage> catchFileNotFoundException(FileNotFoundException ex) {
        ResponseMessage responseMessage = new ResponseMessage()
            .error(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(responseMessage);
    }

}
