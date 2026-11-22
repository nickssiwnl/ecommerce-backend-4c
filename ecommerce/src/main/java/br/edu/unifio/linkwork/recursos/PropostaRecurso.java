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

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.unifio.linkwork.entidades.Proposta;
import br.edu.unifio.linkwork.servicos.PropostaServico;

@RestController
@RequestMapping ("proposta")
public class PropostaRecurso {
    @Autowired
    private PropostaServico propostaServico;

    // GET /proposta  |  GET /proposta?projetoId=1  |  GET /proposta?freelancerId=3
    @GetMapping
    public List<Proposta> listar(@RequestParam (required = false) Integer projetoId,
                                 @RequestParam (required = false) Integer freelancerId) {
        if (projetoId != null) {
            return propostaServico.listarPorProjeto(projetoId);
        }
        if (freelancerId != null) {
            return propostaServico.listarPorFreelancer(freelancerId);
        }
        return propostaServico.listar();
    }

    @GetMapping ("{id}")
    public Proposta buscarPorId(@PathVariable Integer id) {
        return propostaServico.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Proposta> cadastrar(@Valid @RequestBody Proposta proposta) {
        Proposta salva = propostaServico.cadastrar(proposta);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping ("{id}")
    public Proposta atualizar(@PathVariable Integer id, @Valid @RequestBody Proposta proposta) {
        return propostaServico.atualizar(id, proposta);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        propostaServico.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // Funcionalidade do PI: contratação do freelancer
    @PatchMapping ("{id}/aceitar")
    public Proposta aceitar(@PathVariable Integer id) {
        return propostaServico.aceitar(id);
    }

    @PatchMapping ("{id}/recusar")
    public Proposta recusar(@PathVariable Integer id) {
        return propostaServico.recusar(id);
    }
}
