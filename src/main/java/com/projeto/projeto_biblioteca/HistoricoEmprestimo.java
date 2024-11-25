package com.projeto.projeto_biblioteca;

// import java.sql.Timestamp;

// import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class HistoricoEmprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emprestimo_id", nullable = false) // FK para o Empréstimo
    private Emprestimo emprestimo;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false) // FK para o Cliente
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false) // FK para o Livro
    private Livro livro;

    private String dataRetirada;  // Usando String, conforme a entidade Emprestimo
    private String dataDevolucaoPrevista;  // Usando String, conforme a entidade Emprestimo
    private String dataDevolucaoReal;  // Usando String, conforme a entidade Emprestimo
    private double multa;  // Multa aplicada no histórico

    private String status;  // Status do empréstimo (ativo, finalizado, cancelado)

    // @Column(name = "created_at", updatable = false)
    // private Timestamp createdAt;  // Timestamp de criação

    // @Column(name = "updated_at")
    // private Timestamp updatedAt;  // Timestamp de atualização

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
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

    public String getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(String dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public String getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(String dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public double getMulta() {
        return multa;
    }

    public void setMulta(double multa) {
        this.multa = multa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // public Timestamp getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(Timestamp createdAt) {
    //     this.createdAt = createdAt;
    // }

    // public Timestamp getUpdatedAt() {
    //     return updatedAt;
    // }

    // public void setUpdatedAt(Timestamp updatedAt) {
    //     this.updatedAt = updatedAt;
    // }
}
