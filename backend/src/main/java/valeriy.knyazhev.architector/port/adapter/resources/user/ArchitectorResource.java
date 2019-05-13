package valeriy.knyazhev.architector.port.adapter.resources.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import valeriy.knyazhev.architector.application.user.ArchitectorApplicationService;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.validation.Valid;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ArchitectorResource
{

    private ArchitectorApplicationService applicationService;

    @PostMapping("/arhitectors")
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

}