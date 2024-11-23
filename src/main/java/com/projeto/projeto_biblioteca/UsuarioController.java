package com.projeto.projeto_biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cadastro")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;  // Verifique se o nome do pacote está correto

    @PostMapping
    public String cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String usuario,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha, // Campo para confirmar a senha
            Model model) {

        // Verificar se o nome de usuário ou email já existem no banco
        if (usuarioRepository.findByUsuario(usuario) != null) {
            model.addAttribute("error", "Usuário já cadastrado.");
            return "cadastro"; // Retorna à página de cadastro com erro
        }

        if (usuarioRepository.findByEmail(email) != null) {
            model.addAttribute("error", "E-mail já cadastrado.");
            return "cadastro"; // Retorna à página de cadastro com erro
        }

        // Validar formato do e-mail
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            model.addAttribute("error", "O e-mail informado não é válido.");
            return "cadastro"; // Retorna à página de cadastro com erro
        }

        // Verificar se as senhas coincidem
        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("error", "As senhas não coincidem.");
            return "cadastro"; // Retorna à página de cadastro com erro
        }

        // Criar o novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setUsuario(usuario);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);

        try {
            // Salvar o novo usuário no banco
            usuarioRepository.save(novoUsuario);
        } catch (DataIntegrityViolationException e) {
            // Se houver erro de integridade (como violação da unicidade), mostra a mensagem
            model.addAttribute("error", "O nome de usuário ou e-mail já está em uso.");
            return "cadastro"; // Retorna à página de cadastro com erro
        }

        return "redirect:/login"; // Redireciona para a página de login após o cadastro
    }
}
