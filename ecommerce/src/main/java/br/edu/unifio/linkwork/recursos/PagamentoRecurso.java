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

import br.edu.unifio.linkwork.dtos.TotalRecebidoResposta;
import br.edu.unifio.linkwork.entidades.Pagamento;
import br.edu.unifio.linkwork.enums.StatusPagamento;
import br.edu.unifio.linkwork.servicos.PagamentoServico;

@RestController
@RequestMapping ("pagamento")
public class PagamentoRecurso {
    @Autowired
    private PagamentoServico pagamentoServico;

    // GET /pagamento  ou  GET /pagamento?status=CONFIRMADO
    @GetMapping
    public List<Pagamento> listar(@RequestParam (required = false) StatusPagamento status) {
        return status == null ? pagamentoServico.listar() : pagamentoServico.listarPorStatus(status);
    }

    @GetMapping ("{id}")
    public Pagamento buscarPorId(@PathVariable Integer id) {
        return pagamentoServico.buscarPorId(id);
    }

    // GET /pagamento/freelancer/3/total
    @GetMapping ("freelancer/{freelancerId}/total")
    public TotalRecebidoResposta totalRecebido(@PathVariable Integer freelancerId) {
        return pagamentoServico.totalRecebido(freelancerId);
    }

    @PostMapping
    public ResponseEntity<Pagamento> cadastrar(@Valid @RequestBody Pagamento pagamento) {
        Pagamento salvo = pagamentoServico.cadastrar(pagamento);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping ("{id}")
    public Pagamento atualizar(@PathVariable Integer id, @Valid @RequestBody Pagamento pagamento) {
        return pagamentoServico.atualizar(id, pagamento);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        pagamentoServico.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // RF05 - confirma o pagamento, liberando o início do projeto (RN10)
    @PatchMapping ("{id}/confirmar")
    public Pagamento confirmar(@PathVariable Integer id) {
        return pagamentoServico.confirmar(id);
    }
}
