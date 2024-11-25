package com.projeto.projeto_biblioteca;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroService livroService;  // Servi�o para gerenciar livros

    // Listar todos os empr�stimos
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    // M�todo para listar empr�stimos ordenados de acordo com o crit�rio
    public List<Emprestimo> listarEmprestimosOrdenados(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }
        return emprestimoRepository.findAll(Sort.by(orderBy));
    }

    // Novo m�todo para buscar empr�stimos por cliente ou livro, com pesquisa parcial e ordena��o
    public List<Emprestimo> buscarEmprestimosPorClienteOuLivro(String search, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        // Busca empr�stimos pelo nome do cliente ou t�tulo do livro, com busca parcial e ignorando mai�sculas/min�sculas
        return emprestimoRepository.findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
                search, search, Sort.by(orderBy));
    }

    // Buscar empr�stimos por clienteId com pesquisa parcial e ordena��o
    public List<Emprestimo> buscarEmprestimosPorClienteId(Long clienteId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByClienteId(clienteId, Sort.by(orderBy));
    }

    // Buscar empr�stimos por livroId com pesquisa parcial e ordena��o
    public List<Emprestimo> buscarEmprestimosPorLivroId(Long livroId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByLivroId(livroId, Sort.by(orderBy));
    }

    // Buscar empr�stimos por status
    public List<Emprestimo> buscarEmprestimosPorStatus(String status, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByStatus(status, Sort.by(orderBy));
    }

    // Salvar ou atualizar um empr�stimo
    public void salvarEmprestimo(Emprestimo emprestimo) {
        emprestimoRepository.save(emprestimo);
    }

    // Buscar empr�stimo por ID
    public Emprestimo buscarEmprestimoPorId(Long id) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        return emprestimo.orElse(null); // Retorna o empr�stimo ou null
    }

    // Remover um empr�stimo por ID
    public void removerEmprestimo(Long id) {
        emprestimoRepository.deleteById(id);
    }

    // Alterar o status de um livro
    public void alterarStatusLivro(Long livroId, String status) {
        Livro livro = livroService.buscarLivroPorId(livroId);
        if (livro != null) {
            livro.setStatus(status);  // Atualiza o status
            livroService.salvarLivro(livro);  // Salva o livro com o novo status
        }
    }

    // Verifica se o livro j� est� emprestado (n�o devolvido)
    public boolean isLivroEmprestado(Long livroId, String orderBy) {
    if (orderBy == null || orderBy.trim().isEmpty()) {
        orderBy = "dataRetirada";  // Definindo um valor padr�o de ordena��o pela data de retirada
    }

    // Busca empr�stimos ativos de um livro espec�fico com status diferente de "disponivel" e ordena pelo par�metro orderBy
    List<Emprestimo> emprestimosAtivos = emprestimoRepository.findByLivro_IdAndStatusNot(livroId, "inativo", Sort.by(orderBy));
    
    // Retorna verdadeiro se houver empr�stimo ativo
    return !emprestimosAtivos.isEmpty();
}



    public List<Emprestimo> buscarEmprestimosPorCampos(String search, String orderBy) {
        // M�todo n�o implementado - opcional, dependendo dos requisitos adicionais
        throw new UnsupportedOperationException("Unimplemented method 'buscarEmprestimosPorCampos'");
    }
}
