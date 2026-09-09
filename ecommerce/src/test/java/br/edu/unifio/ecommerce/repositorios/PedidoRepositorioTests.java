package br.edu.unifio.ecommerce.repositorios;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import br.edu.unifio.ecommerce.entidades.Cliente;
import br.edu.unifio.ecommerce.entidades.Pedido;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
@SpringBootTest
public class PedidoRepositorioTests {
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
 
    @Test
    public void deveSalvarUmPedidoNovo(){
        var pedido = new Pedido();
        pedido.setData(LocalDateTime.now());
        pedido.setStatus("PENDENTE");
        pedido.setValorTotal(new BigDecimal("450.00"));
 
        Cliente cliente = clienteRepositorio.findById(1).orElseThrow();
        pedido.setCliente(cliente);
 
        pedidoRepositorio.save(pedido);
 
        assertNotNull(pedido.getId());
        assertEquals(6, pedido.getId());
    }
}