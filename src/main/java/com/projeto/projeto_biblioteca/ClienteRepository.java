package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // M�todo para buscar cliente pelo nome exato
    Cliente findByNome(String nome);

    // M�todo para buscar cliente pelo CPF
    Cliente findByCpf(String cpf);

    // M�todo para buscar cliente pelo e-mail
    Cliente findByEmail(String email);

    // M�todo para buscar cliente pelo telefone
    Cliente findByTelefone(String telefone);

    // Novo m�todo para buscar clientes que contenham a string no nome, e-mail, telefone ou CPF,
    // ignorando mai�sculas/min�sculas, e ordenando de acordo com o par�metro 'Sort'
    List<Cliente> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrCpfContainingIgnoreCase(
            String nome, String email, String telefone, String cpf, Sort sort);
    
    // M�todo para buscar clientes que contenham a string no nome, ignorando mai�sculas/min�sculas,
    // e ordenando de acordo com o par�metro 'Sort' (j� existente)
    List<Cliente> findByNomeContainingIgnoreCase(String nome, Sort sort);
}
