package com.projeto.projeto_biblioteca;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    // Listar todos os livros
    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    // M todo para listar livros ordenados de acordo com o crit rio
    public List<Livro> listarLivrosOrdenados(String orderBy) {
        // Verifica se o par metro 'orderBy' est  vazio e aplica uma ordena  o padr o
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso n o seja especificado
        }
        return livroRepository.findAll(Sort.by(orderBy));
    }

    // M todo para buscar livros por m ltiplos termos (t tulo, autor, categoria, editora) com pesquisa parcial e ordena  o
    // M todo para buscar livros por m ltiplos termos (t tulo, autor, categoria, editora, status)
    public List<Livro> buscarLivrosPorMultiplosTermos(String search, String orderBy) {
    // Verifica se o par metro 'orderBy' est  vazio e aplica uma ordena  o padr o
    if (orderBy == null || orderBy.trim().isEmpty()) {
        orderBy = "id"; // Default ordering by 'id' caso n o seja especificado
    }

    // Realiza a busca pelos m ltiplos termos fornecidos (t tulo, autor, categoria, editora, status)
    return livroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrEditoraContainingIgnoreCaseOrStatusContainingIgnoreCase(
            search, search, search, search, search, Sort.by(orderBy));
    }


    // Salvar ou atualizar um livro
    public void salvarLivro(Livro livro) {
        livroRepository.save(livro); // O m todo save do JPA faz tanto a inser  o quanto a atualiza  o
    }

    // Buscar livro por ID
    public Livro buscarLivroPorId(Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.orElse(null); // Retorna o livro ou null caso n o encontrado
    }

    // Remover um livro por ID
    public void removerLivro(Long id) {
        livroRepository.deleteById(id); // Deleta o livro pelo ID
    }

    // M todo para alterar o status de um livro
    public void alterarStatusLivro(Long id, String status) {
        Livro livro = buscarLivroPorId(id); // Busca o livro pelo ID

        if (livro != null) {
            livro.setStatus(status); // Altera o status do livro
            salvarLivro(livro); // Salva as altera  es no banco de dados
        }
    }
}
