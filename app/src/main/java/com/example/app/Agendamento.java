package com.example.app;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Agendamento {
    private String id;
    private String barbeiroId;
    private String barbeiroNome;
    private String clienteId;
    private String dia;
    private String horario;
    private String servico;
    private String status;
    @ServerTimestamp
    private Date dataCriacao;  // Data de criação do agendamento

    // Construtor vazio necessário para Firebase
    public Agendamento() {
    }

    // Construtor com parâmetros
    public Agendamento(String barbeiroId, String barbeiroNome, String clienteId, String dia, String horario, String servico, String status) {
        this.barbeiroId = barbeiroId;
        this.barbeiroNome = barbeiroNome;
        this.clienteId = clienteId;
        this.dia = dia;
        this.horario = horario;
        this.servico = servico;
        this.status = status;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBarbeiroId() { return barbeiroId; }
    public void setBarbeiroId(String barbeiroId) { this.barbeiroId = barbeiroId; }

    public String getBarbeiroNome() { return barbeiroNome; }
    public void setBarbeiroNome(String barbeiroNome) { this.barbeiroNome = barbeiroNome; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
}
