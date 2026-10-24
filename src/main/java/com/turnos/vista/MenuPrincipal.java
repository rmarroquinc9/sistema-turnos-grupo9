package com.turnos.vista;

import com.turnos.modelo.Cliente;
import com.turnos.modelo.ResumenServicio;
import com.turnos.modelo.Servicio;
import com.turnos.modelo.Turno;
import com.turnos.negocio.ServicioNegocio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_LISTA_TURNOS =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner scanner;
    private final ServicioNegocio negocio;

    public MenuPrincipal() throws SQLException {
        scanner = new Scanner(System.in);
        negocio = new ServicioNegocio();
    }

    public void iniciar() {
        boolean salir = false;
        try {
            while (!salir) {
                mostrarMenuPrincipal();
                int opcion = leerEntero("Seleccione una opción: ");
                try {
                    switch (opcion) {
                        case 1 -> gestionarClientes();
                        case 2 -> gestionarServicios();
                        case 3 -> generarTurno();
                        case 4 -> atenderSiguienteTurno();
                        case 5 -> cancelarTurno();
                        case 6 -> mostrarTurnos("TURNOS PENDIENTES", negocio.getTurnosPendientes());
                        case 7 -> mostrarTurnos("TURNOS ATENDIDOS", negocio.getTurnosAtendidos());
                        case 8 -> mostrarTurnos("TURNOS CANCELADOS", negocio.getTurnosCancelados());
                        case 9 -> mostrarHistorialCliente();
                        case 10 -> mostrarResumenPorServicio();
                        case 11 -> salir = true;
                        default -> mostrarError("La opción debe estar entre 1 y 11.");
                    }
                } catch (SQLException | IllegalArgumentException | IllegalStateException exception) {
                    mostrarError(exception.getMessage());
                }
            }
        } finally {
            negocio.cerrar();
            scanner.close();
            System.out.println("Aplicación finalizada.");
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n=== SISTEMA DE TURNOS ===");
        System.out.println("1.  Gestión de clientes");
        System.out.println("2.  Gestión de servicios");
        System.out.println("3.  Generar turno");
        System.out.println("4.  Atender siguiente turno");
        System.out.println("5.  Cancelar turno");
        System.out.println("6.  Consultar turnos pendientes");
        System.out.println("7.  Consultar turnos atendidos");
        System.out.println("8.  Consultar turnos cancelados");
        System.out.println("9.  Historial de un cliente");
        System.out.println("10. Resumen de servicios");
        System.out.println("11. Salir");
    }

    private void gestionarClientes() throws SQLException {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Registrar");
            System.out.println("2. Consultar todos");
            System.out.println("3. Buscar por código");
            System.out.println("4. Actualizar");
            System.out.println("5. Activar/desactivar");
            System.out.println("6. Regresar");

            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarCliente();
                    case 2 -> mostrarClientes(negocio.getClientes());
                    case 3 -> buscarCliente();
                    case 4 -> actualizarCliente();
                    case 5 -> cambiarEstadoCliente();
                    case 6 -> regresar = true;
                    default -> mostrarError("La opción debe estar entre 1 y 6.");
                }
            } catch (SQLException | IllegalArgumentException | IllegalStateException exception) {
                mostrarError(exception.getMessage());
            }
        }
    }

    private void gestionarServicios() throws SQLException {
        boolean regresar = false;
        while (!regresar) {
            System.out.println("\n--- GESTIÓN DE SERVICIOS ---");
            System.out.println("1. Registrar");
            System.out.println("2. Consultar todos");
            System.out.println("3. Buscar por código");
            System.out.println("4. Actualizar");
            System.out.println("5. Activar/desactivar");
            System.out.println("6. Regresar");

            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrarServicio();
                    case 2 -> mostrarServicios(negocio.getServicios());
                    case 3 -> buscarServicio();
                    case 4 -> actualizarServicio();
                    case 5 -> cambiarEstadoServicio();
                    case 6 -> regresar = true;
                    default -> mostrarError("La opción debe estar entre 1 y 6.");
                }
            } catch (SQLException | IllegalArgumentException | IllegalStateException exception) {
                mostrarError(exception.getMessage());
            }
        }
    }

    private void registrarCliente() throws SQLException {
        int codigo = leerEnteroPositivo("Código: ");
        Cliente cliente = new Cliente(
                codigo,
                leerTexto("Nombre: "),
                leerTelefono(),
                leerEstado());
        negocio.registrarCliente(cliente);
        System.out.println("Cliente registrado correctamente.");
    }

    private void buscarCliente() {
        Cliente cliente = negocio.buscarCliente(leerEnteroPositivo("Código: "));
        if (cliente == null) {
            mostrarError("No existe un cliente con ese código.");
        } else {
            System.out.println(cliente);
        }
    }

    private void actualizarCliente() throws SQLException {
        int codigo = leerEnteroPositivo("Código del cliente: ");
        Cliente actual = exigirCliente(codigo);
        Cliente actualizado = new Cliente(
                codigo,
                leerTexto("Nombre [" + actual.getNombre() + "]: "),
                leerTelefono(),
                leerEstado());
        negocio.actualizarCliente(actualizado);
        System.out.println("Cliente actualizado correctamente.");
    }

    private void cambiarEstadoCliente() throws SQLException {
        int codigo = leerEnteroPositivo("Código del cliente: ");
        exigirCliente(codigo);
        negocio.cambiarEstadoCliente(codigo, leerEstado());
        System.out.println("Estado del cliente actualizado.");
    }

    private void registrarServicio() throws SQLException {
        Servicio servicio = new Servicio(
                leerEnteroPositivo("Código: "),
                leerTexto("Nombre: "),
                leerEstado());
        negocio.registrarServicio(servicio);
        System.out.println("Servicio registrado correctamente.");
    }

    private void buscarServicio() throws SQLException {
        Servicio servicio = negocio.buscarServicio(leerEnteroPositivo("Código: "));
        if (servicio == null) {
            mostrarError("No existe un servicio con ese código.");
        } else {
            System.out.println(servicio);
        }
    }

    private void actualizarServicio() throws SQLException {
        int codigo = leerEnteroPositivo("Código del servicio: ");
        Servicio actual = exigirServicio(codigo);
        actual.setNombre(leerTexto("Nombre [" + actual.getNombre() + "]: "));
        actual.setEstado(leerEstado());
        negocio.actualizarServicio(actual);
        System.out.println("Servicio actualizado correctamente.");
    }

    private void cambiarEstadoServicio() throws SQLException {
        int codigo = leerEnteroPositivo("Código del servicio: ");
        exigirServicio(codigo);
        negocio.cambiarEstadoServicio(codigo, leerEstado());
        System.out.println("Estado del servicio actualizado.");
    }

    private void generarTurno() throws SQLException {
        List<Servicio> serviciosActivos = negocio.getServiciosActivos();
        if (serviciosActivos.isEmpty()) {
            System.out.println("No hay servicios activos disponibles para generar turnos.");
            return;
        }
        int codigoCliente = leerEnteroPositivo("Código del cliente: ");
        System.out.println("Servicios activos:");
        serviciosActivos.forEach(servicio ->
                System.out.printf("- %d: %s%n", servicio.getCodigo(), servicio.getNombre()));
        int codigoServicio = leerEnteroPositivo("Código del servicio: ");
        Turno turno = negocio.generarTurno(
                codigoCliente,
                codigoServicio);
        Cliente cliente = negocio.buscarCliente(turno.getCodigoCliente());
        Servicio servicio = negocio.buscarServicio(turno.getCodigoServicio());

        System.out.println("\n=== TURNO GENERADO ===");
        System.out.println("Número de turno: " + turno.getNumero());
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Servicio: " + servicio.getNombre());
        System.out.println("Fecha y hora de generación: "
                + formatearFechaHora(turno.getFechaGeneracion()));
        System.out.println("Estado: " + turno.getEstado());
    }

    private String formatearFechaHora(String fechaGeneracion) {
        return LocalDateTime.parse(fechaGeneracion).format(FORMATO_FECHA_HORA);
    }

    private void atenderSiguienteTurno() throws SQLException {
        Turno turno = negocio.atenderSiguienteTurno();
        if (turno == null) {
            System.out.println("No hay turnos pendientes.");
        } else {
            Cliente cliente = negocio.buscarCliente(turno.getCodigoCliente());
            Servicio servicio = negocio.buscarServicio(turno.getCodigoServicio());

            System.out.println("\n=== TURNO ATENDIDO ===");
            System.out.println("Número de turno: " + turno.getNumero());
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Servicio: " + servicio.getNombre());
            System.out.println("Fecha de generación: "
                    + formatearFechaHora(turno.getFechaGeneracion()));
            System.out.println("Fecha de atención: "
                    + formatearFechaHora(turno.getFechaAtencion()));
            System.out.println("Estado: " + turno.getEstado());
        }
    }

    private void cancelarTurno() throws SQLException {
        int numero = leerEnteroPositivo("Número de turno: ");
        if (negocio.cancelarTurno(numero)) {
            System.out.println("Turno cancelado correctamente.");
        } else {
            mostrarError("No existe un turno pendiente con ese número.");
        }
    }

    private void mostrarHistorialCliente() throws SQLException {
        int codigo = leerEnteroPositivo("Código del cliente: ");
        exigirCliente(codigo);
        mostrarTurnos("HISTORIAL DEL CLIENTE", negocio.getHistorialCliente(codigo));
    }

    private void mostrarResumenPorServicio() throws SQLException {
        System.out.println("\n--- FILTROS DEL RESUMEN POR SERVICIO ---");
        LocalDate desde = leerFechaOpcional("Fecha inicial (AAAA-MM-DD, Enter para omitir): ");
        LocalDate hasta = leerFechaOpcional("Fecha final (AAAA-MM-DD, Enter para omitir): ");
        System.out.println("Servicios:");
        negocio.getServicios().forEach(servicio ->
                System.out.printf("- %d: %s%n", servicio.getCodigo(), servicio.getNombre()));
        Integer codigoServicio = leerCodigoServicioOpcional();
        String estado = leerEstadoTurnoOpcional();

        List<ResumenServicio> resumenes =
                negocio.getResumenPorServicio(desde, hasta, codigoServicio, estado);
        System.out.println("\n--- RESUMEN POR SERVICIO ---");
        System.out.printf("%-24s %10s %11s %11s %11s %10s %12s%n",
                "Servicio", "Pendientes", "Atendidos", "Cancelados", "Total",
                "% cancel.", "Prom. min.");
        System.out.println("-".repeat(95));

        long pendientes = 0;
        long atendidos = 0;
        long cancelados = 0;
        long total = 0;
        double minutosAtencionTotal = 0;
        ResumenServicio masSolicitado = null;
        for (ResumenServicio resumen : resumenes) {
            System.out.printf("%-24s %10d %11d %11d %11d %9.1f%% %12.1f%n",
                    resumen.codigoServicio() + ": " + resumen.nombreServicio(),
                    resumen.pendientes(),
                    resumen.atendidos(),
                    resumen.cancelados(),
                    resumen.total(),
                    resumen.porcentajeCancelacion(),
                    resumen.promedioMinutosAtencion());
            pendientes += resumen.pendientes();
            atendidos += resumen.atendidos();
            cancelados += resumen.cancelados();
            total += resumen.total();
            minutosAtencionTotal += resumen.promedioMinutosAtencion() * resumen.atendidos();
            if (resumen.total() > 0
                    && (masSolicitado == null || resumen.total() > masSolicitado.total())) {
                masSolicitado = resumen;
            }
        }
        System.out.println("-".repeat(95));
        double porcentajeCancelacion = total == 0 ? 0 : cancelados * 100.0 / total;
        double promedioGlobal = atendidos == 0 ? 0 : minutosAtencionTotal / atendidos;
        System.out.printf("%-24s %10d %11d %11d %11d %9.1f%% %12.1f%n",
                "TOTAL GENERAL", pendientes, atendidos, cancelados, total,
                porcentajeCancelacion, promedioGlobal);
        if (masSolicitado == null) {
            System.out.println("Servicio con mayor demanda: sin turnos en el período.");
        } else {
            System.out.printf("Servicio con mayor demanda: %s (%d turnos)%n",
                    masSolicitado.nombreServicio(), masSolicitado.total());
        }
    }

    private LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException exception) {
                mostrarError("La fecha debe tener el formato AAAA-MM-DD.");
            }
        }
    }

    private Integer leerCodigoServicioOpcional() {
        while (true) {
            System.out.print("Código de servicio (Enter para todos): ");
            String valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                return null;
            }
            try {
                int codigo = Integer.parseInt(valor);
                if (codigo > 0) {
                    return codigo;
                }
            } catch (NumberFormatException exception) {
                mostrarError("Debe ingresar un código numérico positivo o dejarlo vacío.");
                continue;
            }
            mostrarError("El código debe ser mayor que cero.");
        }
    }

    private String leerEstadoTurnoOpcional() {
        while (true) {
            System.out.print("Estado (PENDIENTE/ATENDIDO/CANCELADO, Enter para todos): ");
            String estado = scanner.nextLine().trim();
            if (estado.isEmpty()) {
                return null;
            }
            if ("PENDIENTE".equals(estado)
                    || "ATENDIDO".equals(estado)
                    || "CANCELADO".equals(estado)) {
                return estado;
            }
            mostrarError("El estado debe ser PENDIENTE, ATENDIDO o CANCELADO.");
        }
    }

    private void mostrarClientes(List<Cliente> clientes) {
        System.out.println("\n--- CLIENTES ---");
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        clientes.forEach(System.out::println);
    }

    private void mostrarServicios(List<Servicio> servicios) {
        System.out.println("\n--- SERVICIOS ---");
        if (servicios.isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }
        servicios.forEach(System.out::println);
    }

    private void mostrarTurnos(String titulo, List<Turno> turnos) throws SQLException {
        System.out.println("\n=== LISTA DE TURNOS ===");
        System.out.println(titulo);
        if (turnos.isEmpty()) {
            System.out.println("No hay turnos para mostrar.");
            return;
        }
        for (Turno turno : turnos) {
            Cliente cliente = negocio.buscarCliente(turno.getCodigoCliente());
            Servicio servicio = negocio.buscarServicio(turno.getCodigoServicio());
            System.out.printf(
                    "- Turno #%d | Cliente: %s | Servicio: %s | Generado: %s | Estado: %s%n",
                    turno.getNumero(),
                    cliente.getNombre(),
                    servicio.getNombre(),
                    formatearFechaLista(turno.getFechaGeneracion()),
                    turno.getEstado());
        }
    }

    private String formatearFechaLista(String fechaGeneracion) {
        return LocalDateTime.parse(fechaGeneracion).format(FORMATO_LISTA_TURNOS);
    }

    private Cliente exigirCliente(int codigo) {
        Cliente cliente = negocio.buscarCliente(codigo);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con ese código.");
        }
        return cliente;
    }

    private Servicio exigirServicio(int codigo) throws SQLException {
        Servicio servicio = negocio.buscarServicio(codigo);
        if (servicio == null) {
            throw new IllegalArgumentException("No existe un servicio con ese código.");
        }
        return servicio;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException exception) {
                mostrarError("Debe ingresar un valor numérico entero.");
            }
        }
    }

    private int leerEnteroPositivo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            mostrarError("El valor debe ser mayor que cero.");
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            mostrarError("El campo no puede estar vacío.");
        }
    }

    private String leerTelefono() {
        while (true) {
            String telefono = leerTexto("Teléfono (8 dígitos): ");
            if (telefono.matches("\\d{8}")) {
                return telefono;
            }
            mostrarError("El teléfono debe contener exactamente 8 dígitos.");
        }
    }

    private String leerEstado() {
        while (true) {
            String estado = leerTexto("Estado (Activo/Inactivo): ");
            if ("Activo".equals(estado) || "Inactivo".equals(estado)) {
                return estado;
            }
            mostrarError("El estado debe ser Activo o Inactivo.");
        }
    }

    private void mostrarError(String mensaje) {
        System.out.println("Error: " + (mensaje == null ? "operación no válida" : mensaje));
    }
}
