package com.mycompany.agendamentoclinica;

public class Paciente {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String senhaCriptografada;

    // Construtor
    public Paciente(String nome, String cpf, String email, String telefone, String senhaCriptografada) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senhaCriptografada = senhaCriptografada;
    }

    // Getters e Setters (Necessários para acessar os dados com segurança)
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenhaCriptografada() { return senhaCriptografada; }
}