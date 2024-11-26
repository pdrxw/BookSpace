package com.projeto.projeto_biblioteca;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Listar todos os clientes
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Método para listar clientes ordenados de acordo com o critério
    public List<Cliente> listarClientesOrdenados(String orderBy) {
        // Verifica se o parâmetro 'orderBy' está vazio e aplica uma ordenação padrão
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso não seja especificado
        }
        return clienteRepository.findAll(Sort.by(orderBy));
    }

    // Novo método para buscar clientes por nome, e-mail, telefone ou CPF com pesquisa parcial e ordenação
    public List<Cliente> buscarClientesPorCampos(String pesquisa, String orderBy) {
        // Verifica se o parâmetro 'orderBy' está vazio e aplica uma ordenação padrão
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso não seja especificado
        }
        
        // Utiliza o método do repositório para buscar clientes que contenham o valor em qualquer um dos campos
        return clienteRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrCpfContainingIgnoreCase(
                pesquisa, pesquisa, pesquisa, pesquisa, Sort.by(orderBy));
    }

    // Salvar ou atualizar um cliente
    public void salvarCliente(Cliente cliente) {
        clienteRepository.save(cliente); // O método save do JPA faz tanto a inserção quanto a atualização
    }

    // Buscar cliente por ID
    public Cliente buscarClientePorId(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null); // Retorna o cliente ou null caso não encontrado
    }

    // Remover um cliente por ID
    public void removerCliente(Long id) {
        clienteRepository.deleteById(id); // Deleta o cliente pelo ID
    }

    // Buscar cliente por e-mail
    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email); // Busca o cliente pelo e-mail
    }

    // Buscar cliente por telefone
    public Cliente buscarClientePorTelefone(String telefone) {
        return clienteRepository.findByTelefone(telefone); // Busca o cliente pelo telefone
    }  

    // Buscar cliente por CPF
    public Cliente buscarClientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf); // Busca o cliente pelo CPF
    }
    

}
