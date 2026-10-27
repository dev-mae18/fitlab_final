package pe.edu.upeu.fitlab_final.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.upeu.fitlab_final.implementacion.ClienteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.ComprobanteDaoImpl;
import pe.edu.upeu.fitlab_final.implementacion.MembresiaDaoImpl;
import pe.edu.upeu.fitlab_final.interfaces.IClienteDao;
import pe.edu.upeu.fitlab_final.interfaces.IComprobanteDao;
import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Membresia;

public class HomeController {
    @FXML private Label lblClientes;
    @FXML private Label lblMembresias;
    @FXML private Label lblComprobantes;
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IMembresiaDao membresiaDao = new MembresiaDaoImpl();
    private final IComprobanteDao comprobanteDao = new ComprobanteDaoImpl();

    @FXML
    public void initialize() {
        lblClientes.setText(String.valueOf(clienteDao.listAll().size()));
        int activas = 0;
        for (Membresia membresia : membresiaDao.listAll()) {
            if (membresia.isActiva()) activas++;
        }
        lblMembresias.setText(String.valueOf(activas));
        lblComprobantes.setText(String.valueOf(comprobanteDao.listAll().size()));
    }
}
