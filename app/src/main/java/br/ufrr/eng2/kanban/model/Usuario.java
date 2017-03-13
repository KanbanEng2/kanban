package br.ufrr.eng2.kanban.model;

import java.util.List;

/**
 * Modelo para Usuario
 */

public class Usuario {

    public Usuario(String nomeUsuario, String urlFoto) {
        this.nomeUsuario = nomeUsuario;
        this.urlFoto = urlFoto;
    }

    public Usuario() {

    }
    public List<String> getProjetos() {
        return projetos;
    }

    public void setProjetos(List<String> projetos) {
        this.projetos = projetos;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    private List<String> projetos;
    private String nomeUsuario;
    private String urlFoto;
}
