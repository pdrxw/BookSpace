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
        return "login"; // PÃ¡gina de login
    }

    @PostMapping("/login")
    public String autenticarUsuario(
            @RequestParam String usuario,
            @RequestParam String senha,
            HttpSession session,
            Model model) {

        if (authService.autenticar(usuario, senha, session)) {
            return "redirect:/"; // Redireciona para a pÃ¡gina inicial
        }

        model.addAttribute("error", "UsuÃ¡rio ou senha invÃ¡lidos.");
        return "login"; // Retorna Ã  pÃ¡gina de login com erro
    }

    @GetMapping("/")
    public String paginaInicial(HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se nÃ£o estiver logado
        }
        return "index"; // PÃ¡gina inicial para usuÃ¡rios autenticados
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout(session); // Utiliza o serviÃ§o de logout
        return "redirect:/login"; // Redireciona para login
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se nÃ£o estiver logado
        }

        model.addAttribute("usuarios", userService.listarUsuarios());
        return "usuario"; // Nome do arquivo HTML (usuarios.html)
    }

    @GetMapping("/cadastrar")
    public String getUserAdd(Model model, HttpSession session) {
        return "cadastro"; // Nome do arquivo HTML (cadastro.html)
    }
}
