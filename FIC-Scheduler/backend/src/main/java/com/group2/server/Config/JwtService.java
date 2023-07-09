package com.group2.server.Config;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String extractUsername(String token) {
        return null;
        
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigninKey(getSigningKey())
        .build().parseClaimsJws(token)
        .getBody();
    }

}
