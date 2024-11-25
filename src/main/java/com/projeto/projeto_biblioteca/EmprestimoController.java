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

    // @Autowired
    // private HistoricoEmprestimoService historicoEmprestimoService;

    // Listar emprÃ©stimos com pesquisa e ordenaÃ§Ã£o
    @GetMapping
    public String listarEmprestimos(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search, // Para pesquisa
        Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se nÃ£o estiver logado
        }

        List<Emprestimo> emprestimos;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca emprÃ©stimos por mÃºltiplos campos
            emprestimos = emprestimoService.buscarEmprestimosPorCampos(search, orderBy);
        } else {
            // Caso contrÃ¡rio, apenas lista os emprÃ©stimos com a ordenaÃ§Ã£o
            emprestimos = emprestimoService.listarEmprestimosOrdenados(orderBy);
        }

        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("search", search); // Para manter o valor da pesquisa na pÃ¡gina

        return "emprestimos"; // PÃ¡gina de lista de emprÃ©stimos
    }

    // PÃ¡gina para o formulÃ¡rio de cadastro de emprÃ©stimos
    @GetMapping("/cadastro")
    public String cadastroEmprestimo(Model model) {
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");

        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "cadastro-emprestimo"; // PÃ¡gina HTML para cadastro de emprÃ©stimo
    }

    // MÃ©todo para cadastrar um novo emprÃ©stimo
    @PostMapping("/cadastro")
    public String cadastrarEmprestimo(
        @RequestParam Long clienteId,
        @RequestParam Long livroId,
        @RequestParam String dataRetirada,
        @RequestParam String dataPrevistaDevolucao,
        @RequestParam(value = "multaAplicada", defaultValue = "0") String multaAplicada, // Aqui, definimos o valor padrão para 0
        // @RequestParam(value = "status", defaultValue = "emprestado") String status,
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
        return "cadastro-emprestimo"; // Retorna à página de cadastro de empréstimo com erro
    }

    return "redirect:/emprestimos"; // Redireciona para a lista de empréstimos após o cadastro
}


    // PÃ¡gina para o formulÃ¡rio de ediÃ§Ã£o de emprÃ©stimo
    @GetMapping("/editar/{id}")
    public String editarEmprestimo(@PathVariable("id") Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
            return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
        }

        model.addAttribute("emprestimo", emprestimo);

        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "editar-emprestimo"; // PÃ¡gina de ediÃ§Ã£o de emprÃ©stimo
    }

    // MÃ©todo para atualizar o emprÃ©stimo apÃ³s a ediÃ§Ã£o
    @PostMapping("/editar/{id}")
    public String atualizarEmprestimo(
            @PathVariable("id") Long id,
            @RequestParam Long clienteId,
            @RequestParam Long livroId,
            @RequestParam String dataRetirada,
            @RequestParam String dataPrevistaDevolucao,
            // @RequestParam String multaAplicada,
            @RequestParam String status,
            @RequestParam(value = "order_by", defaultValue = "dataRetirada") String orderBy, // Novo parÃ¢metro orderBy
            Model model) {

        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
            return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
        }

        // Verifica se o livro foi alterado
        Long livroAntigoId = emprestimo.getLivro().getId();
        if (!livroAntigoId.equals(livroId)) {
            // Caso o livro tenha sido alterado:
            // Alterar o status do livro antigo para "disponÃ­vel"
            livroService.alterarStatusLivro(livroAntigoId, "disponivel");

            // Alterar o status do novo livro para "indisponÃ­vel"
            livroService.alterarStatusLivro(livroId, "emprestado");
        }

        // Atualiza o emprÃ©stimo com os novos dados
        emprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
        emprestimo.setLivro(livroService.buscarLivroPorId(livroId));
        emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        // emprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));
        emprestimo.setStatus(status);

        try {
            // Salva o emprÃ©stimo atualizado
            emprestimoService.salvarEmprestimo(emprestimo);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o emprÃ©stimo.");
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);
            return "editar-emprestimo"; // Retorna Ã  pÃ¡gina de ediÃ§Ã£o de emprÃ©stimo com erro
        }

        return "redirect:/emprestimos"; // Redireciona para a lista de emprÃ©stimos apÃ³s a atualizaÃ§Ã£o
    }

    // MÃ©todo para remover um emprÃ©stimo
    @PostMapping("/remover/{id}")
    public String removerEmprestimo(@PathVariable("id") Long id, Model model) {
    try {
        // Busca o emprÃ©stimo antes de removÃª-lo
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        
        if (emprestimo != null) {
            // ObtÃ©m o livro que estava emprestado
            Long livroId = emprestimo.getLivro().getId();
            
            // Alterar o status do livro para "disponÃ­vel"
            livroService.alterarStatusLivro(livroId, "disponivel");
            
            // Chama o serviÃ§o para remover o emprÃ©stimo
            emprestimoService.removerEmprestimo(id);
        }
    } catch (Exception e) {
        model.addAttribute("error", "Erro ao remover o emprÃ©stimo.");
        return "redirect:/emprestimos"; // Se houver erro, redireciona para a lista
    }

    return "redirect:/emprestimos"; // Redireciona para a lista de emprÃ©stimos apÃ³s a remoÃ§Ã£o
}
// @GetMapping("/concluir/{id}")
//     public String concluirEmprestimo(@PathVariable("id") Long id, Model model) {
//         Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
//         if (emprestimo == null) {
//             model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
//             return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
//         }

//         model.addAttribute("emprestimo", emprestimo);

//         List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
//         List<Livro> livros = livroService.listarLivrosOrdenados("id");
//         model.addAttribute("clientes", clientes);
//         model.addAttribute("livros", livros);

//         return "concluir-emprestimo"; // PÃ¡gina de ediÃ§Ã£o de emprÃ©stimo
//     }


//     @PostMapping("/concluir/{id}")
//     public String concluirCadastro(
//             @PathVariable("id") Long id,
//             @RequestParam Long clienteId,
//             @RequestParam Long livroId,
//             @RequestParam String dataPrevistaDevolucao,
//             @RequestParam(value = "multaAplicada", defaultValue = "0") String multaAplicada, // Novo parÃ¢metro orderBy
//             Model model) {
         
//         Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
//         if (emprestimo == null) {
//             model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
//             return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
//         }

//         // Verifica se o livro foi alterado
//         Long livroAntigoId = emprestimo.getLivro().getId();
//         if (!livroAntigoId.equals(livroId)) {
//             // Caso o livro tenha sido alterado:
//             // Alterar o status do livro antigo para "disponÃ­vel"
//             livroService.alterarStatusLivro(livroAntigoId, "disponivel");

//             // Alterar o status do novo livro para "indisponÃ­vel"
//             livroService.alterarStatusLivro(livroId, "emprestado");
//         }

//         // Atualiza o emprÃ©stimo com os novos dados
//         emprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
//         emprestimo.setLivro(livroService.buscarLivroPorId(livroId));
//         emprestimo.setDataPrevistaDevolucao(dataPrevistaDevolucao);
//         emprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));

//         try {
//             // Salva o emprÃ©stimo atualizado
//             emprestimoService.salvarEmprestimo(emprestimo);
//         } catch (DataIntegrityViolationException e) {
//             model.addAttribute("error", "Erro ao atualizar o emprÃ©stimo.");
//             List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
//             List<Livro> livros = livroService.listarLivrosOrdenados("id");
//             model.addAttribute("clientes", clientes);
//             model.addAttribute("livros", livros);
//             return "concluir-emprestimo"; // Retorna Ã  pÃ¡gina de ediÃ§Ã£o de emprÃ©stimo com erro
//         }

//         return "redirect:/emprestimos"; // Redireciona para a lista de emprÃ©stimos apÃ³s a atualizaÃ§Ã£o
//     }

    @GetMapping("/concluir/{id}")
    public String concluirEmprestimo(@PathVariable("id") Long id, Model model) {
        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
            return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
        }

        model.addAttribute("emprestimo", emprestimo);

        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "concluir-emprestimo"; // PÃ¡gina de ediÃ§Ã£o de emprÃ©stimo
    }

    // MÃ©todo para atualizar o emprÃ©stimo apÃ³s a ediÃ§Ã£o
    @PostMapping("/concluir/{id}")
    public String concluirCadastro(
            @PathVariable("id") Long id,
            // @RequestParam Long clienteId,
            // @RequestParam Long livroId,
            // @RequestParam String dataRetirada,
            @RequestParam String dataDevolucao,
            @RequestParam(value = "multaAplicada", defaultValue = "0") String multaAplicada,
            // @RequestParam String status,
            // @RequestParam(value = "order_by", defaultValue = "dataRetirada") String orderBy, // Novo parÃ¢metro orderBy
            Model model) {

        Emprestimo emprestimo = emprestimoService.buscarEmprestimoPorId(id);
        if (emprestimo == null) {
            model.addAttribute("error", "EmprÃ©stimo nÃ£o encontrado.");
            return "redirect:/emprestimos"; // Se o emprÃ©stimo nÃ£o for encontrado, redireciona para a lista
        }

        // Verifica se o livro foi alterado
        // Long livroAntigoId = emprestimo.getLivro().getId();
        // if (!livroAntigoId.equals(livroId)) {
        //     // Caso o livro tenha sido alterado:
        //     // Alterar o status do livro antigo para "disponÃ­vel"
        //     livroService.alterarStatusLivro(livroAntigoId, "disponivel");

        //     // Alterar o status do novo livro para "indisponÃ­vel"
        //     livroService.alterarStatusLivro(livroId, "emprestado");
        // }

        // Atualiza o emprÃ©stimo com os novos dados
        // emprestimo.setCliente(clienteService.buscarClientePorId(clienteId));
        // emprestimo.setLivro(livroService.buscarLivroPorId(livroId));
        // emprestimo.setDataRetirada(dataRetirada);
        emprestimo.setDataPrevistaDevolucao(dataDevolucao);
        emprestimo.setMultaAplicada(Double.parseDouble(multaAplicada.replace(",", ".")));
        // emprestimo.setStatus(status);

        try {
            // Salva o emprÃ©stimo atualizado
            emprestimoService.salvarEmprestimo(emprestimo);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o emprÃ©stimo.");
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);
            return "concluir-emprestimo"; // Retorna Ã  pÃ¡gina de ediÃ§Ã£o de emprÃ©stimo com erro
        }

        return "redirect:/emprestimos"; // Redireciona para a lista de emprÃ©stimos apÃ³s a atualizaÃ§Ã£o
    }

}
