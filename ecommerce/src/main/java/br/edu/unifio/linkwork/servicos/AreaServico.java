package br.edu.unifio.linkwork.servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.unifio.linkwork.entidades.Area;
import br.edu.unifio.linkwork.excecoes.RecursoNaoEncontradoExcecao;
import br.edu.unifio.linkwork.excecoes.RegraNegocioExcecao;
import br.edu.unifio.linkwork.excecoes.RequisicaoInvalidaExcecao;
import br.edu.unifio.linkwork.repositorios.AreaRepositorio;
import br.edu.unifio.linkwork.repositorios.ProjetoRepositorio;

@Service
public class AreaServico {
    @Autowired
    private AreaRepositorio areaRepositorio;

    @Autowired
    private ProjetoRepositorio projetoRepositorio;

    public List<Area> listar() {
        return areaRepositorio.findAll(Sort.by("nome"));
    }

    public Area buscarPorId(Integer id) {
        if (id == null) {
            throw new RequisicaoInvalidaExcecao("Informe o id do(a) área");
        }
        return areaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Área", id));
    }

    @Transactional
    public Area cadastrar(Area area) {
        area.setId(null);
        if (areaRepositorio.existsByNomeIgnoreCase(area.getNome().trim())) {
            throw new RegraNegocioExcecao("Já existe uma área com o nome '" + area.getNome() + "'");
        }
        area.setNome(area.getNome().trim());
        return areaRepositorio.save(area);
    }

    @Transactional
    public Area atualizar(Integer id, Area dados) {
        Area area = buscarPorId(id);
        if (areaRepositorio.existsByNomeIgnoreCaseAndIdNot(dados.getNome().trim(), id)) {
            throw new RegraNegocioExcecao("Já existe outra área com o nome '" + dados.getNome() + "'");
        }
        area.setNome(dados.getNome().trim());
        area.setDescricao(dados.getDescricao());
        return areaRepositorio.save(area);
    }

    // Regra de negócio: não é permitido excluir uma área que possua projetos associados
    @Transactional
    public void excluir(Integer id) {
        Area area = buscarPorId(id);
        if (projetoRepositorio.existsByAreaId(id)) {
            throw new RegraNegocioExcecao("A área '" + area.getNome()
                    + "' possui projetos associados e não pode ser excluída");
        }
        areaRepositorio.delete(area);
    }
}
