# Meu Preço — Entrega 5

Aplicativo de **registro pessoal de preços de produtos em mercados**.

O app abre na tela de **Listagem** (Launcher). Pelo **menu de opções** o usuário
pode **Adicionar** um produto (formulário de Cadastro), abrir **Configurações**
ou **Sobre** (Autoria). Ao manter um item pressionado, um **Menu de Ação
Contextual** permite **Editar** ou **Excluir**. O app é **internacionalizado**
(Inglês padrão + Português) e guarda **configurações** com SharedPreferences.

Autor: **Gian Pedro Rodrigues** — Engenharia da Computação — UTFPR

## Configuração do projeto

| Item | Valor |
|---|---|
| Linguagem | Java |
| `minSdk` | 24 |
| `targetSdk` / `compileSdk` | 36 (Android 16) |
| Android Gradle Plugin | 9.0.1 |
| Gradle | 9.1.0 |
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

## Telas (Activities)

- **ListaPrecosActivity** (principal / Launcher) — exibe em uma `ListView` os
  produtos cadastrados, usando o `ProdutoAdapter` customizado. Tem um **menu de
  opções** (Adicionar / Configurações / Sobre) e um **Menu de Ação Contextual**
  (Editar / Excluir) ao manter um item pressionado.
- **CadastroPrecoActivity** (formulário) — menu **Salvar** (valida e devolve com
  `setResult(RESULT_OK)`) e **Limpar**; abre também em **modo edição** já
  preenchida. Botão **Up** cancela.
- **AutoriaActivity** — dados de autoria (aluno, curso, e-mail), descrição, logo
  e nome da UTFPR. Botão **Up** volta.
- **ConfiguracoesActivity** — configurações persistidas em SharedPreferences.

## Entidade e listagem

- **`Produto`** — entidade do tema, com 6 atributos: nome, marca, mercado,
  categoria, unidade e preço.
- Os produtos são guardados em um **`ArrayList<Produto>`** ligado ao
  `ProdutoAdapter`. A cada cadastro devolvido com `RESULT_OK`, o
  `onActivityResult` cria um `Produto`, adiciona ao `ArrayList` e chama
  `notifyDataSetChanged()` para redesenhar a `ListView`.

## Barra do Aplicativo (App Bar)

Cada tela usa uma `androidx.appcompat.widget.Toolbar` definida como
`setSupportActionBar(...)`, funcionando como a Barra do Aplicativo. O recuo do
topo (barra de status / recorte da câmera) é aplicado à Toolbar via
`WindowInsets`, posicionando a barra corretamente no modo borda a borda
(edge-to-edge) obrigatório a partir do API 35.

## Componentes do formulário de Cadastro

- **ScrollView** — permite rolar o formulário em telas pequenas.
- **EditText** (6), **Spinner** (1), **RadioGroup** (2) com **RadioButton** (6),
  **CheckBox** (3). As ações Salvar/Limpar ficam no menu de opções.

## Internacionalização (Entrega 5)

- `values/strings.xml` (e `values/arrays.xml`) em **Inglês** — idioma padrão.
- `values-pt-rBR/strings.xml` (e `arrays.xml`) em **Português do Brasil**.
- Todos os textos fixos (telas, menus e Toasts) têm as duas traduções.
- Um seletor de idioma (Sistema / English / Português) aplica o idioma em tempo
  de execução com `AppCompatDelegate.setApplicationLocales`.

## Configurações com SharedPreferences (Entrega 5)

A classe **`Preferencias`** encapsula o `SharedPreferences` e a
**`ConfiguracoesActivity`** permite:

- **Idioma** — Sistema / Inglês / Português (persistido e reaplicado no início
  pela classe `MeuPrecoApp`).
- **Ordenação da lista** — por Nome ou por Preço.
- **Sugerir último mercado** — quando ligado, o campo Mercado do cadastro já vem
  preenchido com o último mercado usado.
- **Restaurar padrões** — volta todas as configurações ao padrão.

Nenhum login/senha é salvo — apenas preferências de interface.

## Histórico das entregas

- **Entrega 1** — formulário de cadastro (`CadastroPrecoActivity`).
- **Entrega 2** — entidade `Produto`, `ArrayList`, `ListView` com
  `ProdutoAdapter` customizado e clique com Toast.
- **Entrega 3** — tela de Autoria, Barra do Aplicativo, e a lista passa a exibir
  os produtos cadastrados via `startActivityForResult` / `onActivityResult`.
- **Entrega 4** — menus de opções, Menu de Ação Contextual (Editar/Excluir),
  edição de itens e botões Up.
- **Entrega 5** — internacionalização (EN/PT) e configurações persistidas com
  SharedPreferences.

## Créditos

- Ícone do aplicativo: **Freepik** via **Flaticon** (flaticon.com).
- Logo da UTFPR: propriedade da Universidade Tecnológica Federal do Paraná,
  usado apenas para identificação acadêmica.
