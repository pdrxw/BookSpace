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
    private LivroService livroService;  // Serviço para gerenciar livros

    // Listar todos os empréstimos
    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    // Método para listar empréstimos ordenados de acordo com o critério
    public List<Emprestimo> listarEmprestimosOrdenados(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }
        return emprestimoRepository.findAll(Sort.by(orderBy));
    }

    // Novo método para buscar empréstimos por cliente ou livro, com pesquisa parcial e ordenação
    public List<Emprestimo> buscarEmprestimosPorClienteOuLivro(String search, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        // Busca empréstimos pelo nome do cliente ou título do livro, com busca parcial e ignorando maiúsculas/minúsculas
        return emprestimoRepository.findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
                search, search, Sort.by(orderBy));
    }

    // Buscar empréstimos por clienteId com pesquisa parcial e ordenação
    public List<Emprestimo> buscarEmprestimosPorClienteId(Long clienteId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByClienteId(clienteId, Sort.by(orderBy));
    }

    // Buscar empréstimos por livroId com pesquisa parcial e ordenação
    public List<Emprestimo> buscarEmprestimosPorLivroId(Long livroId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByLivroId(livroId, Sort.by(orderBy));
    }

    // Buscar empréstimos por status
    public List<Emprestimo> buscarEmprestimosPorStatus(String status, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return emprestimoRepository.findByStatus(status, Sort.by(orderBy));
    }

    // Salvar ou atualizar um empréstimo
    public void salvarEmprestimo(Emprestimo emprestimo) {
        emprestimoRepository.save(emprestimo);
    }

    // Buscar empréstimo por ID
    public Emprestimo buscarEmprestimoPorId(Long id) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        return emprestimo.orElse(null); // Retorna o empréstimo ou null
    }

    // Remover um empréstimo por ID
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

    // Verifica se o livro já está emprestado (não devolvido)
    public boolean isLivroEmprestado(Long livroId, String orderBy) {
    if (orderBy == null || orderBy.trim().isEmpty()) {
        orderBy = "dataRetirada";  // Definindo um valor padrão de ordenação pela data de retirada
    }

    // Busca empréstimos ativos de um livro específico com status diferente de "disponivel" e ordena pelo parâmetro orderBy
    List<Emprestimo> emprestimosAtivos = emprestimoRepository.findByLivro_IdAndStatusNot(livroId, "inativo", Sort.by(orderBy));
    
    // Retorna verdadeiro se houver empréstimo ativo
    return !emprestimosAtivos.isEmpty();
}



    public List<Emprestimo> buscarEmprestimosPorCampos(String search, String orderBy) {
        // Método não implementado - opcional, dependendo dos requisitos adicionais
        throw new UnsupportedOperationException("Unimplemented method 'buscarEmprestimosPorCampos'");
    }
}
