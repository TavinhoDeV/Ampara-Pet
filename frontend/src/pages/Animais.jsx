import { useCallback, useEffect, useMemo, useState } from "react";
import { api } from "../api.js";
import logoIcone from "../assets/logo-icone.png";

const STATUS = {
  RESGATADO: "Resgatado",
  EM_TRIAGEM: "Em triagem",
  EM_TRATAMENTO: "Em tratamento",
  APTO_ADOCAO: "Apto para adoção",
  ADOTADO: "Adotado",
};
const JORNADA = ["RESGATADO", "EM_TRIAGEM", "EM_TRATAMENTO", "APTO_ADOCAO", "ADOTADO"];
const ICONE = { CAO: "🐶", GATO: "🐱", OUTRO: "🐾" };
const ESPECIE = { CAO: "Cão", GATO: "Gato", OUTRO: "Outro" };
const FORM_VAZIO = { nome: "", especie: "CAO", porte: "", descricao: "", local: "", observacoes: "" };

export default function Animais({ sessao }) {
  const [animais, setAnimais] = useState([]);
  const [filtro, setFiltro] = useState("");
  const [form, setForm] = useState(FORM_VAZIO);
  const [erro, setErro] = useState("");
  const [aviso, setAviso] = useState("");
  const [adocao, setAdocao] = useState(null); // animal que está sendo adotado
  const [adotante, setAdotante] = useState({ nome: "", contato: "" });

  const podeValidar = sessao.perfil === "CLINICA" || sessao.perfil === "ADMIN";
  const ehAdmin = sessao.perfil === "ADMIN";

  const carregar = useCallback(async () => {
    try {
      setAnimais(await api("/animais"));
    } catch (e) {
      setErro(e.message);
    }
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  useEffect(() => {
    if (!aviso) return;
    const t = setTimeout(() => setAviso(""), 4000);
    return () => clearTimeout(t);
  }, [aviso]);

  const resumo = useMemo(() => ({
    total: animais.length,
    aptos: animais.filter((a) => a.status === "APTO_ADOCAO").length,
    adotados: animais.filter((a) => a.status === "ADOTADO").length,
  }), [animais]);

  const visiveis = filtro ? animais.filter((a) => a.status === filtro) : animais;

  async function executar(acao, mensagemOk) {
    setErro(""); setAviso("");
    try {
      await acao();
      if (mensagemOk) setAviso(mensagemOk);
      await carregar();
    } catch (e) {
      setErro(e.message);
    }
  }

  function registrarResgate(ev) {
    ev.preventDefault();
    executar(async () => {
      await api("/resgates", {
        method: "POST",
        corpo: {
          animal: { nome: form.nome, especie: form.especie, porte: form.porte, descricao: form.descricao },
          local: form.local,
          observacoes: form.observacoes,
        },
      });
      setForm(FORM_VAZIO);
    }, "Resgate registrado com sucesso.");
  }

  function validarEtapa(animal, tipo) {
    const nomeEtapa = tipo === "VACINACAO" ? "Vacinação" : "Castração";
    executar(
      () => api(`/animais/${animal.id}/atendimentos`, {
        method: "POST",
        corpo: { tipo, descricao: `${nomeEtapa} validada`, validado: true },
      }),
      `${nomeEtapa} validada para ${animal.nome}.`
    );
  }

  function abrirAdocao(animal) {
    setErro(""); setAviso("");
    setAdotante({ nome: "", contato: "" });
    setAdocao(animal);
  }

  function confirmarAdocao(ev) {
    ev.preventDefault();
    const animal = adocao;
    setErro("");
    api("/adocoes", {
      method: "POST",
      corpo: { animalId: animal.id, adotanteNome: adotante.nome, adotanteContato: adotante.contato },
    })
      .then(() => {
        setAdocao(null);
        setAviso(`Adoção de ${animal.nome} registrada. 🎉`);
        return carregar();
      })
      .catch((e) => setErro(e.message)); // ex.: "Adoção bloqueada: falta validar vacinação..."
  }

  const set = (campo) => (e) => setForm({ ...form, [campo]: e.target.value });

  return (
    <>
      {!adocao && erro && <div className="alerta">{erro}</div>}
      {aviso && <div className="aviso">{aviso}</div>}

      <div className="boas-vindas">
        <img className="boas-marca" src={logoIcone} alt="" />
        <div>
          <p className="manuscrito">Olá, {sessao.nome.split(" ")[0]}!</p>
          <h1>{ehAdmin ? "Painel da administração" : podeValidar ? "Painel da clínica" : "Painel do voluntário"}</h1>
          <p>Acompanhe cada animal do resgate até a adoção.</p>
        </div>
      </div>

      <div className="resumo">
        <div className="resumo-item"><i>🐕</i><div><strong>{resumo.total}</strong><span>Animais acolhidos</span></div></div>
        <div className="resumo-item"><i>💚</i><div><strong>{resumo.aptos}</strong><span>Aptos para adoção</span></div></div>
        <div className="resumo-item"><i>🏡</i><div><strong>{resumo.adotados}</strong><span>Adotados</span></div></div>
      </div>

      <section className="painel">
        <div className="painel-cab">
          <span className="painel-icone">＋</span>
          <div><h2>Registrar resgate</h2><p className="subtitulo">Cadastre o animal e o local onde foi encontrado.</p></div>
        </div>
        <form className="grade" onSubmit={registrarResgate}>
          <label className="campo">Nome do animal
            <input required value={form.nome} onChange={set("nome")} />
          </label>
          <label className="campo">Espécie
            <select value={form.especie} onChange={set("especie")}>
              <option value="CAO">Cão</option>
              <option value="GATO">Gato</option>
              <option value="OUTRO">Outro</option>
            </select>
          </label>
          <label className="campo">Porte
            <input value={form.porte} onChange={set("porte")} placeholder="Pequeno, médio, grande" />
          </label>
          <label className="campo">Local do resgate
            <input required value={form.local} onChange={set("local")} />
          </label>
          <label className="campo larga">Descrição
            <input value={form.descricao} onChange={set("descricao")} placeholder="Cor, aparência, comportamento..." />
          </label>
          <label className="campo larga">Observações
            <input value={form.observacoes} onChange={set("observacoes")} />
          </label>
          <button className="btn">Registrar resgate</button>
        </form>
      </section>

      <section className="painel">
        <div className="painel-cab">
          <span className="painel-icone">🏠</span>
          <div><h2>Animais acolhidos</h2><p className="subtitulo">{visiveis.length} {visiveis.length === 1 ? "animal" : "animais"} nesta visualização</p></div>
        </div>
        <div className="chips">
          <button type="button" className={`chip ${filtro === "" ? "chip-ativo" : ""}`} onClick={() => setFiltro("")}>Todos <b>{animais.length}</b></button>
          {Object.entries(STATUS).map(([k, v]) => (
            <button type="button" key={k} className={`chip ${filtro === k ? "chip-ativo" : ""}`} onClick={() => setFiltro(k)}>
              {v} <b>{animais.filter((a) => a.status === k).length}</b>
            </button>
          ))}
        </div>

        {visiveis.length === 0 && (
          <div className="vazio">
            <span>🐾</span>
            <p>Nenhum animal por aqui ainda.</p>
          </div>
        )}

        <div className="cards">
          {visiveis.map((a) => (
            <article className="card" key={a.id}>
              <div className="card-topo">
                <div className="card-icone">{ICONE[a.especie]}</div>
                <div className="card-titulo">
                  <h3>{a.nome}</h3>
                  <p className="meta">{ESPECIE[a.especie]}{a.porte ? ` · ${a.porte}` : ""}</p>
                </div>
                <span className={`selo selo-${a.status}`}>{STATUS[a.status]}</span>
              </div>
              {a.descricao && <p className="descricao">{a.descricao}</p>}
              <div className="trilha" title={STATUS[a.status]}>
                {JORNADA.map((st, i) => (
                  <span key={st} className={`trilha-passo ${i <= JORNADA.indexOf(a.status) ? "trilha-ok" : ""}`} />
                ))}
              </div>
              <div className="etapas">
                <span className={`etapa ${a.vacinado ? "etapa-ok" : ""}`}>{a.vacinado ? "✔" : "○"} Vacinação</span>
                <span className={`etapa ${a.castrado ? "etapa-ok" : ""}`}>{a.castrado ? "✔" : "○"} Castração</span>
              </div>
              {a.status !== "ADOTADO" && (podeValidar || ehAdmin) && (
                <div className="acoes">
                  {podeValidar && !a.vacinado && (
                    <button className="btn btn-sec btn-peq" onClick={() => validarEtapa(a, "VACINACAO")}>Validar vacinação</button>
                  )}
                  {podeValidar && !a.castrado && (
                    <button className="btn btn-sec btn-peq" onClick={() => validarEtapa(a, "CASTRACAO")}>Validar castração</button>
                  )}
                  {ehAdmin && <button className="btn btn-peq" onClick={() => abrirAdocao(a)}>Registrar adoção</button>}
                </div>
              )}
            </article>
          ))}
        </div>
      </section>

      {adocao && (
        <div className="modal-fundo" onClick={() => setAdocao(null)}>
          <form className="modal" onClick={(e) => e.stopPropagation()} onSubmit={confirmarAdocao}>
            <h2>Adoção de {adocao.nome} {ICONE[adocao.especie]}</h2>
            <p className="subtitulo">Informe os dados de quem vai adotar.</p>
            {erro && <div className="alerta">{erro}</div>}
            <label className="campo">Nome do adotante
              <input required value={adotante.nome} onChange={(e) => setAdotante({ ...adotante, nome: e.target.value })} />
            </label>
            <label className="campo">Contato (e-mail ou telefone)
              <input required value={adotante.contato} onChange={(e) => setAdotante({ ...adotante, contato: e.target.value })} />
            </label>
            <div className="acoes">
              <button type="button" className="btn btn-sec" onClick={() => setAdocao(null)}>Cancelar</button>
              <button className="btn">Confirmar adoção</button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}
