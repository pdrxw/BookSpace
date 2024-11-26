package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private LivroService livroService;

    @Autowired
    private HistoricoEmprestimoService historicoEmprestimoService;

    @GetMapping
    public String listarEmprestimos(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search, // Para pesquisa
        Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        List<Emprestimo> emprestimos;

        if (search != null && !search.trim().isEmpty()) {
            emprestimos = emprestimoService.buscarEmprestimosPorCampos(search, orderBy);
        } else {
            emprestimos = emprestimoService.listarEmprestimosOrdenados(orderBy);
        }

        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("search", search);

        return "emprestimos";
    }

    @GetMapping("/cadastro")
    public String cadastroEmprestimo(Model model) {
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");

        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "cadastro-emprestimo";
    }

    @PostMapping("/cadastro")
    public String cadastrarEmprestimo(
        @RequestParam Long clienteId,
        @RequestParam Long livroId,
        @RequestParam String dataRetirada,
        @RequestParam String dataPrevistaDevolucao,
        @RequestParam(value = "multaAplicada", defaultValue = "0") String multaAplicada, // Aqui, definimos o valor padrão para 0
        @RequestParam(value = "order_by", defaultValue = "dataRetirada") String orderBy, // Novo parâmetro orderBy
        Model model) {

    // Verifica se o livro já está emprestado, passando o parâmetro orderBy
    if (emprestimoService.isLivroEmprestado(livroId, orderBy)) {
        model.addAttribute("error", "Este livro já está emprestado.");
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);
        return "cadastro-emprestimo"; // Retorna à página de cadastro de empréstimo com erro
    }

    Emprestimo novoEmprestimo = new Emprestimo();
    novoEmprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
    novoEmprestimo.setLivro(livroService.buscarLivroPorId(livroId));
    novoEmprestimo.setDataRetirada(dataRetirada);
    novoEmprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
    novoEmprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));
    novoEmprestimo.setStatus("ativo");

    try {
        // Alterar o status do livro para "indisponível" quando retirado
        livroService.alterarStatusLivro(livroId, "emprestado");

        // Salvar o novo empréstimo
        emprestimoService.salvarEmprestimo(novoEmprestimo);

    } catch (DataIntegrityViolationException e) {
        model.addAttribute("error", "Erro ao salvar o empréstimo.");
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);
        return "cadastro-emprestimo";
    }

    return "redirect:/emprestimos";
}


    @GetMapping("/editar/{id}")
    public String editarEmprestimo(@PathVariable("id") Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }

        model.addAttribute("emprestimo", emprestimo);

        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "editar-emprestimo";
    }

    @PostMapping("/editar/{id}")
    public String atualizarEmprestimo(
            @PathVariable("id") Long id,
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            @RequestParam String dataRetirada,
            @RequestParam String dataPrevistaDevolucao,
            @RequestParam(value = "order_by", defaultValue = "dataRetirada") String orderBy,
            Model model) {

        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }

        // Verifica se o livro foi alterado
        Long livroAntigoId = emprestimo.getLivro().getId();
        if (!livroAntigoId.equals(livroId)) {
            livroService.alterarStatusLivro(livroAntigoId, "disponivel");

            livroService.alterarStatusLivro(livroId, "emprestado");
        }

        emprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
        emprestimo.setLivro(livroService.buscarLivroPorId(livroId));
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);

        try {
            emprestimoService.salvarEmprestimo(emprestimo);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o empréstimo.");
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);
            return "editar-emprestimo";
        }

        return "redirect:/emprestimos";
    }

    @PostMapping("/remover/{id}")
    public String removerEmprestimo(@PathVariable("id") Long id, Model model) {
    try {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        
        if (emprestimo != null) {
            Long livroId = emprestimo.getLivro().getId();
            
            livroService.alterarStatusLivro(livroId, "disponivel");
            
            emprestimoService.removerEmprestimo(id);
        }
    } catch (Exception e) {
        model.addAttribute("error", "Erro ao remover o empréstimo.");
        return "redirect:/emprestimos";
    }

    return "redirect:/emprestimos";
}
    @GetMapping("/concluir/{id}")
    public String concluirEmprestimo(@PathVariable("id") Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }

        model.addAttribute("emprestimo", emprestimo);

        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "concluir-emprestimo";
    }

    @PostMapping("/concluir/{id}")
    public String concluirCadastro(
            @PathVariable("id") Long id,
            @RequestParam String dataDevolucao,
            @RequestParam String dataPrevistaDevolucao,
            @RequestParam(value = "multaAplicada", defaultValue = "0") String multaAplicada,
            @RequestParam(value = "status", defaultValue = "inativo" ) String status,
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            Model model) {

        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        HistoricoEmprestimo historicoEmprestimo = new HistoricoEmprestimo();

        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }

        Long livroStatus = emprestimo.getLivro().getId();

        historicoEmprestimo.setLivro(livroService.buscarLivroPorId(livroId));
        historicoEmprestimo.setEmprestimo(emprestimo);
        historicoEmprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
        historicoEmprestimo.setDataRetirada(emprestimo.getDataRetirada());
        historicoEmprestimo.setDataDevolucaoPrevista(emprestimo.getDataPrevistaDevolucao());
        historicoEmprestimo.setDataDevolucaoReal(dataDevolucao);
        historicoEmprestimo.setMulta(Double.parseDouble(multaAplicada.replace(",", ".")));
        historicoEmprestimo.setStatus(emprestimo.getStatus());
        livroService.alterarStatusLivro(livroStatus, "disponivel");
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        emprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));
        emprestimo.setStatus(status);

        try {
            emprestimoService.salvarEmprestimo(emprestimo);
            historicoEmprestimoService.salvarHistoricoEmprestimo(historicoEmprestimo);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o empréstimo.");
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);
            return "concluir-emprestimo";
        }

        return "redirect:/emprestimos";
    }

}