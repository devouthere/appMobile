package com.example.app.model;

public class User {
    private String nome;
    private String email;
    private String senha;
    private String endereco;

    private String phoneNumber;
    private String tipoUsuario;


    public User(String nome, String email, String senha, String phoneNumber, String endereco, String tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.phoneNumber = phoneNumber;
        this.endereco = endereco;
        this.tipoUsuario = tipoUsuario;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

}
