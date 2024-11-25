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
    private EmprestimoService emprestimoService;  // Servi�o para gerenciar empr�stimos

    // Listar todos os hist�ricos de empr�stimos
    public List<HistoricoEmprestimo> listarHistoricoEmprestimos() {
        return historicoEmprestimoRepository.findAll();
    }

    // M�todo para listar hist�ricos de empr�stimos ordenados de acordo com o crit�rio
    public List<HistoricoEmprestimo> listarHistoricoEmprestimosOrdenados(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }
        return historicoEmprestimoRepository.findAll(Sort.by(orderBy));
    }

    // Buscar hist�ricos de empr�stimos por cliente, livro ou outros campos com pesquisa parcial e ordena��o
    public List<HistoricoEmprestimo> buscarHistoricoPorCampos(String search, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        // Busca hist�ricos pelo nome do cliente ou t�tulo do livro, com busca parcial e ignorando mai�sculas/min�sculas
        return historicoEmprestimoRepository.findByClienteNomeContainingIgnoreCaseOrLivroTituloContainingIgnoreCase(
                search, search, Sort.by(orderBy));
    }

    // Buscar hist�rico de empr�stimo por clienteId com pesquisa parcial e ordena��o
    public List<HistoricoEmprestimo> buscarHistoricoPorClienteId(Long clienteId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByClienteId(clienteId, Sort.by(orderBy));
    }

    // Buscar hist�rico de empr�stimo por livroId com pesquisa parcial e ordena��o
    public List<HistoricoEmprestimo> buscarHistoricoPorLivroId(Long livroId, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByLivroId(livroId, Sort.by(orderBy));
    }

    // Buscar hist�rico de empr�stimo por status
    public List<HistoricoEmprestimo> buscarHistoricoPorStatus(String status, String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id'
        }

        return historicoEmprestimoRepository.findByStatus(status, Sort.by(orderBy));
    }

    // Salvar ou atualizar um hist�rico de empr�stimo
    public void salvarHistoricoEmprestimo(HistoricoEmprestimo historicoEmprestimo) {
        historicoEmprestimoRepository.save(historicoEmprestimo);
    }

    // Buscar hist�rico de empr�stimo por ID
    public HistoricoEmprestimo buscarHistoricoEmprestimoPorId(Long id) {
        Optional<HistoricoEmprestimo> historicoEmprestimo = historicoEmprestimoRepository.findById(id);
        return historicoEmprestimo.orElse(null); // Retorna o hist�rico ou null
    }

    // Remover um hist�rico de empr�stimo por ID
    public void removerHistoricoEmprestimo(Long id) {
        historicoEmprestimoRepository.deleteById(id);
    }

    // Alterar o status de um empr�stimo
    public void alterarStatusEmprestimo(Long emprestimoId, String status) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(emprestimoId);
        if (emprestimo != null) {
            emprestimo.setStatus(status);  // Atualiza o status
            emprestimoService.salvarEmprestimo(emprestimo);  // Salva o empr�stimo com o novo status
        }
    }

    // Verifica se o empr�stimo j� foi devolvido
    public boolean isEmprestimoDevolvido(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(emprestimoId);
        return emprestimo != null && "devolvido".equals(emprestimo.getStatus());
    }
}
