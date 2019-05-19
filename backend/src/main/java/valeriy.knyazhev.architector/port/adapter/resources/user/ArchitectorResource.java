package valeriy.knyazhev.architector.port.adapter.resources.user;

import org.apache.http.util.Args;
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.application.user.ArchitectorApplicationService;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ArchitectorResource
{

    private final ArchitectorApplicationService applicationService;

    public ArchitectorResource(@Nonnull ArchitectorApplicationService applicationService)
    {
        this.applicationService = Args.notNull(applicationService, "Application service is required.");
    }

    @PostMapping("/api/signup")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterArchitectorRequest request)
    {
        Architector newArchitector = new Architector();
        newArchitector.setEmail(request.email());
        newArchitector.setPassword(request.password());
        this.applicationService.register(newArchitector);
        return ResponseEntity.ok().body(
            new ResponseMessage()
                .info("Architector with email " + request.email() + " successfully created.")
        );
    }

    @GetMapping(value = "/api/architectors", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> findArchitectors(@RequestParam(defaultValue = "") String query,
                                                   @Nonnull Architector architector)
    {
        List<String> users = this.applicationService.findArchitectors(query).stream()
            .map(Architector::email)
            .filter(user -> !architector.email().equals(user))
            .collect(Collectors.toList());
        return ResponseEntity.ok(singletonMap("architectors", users));
    }

    @GetMapping("/api/me")
    public ResponseEntity<Object> userInfo(@Nonnull Architector architector)
    {
        return ResponseEntity.ok().body(
            new ArchitectorModel(architector.email())
        );
    }



}