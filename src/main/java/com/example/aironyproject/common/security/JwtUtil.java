package com.example.aironyproject.common.security;

import com.example.aironyproject.common.exception.CustomException;
import com.example.aironyproject.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private SecretKey secretKey;

    // 빈 생성 후 딱 한 번만 암호화 키 객체를 생성하여 캐싱
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성 (이메일과 권한을 담음)
    public String createToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 2. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    // 3. 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 4. 토큰에서 권한 추출
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 공통 파싱 로직
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}