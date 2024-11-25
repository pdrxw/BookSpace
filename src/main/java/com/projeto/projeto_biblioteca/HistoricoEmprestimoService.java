package com.projeto.projeto_biblioteca;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HistoricoEmprestimoService {

    @Autowired
    private HistoricoEmprestimoRepository historicoEmprestimoRepository;

    @Autowired
    private EmprestimoService emprestimoService;  // Serviço para gerenciar empréstimos

    // Listar todos os históricos de empréstimos
    public List<HistoricoEmprestimo> listarHistoricoEmprestimos() {
        return historicoEmprestimoRepository.findAll();
    }

    // Método para listar históricos de empréstimos ordenados de acordo com o critério
    public List<HistoricoEmprestimo> listarHistoricoEmprestimosOrdenados(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }
        return historicoEmprestimoRepository.findAll(Sort.by(orderBy));
    }

    // Buscar históricos de empréstimos por cliente, livro ou outros campos com pesquisa parcial e ordenação
    public List<HistoricoEmprestimo> buscarHistoricoPorCampos(String search, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        // Busca históricos pelo nome do cliente ou título do livro, com busca parcial e ignorando maiúsculas/minúsculas
        return historicoEmprestimoRepository.findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
                search, search, Sort.by(orderBy));
    }

    // Buscar histórico de empréstimo por clienteId com pesquisa parcial e ordenação
    public List<HistoricoEmprestimo> buscarHistoricoPorClienteId(Long clienteId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByClienteId(clienteId, Sort.by(orderBy));
    }

    // Buscar histórico de empréstimo por livroId com pesquisa parcial e ordenação
    public List<HistoricoEmprestimo> buscarHistoricoPorLivroId(Long livroId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByLivroId(livroId, Sort.by(orderBy));
    }

    // Buscar histórico de empréstimo por status
    public List<HistoricoEmprestimo> buscarHistoricoPorStatus(String status, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByStatus(status, Sort.by(orderBy));
    }

    // Salvar ou atualizar um histórico de empréstimo
    public void salvarHistoricoEmprestimo(HistoricoEmprestimo historicoEmprestimo) {
        historicoEmprestimoRepository.save(historicoEmprestimo);
    }

    // Buscar histórico de empréstimo por ID
    public HistoricoEmprestimo buscarHistoricoEmprestimoPorId(Long id) {
        Optional<HistoricoEmprestimo> historicoEmprestimo = historicoEmprestimoRepository.findById(id);
        return historicoEmprestimo.orElse(null); // Retorna o histórico ou null
    }

    // Remover um histórico de empréstimo por ID
    public void removerHistoricoEmprestimo(Long id) {
        historicoEmprestimoRepository.deleteById(id);
    }

    // Alterar o status de um empréstimo
    public void alterarStatusEmprestimo(Long emprestimoId, String status) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(emprestimoId);
        if (emprestimo != null) {
            emprestimo.setStatus(status);  // Atualiza o status
            emprestimoService.salvarEmprestimo(emprestimo);  // Salva o empréstimo com o novo status
        }
    }

    // Verifica se o empréstimo já foi devolvido
    public boolean isEmprestimoDevolvido(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(emprestimoId);
        return emprestimo != null && "devolvido".equals(emprestimo.getStatus());
    }
}
