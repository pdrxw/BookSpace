package com.projeto.projeto_biblioteca;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsuario(String usuario);

    Usuario findByEmail(String email); // Adicionando a busca pelo e-mail
}