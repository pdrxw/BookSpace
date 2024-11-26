package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoEmprestimoRepository extends JpaRepository<HistoricoEmprestimo, Long> {

    // Busca históricos de empréstimos pelo nome do cliente ou título do livro (ignorando maiúsculas/minúsculas)
    List<HistoricoEmprestimo> findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca históricos de empréstimos pelo ID do cliente com ordenação
    List<HistoricoEmprestimo> findByClienteId(Long clienteId, Sort sort);

    // Busca históricos de empréstimos pelo ID do livro com ordenação
    List<HistoricoEmprestimo> findByLivroId(Long livroId, Sort sort);

    // Busca históricos de emprestimos pelo status (concluído, ativo, etc.) com ordenacao
    List<HistoricoEmprestimo> findByStatus(String status, Sort sort);

    // Método para busca customizada, por múltiplos campos, conforme necessário
    List<HistoricoEmprestimo> findByClienteNomeContainingIgnoreCaseAndLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca históricos de empréstimos ativos de um livro específico (livro não devolvido)
    List<HistoricoEmprestimo> findByLivro_IdAndStatusNot(Long livroId, String status, Sort sort);
}
