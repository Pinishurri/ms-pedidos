package com.perfulandia.pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perfulandia.pedidos.model.Pedido;
import com.perfulandia.pedidos.model.Pedido.EstadoDelPedido;


// <Pedido, Long> le dice con qué clase trabaja y el tipo de su clave primaria
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Trae todos los pedidos de un cliente especifico   
    List<Pedido> findByClienteReceptor(String clienteReceptor);

    // Trae todos los pedidos que tengan un estado especifico
    List<Pedido> findByEstadoDelPedido(EstadoDelPedido estadoDelPedido);

    // Trae todos los pedidos asociados a una venta especifica
    List<Pedido> findByIdVenta(Long idVenta);
}