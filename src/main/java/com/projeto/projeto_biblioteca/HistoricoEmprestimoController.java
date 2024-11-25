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

    // Listar hist�rico de empr�stimos com pesquisa e ordena��o
    @GetMapping
    public String listarHistoricoEmprestimos(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search, // Para pesquisa
        Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se n�o estiver logado
        }

        List<HistoricoEmprestimo> historicos;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca hist�ricos por m�ltiplos campos
            historicos = historicoEmprestimoService.buscarHistoricoPorCampos(search, orderBy);
        } else {
            // Caso contr�rio, apenas lista os hist�ricos com a ordena��o
            historicos = historicoEmprestimoService.listarHistoricoEmprestimosOrdenados(orderBy);
        }

        model.addAttribute("historicos", historicos);
        model.addAttribute("search", search); // Para manter o valor da pesquisa na p�gina

        return "historico-emprestimos"; // P�gina de lista de hist�rico de empr�stimos
    }

 
}
