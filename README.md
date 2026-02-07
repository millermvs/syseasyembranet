# 📡 Ubiquiti airMAX SNMP Scanner (Java)

Projeto em Java para **descobrir rádios Ubiquiti (airOS/airMAX)** em redes privadas (ex.: `10.10.x.x`) e **coletar informações via SNMP** automaticamente.

A ideia é substituir o cadastro manual de equipamentos, criando uma base de inventário que pode ser integrada depois com um CRUD, dashboard, alertas ou sistema de monitoramento.

---

## ✅ O que este projeto faz

- Varre uma faixa de rede (ex.: `10.10.29.0/24`)
- Identifica quais IPs respondem SNMP
- Coleta dados básicos via MIB padrão (MIB-2), por exemplo:
  - `sysName` (nome do rádio)
  - `sysDescr` (descrição/firmware)
  - `sysObjectID` (identificação do fabricante)
  - `sysUpTime`
  - `sysLocation` / `sysContact`
- Suporta cenários reais de provedor:
  - Alguns rádios respondem **SNMP v1**
  - Outros respondem **SNMP v2c**
  - (o projeto pode tentar v2c e fazer fallback para v1)

---

## 🧩 Por que SNMP?

SNMP é o método mais comum em ambientes airOS/airMAX para:
- inventário automático
- monitoramento
- padronização de cadastro
- leitura de métricas (sinal, CCQ, tráfego, etc. — futuro)

---

## 🛠️ Tecnologias

- Java
- [SNMP4J](https://www.snmp4j.org/) (cliente SNMP para Java)

---

## 🚀 Como executar

### Pré-requisitos
- Java instalado (ex.: 17+ ou 21)
- Rádios com SNMP habilitado (v1 ou v2c)
- Community configurada (ex.: `public`)
- A máquina que roda o scanner precisa ter rota para a rede alvo (ex.: `10.10.x.x`)

### Configuração rápida
No código/config do projeto, ajuste:
- faixa de rede (ex.: `10.10.29.0/24`)
- community (ex.: `public`)
- timeout e retries

### Executar
- Rode a classe `main` do projeto
- O scanner listará os equipamentos encontrados e as informações coletadas

---

## 📌 Exemplo de saída (simplificado)

- `10.10.29.14` → `sysName: 1717_RES_MILLER_VIEIRA_V` → `SNMP: v1`
- `10.10.29.20` → `sysName: POP_TORRE_A_AP` → `SNMP: v2c`

---

## 📚 OIDs usados (base)

O projeto começa com OIDs padronizados (MIB-2), por exemplo:

- `1.3.6.1.2.1.1.5.0` → `sysName`
- `1.3.6.1.2.1.1.1.0` → `sysDescr`
- `1.3.6.1.2.1.1.2.0` → `sysObjectID`
- `1.3.6.1.2.1.1.3.0` → `sysUpTime`
- `1.3.6.1.2.1.1.6.0` → `sysLocation`
- `1.3.6.1.2.1.1.4.0` → `sysContact`

---

## 🧭 Roadmap (próximos passos)

- [ ] Persistir inventário em banco (PostgreSQL)
- [ ] Criar endpoints REST para CRUD/consulta
- [ ] Coletar métricas específicas Ubiquiti (airMAX MIB):
  - sinal / noise
  - CCQ
  - TX/RX rate
- [ ] Execução agendada (Scheduler)
- [ ] Interface web / dashboard

---

## ⚠️ Observações importantes

- SNMP v1/v2c usa community como “senha” simples. Em produção, recomenda-se:
  - trocar `public` por uma community própria
  - restringir SNMP por IP/ACL (apenas servidor de monitoramento)
- O scanner deve rodar de um ponto que tenha visibilidade da rede de gerenciamento.

---

## 📄 Licença
Este projeto pode ser usado e adaptado conforme sua necessidade.
(Defina aqui a licença: MIT, Apache-2.0, etc.)


