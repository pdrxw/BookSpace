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

    // Listar empréstimos com pesquisa e ordenação
    @GetMapping
    public String listarEmprestimos(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search, // Para pesquisa
        Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se não estiver logado
        }

        List<Emprestimo> emprestimos;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca empréstimos por múltiplos campos
            emprestimos = emprestimoService.buscarEmprestimosPorCampos(search, orderBy);
        } else {
            // Caso contrário, apenas lista os empréstimos com a ordenação
            emprestimos = emprestimoService.listarEmprestimosOrdenados(orderBy);
        }

        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("search", search); // Para manter o valor da pesquisa na página

        return "emprestimos"; // Página de lista de empréstimos
    }

    // Página para o formulário de cadastro de empréstimos
    @GetMapping("/cadastro")
    public String cadastroEmprestimo(Model model) {
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");

        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "cadastro-emprestimo"; // Página HTML para cadastro de empréstimo
    }

    // Método para cadastrar um novo empréstimo
    @PostMapping("/cadastro")
    public String cadastrarEmprestimo(
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            @RequestParam String dataRetirada,
            @RequestParam String dataPrevistaDevolucao,
            @RequestParam String multaAplicada,
            @RequestParam String status,
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
        novoEmprestimo.setStatus(status);

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
            return "cadastro-emprestimo"; // Retorna à página de cadastro de empréstimo com erro
        }

        return "redirect:/emprestimos"; // Redireciona para a lista de empréstimos após o cadastro
    }

    // Página para o formulário de edição de empréstimo
    @GetMapping("/editar/{id}")
    public String editarEmprestimo(@PathVariable("id") Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos"; // Se o empréstimo não for encontrado, redireciona para a lista
        }

        model.addAttribute("emprestimo", emprestimo);

        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "editar-emprestimo"; // Página de edição de empréstimo
    }

    // Método para atualizar o empréstimo após a edição
    @PostMapping("/editar/{id}")
    public String atualizarEmprestimo(
            @PathVariable("id") Long id,
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            @RequestParam String dataRetirada,
            @RequestParam String dataPrevistaDevolucao,
            @RequestParam String multaAplicada,
            @RequestParam String status,
            @RequestParam(value = "order_by", defaultValue = "dataRetirada") String orderBy, // Novo parâmetro orderBy
            Model model) {

        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "Empréstimo não encontrado.");
            return "redirect:/emprestimos"; // Se o empréstimo não for encontrado, redireciona para a lista
        }

        // Verifica se o livro foi alterado
        Long livroAntigoId = emprestimo.getLivro().getId();
        if (!livroAntigoId.equals(livroId)) {
            // Caso o livro tenha sido alterado:
            // Alterar o status do livro antigo para "disponível"
            livroService.alterarStatusLivro(livroAntigoId, "disponivel");

            // Alterar o status do novo livro para "indisponível"
            livroService.alterarStatusLivro(livroId, "emprestado");
        }

        // Atualiza o empréstimo com os novos dados
        emprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
        emprestimo.setLivro(livroService.buscarLivroPorId(livroId));
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        emprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));
        emprestimo.setStatus(status);

        try {
            // Salva o empréstimo atualizado
            emprestimoService.salvarEmprestimo(emprestimo);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o empréstimo.");
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);
            return "editar-emprestimo"; // Retorna à página de edição de empréstimo com erro
        }

        return "redirect:/emprestimos"; // Redireciona para a lista de empréstimos após a atualização
    }

    // Método para remover um empréstimo
    @PostMapping("/remover/{id}")
    public String removerEmprestimo(@PathVariable("id") Long id, Model model) {
        try {
            emprestimoService.removerEmprestimo(id); // Chama o serviço para remover o empréstimo
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao remover o empréstimo.");
            return "redirect:/emprestimos"; // Se houver erro, redireciona para a lista
        }

        return "redirect:/emprestimos"; // Redireciona para a lista de empréstimos após a remoção
    }
}
