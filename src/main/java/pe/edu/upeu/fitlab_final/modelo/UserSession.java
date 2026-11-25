package pe.edu.upeu.fitlab_final.modelo;

public class UserSession {
    private static UserSession instance;
    private Usuario userLogin;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Usuario getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(Usuario usuario) {
        this.userLogin = usuario;
    }

    public void cleanSession() {
        userLogin = null;
    }
}
