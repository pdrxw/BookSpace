package com.projeto.projeto_biblioteca;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    // M todo para buscar livro pelo t tulo exato
    Livro findByTitulo(String titulo);

    // M todo para buscar livro pelo autor
    Livro findByAutor(String autor);

    // M todo para buscar livro pela categoria
    Livro findByCategoria(String categoria);

    // M todo para buscar livro pela editora
    Livro findByEditora(String editora);

    // M todo para buscar livro pela data de admiss o
    Livro findByDataAdmissao(String dataAdmissao);

    // M todo para buscar livro pelo status
    Livro findByStatus(String status);

    // Novo m todo para buscar livros que contenham a string no t tulo, autor, categoria ou editora,
    // ignorando mai sculas/min sculas, e ordenando de acordo com o par metro 'Sort'
    // Adicionar o campo status   busca por m ltiplos termos
    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrEditoraContainingIgnoreCaseOrStatusContainingIgnoreCase(
        String titulo, String autor, String categoria, String editora, String status, Sort sort);

}
