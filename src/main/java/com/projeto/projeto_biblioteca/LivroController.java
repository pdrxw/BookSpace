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

    // Listar livros com pesquisa e ordenação
    @GetMapping
    public String listarLivros(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search,  // Recebe o parâmetro 'search' para a pesquisa
        Model model, HttpSession session) {
        
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se não estiver logado
        }

        List<Livro> livros;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca livros com múltiplos parâmetros
            livros = livroService.buscarLivrosPorMultiplosTermos(search, orderBy);
        } else {
            // Caso contrário, apenas lista os livros com a ordenação
            livros = livroService.listarLivrosOrdenados(orderBy);
        }

        // Adiciona os livros na model
        model.addAttribute("livros", livros);
        model.addAttribute("search", search);  // Para manter o valor da pesquisa na página

        return "livros";  // Retorna a página de livros
    }

    // Página para o formulário de cadastro de livros
    @GetMapping("/cadastro")
    public String cadastroLivro() {
        return "cadastro-livro"; // Página HTML para cadastro de livro
    }

    // Método para cadastrar um novo livro
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
            return "cadastro-livro"; // Retorna à página de cadastro de livro com erro
        }

        return "redirect:/livros"; // Redireciona para a lista de livros após o cadastro
    }

    // Método para exibir a página de edição de livro
    @GetMapping("/editar/{id}")
    public String editarLivro(@PathVariable("id") Long id, Model model) {
        Livro livro = livroService.buscarLivroPorId(id);
        if (livro == null) {
            model.addAttribute("error", "Livro não encontrado.");
            return "redirect:/livros"; // Se o livro não for encontrado, redireciona para a lista
        }
        model.addAttribute("livro", livro); // Passa o livro para o formulário de edição
        return "editar-livro"; // Página de edição de livro
    }

    // Método para atualizar o livro após a edição
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
            return "redirect:/livros"; // Se o livro não for encontrado, redireciona para a lista
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
            return "editar-livro"; // Retorna à página de edição de livro com erro
        }

        return "redirect:/livros"; // Redireciona para a lista de livros após a atualização
    }

    // Método para remover um livro
    @PostMapping("/remover/{id}")
    public String removerLivro(@PathVariable("id") Long id, Model model) {
        try {
            livroService.removerLivro(id); // Chama o serviço para remover o livro
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao remover o livro.");
            return "redirect:/livros"; // Se houver erro, redireciona para a lista
        }

        return "redirect:/livros"; // Redireciona para a lista de livros após a remoção
    }
}
