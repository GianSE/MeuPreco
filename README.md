# Meu Preço — Entrega 1

Aplicativo de **registro pessoal de preços de produtos em mercados**.

Esta entrega contém a Activity `CadastroPrecoActivity`, com o formulário de
cadastro de um registro de preço.

Autor: **Gian Pedro Rodrigues**

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

## Componentes usados no formulário

- **ScrollView** — raiz do layout, permite rolar o formulário em telas pequenas.
- **TextView** — cabeçalho, subtítulo e o rótulo de cada campo.
- **EditText** (6) — produto, marca, mercado, preço, quantidade e observações.
- **Spinner** (1) — categoria do produto (`arrays.xml`).
- **RadioGroup** (2) com **RadioButton** (6) — unidade de medida
  (Unidade / Quilo / Litro) e forma de pagamento (Dinheiro / Cartão / Pix).
- **CheckBox** (3) — preço promocional, mercado favorito e aviso de queda de preço.
- **Button** (2) — "Salvar" e "Limpar".

## Comportamento dos botões

**Salvar** — lê os valores dos EditText, do Spinner, dos CheckBox e dos
RadioButton selecionados e valida cada um deles. Se algum EditText estiver
vazio (ou com valor numérico inválido), se nenhuma categoria for escolhida no
Spinner ou se algum RadioGroup estiver sem seleção, é exibido um Toast com a
mensagem de erro, a tela rola até o campo com problema e o foco de edição volta
para ele. Estando tudo válido, um Toast mostra o resumo do registro.

> O Toast exibe no máximo duas linhas a partir do Android 12 (API 31), então
> parte do resumo pode aparecer cortada. Nas próximas entregas ele será
> substituído por uma tela de listagem.

**Limpar** — apaga o texto de todos os EditText, desmarca os RadioButton
(`RadioGroup.clearCheck()`) e os CheckBox, volta o Spinner para o item inicial,
devolve o foco ao primeiro campo e mostra um Toast confirmando a ação.

## Estrutura dos arquivos

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/meupreco/CadastroPrecoActivity.java
└── res/
    ├── drawable/ic_launcher.xml
    ├── layout/activity_cadastro_preco.xml
    └── values/  (arrays, colors, strings, styles, themes)
```
