package com.almir.estoque;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SetupAdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupAdminController(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // TELA PARA CRIAR O PRIMEIRO ADMIN
    @GetMapping("/setup-admin")
    public String showSetupForm(Model model) {

        // SE JÁ EXISTE ALGUM USUÁRIO, NÃO FAZ MAIS SENTIDO USAR ESSA TELA
        if (userRepository.count() > 0) {
            return "redirect:/login";
        }

        model.addAttribute("user", new User());
        return "setup-admin";
    }

    // PROCESSA O FORM DO PRIMEIRO ADMIN
    @PostMapping("/setup-admin")
    public String processSetup(@ModelAttribute("user") User user,
                               RedirectAttributes redirectAttributes) {

        // SE JÁ EXISTE USUÁRIO, IGNORA ESSA AÇÃO
        if (userRepository.count() > 0) {
            return "redirect:/login";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message",
                "Usuário administrador criado com sucesso. Faça login.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/login";
    }
}
