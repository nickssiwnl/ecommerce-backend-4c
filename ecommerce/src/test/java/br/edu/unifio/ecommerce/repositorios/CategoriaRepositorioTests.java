package br.edu.unifio.ecommerce.repositorios;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
 
import br.edu.unifio.ecommerce.entidades.Categoria;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
 
@SpringBootTest
public class CategoriaRepositorioTests {
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;
 
    @Test
    public void deveSalvarUmaCategoriaNova(){
        var categoria = new Categoria();
        categoria.setNome("Brinquedos");
        categoria.setDescricao("Brinquedos e Jogos Infantis");
 
        categoriaRepositorio.save(categoria);
 
        assertNotNull(categoria.getId());
        assertEquals((short) 6, categoria.getId());
    }
}