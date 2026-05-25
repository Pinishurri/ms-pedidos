package com.perfulandia.pedidos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.perfulandia.pedidos.model.Pedido;
import com.perfulandia.pedidos.model.Pedido.EstadoDelPedido;
import com.perfulandia.pedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    @Autowired
    private PedidoRepository repositorioPedidos;

    // RestTemplate es el objeto que usamos para llamar a otros microservicios por HTTP
    // funciona igual que cuando el navegador hace una peticion a una pagina web
    @Autowired
    private RestTemplate restTemplate;

    // Lee la URL del microservicio de inventario desde el application.properties
    @Value("${url.microservicio.inventario}")
    private String urlInventario;

    
    // CREAR PEDIDO
    // Antes de crear el pedido verifica que haya stock disponible
    // en el microservicio de inventario
  
    public Optional<Pedido> crearPedido(Pedido pedidoNuevo) {
        log.info("Intentando crear pedido para el cliente: {}", pedidoNuevo.getClienteReceptor());

        // Llamamos al microservicio de inventario para verificar
        // si hay stock disponible del producto que se esta pidiendo
        try {
            String urlConsulta = urlInventario + "/api/inventario/producto/" + pedidoNuevo.getIdProducto();
            // getForObject hace un GET al microservicio de inventario y convierte
            // la respuesta JSON en un objeto Java de tipo Object
            Object respuestaInventario = restTemplate.getForObject(urlConsulta, Object.class);

            if (respuestaInventario == null) {
                log.warn("No se encontro el producto id: {} en inventario", pedidoNuevo.getIdProducto());
                return Optional.empty();
            }

            log.info("Producto disponible en inventario, creando pedido");
        } catch (Exception error) {
            log.error("Error al consultar inventario: {}", error.getMessage());
            return Optional.empty();
        }

        // Si hay stock disponible creamos el pedido
        pedidoNuevo.setEstadoDelPedido(EstadoDelPedido.PENDIENTE);
        pedidoNuevo.setLogDeEstado("Pedido creado el " + LocalDateTime.now());
        Pedido pedidoGuardado = repositorioPedidos.save(pedidoNuevo);
        log.info("Pedido creado correctamente con id: {}", pedidoGuardado.getIdPedido());
        return Optional.of(pedidoGuardado);
    }

    
    // CONSULTAR ESTADO DE UN PEDIDO
    // Devuelve el pedido con su estado actual
    
    public Optional<Pedido> consultarEstado(Long idPedido) {
        log.info("Consultando estado del pedido con id: {}", idPedido);
        Optional<Pedido> pedido = repositorioPedidos.findById(idPedido);
        if (pedido.isEmpty()) {
            log.warn("No se encontro el pedido con id: {}", idPedido);
        }
        return pedido;
    }

   
    // ACTUALIZAR ESTADO DEL PEDIDO
    // Cambia el estado del pedido y registra el cambio en el log
    
    public Optional<Pedido> actualizarEstado(Long idPedido, EstadoDelPedido estadoNuevo) {
        log.info("Actualizando estado del pedido id: {} a {}", idPedido, estadoNuevo);
        Optional<Pedido> busqueda = repositorioPedidos.findById(idPedido);

        if (busqueda.isEmpty()) {
            log.warn("No se encontro el pedido con id: {}", idPedido);
            return Optional.empty();
        }

        Pedido pedidoEncontrado = busqueda.get();
        // Guardamos el cambio de estado en el log para tener trazabilidad
        String registroCambio = pedidoEncontrado.getEstadoDelPedido() + " -> " +
                estadoNuevo + " el " + LocalDateTime.now();
        pedidoEncontrado.setLogDeEstado(registroCambio);
        pedidoEncontrado.setEstadoDelPedido(estadoNuevo);

        Pedido pedidoActualizado = repositorioPedidos.save(pedidoEncontrado);
        log.info("Estado del pedido actualizado correctamente");
        return Optional.of(pedidoActualizado);
    }

    
    // CANCELAR PEDIDO
    // Cambia el estado del pedido a CANCELADO
    
    public Optional<Pedido> cancelarPedido(Long idPedido) {
        log.info("Cancelando pedido con id: {}", idPedido);
        return actualizarEstado(idPedido, EstadoDelPedido.CANCELADO);
    }

    
    // REGISTRAR OBSERVACIONES
    // Agrega observaciones adicionales a un pedido existente
    
    public Optional<Pedido> registrarObservaciones(Long idPedido, String observaciones) {
        log.info("Registrando observaciones en pedido id: {}", idPedido);
        Optional<Pedido> busqueda = repositorioPedidos.findById(idPedido);

        if (busqueda.isEmpty()) {
            log.warn("No se encontro el pedido con id: {}", idPedido);
            return Optional.empty();
        }

        Pedido pedidoEncontrado = busqueda.get();
        pedidoEncontrado.setObservaciones(observaciones);
        Pedido pedidoActualizado = repositorioPedidos.save(pedidoEncontrado);
        log.info("Observaciones registradas correctamente en pedido id: {}", idPedido);
        return Optional.of(pedidoActualizado);
    }

    
    // REASIGNAR PEDIDO
    // Cambia la direccion de entrega de un pedido existente
    
    public Optional<Pedido> reasignarPedido(Long idPedido, String nuevaDireccion) {
        log.info("Reasignando pedido id: {} a nueva direccion: {}", idPedido, nuevaDireccion);
        Optional<Pedido> busqueda = repositorioPedidos.findById(idPedido);

        if (busqueda.isEmpty()) {
            log.warn("No se encontro el pedido con id: {}", idPedido);
            return Optional.empty();
        }

        Pedido pedidoEncontrado = busqueda.get();
        pedidoEncontrado.setDireccionDeEntrega(nuevaDireccion);
        Pedido pedidoActualizado = repositorioPedidos.save(pedidoEncontrado);
        log.info("Pedido reasignado correctamente a: {}", nuevaDireccion);
        return Optional.of(pedidoActualizado);
    }

    
    // CONSULTAR HISTORIAL DE PEDIDOS DE UN CLIENTE
    // Devuelve todos los pedidos de un cliente especifico
    
    public List<Pedido> consultarHistorial(String clienteReceptor) {
        log.info("Consultando historial de pedidos del cliente: {}", clienteReceptor);
        return repositorioPedidos.findByClienteReceptor(clienteReceptor);
    }

   
    // LISTAR TODOS LOS PEDIDOS
    // Devuelve todos los pedidos del sistema
    
    public List<Pedido> listarTodos() {
        log.info("Listando todos los pedidos");
        return repositorioPedidos.findAll();
    }
}