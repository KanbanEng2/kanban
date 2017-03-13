package br.ufrr.eng2.kanban.model;

import java.util.List;

/**
 * Modelo para Projeto
 */

public class Projeto {


    public Projeto() {
    }


    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public List<String> getMembrosProjeto() {
        return membrosProjeto;
    }

    public void setMembrosProjeto(List<String> membrosProjeto) {
        this.membrosProjeto = membrosProjeto;
    }

    public List<Tarefa> getTarefasProjeto() {
        return tarefasProjeto;
    }

    public void setTarefasProjeto(List<Tarefa> tarefasProjeto) {
        this.tarefasProjeto = tarefasProjeto;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    private String nomeProjeto;
    private List<String> membrosProjeto;
    private List<Tarefa> tarefasProjeto;
    private String ownerUuid;

    public Projeto(String nomeProjeto, List<String> membrosProjeto, List<Tarefa> tarefasProjeto, String ownerUuid) {
        this.nomeProjeto = nomeProjeto;
        this.membrosProjeto = membrosProjeto;
        this.tarefasProjeto = tarefasProjeto;
        this.ownerUuid = ownerUuid;
    }
}
