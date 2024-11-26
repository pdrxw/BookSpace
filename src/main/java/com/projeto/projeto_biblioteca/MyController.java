package com.projeto.projeto_biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MyController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticarUsuario(
            @RequestParam String usuario,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        if (authService.autenticar(usuario, senha, session)) {
            return "redirect:/";
        }

        model.addAttribute("error", "Usuário ou senha inválidos.");
        return "login";
    }

    @GetMapping("/")
    public String paginaInicial(HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session);
        return "redirect:/login";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarios", userService.listarUsuarios());
        return "usuario";
    }

    @GetMapping("/cadastrar")
    public String getUserAdd(Model model, HttpSession session) {
        return "cadastro";
    }
}
