package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Método para buscar cliente pelo nome exato
    Cliente findByNome(String nome);

    // Método para buscar cliente pelo CPF
    Cliente findByCpf(String cpf);

    // Método para buscar cliente pelo e-mail
    Cliente findByEmail(String email);

    // Método para buscar cliente pelo telefone
    Cliente findByTelefone(String telefone);

    // Novo método para buscar clientes que contenham a string no nome, e-mail, telefone ou CPF,
    // ignorando maiúsculas/minúsculas, e ordenando de acordo com o parâmetro 'Sort'
    List<Cliente> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrCpfContainingIgnoreCase(
            String nome, String email, String telefone, String cpf, Sort sort);
    
    // Método para buscar clientes que contenham a string no nome, ignorando maiúsculas/minúsculas,
    // e ordenando de acordo com o parâmetro 'Sort' (já existente)
    List<Cliente> findByNomeContainingIgnoreCase(String nome, Sort sort);
}
