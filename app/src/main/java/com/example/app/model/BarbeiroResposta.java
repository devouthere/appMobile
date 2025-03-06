package com.example.app.model;

import java.util.List;

public class BarbeiroResposta {
    private List<String> servicos;
    private List<String> diasDisponiveis;


    public BarbeiroResposta() {
    }

    public BarbeiroResposta(List<String> servicos, List<String> diasDisponiveis) {
        this.servicos = servicos;
        this.diasDisponiveis = diasDisponiveis;
    }


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