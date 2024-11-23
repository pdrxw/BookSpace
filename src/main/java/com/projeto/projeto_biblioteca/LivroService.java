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

    // Método para listar livros ordenados de acordo com o critério
    public List<Livro> listarLivrosOrdenados(String orderBy) {
        // Verifica se o parâmetro 'orderBy' está vazio e aplica uma ordenação padrão
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso não seja especificado
        }
        return livroRepository.findAll(Sort.by(orderBy));
    }

    // Método para buscar livros por múltiplos termos (título, autor, categoria, editora) com pesquisa parcial e ordenação
    public List<Livro> buscarLivrosPorMultiplosTermos(String search, String orderBy) {
        // Verifica se o parâmetro 'orderBy' está vazio e aplica uma ordenação padrão
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso não seja especificado
        }

        // Realiza a busca pelos múltiplos termos fornecidos (título, autor, categoria, editora)
        return livroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrEditoraContainingIgnoreCase(
                search, search, search, search, Sort.by(orderBy));
    }

    // Salvar ou atualizar um livro
    public void salvarLivro(Livro livro) {
        livroRepository.save(livro); // O método save do JPA faz tanto a inserção quanto a atualização
    }

    // Buscar livro por ID
    public Livro buscarLivroPorId(Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.orElse(null); // Retorna o livro ou null caso não encontrado
    }

    // Remover um livro por ID
    public void removerLivro(Long id) {
        livroRepository.deleteById(id); // Deleta o livro pelo ID
    }

    // Método para alterar o status de um livro
    public void alterarStatusLivro(Long id, String status) {
        Livro livro = buscarLivroPorId(id); // Busca o livro pelo ID

        if (livro != null) {
            livro.setStatus(status); // Altera o status do livro
            salvarLivro(livro); // Salva as alterações no banco de dados
        }
    }
}
