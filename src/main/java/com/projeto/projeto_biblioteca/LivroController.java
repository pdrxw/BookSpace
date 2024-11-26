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
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    // Listar livros com pesquisa e ordena  o
    @GetMapping
    public String listarLivros(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search,  // Recebe o par metro 'search' para a pesquisa
        Model model, HttpSession session) {
        
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se n o estiver logado
        }

        List<Livro> livros;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca livros com m ltiplos par metros
            livros = livroService.buscarLivrosPorMultiplosTermos(search, orderBy);
        } else {
            // Caso contr rio, apenas lista os livros com a ordena  o
            livros = livroService.listarLivrosOrdenados(orderBy);
        }

        // Adiciona os livros na model
        model.addAttribute("livros", livros);
        model.addAttribute("search", search);  // Para manter o valor da pesquisa na p gina

        return "livros";  // Retorna a p gina de livros
    }

    // P gina para o formul rio de cadastro de livros
    @GetMapping("/cadastro")
    public String cadastroLivro() {
        return "cadastro-livro"; // P gina HTML para cadastro de livro
    }

    // M todo para cadastrar um novo livro
    @PostMapping("/cadastro")
    public String cadastrarLivro(
            @RequestParam String titulo,
            @RequestParam String autor,
            @RequestParam String categoria,
            @RequestParam String editora,
            @RequestParam String dataAdmissao,
            @RequestParam String status,
            Model model) {

        Livro novoLivro = new Livro();
        novoLivro.setTitulo(titulo);
        novoLivro.setAutor(autor);
        novoLivro.setCategoria(categoria);
        novoLivro.setEditora(editora);
        novoLivro.setDataAdmissao(dataAdmissao);
        novoLivro.setStatus(status);

        try {
            livroService.salvarLivro(novoLivro);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao salvar o livro.");
            return "cadastro-livro"; // Retorna   p gina de cadastro de livro com erro
        }

        return "redirect:/livros"; // Redireciona para a lista de livros ap s o cadastro
    }

    // M todo para exibir a p gina de edi  o de livro
    @GetMapping("/editar/{id}")
    public String editarLivro(@PathVariable("id") Long id, Model model) {
        Livro livro = livroService.buscarLivroPorId(id);
        if (livro == null) {
            model.addAttribute("error", "Livro não encontrado.");
            return "redirect:/livros"; // Se o livro n o for encontrado, redireciona para a lista
        }
        model.addAttribute("livro", livro); // Passa o livro para o formul rio de edi  o
        return "editar-livro"; // P gina de edi  o de livro
    }

    // M todo para atualizar o livro ap s a edi  o
    @PostMapping("/editar/{id}")
    public String atualizarLivro(
            @PathVariable("id") Long id,
            @RequestParam String titulo,
            @RequestParam String autor,
            @RequestParam String categoria,
            @RequestParam String editora,
            @RequestParam String dataAdmissao,
            @RequestParam String status,
            Model model) {
        
        Livro livro = livroService.buscarLivroPorId(id);
        if (livro == null) {
            model.addAttribute("error", "Livro não encontrado.");
            return "redirect:/livros"; // Se o livro n o for encontrado, redireciona para a lista
        }

        livro.setTitulo(titulo);
        livro.setAutor(autor);
        livro.setCategoria(categoria);
        livro.setEditora(editora);
        livro.setDataAdmissao(dataAdmissao);
        livro.setStatus(status);

        try {
            livroService.salvarLivro(livro); // Atualiza o livro
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o livro.");
            return "editar-livro"; // Retorna   p gina de edi  o de livro com erro
        }

        return "redirect:/livros"; // Redireciona para a lista de livros ap s a atualiza  o
    }

    // M todo para remover um livro
    @PostMapping("/remover/{id}")
    public String removerLivro(@PathVariable("id") Long id, Model model) {
        try {
            livroService.removerLivro(id); // Chama o servi o para remover o livro
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao remover o livro.");
            return "redirect:/livros"; // Se houver erro, redireciona para a lista
        }

        return "redirect:/livros"; // Redireciona para a lista de livros ap s a remo  o
    }
}
