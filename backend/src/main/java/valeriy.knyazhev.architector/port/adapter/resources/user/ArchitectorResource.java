package valeriy.knyazhev.architector.port.adapter.resources.user;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.user.ArchitectorApplicationService;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import javax.validation.Valid;

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

    @GetMapping("/api/me")
    public ResponseEntity<Object> userInfo(@Nonnull Architector architector)
    {
        return ResponseEntity.ok().body(
            new ArchitectorModel(architector.email())
        );
    }

}