package valeriy.knyazhev.architector.application.user;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author Valeriy Knyazhev
 */
public class JwtTokenProvider
{

    //    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

//    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000; // 1h

    private UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService)
    {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init()
    {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, List<String> roles)
    {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
            .setClaims(claims)//
            .setIssuedAt(now)//
            .setExpiration(validity)//
            .signWith(SignatureAlgorithm.HS256, secretKey)//
            .compact();
    }

    public Authentication getAuthentication(String token)
    {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token)
    {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req)
    {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token)
    {
        try
        {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e)
        {
            throw new IllegalStateException("Expired or invalid JWT token");
        }
    }
}