package pe.edu.upeu.fitlab_final.implementacion;

import pe.edu.upeu.fitlab_final.interfaces.IMembresiaDao;
import pe.edu.upeu.fitlab_final.modelo.Cliente;
import pe.edu.upeu.fitlab_final.modelo.Entrenador;
import pe.edu.upeu.fitlab_final.modelo.Membresia;
import pe.edu.upeu.fitlab_final.modelo.Plan;
import pe.edu.upeu.fitlab_final.utils.ConexionDB;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MembresiaDaoImpl implements IMembresiaDao {
    private static final String CONSULTA = "SELECT m.*,c.nombre,c.apellidos,c.dni,c.edad,c.genero,c.estudiante,c.alergias,p.nombre AS plan_nombre,p.precio,p.duracionDias,e.identrenador AS ent_identrenador,e.nombre AS ent_nombre,e.apellidos AS ent_apellidos,e.dni AS ent_dni,e.especialidad AS ent_especialidad,e.turno AS ent_turno FROM membresias m JOIN clientes c ON c.idcliente=m.idcliente JOIN planes p ON p.idplan=m.idplan LEFT JOIN entrenadores e ON e.identrenador=m.identrenador";

    @Override
    public boolean insert(Membresia e) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = prepararRegistro(cn, e);
                if (actual == null) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (!aforoDisponible(cn, actual)) return flag;
                if (contarComprobantes(cn) >= 10000) return flag;
                String query = "INSERT INTO membresias(idcliente,idplan,identrenador,horario,tipoDias,fechaInicio,fechaVencimiento,activa,renovaciones,idusuario) VALUES(?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setInt(1, actual.getCliente().getIdcliente());
                    st.setInt(2, actual.getPlan().getIdplan());
                    if (actual.getEntrenador() == null) st.setNull(3, Types.INTEGER);
                    else st.setInt(3, actual.getEntrenador().getIdentrenador());
                    st.setString(4, actual.getHorario());
                    st.setString(5, actual.getTipoDias());
                    st.setString(6, actual.getFechaInicio().toString());
                    st.setString(7, actual.getFechaVencimiento().toString());
                    st.setBoolean(8, actual.isActiva());
                    st.setInt(9, actual.getRenovaciones());
                    st.setInt(10, actual.getIdUsuario());
                    st.executeUpdate();
                }
                try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) actual.setIdmembresia(rs.getInt(1));
                }
                generarComprobante(cn, actual, "REGISTRO");
                cn.commit();
                flag = true;
                e.setIdmembresia(actual.getIdmembresia());
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al registrar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean update(Membresia e) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                actual.setHorario(e.getHorario());
                actual.setTipoDias(e.getTipoDias());
                actual.setEntrenador(e.getEntrenador());
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (actual.isActiva() && !aforoDisponible(cn, actual)) return flag;
                String query = "UPDATE membresias SET horario=?,tipoDias=?,identrenador=?,idusuario=? WHERE idmembresia=?";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setString(1, actual.getHorario());
                    st.setString(2, actual.getTipoDias());
                    if (actual.getEntrenador() == null) st.setNull(3, Types.INTEGER);
                    else st.setInt(3, actual.getEntrenador().getIdentrenador());
                    st.setInt(4, actual.getIdUsuario());
                    st.setInt(5, actual.getIdmembresia());
                    st.executeUpdate();
                }
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al actualizar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean cancelar(Membresia e) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null || !actual.isActiva()) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                actual.setActiva(false);
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                String query = "UPDATE membresias SET activa=?,idusuario=? WHERE idmembresia=?";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setBoolean(1, actual.isActiva());
                    st.setInt(2, actual.getIdUsuario());
                    st.setInt(3, actual.getIdmembresia());
                    st.executeUpdate();
                }
                anularComprobante(cn, actual.getCliente().getIdcliente());
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al cancelar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean reactivar(Membresia e, boolean conservarVigencia) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null || actual.isActiva()) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                if (conservarVigencia) {
                    if (actual.getFechaVencimiento().isBefore(LocalDate.now())) return flag;
                    actual.setActiva(true);
                } else {
                    iniciarPeriodo(actual);
                }
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (!aforoDisponible(cn, actual)) return flag;
                if (contarComprobantes(cn) >= 10000) return flag;
                String query;
                if (conservarVigencia) {
                    query = "UPDATE membresias SET activa=?,idusuario=? WHERE idmembresia=?";
                } else {
                    query = "UPDATE membresias SET fechaInicio=?,fechaVencimiento=?,activa=?,idusuario=? WHERE idmembresia=?";
                }
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    if (conservarVigencia) {
                        st.setBoolean(1, actual.isActiva());
                        st.setInt(2, actual.getIdUsuario());
                        st.setInt(3, actual.getIdmembresia());
                    } else {
                        st.setString(1, actual.getFechaInicio().toString());
                        st.setString(2, actual.getFechaVencimiento().toString());
                        st.setBoolean(3, actual.isActiva());
                        st.setInt(4, actual.getIdUsuario());
                        st.setInt(5, actual.getIdmembresia());
                    }
                    st.executeUpdate();
                }
                generarComprobante(cn, actual, "REACTIVACION");
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al reactivar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean nuevaMembresia(Membresia e) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null || (actual.isActiva() && !actual.getFechaVencimiento().isBefore(LocalDate.now())) || e.getPlan() == null) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                Plan plan = buscarPlan(cn, e.getPlan().getIdplan());
                if (plan == null) return flag;
                actual.setPlan(plan);
                actual.setHorario(e.getHorario());
                actual.setTipoDias(e.getTipoDias());
                actual.setEntrenador(e.getEntrenador());
                actual.setRenovaciones(0);
                iniciarPeriodo(actual);
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (!aforoDisponible(cn, actual)) return flag;
                if (contarComprobantes(cn) >= 10000) return flag;
                String query = "UPDATE membresias SET idplan=?,identrenador=?,horario=?,tipoDias=?,fechaInicio=?,fechaVencimiento=?,activa=?,renovaciones=?,idusuario=? WHERE idmembresia=?";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setInt(1, actual.getPlan().getIdplan());
                    if (actual.getEntrenador() == null) st.setNull(2, Types.INTEGER);
                    else st.setInt(2, actual.getEntrenador().getIdentrenador());
                    st.setString(3, actual.getHorario());
                    st.setString(4, actual.getTipoDias());
                    st.setString(5, actual.getFechaInicio().toString());
                    st.setString(6, actual.getFechaVencimiento().toString());
                    st.setBoolean(7, actual.isActiva());
                    st.setInt(8, actual.getRenovaciones());
                    st.setInt(9, actual.getIdUsuario());
                    st.setInt(10, actual.getIdmembresia());
                    st.executeUpdate();
                }
                generarComprobante(cn, actual, "REGISTRO");
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al adquirir nueva membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean renovar(Membresia e) {
        boolean flag = false;
        if (e == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null || !actual.isActiva()) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                if (ChronoUnit.DAYS.between(LocalDate.now(), actual.getFechaVencimiento()) > 3) return flag;
                iniciarPeriodo(actual);
                actual.setRenovaciones(actual.getRenovaciones() + 1);
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (!aforoDisponible(cn, actual)) return flag;
                if (contarComprobantes(cn) >= 10000) return flag;
                String query = "UPDATE membresias SET fechaInicio=?,fechaVencimiento=?,activa=?,renovaciones=?,idusuario=? WHERE idmembresia=?";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setString(1, actual.getFechaInicio().toString());
                    st.setString(2, actual.getFechaVencimiento().toString());
                    st.setBoolean(3, actual.isActiva());
                    st.setInt(4, actual.getRenovaciones());
                    st.setInt(5, actual.getIdUsuario());
                    st.setInt(6, actual.getIdmembresia());
                    st.executeUpdate();
                }
                generarComprobante(cn, actual, "RENOVACION");
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al renovar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public boolean migrarPlan(Membresia e, Plan plan) {
        boolean flag = false;
        if (e == null || plan == null) return flag;
        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Membresia actual = buscar(cn, e.getIdmembresia());
                if (actual == null || !actual.isActiva()) return flag;
                actual.setIdUsuario(e.getIdUsuario());
                Plan nuevoPlan = buscarPlan(cn, plan.getIdplan());
                if (nuevoPlan == null || nuevoPlan.getIdplan() == actual.getPlan().getIdplan()) return flag;
                actual.setPlan(nuevoPlan);
                if (actual.getDiasAsistencia().isEmpty()) return flag;
                if (!entrenadorCompatible(cn, actual)) return flag;
                if (!aforoDisponible(cn, actual)) return flag;
                if (contarComprobantes(cn) >= 10000) return flag;
                String query = "UPDATE membresias SET idplan=?,idusuario=? WHERE idmembresia=?";
                try (PreparedStatement st = cn.prepareStatement(query)) {
                    st.setInt(1, actual.getPlan().getIdplan());
                    st.setInt(2, actual.getIdUsuario());
                    st.setInt(3, actual.getIdmembresia());
                    st.executeUpdate();
                }
                generarComprobante(cn, actual, "MIGRACION");
                cn.commit();
                flag = true;
            } finally {
                if (!flag) cn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al migrar membresía: " + ex.getMessage());
        }
        return flag;
    }

    @Override
    public Membresia searchById(int id) {
        Membresia membresia = null;
        String query = CONSULTA + " WHERE m.idmembresia=?";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                    Plan plan = new Plan();
                    plan.setIdplan(rs.getInt("idplan"));
                    plan.setNombre(rs.getString("plan_nombre"));
                    plan.setPrecio(rs.getDouble("precio"));
                    plan.setDuracionDias(rs.getInt("duracionDias"));
                    Entrenador entrenador = null;
                    if (rs.getObject("ent_identrenador") != null) {
                        entrenador = new Entrenador();
                        entrenador.setIdentrenador(rs.getInt("ent_identrenador"));
                        entrenador.setNombre(rs.getString("ent_nombre"));
                        entrenador.setApellidos(rs.getString("ent_apellidos"));
                        entrenador.setDni(rs.getString("ent_dni"));
                        entrenador.setEspecialidad(rs.getString("ent_especialidad"));
                        entrenador.setTurno(rs.getString("ent_turno"));
                    }
                    membresia = new Membresia();
                    membresia.setIdmembresia(rs.getInt("idmembresia"));
                    membresia.setCliente(cliente);
                    membresia.setPlan(plan);
                    membresia.setEntrenador(entrenador);
                    membresia.setHorario(rs.getString("horario"));
                    membresia.setTipoDias(rs.getString("tipoDias"));
                    membresia.setFechaInicio(LocalDate.parse(rs.getString("fechaInicio")));
                    membresia.setFechaVencimiento(LocalDate.parse(rs.getString("fechaVencimiento")));
                    membresia.setActiva(rs.getBoolean("activa"));
                    membresia.setRenovaciones(rs.getInt("renovaciones"));
                    membresia.setIdUsuario(rs.getInt("idusuario"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar membresía por id: " + ex.getMessage());
        }
        return membresia;
    }

    @Override
    public Membresia searchByCliente(int idcliente) {
        Membresia membresia = null;
        String query = CONSULTA + " WHERE m.idcliente=?";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idcliente);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                    Plan plan = new Plan();
                    plan.setIdplan(rs.getInt("idplan"));
                    plan.setNombre(rs.getString("plan_nombre"));
                    plan.setPrecio(rs.getDouble("precio"));
                    plan.setDuracionDias(rs.getInt("duracionDias"));
                    Entrenador entrenador = null;
                    if (rs.getObject("ent_identrenador") != null) {
                        entrenador = new Entrenador();
                        entrenador.setIdentrenador(rs.getInt("ent_identrenador"));
                        entrenador.setNombre(rs.getString("ent_nombre"));
                        entrenador.setApellidos(rs.getString("ent_apellidos"));
                        entrenador.setDni(rs.getString("ent_dni"));
                        entrenador.setEspecialidad(rs.getString("ent_especialidad"));
                        entrenador.setTurno(rs.getString("ent_turno"));
                    }
                    membresia = new Membresia();
                    membresia.setIdmembresia(rs.getInt("idmembresia"));
                    membresia.setCliente(cliente);
                    membresia.setPlan(plan);
                    membresia.setEntrenador(entrenador);
                    membresia.setHorario(rs.getString("horario"));
                    membresia.setTipoDias(rs.getString("tipoDias"));
                    membresia.setFechaInicio(LocalDate.parse(rs.getString("fechaInicio")));
                    membresia.setFechaVencimiento(LocalDate.parse(rs.getString("fechaVencimiento")));
                    membresia.setActiva(rs.getBoolean("activa"));
                    membresia.setRenovaciones(rs.getInt("renovaciones"));
                    membresia.setIdUsuario(rs.getInt("idusuario"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar membresía por cliente: " + ex.getMessage());
        }
        return membresia;
    }

    @Override
    public List<Membresia> listAll() {
        List<Membresia> lista = new ArrayList<>();
        String query = CONSULTA + " ORDER BY m.idmembresia";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement st = cn.prepareStatement(query); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Membresia membresia;
                Cliente cliente = new Cliente();
                cliente.setIdcliente(rs.getInt("idcliente"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellidos(rs.getString("apellidos"));
                cliente.setDni(rs.getString("dni"));
                cliente.setEdad(rs.getInt("edad"));
                cliente.setGenero(rs.getString("genero"));
                cliente.setEstudiante(rs.getBoolean("estudiante"));
                cliente.setAlergias(rs.getString("alergias"));
                Plan plan = new Plan();
                plan.setIdplan(rs.getInt("idplan"));
                plan.setNombre(rs.getString("plan_nombre"));
                plan.setPrecio(rs.getDouble("precio"));
                plan.setDuracionDias(rs.getInt("duracionDias"));
                Entrenador entrenador = null;
                if (rs.getObject("ent_identrenador") != null) {
                    entrenador = new Entrenador();
                    entrenador.setIdentrenador(rs.getInt("ent_identrenador"));
                    entrenador.setNombre(rs.getString("ent_nombre"));
                    entrenador.setApellidos(rs.getString("ent_apellidos"));
                    entrenador.setDni(rs.getString("ent_dni"));
                    entrenador.setEspecialidad(rs.getString("ent_especialidad"));
                    entrenador.setTurno(rs.getString("ent_turno"));
                }
                membresia = new Membresia();
                membresia.setIdmembresia(rs.getInt("idmembresia"));
                membresia.setCliente(cliente);
                membresia.setPlan(plan);
                membresia.setEntrenador(entrenador);
                membresia.setHorario(rs.getString("horario"));
                membresia.setTipoDias(rs.getString("tipoDias"));
                membresia.setFechaInicio(LocalDate.parse(rs.getString("fechaInicio")));
                membresia.setFechaVencimiento(LocalDate.parse(rs.getString("fechaVencimiento")));
                membresia.setActiva(rs.getBoolean("activa"));
                membresia.setRenovaciones(rs.getInt("renovaciones"));
                membresia.setIdUsuario(rs.getInt("idusuario"));
                lista.add(membresia);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar membresías: " + ex.getMessage());
        }
        return lista;
    }

    private Membresia buscar(Connection cn, int id) throws SQLException {
        Membresia membresia = null;
        String query = CONSULTA + " WHERE m.idmembresia=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                    Plan plan = new Plan();
                    plan.setIdplan(rs.getInt("idplan"));
                    plan.setNombre(rs.getString("plan_nombre"));
                    plan.setPrecio(rs.getDouble("precio"));
                    plan.setDuracionDias(rs.getInt("duracionDias"));
                    Entrenador entrenador = null;
                    if (rs.getObject("ent_identrenador") != null) {
                        entrenador = new Entrenador();
                        entrenador.setIdentrenador(rs.getInt("ent_identrenador"));
                        entrenador.setNombre(rs.getString("ent_nombre"));
                        entrenador.setApellidos(rs.getString("ent_apellidos"));
                        entrenador.setDni(rs.getString("ent_dni"));
                        entrenador.setEspecialidad(rs.getString("ent_especialidad"));
                        entrenador.setTurno(rs.getString("ent_turno"));
                    }
                    membresia = new Membresia();
                    membresia.setIdmembresia(rs.getInt("idmembresia"));
                    membresia.setCliente(cliente);
                    membresia.setPlan(plan);
                    membresia.setEntrenador(entrenador);
                    membresia.setHorario(rs.getString("horario"));
                    membresia.setTipoDias(rs.getString("tipoDias"));
                    membresia.setFechaInicio(LocalDate.parse(rs.getString("fechaInicio")));
                    membresia.setFechaVencimiento(LocalDate.parse(rs.getString("fechaVencimiento")));
                    membresia.setActiva(rs.getBoolean("activa"));
                    membresia.setRenovaciones(rs.getInt("renovaciones"));
                    membresia.setIdUsuario(rs.getInt("idusuario"));
                }
            }
        }
        return membresia;
    }

    private Membresia prepararRegistro(Connection cn, Membresia solicitud) throws SQLException {
        if (solicitud.getCliente() == null || solicitud.getPlan() == null) return null;
        Cliente cliente = null;
        String query = "SELECT * FROM clientes WHERE idcliente=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, solicitud.getCliente().getIdcliente());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setIdcliente(rs.getInt("idcliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellidos(rs.getString("apellidos"));
                    cliente.setDni(rs.getString("dni"));
                    cliente.setEdad(rs.getInt("edad"));
                    cliente.setGenero(rs.getString("genero"));
                    cliente.setEstudiante(rs.getBoolean("estudiante"));
                    cliente.setAlergias(rs.getString("alergias"));
                }
            }
        }
        if (cliente == null) return null;
        Plan plan = buscarPlan(cn, solicitud.getPlan().getIdplan());
        if (plan == null) return null;
        Membresia actual = new Membresia();
        actual.setCliente(cliente);
        actual.setPlan(plan);
        actual.setEntrenador(solicitud.getEntrenador());
        actual.setHorario(solicitud.getHorario());
        actual.setTipoDias(solicitud.getTipoDias());
        actual.setRenovaciones(0);
        iniciarPeriodo(actual);
        return actual;
    }

    private Plan buscarPlan(Connection cn, int idplan) throws SQLException {
        Plan plan = null;
        String query = "SELECT * FROM planes WHERE idplan=?";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idplan);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    plan = new Plan();
                    plan.setIdplan(rs.getInt("idplan"));
                    plan.setNombre(rs.getString("nombre"));
                    plan.setPrecio(rs.getDouble("precio"));
                    plan.setDuracionDias(rs.getInt("duracionDias"));
                }
            }
        }
        return plan;
    }

    private void iniciarPeriodo(Membresia e) {
        LocalDate hoy = LocalDate.now();
        e.setFechaInicio(hoy);
        e.setFechaVencimiento(hoy.plusDays(e.getPlan().getDuracionDias()));
        e.setActiva(true);
    }

    private boolean aforoDisponible(Connection cn, Membresia e) throws SQLException {
        String query = "SELECT COUNT(*) FROM membresias WHERE activa=1 AND horario=? AND idmembresia<>? AND (tipoDias='TODOS' OR instr('-'||tipoDias||'-','-'||?||'-')>0)";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, e.getHorario());
            st.setInt(2, e.getIdmembresia());
            for (String dia : e.getDiasAsistencia()) {
                st.setString(3, dia);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) >= 50) return false;
                }
            }
            return true;
        }
    }

    private boolean entrenadorCompatible(Connection cn, Membresia e) throws SQLException {
        if (e.getEntrenador() == null) return true;
        try (PreparedStatement st = cn.prepareStatement("SELECT turno FROM entrenadores WHERE identrenador=?")) {
            st.setInt(1, e.getEntrenador().getIdentrenador());
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getString("turno").equals(e.getHorario());
            }
        }
    }

    private int contarComprobantes(Connection cn) throws SQLException {
        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM comprobantes")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void anularComprobante(Connection cn, int idcliente) throws SQLException {
        String query = "UPDATE comprobantes SET estado='Anulado' WHERE idcomprobante=(SELECT MAX(idcomprobante) FROM comprobantes WHERE idcliente=?) AND estado='Enviado'";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setInt(1, idcliente);
            st.executeUpdate();
        }
    }

    private void generarComprobante(Connection cn, Membresia e, String tipo) throws SQLException {
        long siguiente;
        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery("SELECT MAX(COALESCE((SELECT seq FROM sqlite_sequence WHERE name='comprobantes'),0),COALESCE(MAX(idcomprobante),0))+1 FROM comprobantes")) {
            siguiente = rs.next() ? rs.getLong(1) : 1;
        }
        double subtotal = e.getPlan().getPrecio();
        double descuento = e.getCliente().calcularDescuento(subtotal);
        String query = "INSERT INTO comprobantes(codigo,idcliente,tipo,estado,subtotal,descuento,total,fecha,idusuario) VALUES(?,?,?,'Enviado',?,?,?,?,?)";
        try (PreparedStatement st = cn.prepareStatement(query)) {
            st.setString(1, "FBL-" + (1000 + siguiente));
            st.setInt(2, e.getCliente().getIdcliente());
            st.setString(3, tipo);
            st.setDouble(4, subtotal);
            st.setDouble(5, descuento);
            st.setDouble(6, subtotal - descuento);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
            st.setString(7, LocalDateTime.now().format(formato));
            st.setInt(8, e.getIdUsuario());
            st.executeUpdate();
        }
    }

}
