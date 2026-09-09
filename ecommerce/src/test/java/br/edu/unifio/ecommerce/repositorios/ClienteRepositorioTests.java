package br.edu.unifio.ecommerce.repositorios;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
 
import br.edu.unifio.ecommerce.entidades.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
@SpringBootTest
public class ClienteRepositorioTests {
    @Autowired
    private ClienteRepositorio clienteRepositorio;
 
    @Test
    public void deveSalvarUmClienteNovo(){
        var cliente = new Cliente();
        cliente.setNome("Fernanda Costa");
        cliente.setEmail("fernanda.costa@email.com");
        cliente.setTelefone("(11) 96789-0123");
 
        clienteRepositorio.save(cliente);
 
        assertNotNull(cliente.getId());
        assertEquals(6, cliente.getId());
    }
}