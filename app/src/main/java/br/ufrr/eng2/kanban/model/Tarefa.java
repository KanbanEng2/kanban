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

    public Tarefa(String nomeTarefa, String descricaoTarefa, int estadoTarefa, String encerramentoTarefa, int categoriaTarefa) {
        this.nomeTarefa = nomeTarefa;
        this.descricaoTarefa = descricaoTarefa;
        this.estadoTarefa = estadoTarefa;
        this.encerramentoTarefa = encerramentoTarefa;
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

    public String getEncerramentoTarefa() {
        return encerramentoTarefa;
    }

    public void setEncerramentoTarefa(String encerramentoTarefa) {
        this.encerramentoTarefa = encerramentoTarefa;
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
    private String encerramentoTarefa;
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

    public String getTimestampEstimate() {
        return timestampEstimate;
    }

    public void setTimestampEstimate(String timestampEstimate) {
        this.timestampEstimate = timestampEstimate;
    }

    private String timestampCreation;
    private String timestampDone;
    private String timestampEstimate;

    public String getOwnedId() {
        return ownedId;
    }

    public void setOwnedId(String ownedId) {
        this.ownedId = ownedId;
    }

    private String ownedId;


}
