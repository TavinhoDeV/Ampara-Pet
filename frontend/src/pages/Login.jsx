import { useState } from "react";
import { api, salvarSessao } from "../api.js";
import logoMarca from "../assets/logo-marca.png";
import logoIcone from "../assets/logo-icone.png";

const CAMINHOS = {
  mail: <><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m3 7 9 6 9-6" /></>,
  lock: <><rect x="5" y="11" width="14" height="10" rx="2" /><path d="M8 11V8a4 4 0 0 1 8 0v3" /></>,
  eye: <><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" /><circle cx="12" cy="12" r="3" /></>,
  eyeoff: <><path d="M3 3l18 18" /><path d="M10.6 6.1A10 10 0 0 1 12 6c6.5 0 10 6 10 6a17 17 0 0 1-3.2 3.9M6.6 6.6C3.7 8.4 2 12 2 12s3.5 6 10 6a10 10 0 0 0 4-.8" /></>,
  user: <><circle cx="12" cy="8" r="4" /><path d="M4 21a8 8 0 0 1 16 0" /></>,
  users: <><circle cx="9" cy="8" r="3.5" /><path d="M2.5 20a6.5 6.5 0 0 1 13 0" /><path d="M16 4.5a3.5 3.5 0 0 1 0 7M18 14a6.5 6.5 0 0 1 3.5 6" /></>,
  shield: <path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z" />,
  heart: <path d="M12 20s-8-5-8-11a4.5 4.5 0 0 1 8-2.5A4.5 4.5 0 0 1 20 9c0 6-8 11-8 11z" />,
  arrow: <path d="M5 12h14M13 6l6 6-6 6" />,
  pin: <><path d="M12 21s7-6.2 7-11.5A7 7 0 0 0 5 9.5C5 14.8 12 21 12 21z" /><circle cx="12" cy="9.5" r="2.5" /></>,
  clip: <><rect x="6" y="4" width="12" height="17" rx="2" /><path d="M9 4h6v3H9zM9 12h6M9 16h4" /></>,
  pulse: <path d="M3 12h4l2-5 4 10 2-5h6" />,
  home: <><path d="M3 11l9-8 9 8" /><path d="M5 10v10h14V10" /><path d="M10 20v-6h4v6" /></>,
  pata: <><circle cx="6" cy="11" r="2" /><circle cx="10" cy="6.5" r="2" /><circle cx="14" cy="6.5" r="2" /><circle cx="18" cy="11" r="2" /><path d="M12 12.5c-3 0-5 2.4-5 4.8 0 2 1.5 3 3 3 1 0 1.5-.5 2-.5s1 .5 2 .5c1.5 0 3-1 3-3 0-2.4-2-4.8-5-4.8z" /></>,
};

function Icone({ nome, tam = 20 }) {
  return (
    <svg width={tam} height={tam} viewBox="0 0 24 24" fill={nome === "pata" ? "currentColor" : "none"}
         stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {CAMINHOS[nome]}
    </svg>
  );
}

export default function Login({ onLogin }) {
  const [modo, setModo] = useState("entrar"); // "entrar" | "cadastrar"
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [mostrar, setMostrar] = useState(false);
  const [erros, setErros] = useState({});
  const [erroGeral, setErroGeral] = useState("");
  const [nota, setNota] = useState("");
  const [carregando, setCarregando] = useState(false);

  function validar() {
    const e = {};
    if (modo === "cadastrar" && !nome.trim()) e.nome = "Informe o seu nome.";
    if (!email.trim()) e.email = "Informe o e-mail.";
    else if (!/^\S+@\S+\.\S+$/.test(email)) e.email = "E-mail inválido.";
    if (!senha) e.senha = "Informe a senha.";
    else if (modo === "cadastrar" && senha.length < 8) e.senha = "A senha deve ter ao menos 8 caracteres.";
    return e;
  }

  async function enviar(ev) {
    ev.preventDefault();
    setErroGeral(""); setNota("");
    const e = validar();
    setErros(e);
    if (Object.keys(e).length) return;

    setCarregando(true);
    try {
      if (modo === "cadastrar") {
        await api("/auth/cadastro", { method: "POST", corpo: { nome, email, senha } });
      }
      const sessao = await api("/auth/login", { method: "POST", corpo: { email, senha } });
      salvarSessao(sessao);
      onLogin(sessao);
    } catch (err) {
      setErros(err.campos ?? {});
      setErroGeral(err.message);
    } finally {
      setCarregando(false);
    }
  }

  function trocarModo() {
    setModo(modo === "entrar" ? "cadastrar" : "entrar");
    setErros({}); setErroGeral(""); setNota("");
  }

  const emBreve = () => setNota("Este recurso ainda está em desenvolvimento nesta versão de testes.");

  return (
    <div className="login-pagina">
      <div className="arco arco-claro" />
      <div className="arco arco-medio" />

      <section className="login-hero">
        <img className="hero-marca-dagua" src={logoIcone} alt="" />

        <div className="marca">
          <span className="marca-icone"><img src={logoIcone} alt="" /></span>
          <span className="logo">AMPARA</span>
        </div>

        <p className="manuscrito frase-topo">Todo animal merece<br />uma nova chance.</p>

        <div className="hero-miolo">
          <p className="hero-selo">Triagem e acolhimento de animais urbanos</p>
          <h1 className="hero-titulo">
            CUIDADO QUE TRANSFORMA
            <span className="manuscrito">vidas.</span>
          </h1>
          <p className="hero-texto">
            Uma plataforma para conectar resgate, triagem, tratamento e adoção de animais em situação de rua.
          </p>

          <div className="jornada">
            <p className="jornada-rotulo">A jornada de cada animal</p>
            <ol>
              {[["pin", "Resgate"], ["clip", "Triagem"], ["pulse", "Tratamento"], ["home", "Adoção"]].map(([ic, rotulo]) => (
                <li key={rotulo}>
                  <span className="jornada-no"><Icone nome={ic} /></span>
                  <b>{rotulo}</b>
                </li>
              ))}
            </ol>
          </div>
        </div>

        <ul className="hero-lista">
          <li><span className="hero-icone"><Icone nome="shield" /></span><div><strong>Mais proteção</strong><small>para os animais</small></div></li>
          <li><span className="hero-icone"><Icone nome="users" /></span><div><strong>Mais conexão</strong><small>entre pessoas e ONGs</small></div></li>
          <li><span className="hero-icone"><Icone nome="heart" /></span><div><strong>Mais vidas</strong><small>com novas oportunidades</small></div></li>
        </ul>
      </section>

      <section className="login-painel">
        <div className="folha folha-1" />
        <div className="folha folha-2" />
        <div className="bola" />
        <p className="topo-direita"><Icone nome="heart" tam={30} /><span>Pequenas atitudes<br />grandes mudanças.</span></p>

        <form className="login-card" onSubmit={enviar} noValidate>
          <img className="login-logo" src={logoMarca} alt="Ampara" />
          <h2 className="login-titulo">{modo === "entrar" ? "BEM-VINDO(A)!" : "CRIE SUA CONTA"}</h2>
          <p className="subtitulo">
            {modo === "entrar"
              ? "Acesse sua conta para continuar fazendo a diferença."
              : "Cadastre-se como voluntário e ajude a registrar resgates."}
          </p>

          {modo === "cadastrar" && (
            <label className="campo"><span className="rotulo">Nome <b>*</b></span>
              <span className="entrada"><Icone nome="user" />
                <input value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Seu nome" autoComplete="name" />
              </span>
              {erros.nome && <small className="erro">{erros.nome}</small>}
            </label>
          )}

          <label className="campo"><span className="rotulo">E-mail <b>*</b></span>
            <span className="entrada"><Icone nome="mail" />
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="seu@email.com" autoComplete="email" />
            </span>
            {erros.email && <small className="erro">{erros.email}</small>}
          </label>

          <label className="campo"><span className="rotulo">Senha <b>*</b></span>
            <span className="entrada"><Icone nome="lock" />
              <input type={mostrar ? "text" : "password"} value={senha} onChange={(e) => setSenha(e.target.value)}
                     placeholder="Sua senha" autoComplete={modo === "entrar" ? "current-password" : "new-password"} />
              <button type="button" className="olho" onClick={() => setMostrar(!mostrar)}
                      aria-label={mostrar ? "Ocultar senha" : "Mostrar senha"}>
                <Icone nome={mostrar ? "eyeoff" : "eye"} />
              </button>
            </span>
            {erros.senha && <small className="erro">{erros.senha}</small>}
          </label>

          {modo === "entrar" && (
            <button type="button" className="link-direita" onClick={emBreve}>Esqueci minha senha?</button>
          )}

          {erroGeral && <div className="alerta">{erroGeral}</div>}
          {nota && <div className="nota">{nota}</div>}

          <button className="btn btn-grande btn-largo" disabled={carregando}>
            <span>{carregando ? "Aguarde..." : modo === "entrar" ? "Entrar" : "Criar conta"}</span>
            {!carregando && <Icone nome="arrow" />}
          </button>

          {modo === "entrar" && (
            <>
              <div className="divisor"><span>ou</span></div>
              <button type="button" className="btn btn-inst" onClick={emBreve}>
                <Icone nome="users" tam={18} /> Acessar como instituição
              </button>
            </>
          )}

          <div className="rodape-card">
            {modo === "entrar" ? "Ainda não tem uma conta? " : "Já tem conta? "}
            <button type="button" className="btn-link" onClick={trocarModo}>
              {modo === "entrar" ? "Cadastre-se" : "Entrar"}
            </button>
          </div>
          <p className="assinatura">AMPARA · Cidades mais humanas para todos <Icone nome="heart" tam={12} /></p>
        </form>

        <p className="manuscrito frase-canto">Faça sua parte para<br />um amanhã melhor.</p>
      </section>
    </div>
  );
}
