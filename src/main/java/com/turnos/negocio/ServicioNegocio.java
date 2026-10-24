package com.turnos.negocio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.turnos.dao.ClienteDAOImpl;
import com.turnos.dao.IClienteDAO;
import com.turnos.dao.IServicioDAO;
import com.turnos.dao.ITurnoDAO;
import com.turnos.dao.ServicioDAOImpl;
import com.turnos.dao.TurnoDAOImpl;
import com.turnos.estructuras.BST;
import com.turnos.estructuras.ColaEnlazada;
import com.turnos.modelo.Cliente;
import com.turnos.modelo.ResumenServicio;
import com.turnos.modelo.Servicio;
import com.turnos.modelo.Turno;

public class ServicioNegocio {

    private final BST clientes;
    private final ColaEnlazada<Turno> colaTurnos;
    private final IClienteDAO clienteDAO;
    private final IServicioDAO servicioDAO;
    private final ITurnoDAO turnoDAO;

    public ServicioNegocio() throws SQLException {
        this(new ClienteDAOImpl(), new ServicioDAOImpl(), new TurnoDAOImpl());
    }

    public ServicioNegocio(IClienteDAO clienteDAO, IServicioDAO servicioDAO,
                           ITurnoDAO turnoDAO) throws SQLException {
        this.clienteDAO = Objects.requireNonNull(clienteDAO, "El DAO de clientes es obligatorio");
        this.servicioDAO = Objects.requireNonNull(servicioDAO, "El DAO de servicios es obligatorio");
        this.turnoDAO = Objects.requireNonNull(turnoDAO, "El DAO de turnos es obligatorio");
        this.clientes = new BST();
        this.colaTurnos = new ColaEnlazada<>();
        cargarDatosIniciales();
    }

    public Cliente registrarCliente(Cliente cliente) throws SQLException {
        Objects.requireNonNull(cliente, "El cliente es obligatorio");
        if (clientes.buscar(cliente.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese código");
        }

        clienteDAO.crear(cliente);
        clientes.insertar(cliente);
        return cliente;
    }

    public Cliente buscarCliente(int codigo) {
        return clientes.buscar(codigo);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes.recorridoInorden());
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        Objects.requireNonNull(cliente, "El cliente es obligatorio");
        Cliente clienteActual = exigirCliente(cliente.getCodigo());

        clienteDAO.actualizar(cliente);
        clienteActual.setNombre(cliente.getNombre());
        clienteActual.setTelefono(cliente.getTelefono());
        clienteActual.setEstado(cliente.getEstado());
    }

    public void cambiarEstadoCliente(int codigo, String estado) throws SQLException {
        Cliente cliente = exigirCliente(codigo);
        if ("Inactivo".equals(estado) && tieneTurnoPendiente(codigo)) {
            throw new IllegalStateException(
                    "No se puede desactivar un cliente con turno pendiente");
        }

        clienteDAO.cambiarEstado(codigo, estado);
        cliente.setEstado(estado);
    }

    public Servicio registrarServicio(Servicio servicio) throws SQLException {
        Objects.requireNonNull(servicio, "El servicio es obligatorio");
        validarServicio(servicio);
        if (servicioDAO.buscarPorCodigo(servicio.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un servicio con ese código");
        }

        servicioDAO.crear(servicio);
        return servicio;
    }

    public Servicio buscarServicio(int codigo) throws SQLException {
        return servicioDAO.buscarPorCodigo(codigo).orElse(null);
    }

    public List<Servicio> getServicios() throws SQLException {
        return servicioDAO.leerTodos();
    }

    public List<Servicio> getServiciosActivos() throws SQLException {
        return servicioDAO.leerActivos();
    }

    public void actualizarServicio(Servicio servicio) throws SQLException {
        Objects.requireNonNull(servicio, "El servicio es obligatorio");
        validarServicio(servicio);
        exigirServicio(servicio.getCodigo());
        servicioDAO.actualizar(servicio);
    }

    public void cambiarEstadoServicio(int codigo, String estado) throws SQLException {
        validarEstadoServicio(estado);
        exigirServicio(codigo);
        if ("Inactivo".equals(estado) && turnoDAO.tieneTurnoPendientePorServicio(codigo)) {
            throw new IllegalStateException(
                    "No se puede desactivar un servicio con turnos pendientes");
        }
        servicioDAO.cambiarEstado(codigo, estado);
    }

    public Turno generarTurno(int codigoCliente, int codigoServicio) throws SQLException {
        Cliente cliente = exigirCliente(codigoCliente);
        Servicio servicio = exigirServicio(codigoServicio);

        validarActivo(cliente.getEstado(), "El cliente está inactivo");
        validarActivo(servicio.getEstado(), "El servicio está inactivo");
        if (tieneTurnoPendiente(codigoCliente)) {
            throw new IllegalStateException("El cliente ya tiene un turno pendiente");
        }

        Turno turno = new Turno(
                0,
                codigoCliente,
                codigoServicio,
                LocalDateTime.now().toString(),
                "PENDIENTE",
                null);
        Turno turnoGuardado = turnoDAO.crear(turno);
        colaTurnos.encolar(turnoGuardado);
        return turnoGuardado;
    }

    public Turno atenderSiguienteTurno() throws SQLException {
        Turno turno = colaTurnos.consultarPrimero();
        if (turno == null) {
            return null;
        }

        String fechaAtencion = LocalDateTime.now().toString();
        turnoDAO.actualizarEstado(turno.getNumero(), "ATENDIDO", fechaAtencion);
        colaTurnos.desencolar();
        turno.setEstado("ATENDIDO");
        turno.setFechaAtencion(fechaAtencion);
        return turno;
    }

    public boolean cancelarTurno(int numero) throws SQLException {
        Turno turno = buscarEnCola(numero);
        if (turno == null) {
            return false;
        }

        turnoDAO.actualizarEstado(numero, "CANCELADO", turno.getFechaAtencion());
        boolean retirado = colaTurnos.cancelarPorNumero(numero);
        if (retirado) {
            turno.setEstado("CANCELADO");
        }
        return retirado;
    }

    public List<Turno> getTurnosPendientes() {
        return new ArrayList<>(colaTurnos.recorrer());
    }

    public List<Turno> getTurnosAtendidos() throws SQLException {
        return filtrarPorEstado(turnoDAO.leerTodos(), "ATENDIDO");
    }

    public List<Turno> getTurnosCancelados() throws SQLException {
        return filtrarPorEstado(turnoDAO.leerTodos(), "CANCELADO");
    }

    public List<Turno> getHistorialCliente(int codigoCliente) throws SQLException {
        return turnoDAO.buscarPorCliente(codigoCliente);
    }

    public List<ResumenServicio> getResumenPorServicio() throws SQLException {
        return getResumenPorServicio(null, null, null, null);
    }

    public List<ResumenServicio> getResumenPorServicio(LocalDate desde, LocalDate hasta,
                                                        Integer codigoServicio, String estado)
            throws SQLException {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final");
        }
        if (estado != null && !List.of("PENDIENTE", "ATENDIDO", "CANCELADO").contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado debe ser PENDIENTE, ATENDIDO o CANCELADO");
        }
        if (codigoServicio != null) {
            exigirServicio(codigoServicio);
        }
        return turnoDAO.resumenPorServicio(desde, hasta, codigoServicio, estado);
    }

    public void cerrar() {
        // Los DAOs usan conexiones por operación y las cierran automáticamente.
    }

    private void cargarDatosIniciales() throws SQLException {
        for (Cliente cliente : clienteDAO.leerTodos()) {
            clientes.insertar(cliente);
        }
        for (Turno turno : turnoDAO.leerTodos()) {
            if ("PENDIENTE".equals(turno.getEstado())) {
                colaTurnos.encolar(turno);
            }
        }
    }

    private Cliente exigirCliente(int codigo) {
        Cliente cliente = clientes.buscar(codigo);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con código " + codigo);
        }
        return cliente;
    }

    private Servicio exigirServicio(int codigo) throws SQLException {
        return servicioDAO.buscarPorCodigo(codigo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un servicio con código " + codigo));
    }

    private void validarServicio(Servicio servicio) {
        if (servicio.getCodigo() <= 0) {
            throw new IllegalArgumentException("El código del servicio debe ser mayor que cero");
        }
        if (servicio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio no puede estar vacío");
        }
        validarEstadoServicio(servicio.getEstado());
    }

    private void validarEstadoServicio(String estado) {
        if (!"Activo".equals(estado) && !"Inactivo".equals(estado)) {
            throw new IllegalArgumentException("El estado debe ser Activo o Inactivo");
        }
    }

    private boolean tieneTurnoPendiente(int codigoCliente) {
        for (Turno turno : colaTurnos.recorrer()) {
            if (turno.getCodigoCliente() == codigoCliente) {
                return true;
            }
        }
        return false;
    }

    private Turno buscarEnCola(int numero) {
        for (Turno turno : colaTurnos.recorrer()) {
            if (turno.getNumero() == numero) {
                return turno;
            }
        }
        return null;
    }

    private List<Turno> filtrarPorEstado(List<Turno> turnos, String estado) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno turno : turnos) {
            if (estado.equals(turno.getEstado())) {
                resultado.add(turno);
            }
        }
        return resultado;
    }

    private void validarActivo(String estado, String mensaje) {
        if (!"Activo".equals(estado)) {
            throw new IllegalStateException(mensaje);
        }
    }
}
