package com.almir.estoque;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @PostMapping
    public String save(@ModelAttribute("user") User user,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "user-form";
        }

        boolean novo = (user.getId() == null);

        if (user.getId() != null) {
            // edição: mantém senha antiga se campo vier vazio
            User existing = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + user.getId()));

            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(existing.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            // novo usuário: sempre codifica senha
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message",
                novo ? "Usuário criado com sucesso!" : "Usuário atualizado com sucesso!");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));

        // não mostramos a senha atual no formulário
        user.setPassword("");
        model.addAttribute("user", user);
        return "user-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Usuário excluído com sucesso!");
        redirectAttributes.addFlashAttribute("messageType", "danger");

        return "redirect:/users";
    }
}
