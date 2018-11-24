package valeriy.knyazhev.architector.port.adapter;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.IFCProjectReader;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ProjectResource {

    private final IFCProjectReader projectReader;

    public ProjectResource(@Nonnull IFCProjectReader projectReader) {
        this.projectReader = Args.notNull(projectReader, "Project reader is required.");
    }

    @PostMapping(value = "/projects",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> importDataFromUrl(@RequestBody ReadProjectCommand command) {
        Args.notNull(command, "Read project command is required.");
        try {
            URL projectUrl = new URL(command.projectUrl());
            this.projectReader.readProjectFromUrl(projectUrl);
            return ResponseEntity.ok().body(new ResponseMessage().info("Project is read."));
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage().error(e.getMessage()));
        }
    }

}
