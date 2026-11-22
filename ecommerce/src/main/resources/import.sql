-- =====================================================================
-- Dados iniciais do LinkWork (carregados a cada execução: ddl-auto=create)
-- =====================================================================

-- Area
insert into area (nome, descricao) values ('Desenvolvimento Web', 'Sites, sistemas web e APIs');
insert into area (nome, descricao) values ('Design Gráfico', 'Identidade visual, UI/UX e materiais gráficos');
insert into area (nome, descricao) values ('Marketing Digital', 'Tráfego pago, SEO e redes sociais');
insert into area (nome, descricao) values ('Redação e Tradução', 'Textos, revisão e tradução');
insert into area (nome, descricao) values ('Dados e IA', 'Análise de dados, BI e inteligência artificial');

-- Usuario (tipo: FREELANCER, EMPRESA, PESSOA_FISICA)
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Tech Solutions Ltda', 'contato@techsolutions.com', '12345678000190', 'EMPRESA', '(14) 3322-1100', 'Ourinhos', null, false);
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Maria Oliveira', 'maria.oliveira@email.com', '11122233344', 'PESSOA_FISICA', '(18) 99876-5432', 'Assis', null, false);
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Lucas Andrade', 'lucas.andrade@email.com', '22233344455', 'FREELANCER', '(14) 99123-4567', 'Ourinhos', 'Java, Spring Boot, MySQL, React, HTML, CSS', false);
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Beatriz Lima', 'beatriz.lima@email.com', '33344455566', 'FREELANCER', '(14) 99234-5678', 'Marília', 'Figma, Photoshop, Illustrator, UI/UX', false);
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Rafael Costa', 'rafael.costa@email.com', '44455566677', 'FREELANCER', '(11) 99345-6789', 'São Paulo', 'SEO, Google Ads, Copywriting', true);
insert into usuario (nome, email, documento, tipo, telefone, cidade, competencias, bloqueado) values ('Comércio Rápido Ltda', 'financeiro@comerciorapido.com', '98765432000110', 'EMPRESA', '(14) 3301-2200', 'Ourinhos', null, false);

-- Projeto (status: ABERTO, AGUARDANDO_PAGAMENTO, EM_ANDAMENTO, FINALIZADO, CANCELADO)
insert into projeto (titulo, descricao, valor, prazo, cidade, requisitos, status, data_publicacao, area_id, contratante_id, freelancer_id) values ('API REST para loja virtual', 'Desenvolvimento de uma API REST para loja virtual com cadastro de produtos, carrinho e pedidos.', 5000.00, '2027-02-28', 'Remoto', 'Java, Spring Boot, MySQL', 'ABERTO', '2026-11-01 10:00:00', 1, 1, null);
insert into projeto (titulo, descricao, valor, prazo, cidade, requisitos, status, data_publicacao, area_id, contratante_id, freelancer_id) values ('Identidade visual para padaria', 'Criação de logotipo, paleta de cores e cardápio para uma padaria de bairro em Assis.', 1200.00, '2027-01-15', 'Assis', 'Illustrator, Photoshop', 'ABERTO', '2026-11-05 14:30:00', 2, 2, null);
insert into projeto (titulo, descricao, valor, prazo, cidade, requisitos, status, data_publicacao, area_id, contratante_id, freelancer_id) values ('Site institucional responsivo', 'Site institucional responsivo com páginas de serviços, contato e blog para empresa local.', 3000.00, '2027-01-30', 'Ourinhos', 'React, HTML, CSS', 'AGUARDANDO_PAGAMENTO', '2026-10-20 09:00:00', 1, 6, 3);
insert into projeto (titulo, descricao, valor, prazo, cidade, requisitos, status, data_publicacao, area_id, contratante_id, freelancer_id) values ('Sistema de controle de estoque', 'Sistema web para controle de estoque com entradas, saídas e relatórios de produtos.', 4500.00, '2026-12-20', 'Ourinhos', 'Java, MySQL', 'EM_ANDAMENTO', '2026-10-01 08:00:00', 1, 6, 3);
insert into projeto (titulo, descricao, valor, prazo, cidade, requisitos, status, data_publicacao, area_id, contratante_id, freelancer_id) values ('Banners para redes sociais', 'Criação de 10 banners para campanha de lançamento nas redes sociais da empresa.', 800.00, '2026-10-30', 'Remoto', 'Photoshop, Figma', 'FINALIZADO', '2026-09-15 11:00:00', 2, 6, 4);

-- Proposta (status: PENDENTE, ACEITA, RECUSADA)
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (4800.00, 30, 'Tenho experiência com Spring Boot e MySQL e posso entregar a API em 30 dias.', 'PENDENTE', '2026-11-02 09:15:00', 1, 3);
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (1100.00, 20, 'Posso criar a identidade visual completa com três propostas de logotipo.', 'PENDENTE', '2026-11-06 16:00:00', 2, 4);
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (2800.00, 25, 'Desenvolvo o site em React, totalmente responsivo, em 25 dias.', 'ACEITA', '2026-10-21 10:00:00', 3, 3);
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (4300.00, 40, 'Sistema de estoque completo com relatórios em 40 dias.', 'ACEITA', '2026-10-02 13:00:00', 4, 3);
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (800.00, 10, 'Entrego os 10 banners em até 10 dias, com duas rodadas de ajustes.', 'ACEITA', '2026-09-16 15:00:00', 5, 4);
insert into proposta (valor, prazo_dias, mensagem, status, data_envio, projeto_id, freelancer_id) values (3200.00, 30, 'Posso fazer o layout e o desenvolvimento do site institucional.', 'RECUSADA', '2026-10-22 08:30:00', 3, 4);

-- Mensagem
insert into mensagem (conteudo, data_envio, remetente_id, destinatario_id, projeto_id) values ('Olá Lucas, vi sua proposta para a API. Você tem portfólio?', '2026-11-02 11:00:00', 1, 3, 1);
insert into mensagem (conteudo, data_envio, remetente_id, destinatario_id, projeto_id) values ('Olá! Tenho sim, envio os links do GitHub ainda hoje.', '2026-11-02 11:20:00', 3, 1, 1);
insert into mensagem (conteudo, data_envio, remetente_id, destinatario_id, projeto_id) values ('Proposta aceita! Vamos registrar o pagamento em breve.', '2026-10-23 09:00:00', 6, 3, 3);
insert into mensagem (conteudo, data_envio, remetente_id, destinatario_id, projeto_id) values ('Perfeito, aguardo a confirmação para iniciar.', '2026-10-23 09:30:00', 3, 6, 3);
insert into mensagem (conteudo, data_envio, remetente_id, destinatario_id, projeto_id) values ('Beatriz, os banners ficaram ótimos. Obrigado!', '2026-10-29 17:00:00', 6, 4, 5);

-- Pagamento (status: PENDENTE, CONFIRMADO | forma: PIX, CARTAO_CREDITO, CARTAO_DEBITO, BOLETO)
insert into pagamento (valor, data, status, forma, projeto_id) values (2800.00, '2026-10-24 10:00:00', 'PENDENTE', 'BOLETO', 3);
insert into pagamento (valor, data, status, forma, projeto_id) values (4300.00, '2026-10-03 09:00:00', 'CONFIRMADO', 'PIX', 4);
insert into pagamento (valor, data, status, forma, projeto_id) values (800.00, '2026-09-17 10:00:00', 'CONFIRMADO', 'CARTAO_CREDITO', 5);
