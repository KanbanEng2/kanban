package br.ufrr.eng2.kanban.model;

/**
 * Modelo para Usuario
 */

public class Usuario {

    private int uuid;
    private String nomeUsuario;

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public int getTokenUuid() {
        return tokenUuid;
    }

    public void setTokenUuid(int tokenUuid) {
        this.tokenUuid = tokenUuid;
    }

    public Usuario(int uuid, String nomeUsuario, int tokenUuid) {

        this.uuid = uuid;
        this.nomeUsuario = nomeUsuario;
        this.tokenUuid = tokenUuid;
    }

    private int tokenUuid;

}
