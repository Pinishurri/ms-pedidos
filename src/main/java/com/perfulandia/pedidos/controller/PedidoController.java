package com.perfulandia.pedidos.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perfulandia.pedidos.dto.PedidoDTO;
import com.perfulandia.pedidos.model.Pedido;
import com.perfulandia.pedidos.model.Pedido.EstadoDelPedido;
import com.perfulandia.pedidos.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService servicioPedidos;

    // POST /api/pedidos/crear
    // Crea un pedido nuevo verificando disponibilidad en inventario
    @PostMapping("/crear")
    public ResponseEntity<Pedido> crearPedido(@Valid @RequestBody PedidoDTO datosPedido) {
        log.info("Solicitud para crear pedido del cliente: {}", datosPedido.getClienteReceptor());

        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setIdVenta(datosPedido.getIdVenta());
        pedidoNuevo.setClienteReceptor(datosPedido.getClienteReceptor());
        pedidoNuevo.setDireccionDeEntrega(datosPedido.getDireccionDeEntrega());
        pedidoNuevo.setCanalDeOrigen(datosPedido.getCanalDeOrigen());
        pedidoNuevo.setObservaciones(datosPedido.getObservaciones());
        pedidoNuevo.setIdProducto(datosPedido.getIdProducto());
        pedidoNuevo.setCantidad(datosPedido.getCantidad());

        Optional<Pedido> resultado = servicioPedidos.crearPedido(pedidoNuevo);

        if (resultado.isPresent()) {
            return new ResponseEntity<>(resultado.get(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/pedidos/{id}
    // Consulta el estado actual de un pedido especifico
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> consultarEstado(@PathVariable Long id) {
        log.info("Solicitud para consultar estado del pedido id: {}", id);
        Optional<Pedido> pedido = servicioPedidos.consultarEstado(id);

        if (pedido.isPresent()) {
            return new ResponseEntity<>(pedido.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT /api/pedidos/actualizar/{id}
    // Actualiza el estado de un pedido
    
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoDelPedido estadoNuevo) {
        log.info("Solicitud para actualizar estado del pedido id: {}", id);
        Optional<Pedido> resultado = servicioPedidos.actualizarEstado(id, estadoNuevo);

        if (resultado.isPresent()) {
            return new ResponseEntity<>(resultado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT /api/pedidos/cancelar/{id}
    // Cancela un pedido cambiando su estado a CANCELADO
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long id) {
        log.info("Solicitud para cancelar pedido id: {}", id);
        Optional<Pedido> resultado = servicioPedidos.cancelarPedido(id);

        if (resultado.isPresent()) {
            return new ResponseEntity<>(resultado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT /api/pedidos/observaciones/{id}
    // Agrega observaciones a un pedido existente
    
    @PutMapping("/observaciones/{id}")
    public ResponseEntity<Pedido> registrarObservaciones(
            @PathVariable Long id,
            @RequestParam String observaciones) {
        log.info("Solicitud para registrar observaciones en pedido id: {}", id);
        Optional<Pedido> resultado = servicioPedidos.registrarObservaciones(id, observaciones);

        if (resultado.isPresent()) {
            return new ResponseEntity<>(resultado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // PUT /api/pedidos/reasignar/{id}
    // Cambia la direccion de entrega de un pedido
    
    @PutMapping("/reasignar/{id}")
    public ResponseEntity<Pedido> reasignarPedido(
            @PathVariable Long id,
            @RequestParam String nuevaDireccion) {
        log.info("Solicitud para reasignar pedido id: {}", id);
        Optional<Pedido> resultado = servicioPedidos.reasignarPedido(id, nuevaDireccion);

        if (resultado.isPresent()) {
            return new ResponseEntity<>(resultado.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET /api/pedidos/historial?clienteReceptor=Juan
    // Devuelve todos los pedidos de un cliente especifico
    @GetMapping("/historial")
    public ResponseEntity<List<Pedido>> consultarHistorial(@RequestParam String clienteReceptor) {
        log.info("Solicitud para consultar historial del cliente: {}", clienteReceptor);
        List<Pedido> pedidos = servicioPedidos.consultarHistorial(clienteReceptor);
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }
}