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

    public Tarefa(String nomeTarefa, String descricaoTarefa, int estadoTarefa, String timestampEstimative, int categoriaTarefa) {
        this.nomeTarefa = nomeTarefa;
        this.descricaoTarefa = descricaoTarefa;
        this.estadoTarefa = estadoTarefa;
        this.timestampEstimative = timestampEstimative;
        this.categoriaTarefa = categoriaTarefa;
    }

    public Tarefa() {

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

    public String gettimestampEstimative() {
        return timestampEstimative;
    }

    public void settimestampEstimative(String timestampEstimative) {
        this.timestampEstimative = timestampEstimative;
    }

    public int getCategoriaTarefa() {
        return categoriaTarefa;
    }

    public void setCategoriaTarefa(int categoriaTarefa) {
        this.categoriaTarefa = categoriaTarefa;
    }

    private String nomeTarefa;
    private String descricaoTarefa;
    private int estadoTarefa;
    private String timestampEstimative;
    private int categoriaTarefa;

    public String getTimestampCreation() {
        return timestampCreation;
    }

    public void setTimestampCreation(String timestampCreation) {
        this.timestampCreation = timestampCreation;
    }

    public String getTimestampDone() {
        return timestampDone;
    }

    public void setTimestampDone(String timestampDone) {
        this.timestampDone = timestampDone;
    }

    private String timestampCreation;
    private String timestampDone;

    public String getOwnedId() {
        return ownedId;
    }

    public void setOwnedId(String ownedId) {
        this.ownedId = ownedId;
    }

    private String ownedId;


}
