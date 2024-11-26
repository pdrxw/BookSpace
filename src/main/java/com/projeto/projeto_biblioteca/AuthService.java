package com.projeto.projeto_biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean autenticar(String usuario, String senha, HttpSession session) {
        Usuario usuarioExistente = usuarioRepository.findByUsuario(usuario);
        if (usuarioExistente != null && usuarioExistente.getSenha().equals(senha)) {
            session.setAttribute("usuarioLogado", usuarioExistente);
            return true;
        }
        return false;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}