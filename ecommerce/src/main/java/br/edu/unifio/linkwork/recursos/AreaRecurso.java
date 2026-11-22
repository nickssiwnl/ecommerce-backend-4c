package br.edu.unifio.linkwork.recursos;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import br.edu.unifio.linkwork.entidades.Area;
import br.edu.unifio.linkwork.servicos.AreaServico;

@RestController
@RequestMapping ("area")
public class AreaRecurso {
    @Autowired
    private AreaServico areaServico;

    @GetMapping
    public List<Area> listar() {
        return areaServico.listar();
    }

    @GetMapping ("{id}")
    public Area buscarPorId(@PathVariable Integer id) {
        return areaServico.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Area> cadastrar(@Valid @RequestBody Area area) {
        Area salva = areaServico.cadastrar(area);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping ("{id}")
    public Area atualizar(@PathVariable Integer id, @Valid @RequestBody Area area) {
        return areaServico.atualizar(id, area);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        areaServico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
