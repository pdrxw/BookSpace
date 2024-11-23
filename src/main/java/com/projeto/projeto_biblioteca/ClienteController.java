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
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private LivroService livroService; // Servi�o para acessar livros

    // Listar clientes com pesquisa e ordena��o
    @GetMapping
    public String listarClientes(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search,  // Recebe o par�metro 'search' para a pesquisa
        Model model, HttpSession session) {
        
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se n�o estiver logado
        }

        List<Cliente> clientes;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca clientes por m�ltiplos campos com ordena��o
            clientes = clienteService.buscarClientesPorCampos(search, orderBy);
        } else {
            // Caso contr�rio, apenas lista os clientes com a ordena��o
            clientes = clienteService.listarClientesOrdenados(orderBy);
        }

        // Adiciona os clientes na model
        model.addAttribute("clientes", clientes);
        model.addAttribute("search", search);  // Para manter o valor da pesquisa na p�gina

        return "clientes";  // Retorna a p�gina de clientes
    }

    // P�gina para o formul�rio de cadastro de clientes
    @GetMapping("/cadastro")
    public String cadastroCliente(Model model) {
        // Carregando as listas de clientes e livros para preencher os selects
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id"); // Ou outro m�todo que voc� tenha para listar os livros

        // Adicionando as listas ao modelo
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "cadastro-cliente"; // P�gina HTML para cadastro de cliente
    }

    // M�todo para cadastrar um novo cliente
    @PostMapping("/cadastro")
    public String cadastrarCliente(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String dataNascimento,  // A data continua como String
            @RequestParam String cpf,
            Model model) {

        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);
        novoCliente.setTelefone(telefone);
        novoCliente.setDataNascimento(dataNascimento);  // Mantendo a data como String
        novoCliente.setCpf(cpf);
        novoCliente.setDataCadastro("2024-11-22");  // A data de cadastro � uma String fixa (poderia ser `LocalDate.now()` em outro contexto)

        try {
            clienteService.salvarCliente(novoCliente);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao salvar o cliente.");
            
            // Carregando as listas de clientes e livros para preencher os selects na p�gina de cadastro
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);

            return "cadastro-cliente"; // Retorna � p�gina de cadastro de cliente com erro
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes ap�s o cadastro
    }

    // M�todo para exibir a p�gina de edi��o de cliente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") Long id, Model model) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        if (cliente == null) {
            model.addAttribute("error", "Cliente n�o encontrado.");
            return "redirect:/clientes"; // Se o cliente n�o for encontrado, redireciona para a lista
        }
        model.addAttribute("cliente", cliente); // Passa o cliente para o formul�rio de edi��o

        // Carregando as listas de clientes e livros para preencher os selects na p�gina de edi��o
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "editar-cliente"; // P�gina de edi��o de cliente
    }

    // M�todo para atualizar o cliente ap�s a edi��o
    @PostMapping("/editar/{id}")
    public String atualizarCliente(
            @PathVariable("id") Long id,
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String dataNascimento,  // A data continua como String
            @RequestParam String cpf,
            Model model) {
        
        Cliente cliente = clienteService.buscarClientePorId(id);
        if (cliente == null) {
            model.addAttribute("error", "Cliente n�o encontrado.");
            return "redirect:/clientes"; // Se o cliente n�o for encontrado, redireciona para a lista
        }

        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setDataNascimento(dataNascimento);  // Mantendo a data como String
        cliente.setCpf(cpf);

        try {
            clienteService.salvarCliente(cliente); // Atualiza o cliente
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao atualizar o cliente.");
            
            // Carregando as listas de clientes e livros para preencher os selects na p�gina de edi��o
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);

            return "editar-cliente"; // Retorna � p�gina de edi��o de cliente com erro
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes ap�s a atualiza��o
    }

    // M�todo para remover um cliente
    @PostMapping("/remover/{id}")
    public String removerCliente(@PathVariable("id") Long id, Model model) {
        try {
            clienteService.removerCliente(id); // Chama o servi�o para remover o cliente
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao remover o cliente.");
            return "redirect:/clientes"; // Se houver erro, redireciona para a lista
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes ap�s a remo��o
    }
}
