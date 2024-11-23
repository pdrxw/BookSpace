package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Busca empr�stimos pelo nome do cliente ou t�tulo do livro (ignorando mai�sculas/min�sculas)
    List<Emprestimo> findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca empr�stimos pelo ID do cliente com ordena��o
    List<Emprestimo> findByClienteId(Long clienteId, Sort sort);

    // Busca empr�stimos pelo ID do livro com ordena��o
    List<Emprestimo> findByLivroId(Long livroId, Sort sort);

    // Busca empr�stimos pelo status (dispon�vel, emprestado, etc.) com ordena��o
    List<Emprestimo> findByStatus(String status, Sort sort);

    // M�todo para busca customizada, por m�ltiplos campos, conforme necess�rio
    List<Emprestimo> findByClienteNomeContainingIgnoreCaseAndLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca empr�stimos ativos de um livro espec�fico (livro n�o devolvido)
    List<Emprestimo> findByLivro_IdAndStatusNot(Long livroId, String status, Sort sort);
}
