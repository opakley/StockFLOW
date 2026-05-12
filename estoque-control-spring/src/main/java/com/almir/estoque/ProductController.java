package com.almir.estoque;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    @Autowired
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    // LISTA PAGINADA + BUSCA + ORDEM (SEM CATEGORIA)
    @GetMapping("")
    public String list(@RequestParam(value = "search", required = false) String search,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                       @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                       Model model) {

        int pageSize = 5;

        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Product> pageProducts;
        if (search != null && !search.isBlank()) {
            pageProducts = repository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            pageProducts = repository.findAll(pageable);
        }

        model.addAttribute("products", pageProducts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageProducts.getTotalPages());

        model.addAttribute("search", search);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "products";
    }

    // FORM NOVO PRODUTO
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    // SALVAR (CRIAR / EDITAR)
    @PostMapping("")
    public String save(@Valid @ModelAttribute("product") Product product,
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product-form";
        }

        boolean novo = (product.getId() == null);

        repository.save(product);

        if (novo) {
            redirectAttributes.addFlashAttribute("message", "Produto cadastrado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Produto atualizado com sucesso!");
        }
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/products";
    }

    // EDITAR
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

        Product product = repository.findById(id).orElse(null);

        if (product == null) {
            redirectAttributes.addFlashAttribute("message", "Produto não encontrado.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/products";
        }

        model.addAttribute("product", product);
        return "product-form";
    }

    // EXCLUIR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        if (repository.existsById(id)) {
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Produto excluído com sucesso!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } else {
            redirectAttributes.addFlashAttribute("message", "Produto não encontrado.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/products";
    }
}
