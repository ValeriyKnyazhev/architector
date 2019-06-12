package valeriy.knyazhev.architector.port.adapter.resources.user;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import valeriy.knyazhev.architector.application.project.command.CreateProjectCommand;
import valeriy.knyazhev.architector.application.security.JwtTokenProvider;
import valeriy.knyazhev.architector.application.user.ArchitectorAlreadyExistException;
import valeriy.knyazhev.architector.application.user.ArchitectorApplicationService;
import valeriy.knyazhev.architector.application.user.ArchitectorNotFoundException;
import valeriy.knyazhev.architector.domain.model.project.ProjectId;
import valeriy.knyazhev.architector.domain.model.user.Architector;
import valeriy.knyazhev.architector.domain.model.user.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Valeriy Knyazhev
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ArchitectorResource.class)
public class ArchitectorResourceTests
{

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private ArchitectorApplicationService applicationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void shouldGrantApiToken()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        String command = "{" +
                         "\"email\":\"" + email + "\"," +
                         "\"password\":\"" + password + "\"" +
                         "}";
        String token = "token-1-2-3";
        when(this.applicationService.findByEmail(eq(email)))
            .thenReturn(createArchitector(email));
        when(this.jwtTokenProvider.createToken(eq(email), any()))
            .thenReturn(token);

        // expect
        this.mockMvc.perform(post("/api/token")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.token").value(token));
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

    @Test
    public void shouldNotGrantApiTokenIfNotAuthenticated()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        String command = "{" +
                         "\"email\":\"" + email + "\"," +
                         "\"password\":\"" + password + "\"" +
                         "}";
        when(this.authenticationManager.authenticate(any(Authentication.class)))
            .thenThrow(new AuthenticationServiceException("not authenticated"));

        // expect
        this.mockMvc.perform(post("/api/token")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotGrantApiTokenIfArchitectorNotFound()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        String command = "{" +
                         "\"email\":\"" + email + "\"," +
                         "\"password\":\"" + password + "\"" +
                         "}";
        when(this.applicationService.findByEmail(eq(email)))
            .thenThrow(new ArchitectorNotFoundException(email));

        // expect
        this.mockMvc.perform(post("/api/token")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldGetArchitectorsList()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        when(this.applicationService.findArchitectors(""))
            .thenReturn(List.of(createArchitector(email)));

        // expect
        this.mockMvc.perform(get("/api/architectors")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.architectors").isArray());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldGetEmptyArchitectorsList()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        when(this.applicationService.findArchitectors(""))
            .thenReturn(List.of(createArchitector(email)));

        // expect
        this.mockMvc.perform(get("/api/architectors")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.architectors").isEmpty());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldGetArchitectorsListByQuery()
        throws Exception
    {
        // given
        String email = "captain.america@architector.ru";
        String query = "captain";
        when(this.applicationService.findArchitectors(query))
            .thenReturn(List.of(createArchitector(email)));

        // expect
        this.mockMvc.perform(get("/api/architectors?query={query}", query)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.architectors").isArray())
            .andExpect(jsonPath("$.architectors").isNotEmpty());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldReturnArchitectorPersonalInfo()
        throws Exception
    {
        // expect
        this.mockMvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldCreateNewArchitector()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        String command = "{" +
                         "\"email\":\"" + email + "\"," +
                         "\"password\":\"" + password + "\"" +
                         "}";

        // expect
        this.mockMvc.perform(post("/api/signup")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.info").exists());
        verify(this.applicationService, times(1)).register(any(Architector.class));
    }

    @Test
    @WithMockUser("tony.stark@architector.ru")
    public void shouldNotCreateNewArchitectorIfAlreadyExist()
        throws Exception
    {
        // given
        String email = "tony.stark@architector.ru";
        String password = "pswd";
        String command = "{" +
                         "\"email\":\"" + email + "\"," +
                         "\"password\":\"" + password + "\"" +
                         "}";
        when(this.applicationService.register(any(Architector.class)))
            .thenThrow(new ArchitectorAlreadyExistException(email));

        // expect
        this.mockMvc.perform(post("/api/signup")
            .content(command)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    private static Architector createArchitector(String email)
    {
        Architector architector = new Architector();
        architector.setId(1L);
        architector.setEmail(email);
        architector.setPassword("pswd");
        architector.setRoles(Set.of(new Role("USER")));
        return architector;
    }

}