package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/historico-emprestimos")
public class HistoricoEmprestimoController {

    @Autowired
    private HistoricoEmprestimoService historicoEmprestimoService;

    // Listar histórico de empréstimos com pesquisa e ordenação
    @GetMapping
    public String listarHistoricoEmprestimos(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search, // Para pesquisa
        Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se não estiver logado
        }

        List<HistoricoEmprestimo> historicos;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca históricos por múltiplos campos
            historicos = historicoEmprestimoService.buscarHistoricoPorCampos(search, orderBy);
        } else {
            // Caso contrário, apenas lista os históricos com a ordenação
            historicos = historicoEmprestimoService.listarHistoricoEmprestimosOrdenados(orderBy);
        }

        model.addAttribute("historicos", historicos);
        model.addAttribute("search", search); // Para manter o valor da pesquisa na página

        return "historico-emprestimos"; // Página de lista de histórico de empréstimos
    }

 
}
