package br.edu.unifio.linkwork.recursos;

import java.math.BigDecimal;
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

import br.edu.unifio.linkwork.dtos.CompatibilidadeResposta;
import br.edu.unifio.linkwork.dtos.PainelProjetoResposta;
import br.edu.unifio.linkwork.entidades.Projeto;
import br.edu.unifio.linkwork.enums.StatusProjeto;
import br.edu.unifio.linkwork.servicos.ProjetoServico;

@RestController
@RequestMapping ("projeto")
public class ProjetoRecurso {
    @Autowired
    private ProjetoServico projetoServico;

    // GET /projeto  ou  GET /projeto?status=ABERTO
    @GetMapping
    public List<Projeto> listar(@RequestParam (required = false) StatusProjeto status) {
        return status == null ? projetoServico.listar() : projetoServico.listarPorStatus(status);
    }

    @GetMapping ("{id}")
    public Projeto buscarPorId(@PathVariable Integer id) {
        return projetoServico.buscarPorId(id);
    }

    // RF03 - GET /projeto/busca?areaId=1&cidade=Remoto&valorMin=1000&valorMax=6000&termo=api
    @GetMapping ("busca")
    public List<Projeto> filtrar(@RequestParam (required = false) Integer areaId,
                                 @RequestParam (required = false) String cidade,
                                 @RequestParam (required = false) BigDecimal valorMin,
                                 @RequestParam (required = false) BigDecimal valorMax,
                                 @RequestParam (required = false) String termo) {
        return projetoServico.filtrar(areaId, cidade, valorMin, valorMax, termo);
    }

    // RF06 - GET /projeto/painel  ou  GET /projeto/painel?usuarioId=3
    @GetMapping ("painel")
    public List<PainelProjetoResposta> painel(@RequestParam (required = false) Integer usuarioId) {
        return projetoServico.painel(usuarioId);
    }

    // RN07 - GET /projeto/1/compatibilidade/3
    @GetMapping ("{id}/compatibilidade/{freelancerId}")
    public CompatibilidadeResposta compatibilidade(@PathVariable Integer id, @PathVariable Integer freelancerId) {
        return projetoServico.analisarCompatibilidade(id, freelancerId);
    }

    // GET /projeto/recomendados/3
    @GetMapping ("recomendados/{freelancerId}")
    public List<CompatibilidadeResposta> recomendados(@PathVariable Integer freelancerId) {
        return projetoServico.recomendarProjetos(freelancerId);
    }

    @PostMapping
    public ResponseEntity<Projeto> cadastrar(@Valid @RequestBody Projeto projeto) {
        Projeto salvo = projetoServico.cadastrar(projeto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(uri).body(salvo);
    }

    @PutMapping ("{id}")
    public Projeto atualizar(@PathVariable Integer id, @Valid @RequestBody Projeto projeto) {
        return projetoServico.atualizar(id, projeto);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        projetoServico.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // RN10 - inicia o projeto somente com pagamento confirmado
    @PatchMapping ("{id}/iniciar")
    public Projeto iniciar(@PathVariable Integer id) {
        return projetoServico.iniciar(id);
    }

    // RN12 - marca o projeto como FINALIZADO
    @PatchMapping ("{id}/concluir")
    public Projeto concluir(@PathVariable Integer id) {
        return projetoServico.concluir(id);
    }

    @PatchMapping ("{id}/cancelar")
    public Projeto cancelar(@PathVariable Integer id) {
        return projetoServico.cancelar(id);
    }
}
