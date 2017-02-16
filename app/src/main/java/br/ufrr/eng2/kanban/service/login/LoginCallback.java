package br.ufrr.eng2.kanban.service.login;

/**
 * Created by edwino on 15/02/17.
 */

public interface LoginCallback {
    public void onLoginError(Object info, int errorCode, LoginInterface loginInterface);
    public void onSuccess(Object info, LoginInterface loginInterface);
}
