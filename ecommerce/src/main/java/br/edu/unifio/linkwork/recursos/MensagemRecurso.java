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

import org.springframework.web.bind.annotation.RequestParam;

import br.edu.unifio.linkwork.entidades.Mensagem;
import br.edu.unifio.linkwork.servicos.MensagemServico;

@RestController
@RequestMapping ("mensagem")
public class MensagemRecurso {
    @Autowired
    private MensagemServico mensagemServico;

    @GetMapping
    public List<Mensagem> listar() {
        return mensagemServico.listar();
    }

    @GetMapping ("{id}")
    public Mensagem buscarPorId(@PathVariable Integer id) {
        return mensagemServico.buscarPorId(id);
    }

    // RN09 - GET /mensagem/conversa?usuario1=1&usuario2=3
    @GetMapping ("conversa")
    public List<Mensagem> conversa(@RequestParam Integer usuario1, @RequestParam Integer usuario2) {
        return mensagemServico.buscarConversa(usuario1, usuario2);
    }

    // GET /mensagem/projeto/1
    @GetMapping ("projeto/{projetoId}")
    public List<Mensagem> listarPorProjeto(@PathVariable Integer projetoId) {
        return mensagemServico.listarPorProjeto(projetoId);
    }

    @PostMapping
    public ResponseEntity<Mensagem> enviar(@Valid @RequestBody Mensagem mensagem) {
        Mensagem salva = mensagemServico.enviar(mensagem);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salva.getId()).toUri();
        return ResponseEntity.created(uri).body(salva);
    }

    @PutMapping ("{id}")
    public Mensagem atualizar(@PathVariable Integer id, @Valid @RequestBody Mensagem mensagem) {
        return mensagemServico.atualizar(id, mensagem);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        mensagemServico.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
