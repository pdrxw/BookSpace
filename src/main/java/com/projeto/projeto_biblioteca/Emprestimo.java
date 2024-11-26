package com.projeto.projeto_biblioteca;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente; // Relacionamento com Cliente (FK)

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro; // Relacionamento com Livro (FK)

    private String dataRetirada; // Alterado para String
    private String dataPrevistaDevolucao; // Alterado para String
    private String dataDevolucao; // Alterado para String
    private String status; // (pendencia, concluido, atrasado)
    private double multaAplicada;

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public String getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(String dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public String getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public void setDataPrevistaDevolucao(String dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public String getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(String dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getMultaAplicada() {
        return multaAplicada;
    }

    public void setMultaAplicada(double multaAplicada) {
        this.multaAplicada = multaAplicada;
    }

    public void setClienteId(Long clienteId) {
        throw new UnsupportedOperationException("Unimplemented method 'setClienteId'");
    }

    public void setLivroId(Long livroId) {
        throw new UnsupportedOperationException("Unimplemented method 'setLivroId'");
    }
}