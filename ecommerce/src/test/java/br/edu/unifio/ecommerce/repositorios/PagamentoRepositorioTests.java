package br.edu.unifio.ecommerce.repositorios;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import br.edu.unifio.ecommerce.entidades.Cliente;
import br.edu.unifio.ecommerce.entidades.Pagamento;
import br.edu.unifio.ecommerce.entidades.Pedido;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
@SpringBootTest
public class PagamentoRepositorioTests {
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private PagamentoRepositorio pagamentoRepositorio;
 
    @Test
    public void deveSalvarUmPagamentoNovo(){
        // Todos os pedidos do import.sql (id 1 a 5) ja possuem pagamento (relacao @OneToOne),
        // entao criamos um novo pedido para associar o pagamento sem violar a restricao de unicidade.
        var pedido = new Pedido();
        pedido.setData(LocalDateTime.now());
        pedido.setStatus("CONCLUIDO");
        pedido.setValorTotal(new BigDecimal("450.00"));
 
        Cliente cliente = clienteRepositorio.findById(1).orElseThrow();
        pedido.setCliente(cliente);
 
        pedidoRepositorio.save(pedido);
 
        var pagamento = new Pagamento();
        pagamento.setValor(new BigDecimal("450.00"));
        pagamento.setData(LocalDateTime.now());
        pagamento.setStatus("APROVADO");
        pagamento.setTipo("PIX");
        pagamento.setPedido(pedido);
 
        pagamentoRepositorio.save(pagamento);
 
        assertNotNull(pagamento.getId());
        assertEquals(6, pagamento.getId());
    }
}