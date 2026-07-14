package com.mycompany.agendamentoclinica;

public class Medico {
    private String nome;
    private String crm;
    private String email;
    private String telefone;
    private String senhaCriptografada;

    public Medico(String nome, String crm, String email, String telefone, String senhaCriptografada) {
        this.nome = nome;
        this.crm = crm;
        this.email = email;
        this.telefone = telefone;
        this.senhaCriptografada = senhaCriptografada;
    }

    // Getters para salvar no banco
    public String getNome() { return nome; }
    public String getCrm() { return crm; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenhaCriptografada() { return senhaCriptografada; }
}