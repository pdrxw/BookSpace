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

    // M todo para listar clientes ordenados de acordo com o crit rio
    public List<Cliente> listarClientesOrdenados(String orderBy) {
        // Verifica se o par metro 'orderBy' est  vazio e aplica uma ordena  o padr o
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso n o seja especificado
        }
        return clienteRepository.findAll(Sort.by(orderBy));
    }

    // Novo m todo para buscar clientes por nome, e-mail, telefone ou CPF com pesquisa parcial e ordena  o
    public List<Cliente> buscarClientesPorCampos(String pesquisa, String orderBy) {
        // Verifica se o par metro 'orderBy' est  vazio e aplica uma ordena  o padr o
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = "id"; // Default ordering by 'id' caso n o seja especificado
        }
        
        // Utiliza o m todo do reposit rio para buscar clientes que contenham o valor em qualquer um dos campos
        return clienteRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefoneContainingIgnoreCaseOrCpfContainingIgnoreCase(
                pesquisa, pesquisa, pesquisa, pesquisa, Sort.by(orderBy));
    }

    // Salvar ou atualizar um cliente
    public void salvarCliente(Cliente cliente) {
        clienteRepository.save(cliente); // O m todo save do JPA faz tanto a inser  o quanto a atualiza  o
    }

    // Buscar cliente por ID
    public Cliente buscarClientePorId(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null); // Retorna o cliente ou null caso n o encontrado
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