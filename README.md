# Meu Preço — Entrega Final

Aplicativo de **registro pessoal de preços de produtos em mercados**.

O usuário cadastra os **mercados** onde faz compras e, a cada compra, registra o
**preço** de um produto: nome, marca, mercado, data da compra, preço,
quantidade, categoria, unidade, forma de pagamento e opções. Tudo é gravado em um
banco de dados local com **Room**, para comparar os preços ao longo do tempo.

Autor: **Gian Pedro Rodrigues** — Engenharia da Computação — UTFPR

## Configuração do projeto

| Item | Valor |
|---|---|
| Linguagem | Java |
| `minSdk` | 24 |
| `targetSdk` / `compileSdk` | 36 (Android 16) |
| Android Gradle Plugin | 9.0.1 |
| Gradle | 9.1.0 |
| Room | 2.8.5 (`annotationProcessor`) |
| Desugaring | `desugar_jdk_libs` 2.1.5 (para `LocalDate` com minSdk 24) |
| IDE | Android Studio Quail 2026.1.3 |

## Ambiente de teste

Testado em **dispositivo real**, cuja tela é maior que o mínimo de 4.7"
exigido (perfil Nexus 4).

| Item | Valor |
|---|---|
| Aparelho | Samsung Galaxy A16 5G (SM-A166M) |
| Sistema | Android 16 (API 36) |
| Resolução | 1080 × 2340 px |
| Densidade | 450 dpi |
| Tamanho da tela | ~6.7" (acima do mínimo de 4.7") |

## Banco de dados (Room)

Pacote `persistencia` (banco, DAOs e conversor) e pacote `modelo` (entidades).

| Tabela | Entidade | Campos |
|---|---|---|
| `mercados` | `Mercado` | `id` (PK), `nome` (único), `cidade` |
| `produtos` | `Produto` | `id` (PK), `nome`, `marca`, **`mercadoId` (FK)**, `categoria`, `unidade`, `preco`, `quantidade`, `pagamento`, `promocao`, `favorito`, `lembrete`, `observacoes`, **`dataCompra` (`LocalDate`)** |

- **Chave estrangeira**: `produtos.mercadoId → mercados.id`, com
  `onDelete = RESTRICT`. Um mercado que ainda tem preços não pode ser excluído
  (o app avisa com um AlertDialog).
- **LocalDate**: o `ConversorData` (`@TypeConverter`) grava a data como número
  de dias (`toEpochDay`) e a converte de volta ao ler.
- **Consultas** (`MercadoDao` e `ProdutoDao`): inserir, alterar, excluir,
  buscar por id, listar com `INNER JOIN` (preço + nome do mercado) e
  `LEFT JOIN ... COUNT` (mercado + total de preços).
- Categoria, unidade e pagamento são gravados como **códigos numéricos**, e o
  texto exibido vem dos recursos de cada idioma. Assim os dados continuam
  corretos se o idioma do aparelho mudar.

## Telas (Activities)

| Activity | Função | Tela pai (Up) |
|---|---|---|
| `ListaPrecosActivity` | Launcher. Lista os preços do banco com o `ProdutoAdapter` | — |
| `CadastroPrecoActivity` | Insere/altera um preço. **Spinner de mercados** (FK) e **DatePicker** | Lista de preços |
| `ListaMercadosActivity` | Lista os mercados com o `MercadoAdapter` | Lista de preços |
| `CadastroMercadoActivity` | Insere/altera um mercado | Lista de mercados |
| `ConfiguracoesActivity` | Configurações (SharedPreferences) | Lista de preços |
| `AutoriaActivity` | O que o app faz e dados da autoria | Lista de preços |

## Requisitos da Entrega Final

- **Room**: consulta, inserção, alteração e remoção nas duas tabelas.
- **Chave estrangeira** entre `produtos` e `mercados`.
- **LocalDate** (`dataCompra`) persistido e editado com **DatePicker**.
- **Spinner** com os mercados (segunda entidade) no cadastro de preço.
- **AlertDialog** para confirmar antes de excluir preços e mercados, e para
  avisar quando um mercado em uso não pode ser excluído ou quando ainda não há
  mercados cadastrados.
- **Adapters customizados** com ViewHolder: `ProdutoAdapter` e `MercadoAdapter`.
- **Menus de opções** com ícones na barra (Adicionar, Mercados, Configurações,
  Sobre; Salvar e Limpar nos cadastros).
- **Menu de Ação Contextual** com ícones (Editar, Excluir) nas duas listas, com
  o item selecionado destacado e o nome dele no título da barra.
- **Botões Up** em todas as Activities secundárias, com `parentActivityName`.
- **SharedPreferences**: ordenação da lista (nome, preço ou data) e sugestão do
  último mercado usado. Nenhum login/senha é salvo.
- **Idiomas**: inglês padrão (`values/`) e português do Brasil
  (`values-pt-rBR/`), escolhidos automaticamente pelo idioma do sistema.
- **LocalDate → String** com `DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)`
  e o `Locale` do aparelho (classe `Formatacao`): "Oct 23, 2026" em inglês e
  "23 de out. de 2026" em português.

## Histórico das entregas

- **Entrega 1** — formulário de cadastro (`CadastroPrecoActivity`).
- **Entrega 2** — entidade `Produto`, `ArrayList`, `ListView` com
  `ProdutoAdapter` customizado e clique com Toast.
- **Entrega 3** — tela de Autoria, Barra do Aplicativo, e a lista passa a exibir
  os produtos cadastrados via `startActivityForResult` / `onActivityResult`.
- **Entrega 4** — menus de opções, Menu de Ação Contextual (Editar/Excluir) com
  o item selecionado destacado, edição de itens e botões Up.
- **Entrega 5** — internacionalização (EN/PT) automática e configurações
  persistidas com SharedPreferences.
- **Entrega Final** — persistência com Room (Mercado + Produto com chave
  estrangeira), `LocalDate` com DatePicker, AlertDialogs e telas de mercados.

## Créditos

- Ícone do aplicativo: **Freepik** via **Flaticon** (flaticon.com).
- Ícones da barra: Material Icons (Google), licença Apache 2.0.
- Logo da UTFPR: propriedade da Universidade Tecnológica Federal do Paraná,
  usado apenas para identificação acadêmica.
