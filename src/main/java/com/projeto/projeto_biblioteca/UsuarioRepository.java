package com.projeto.projeto_biblioteca;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar usuário pelo nome de usuário
    Usuario findByUsuario(String usuario);

    // Método para buscar usuário pelo e-mail
    Usuario findByEmail(String email); // Adicionando a busca pelo e-mail
}
