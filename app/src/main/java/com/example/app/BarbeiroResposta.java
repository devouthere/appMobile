package com.example.app;

import java.util.List;

public class BarbeiroResposta {
    private List<String> servicos;
    private List<String> diasDisponiveis;

    // Construtor vazio necessário para o Firestore
    public BarbeiroResposta() {
    }

    public BarbeiroResposta(List<String> servicos, List<String> diasDisponiveis) {
        this.servicos = servicos;
        this.diasDisponiveis = diasDisponiveis;
    }

    // Getters e Setters
    public List<String> getServicos() {
        return servicos;
    }

    public void setServicos(List<String> servicos) {
        this.servicos = servicos;
    }

    public List<String> getDiasDisponiveis() {
        return diasDisponiveis;
    }

    public void setDiasDisponiveis(List<String> diasDisponiveis) {
        this.diasDisponiveis = diasDisponiveis;
    }
}