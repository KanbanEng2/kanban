package br.ufrr.eng2.kanban.model;

/**
 * Modelo para Projeto
 */

public class Projeto {
    private int uuid;
    private String nomeProjeto;
    private int ownerUuid;

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public int getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(int ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public Projeto(int uuid, String nomeProjeto, int ownerUuid) {

        this.uuid = uuid;
        this.nomeProjeto = nomeProjeto;
        this.ownerUuid = ownerUuid;
    }
}
