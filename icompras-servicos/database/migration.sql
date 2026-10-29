--database clientes
alter table clientes add column ativo boolean;

update clientes set ativo = true;

--database produtos
alter table produtos add column ativo boolean;

update produtos set ativo = true;