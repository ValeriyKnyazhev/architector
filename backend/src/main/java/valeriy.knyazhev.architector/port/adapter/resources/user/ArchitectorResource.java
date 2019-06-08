package valeriy.knyazhev.architector.port.adapter.resources.user;

import org.apache.http.util.Args;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import valeriy.knyazhev.architector.application.user.ArchitectorApplicationService;
import valeriy.knyazhev.architector.application.security.JwtTokenProvider;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.Role;
import valeriy.knyazhev.architector.port.adapter.util.ResponseMessage;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Valeriy Knyazhev
 */
@RestController
public class ArchitectorResource
{

    private final AuthenticationManager authenticationManager;

    private final ArchitectorApplicationService applicationService;

    private final JwtTokenProvider jwtTokenProvider;

    public ArchitectorResource(@Nonnull AuthenticationManager authenticationManager,
                               @Nonnull ArchitectorApplicationService applicationService,
                               @Nonnull JwtTokenProvider jwtTokenProvider)
    {
        this.authenticationManager = Args.notNull(authenticationManager, "Authentication manager is required.");
        this.applicationService = Args.notNull(applicationService, "Application service is required.");
        this.jwtTokenProvider = Args.notNull(jwtTokenProvider, "Jwt token provider is required.");
    }

    @PostMapping("/api/token")
    public ResponseEntity login(@RequestBody AuthenticationRequest data)
    {
        try
        {
            String email = data.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, data.getPassword()));
            List<String> authorities = this.applicationService.findByEmail(email)
                .roles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
            String token = jwtTokenProvider.createToken(email, authorities);
            Map<Object, Object> model = new HashMap<>();
            model.put("email", email);
            model.put("token", token);
            return ResponseEntity.ok(model);
        } catch (AuthenticationException e)
        {
            throw new BadCredentialsException("Invalid email/password supplied");
        }
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
        return ResponseEntity.ok(Map.of("architectors", users));
    }

    @GetMapping("/api/me")
    public ResponseEntity<Object> userInfo(@Nonnull Architector architector)
    {
        return ResponseEntity.ok().body(
            new ArchitectorModel(architector.email())
        );
    }


}