package com.kleyson.solicitacao.security;

import com.kleyson.solicitacao.usuario.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // chave secreta para assinar o token
    private final String SECRET_KEY =
            "minha-chave-secreta-super-segura-com-mais-de-32-caracteres";

    public String gerarToken(Usuario usuario) {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes()
        );

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + 1000 * 60 * 60
                )) // 1 hora
                .signWith(key)
                .compact();
    }
}