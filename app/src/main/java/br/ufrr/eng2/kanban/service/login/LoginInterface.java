package br.ufrr.eng2.kanban.service.login;

/**
 * Created by edwino on 15/02/17.
 */

public interface LoginInterface {
    public void signIn();
    public void fbRegistry(Object info);
    public int getProviderId();
}
