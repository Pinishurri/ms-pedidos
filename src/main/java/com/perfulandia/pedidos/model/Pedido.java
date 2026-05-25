package com.perfulandia.pedidos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    // Clave primaria, se genera automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    // Id de la venta asociada a este pedido
    @NotNull(message = "El id de la venta no puede estar vacio")
    @Column(nullable = false)
    private Long idVenta;

    // Nombre del cliente que recibe el pedido
    @NotBlank(message = "El cliente receptor no puede estar vacio")
    @Column(nullable = false, length = 100)
    private String clienteReceptor;

    // Direccion donde se entrega el pedido
    @NotBlank(message = "La direccion de entrega no puede estar vacia")
    @Column(nullable = false, length = 200)
    private String direccionDeEntrega;

    // Estado actual del pedido: PENDIENTE, CONFIRMADO, EN_CAMINO, ENTREGADO o CANCELADO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDelPedido estadoDelPedido = EstadoDelPedido.PENDIENTE;

    // Fecha estimada en que llegara el pedido al cliente
    private LocalDateTime fechaEntregaEstimada;

    // Canal por donde se origino el pedido, ej: "WEB", "TIENDA", "TELEFONO"
    @NotBlank(message = "El canal de origen no puede estar vacio")
    @Column(nullable = false, length = 50)
    private String canalDeOrigen;

    // Observaciones adicionales del pedido
    // ej: "Dejar en conserjeria", "Llamar antes de entregar"
    @Column(length = 500)
    private String observaciones;

    // Registro de los cambios de estado del pedido
    // ej: "PENDIENTE -> CONFIRMADO el 25/05/2026"
    @Column(length = 500)
    private String logDeEstado;

    // Id del producto asociado a este pedido
    // lo usamos para verificar disponibilidad con el microservicio de inventario
    @NotNull(message = "El id del producto no puede estar vacio")
    @Column(nullable = false)
    private Long idProducto;

    // Cantidad de unidades del producto que se pidieron
    @NotNull(message = "La cantidad no puede estar vacia")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private int cantidad;

    // Los cinco estados posibles de un pedido
    public enum EstadoDelPedido {
        PENDIENTE,
        CONFIRMADO,
        EN_CAMINO,
        ENTREGADO,
        CANCELADO
    }
}