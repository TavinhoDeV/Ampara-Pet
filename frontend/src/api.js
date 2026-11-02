const BASE = import.meta.env.VITE_API_URL ?? "";
const CHAVE = "ampara.sessao";

export function getSessao() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE));
  } catch {
    return null;
  }
}
export function salvarSessao(sessao) {
  localStorage.setItem(CHAVE, JSON.stringify(sessao));
}
export function limparSessao() {
  localStorage.removeItem(CHAVE);
}

export async function api(caminho, { method = "GET", corpo } = {}) {
  const sessao = getSessao();
  const resp = await fetch(`${BASE}/api${caminho}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(sessao ? { Authorization: `Bearer ${sessao.token}` } : {}),
    },
    body: corpo ? JSON.stringify(corpo) : undefined,
  });

  // Token expirado/inválido: volta para o login.
  if (resp.status === 401 && sessao) {
    limparSessao();
    window.location.reload();
  }

  const dados = resp.status === 204 ? null : await resp.json().catch(() => null);
  if (!resp.ok) {
    const erro = new Error(dados?.mensagem ?? "Erro inesperado. Tente novamente.");
    erro.campos = dados?.campos;
    throw erro;
  }
  return dados;
}
