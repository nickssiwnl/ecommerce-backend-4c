package br.edu.unifio.ecommerce.repositorios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;


import br.edu.unifio.ecommerce.entidades.Categoria;
import br.edu.unifio.ecommerce.entidades.Produto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest 
public class ProdutoRepositoriotests {
    @Autowired 
    private CategoriaRepositorio categoriaRepositorio;
    @Autowired
    private ProdutoRepositorio produtoRepositorio;


    @Test
    public void deveSalvarUmProdutoNovo(){
    var produto = new Produto();
    produto.setNome("Notebook Lenovo Legion 5i");
    produto.setDescricao("Notebook Gamer Lenovo Legion 5i, Intel Core i7, 16GB RAM, 512GB SSD, NVIDIA GeForce RTX 3060");
    produto.setPreco(new BigDecimal("7999.99"));
    produto.setEstoque(Short.parseShort("10"));

    Categoria categoria = categoriaRepositorio.findById(1).orElseThrow();
    produto.setCategoria(categoria);

    

    produtoRepositorio.save(produto);

    assertNotNull(produto.getId());
    assertEquals(6, produto.getId());

}
}
