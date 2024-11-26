package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Método para buscar livro pelo título exato
    Livro findByTitulo(String titulo);

    // Método para buscar livro pelo autor
    Livro findByAutor(String autor);

    // Método para buscar livro pela categoria
    Livro findByCategoria(String categoria);

    // Método para buscar livro pela editora
    Livro findByEditora(String editora);

    // Método para buscar livro pela data de admissão
    Livro findByDataAdmissao(String dataAdmissao);

    // Método para buscar livro pelo status
    Livro findByStatus(String status);

    // Novo método para buscar livros que contenham a string no título, autor, categoria ou editora,
    // ignorando maiúsculas/minúsculas, e ordenando de acordo com o parâmetro 'Sort'
    // Adicionar o campo status à busca por múltiplos termos
    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrEditoraContainingIgnoreCaseOrStatusContainingIgnoreCase(
        String titulo, String autor, String categoria, String editora, String status, Sort sort);

}
