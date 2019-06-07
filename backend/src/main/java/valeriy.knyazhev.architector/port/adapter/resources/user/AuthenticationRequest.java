package valeriy.knyazhev.architector.port.adapter.resources.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Valeriy Knyazhev
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements Serializable
{

    private String email;

    private String password;

}