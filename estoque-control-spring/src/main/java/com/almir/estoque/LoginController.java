package com.almir.estoque;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {

        // SE NÃO EXISTE NENHUM USUÁRIO, REDIRECIONA PARA CRIAR ADMIN
        if (userRepository.count() == 0) {
            return "redirect:/setup-admin";
        }

        // SE JÁ EXISTE USUÁRIO, MOSTRA LOGIN NORMAL
        return "login";
    }
}
