package br.ufrr.eng2.kanban.model;

import java.util.Date;

/**
 * Modelo para Tarefa
 */



public class Tarefa {
    static public final int ESTADO_DOING = 2320;
    static public final int ESTADO_DONE  = 2321;
    static public final int ESTADO_TODO  = 2322;

    static public final int CATEGORIA_ANALISE = 2330;
    static public final int CATEGORIA_DESENVOLVIMENTO = 2331;
    static public final int CATEGORIA_CORRECAO = 2332;

    private int uuid;
    private String nomeTarefa;
    private String descricaoTarefa;
    private int estadoTarefa;

    public Date getEncerramentoTarefa() {
        return encerramentoTarefa;
    }

    public void setEncerramentoTarefa(Date encerramentoTarefa) {
        this.encerramentoTarefa = encerramentoTarefa;
    }

    private Date encerramentoTarefa;

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

    public int getCategoriaTarefa() {
        return categoriaTarefa;
    }

    public void setCategoriaTarefa(int categoriaTarefa) {
        this.categoriaTarefa = categoriaTarefa;
    }

    public Tarefa(int uuid, String nomeTarefa, String descricaoTarefa, int estadoTarefa, int categoriaTarefa) {

        this.uuid = uuid;
        this.nomeTarefa = nomeTarefa;
        this.descricaoTarefa = descricaoTarefa;
        this.estadoTarefa = estadoTarefa;
        this.categoriaTarefa = categoriaTarefa;
    }

    public Tarefa(int uuid, String nomeTarefa, String descricaoTarefa, int estadoTarefa, int categoriaTarefa, Date encerramentoTarefa) {

        this.uuid = uuid;
        this.nomeTarefa = nomeTarefa;
        this.descricaoTarefa = descricaoTarefa;
        this.estadoTarefa = estadoTarefa;
        this.categoriaTarefa = categoriaTarefa;
        this.encerramentoTarefa = encerramentoTarefa;
    }

    private int categoriaTarefa;


}
