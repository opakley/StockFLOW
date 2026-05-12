package com.almir.estoque;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Quando acessar "/", redireciona para /home
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    // Dashboard
    @GetMapping("/home")
    public String home(Model model) {

        // Busca todos os produtos
        List<Product> products = productRepository.findAll();

        // Total de produtos cadastrados
        long totalProducts = products.size();

        // Quantidade total em estoque
        int totalQuantity = products.stream()
                .map(p -> p.getQuantity() != null ? p.getQuantity() : 0)
                .mapToInt(Integer::intValue)
                .sum();

        // Valor total em estoque (preço x quantidade)
        double totalValue = products.stream()
                .mapToDouble(p -> {
                    int q = p.getQuantity() != null ? p.getQuantity() : 0;
                    double price = p.getPrice() != null ? p.getPrice() : 0.0;
                    return q * price;
                })
                .sum();

        // Dados para os gráficos
        List<String> names = products.stream()
                .map(Product::getName)
                .collect(Collectors.toList());

        List<Integer> quantities = products.stream()
                .map(p -> p.getQuantity() != null ? p.getQuantity() : 0)
                .collect(Collectors.toList());

        List<Double> values = products.stream()
                .map(p -> {
                    int q = p.getQuantity() != null ? p.getQuantity() : 0;
                    double price = p.getPrice() != null ? p.getPrice() : 0.0;
                    return q * price;
                })
                .collect(Collectors.toList());

        // Envia para a tela
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("names", names);
        model.addAttribute("quantities", quantities);
        model.addAttribute("values", values);

        return "home";
    }
}
