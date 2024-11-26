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
    private ClienteRepository clienteRepository;

    @Autowired
    private LivroService livroService; // Serviço para acessar livros

    // Listar clientes com pesquisa e ordenação
    @GetMapping
    public String listarClientes(
        @RequestParam(value = "order_by", defaultValue = "id") String orderBy,
        @RequestParam(value = "search", required = false) String search,  // Recebe o parâmetro 'search' para a pesquisa
        Model model, HttpSession session) {
        
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login"; // Redireciona para login se não estiver logado
        }

        List<Cliente> clientes;

        if (search != null && !search.trim().isEmpty()) {
            // Se houver valor em 'search', busca clientes por múltiplos campos com ordenação
            clientes = clienteService.buscarClientesPorCampos(search, orderBy);
        } else {
            // Caso contrário, apenas lista os clientes com a ordenação
            clientes = clienteService.listarClientesOrdenados(orderBy);
        }

        // Adiciona os clientes na model
        model.addAttribute("clientes", clientes);
        model.addAttribute("search", search);  // Para manter o valor da pesquisa na página

        return "clientes";  // Retorna a página de clientes
    }

    // Página para o formulário de cadastro de clientes
    @GetMapping("/cadastro")
    public String cadastroCliente(Model model) {
        // Carregando as listas de clientes e livros para preencher os selects
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id"); // Ou outro método que você tenha para listar os livros

        // Adicionando as listas ao modelo
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "cadastro-cliente"; // Página HTML para cadastro de cliente
    }

    // Método para cadastrar um novo cliente
    @PostMapping("/cadastro")
    public String cadastrarCliente(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String dataNascimento,
            @RequestParam String cpf,
            Model model) {

        if (clienteRepository.findByCpf(cpf) != null) { 
            model.addAttribute("error", "CPF já cadastrado."); 
            return "cadastro-cliente"; // Retorna a página de cadastro com erro
        } // Verifica se o cliente existe

        if (clienteRepository.findByEmail(email) != null) {
            model.addAttribute("error", "E-mail já cadastrado.");
            return "cadastro-cliente"; // Retorna a página de cadastro com erro
        }

        if (clienteRepository.findByTelefone(telefone) != null) {
            model.addAttribute("error", "Telefone já cadastrado.");
            return "cadastro-cliente"; // Retorna a página de cadastro com erro
        }

        // Cria um novo cliente 
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(nome);
        novoCliente.setEmail(email);
        novoCliente.setTelefone(telefone);
        novoCliente.setDataNascimento(dataNascimento);
        novoCliente.setCpf(cpf);
        novoCliente.setDataCadastro("2024-11-26");

        try {
            clienteService.salvarCliente(novoCliente);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Erro ao salvar o cliente.");
            
            // Carregando as listas de clientes e livros para preencher os selects na página de cadastro
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);

            return "cadastro-cliente"; // Retorna à página de cadastro de cliente com erro
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes após o cadastro
    }

    // Método para exibir a página de edição de cliente
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") Long id, Model model) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        if (cliente == null) {
            model.addAttribute("error", "Cliente não encontrado.");
            return "redirect:/clientes"; // Se o cliente não for encontrado, redireciona para a lista
        }
        model.addAttribute("cliente", cliente); // Passa o cliente para o formulário de edição

        // Carregando as listas de clientes e livros para preencher os selects na página de edição
        List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
        List<Livro> livros = livroService.listarLivrosOrdenados("id");
        model.addAttribute("clientes", clientes);
        model.addAttribute("livros", livros);

        return "editar-cliente"; // Página de edição de cliente
    }

    // Método para atualizar o cliente após a edição
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
            model.addAttribute("error", "Cliente não encontrado.");
            return "redirect:/clientes"; // Se o cliente não for encontrado, redireciona para a lista
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
            
            // Carregando as listas de clientes e livros para preencher os selects na página de edição
            List<Cliente> clientes = clienteService.listarClientesOrdenados("id");
            List<Livro> livros = livroService.listarLivrosOrdenados("id");
            model.addAttribute("clientes", clientes);
            model.addAttribute("livros", livros);

            return "editar-cliente"; // Retorna à página de edição de cliente com erro
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes após a atualização
    }

    // Método para remover um cliente
    @PostMapping("/remover/{id}")
    public String removerCliente(@PathVariable("id") Long id, Model model) {
        try {
            clienteService.removerCliente(id); // Chama o serviço para remover o cliente
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao remover o cliente.");
            return "redirect:/clientes"; // Se houver erro, redireciona para a lista
        }

        return "redirect:/clientes"; // Redireciona para a lista de clientes após a remoção
    }
}