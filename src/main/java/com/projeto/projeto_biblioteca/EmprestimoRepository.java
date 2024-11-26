package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Busca empréstimos pelo nome do cliente ou título do livro (ignorando maiúsculas/minúsculas)
    List<Emprestimo> findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca empréstimos pelo ID do cliente com ordenação
    List<Emprestimo> findByClienteId(Long clienteId, Sort sort);

    // Busca empréstimos pelo ID do livro com ordenação
    List<Emprestimo> findByLivroId(Long livroId, Sort sort);

    // Busca empréstimos pelo status (disponível, emprestado, etc.) com ordenação
    List<Emprestimo> findByStatus(String status, Sort sort);

    // Método para busca customizada, por múltiplos campos, conforme necessário
    List<Emprestimo> findByClienteNomeContainingIgnoreCaseAndLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca empréstimos ativos de um livro específico (livro não devolvido)
    List<Emprestimo> findByLivro_IdAndStatusNot(Long livroId, String status, Sort sort);
}
