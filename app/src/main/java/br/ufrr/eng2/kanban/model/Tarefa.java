package br.ufrr.eng2.kanban.model;

/**
 * Modelo para Tarefa
 */



public class Tarefa {
    static public final int ESTADO_DOING = 2320;
    static public final int ESTADO_DONE  = 2321;
    static public final int ESTADO_TODO  = 2322;


    public Tarefa(int uuid, String nomeTarefa, String descricaoTarefa, int estadoTarefa) {
        this.uuid = uuid;
        this.nomeTarefa = nomeTarefa;
        this.descricaoTarefa = descricaoTarefa;
        this.estadoTarefa = estadoTarefa;
    }

    private int uuid;
    private String nomeTarefa;
    private String descricaoTarefa;
    private int estadoTarefa;


    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getNomeTarefa() {
        return nomeTarefa;
    }

    public void setNomeTarefa(String nomeTarefa) {
        this.nomeTarefa = nomeTarefa;
    }

    public String getDescricaoTarefa() {
        return descricaoTarefa;
    }

    public void setDescricaoTarefa(String descricaoTarefa) {
        this.descricaoTarefa = descricaoTarefa;
    }

    public int getEstadoTarefa() {
        return estadoTarefa;
    }

    public void setEstadoTarefa(int estadoTarefa) {
        this.estadoTarefa = estadoTarefa;
    }
}
