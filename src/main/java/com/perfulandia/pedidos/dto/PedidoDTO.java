package com.perfulandia.pedidos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO para crear un pedido nuevo
// Solo acepta los campos necesarios, el id y el estado los maneja el sistema solo
@Data
public class PedidoDTO {

    // Id de la venta que origino este pedido
    @NotNull(message = "El id de la venta no puede estar vacio")
    private Long idVenta;

    // Nombre del cliente que recibe el pedido
    @NotBlank(message = "El cliente receptor no puede estar vacio")
    private String clienteReceptor;

    // Direccion donde se entrega el pedido
    @NotBlank(message = "La direccion de entrega no puede estar vacia")
    private String direccionDeEntrega;

    // Canal por donde se origino el pedido, ej: "WEB", "TIENDA", "TELEFONO"
    @NotBlank(message = "El canal de origen no puede estar vacio")
    private String canalDeOrigen;

    // Observaciones adicionales del pedido
    private String observaciones;

    // Id del producto que se esta pidiendo
    @NotNull(message = "El id del producto no puede estar vacio")
    private Long idProducto;

    // Cantidad de unidades del producto que se piden
    @NotNull(message = "La cantidad no puede estar vacia")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;
}