 
-- Categoria 

insert into categoria (nome, descricao) values ('Informática', 'Produtos de Informática');
insert into categoria (nome, descricao) values ('Livros', 'Livros Técnicos');
insert into categoria (nome, descricao) values ('Eletrônicos', 'Aparelhos Eletrônicos');
insert into categoria (nome, descricao) values ('Móveis', 'Móveis para Escritório e Casa');
insert into categoria (nome, descricao) values ('Papelaria', 'Materiais de Escritório');


-- Produto (5 registros)

insert into produto (nome, descricao, preco, estoque, categoria_id) values ('Código Limpo', 'Livro do Autor Robert C. Martin', 100.00, 20, 2);
insert into produto (nome, descricao, preco, estoque, categoria_id) values ('Notebook Dell', 'Notebook Dell Inspiron 15', 3500.00, 10, 1);
insert into produto (nome, descricao, preco, estoque, categoria_id) values ('Smart TV 50"', 'Smart TV LED 50 polegadas 4K', 2200.00, 15, 3);
insert into produto (nome, descricao, preco, estoque, categoria_id) values ('Cadeira de Escritório', 'Cadeira ergonômica com apoio lombar', 750.00, 8, 4);
insert into produto (nome, descricao, preco, estoque, categoria_id) values ('Caderno Universitário', 'Caderno 10 matérias 200 folhas', 25.00, 100, 5);


-- Cliente (5 registros)

insert into cliente (nome, email, telefone) values ('João Silva', 'joao.silva@email.com', '(11) 91234-5678');
insert into cliente (nome, email, telefone) values ('Maria Oliveira', 'maria.oliveira@email.com', '(11) 92345-6789');
insert into cliente (nome, email, telefone) values ('Carlos Souza', 'carlos.souza@email.com', '(11) 93456-7890');
insert into cliente (nome, email, telefone) values ('Ana Pereira', 'ana.pereira@email.com', '(11) 94567-8901');
insert into cliente (nome, email, telefone) values ('Pedro Santos', 'pedro.santos@email.com', '(11) 95678-9012');


-- Pedido (5 registros)

insert into pedido (data, status, valor_total, cliente_id) values ('2024-01-10 09:30:00', 'CONCLUIDO', 3600.00, 1);
insert into pedido (data, status, valor_total, cliente_id) values ('2024-02-15 14:20:00', 'CONCLUIDO', 2200.00, 2);
insert into pedido (data, status, valor_total, cliente_id) values ('2024-03-05 11:00:00', 'PENDENTE', 775.00, 3);
insert into pedido (data, status, valor_total, cliente_id) values ('2024-03-20 16:45:00', 'CONCLUIDO', 100.00, 4);
insert into pedido (data, status, valor_total, cliente_id) values ('2024-04-02 10:10:00', 'CANCELADO', 25.00, 5);


-- ItemPedido (5 registros)

insert into item_pedido (quantidade, valor_unitario, pedido_id, produto_id) values (1, 3500.00, 1, 2);
insert into item_pedido (quantidade, valor_unitario, pedido_id, produto_id) values (1, 2200.00, 2, 3);
insert into item_pedido (quantidade, valor_unitario, pedido_id, produto_id) values (1, 750.00, 3, 4);
insert into item_pedido (quantidade, valor_unitario, pedido_id, produto_id) values (1, 100.00, 4, 1);
insert into item_pedido (quantidade, valor_unitario, pedido_id, produto_id) values (1, 25.00, 5, 5);


-- Pagamento (5 registros)

insert into pagamento (valor, data, status, tipo, pedido_id) values (3600.00, '2024-01-10 09:35:00', 'APROVADO', 'CARTAO_CREDITO', 1);
insert into pagamento (valor, data, status, tipo, pedido_id) values (2200.00, '2024-02-15 14:25:00', 'APROVADO', 'PIX', 2);
insert into pagamento (valor, data, status, tipo, pedido_id) values (775.00, '2024-03-05 11:05:00', 'PENDENTE', 'BOLETO', 3);
insert into pagamento (valor, data, status, tipo, pedido_id) values (100.00, '2024-03-20 16:50:00', 'APROVADO', 'CARTAO_DEBITO', 4);
insert into pagamento (valor, data, status, tipo, pedido_id) values (25.00, '2024-04-02 10:15:00', 'ESTORNADO', 'PIX', 5);
