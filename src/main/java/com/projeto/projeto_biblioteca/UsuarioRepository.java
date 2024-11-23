package com.projeto.projeto_biblioteca;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // M�todo para buscar usu�rio pelo nome de usu�rio
    Usuario findByUsuario(String usuario);

    // M�todo para buscar usu�rio pelo e-mail
    Usuario findByEmail(String email); // Adicionando a busca pelo e-mail
}
