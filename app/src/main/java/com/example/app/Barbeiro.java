package com.example.app;

import java.io.Serializable;
import java.util.List;

public class Barbeiro implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private String email;
    private String endereco;
    private List<String> diasDisponiveis;
    private List<String> servicos;

    public Barbeiro() {
        // Construtor vazio necessário para o Firebase
    }

    public Barbeiro(String id, String nome, String email, String endereco, List<String> diasDisponiveis, List<String> servicos) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.diasDisponiveis = diasDisponiveis;
        this.servicos = servicos;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public List<String> getDiasDisponiveis() { return diasDisponiveis; }
    public void setDiasDisponiveis(List<String> diasDisponiveis) { this.diasDisponiveis = diasDisponiveis; }

    public List<String> getServicos() { return servicos; }
    public void setServicos(List<String> servicos) { this.servicos = servicos; }
}
