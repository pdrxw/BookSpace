package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoEmprestimoRepository extends JpaRepository<HistoricoEmprestimo, Long> {

    // Busca hist�ricos de empr�stimos pelo nome do cliente ou t�tulo do livro (ignorando mai�sculas/min�sculas)
    List<HistoricoEmprestimo> findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca hist�ricos de empr�stimos pelo ID do cliente com ordena��o
    List<HistoricoEmprestimo> findByClienteId(Long clienteId, Sort sort);

    // Busca hist�ricos de empr�stimos pelo ID do livro com ordena��o
    List<HistoricoEmprestimo> findByLivroId(Long livroId, Sort sort);

    // Busca hist�ricos de empr�stimos pelo status (conclu�do, ativo, etc.) com ordena��o
    List<HistoricoEmprestimo> findByStatus(String status, Sort sort);

    // M�todo para busca customizada, por m�ltiplos campos, conforme necess�rio
    List<HistoricoEmprestimo> findByClienteNomeContainingIgnoreCaseAndLivroTituloContainingIgnoreCase(
            String nomeCliente, String tituloLivro, Sort sort);

    // Busca hist�ricos de empr�stimos ativos de um livro espec�fico (livro n�o devolvido)
    List<HistoricoEmprestimo> findByLivro_IdAndStatusNot(Long livroId, String status, Sort sort);
}
