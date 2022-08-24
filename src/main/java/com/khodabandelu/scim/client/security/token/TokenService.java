package com.khodabandelu.scim.client.security.token;


import com.khodabandelu.scim.client.domains.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class TokenService {
    Logger logger = Logger.getLogger(TokenService.class.getName());

    @Value("${scim.jwt.secret}")
    private String secret;

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("organization", user.getOrganizationId())
                .claim("roles", roles)
                .signWith(key)
                .compact();
    }

    public UsernamePasswordAuthenticationToken validateTokenAndRetrieve(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Set<GrantedAuthority> authorities =
                    Arrays.stream(Optional.ofNullable(claims.get("roles")).orElse("").toString().split(","))
                            .filter(role -> !role.isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());

            var user = User.builder()
                    .email(claims.getSubject())
                    .organizationId(Optional.ofNullable(claims.get("organization")).orElse("").toString())
                    .password("")
                    .build();
            return new UsernamePasswordAuthenticationToken(user, token, authorities);
        } catch (JwtException | IllegalArgumentException e) {
            logger.log(Level.WARNING, "Validate token is failed");
            return null;
        }
    }

}
