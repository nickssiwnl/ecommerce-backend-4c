package br.edu.unifio.ecommerce.repositorios;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
 
import br.edu.unifio.ecommerce.entidades.ItemPedido;
import br.edu.unifio.ecommerce.entidades.Pedido;
import br.edu.unifio.ecommerce.entidades.Produto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
@SpringBootTest
public class ItemPedidoRepositorioTests {
    @Autowired
    private PedidoRepositorio pedidoRepositorio;
    @Autowired
    private ProdutoRepositorio produtoRepositorio;
    @Autowired
    private ItemPedidoRepositorio itemPedidoRepositorio;
 
    @Test
    public void deveSalvarUmItemPedidoNovo(){
        var itemPedido = new ItemPedido();
        itemPedido.setQuantidade(2);
        itemPedido.setValorUnitario(new BigDecimal("25.00"));
 
        Pedido pedido = pedidoRepositorio.findById(1).orElseThrow();
        itemPedido.setPedido(pedido);
 
        Produto produto = produtoRepositorio.findById(5).orElseThrow();
        itemPedido.setProduto(produto);
 
        itemPedidoRepositorio.save(itemPedido);
 
        assertNotNull(itemPedido.getId());
        assertEquals(6, itemPedido.getId());
    }
}