import { useState } from "react";
import { getSessao, limparSessao } from "./api.js";
import Login from "./pages/Login.jsx";
import logoIcone from "./assets/logo-icone.png";
import Animais from "./pages/Animais.jsx";

const PERFIL = { VOLUNTARIO: "Voluntário", CLINICA: "Clínica", ADMIN: "Administrador" };

export default function App() {
  const [sessao, setSessao] = useState(getSessao());

  function sair() {
    limparSessao();
    setSessao(null);
  }

  if (!sessao) return <Login onLogin={setSessao} />;

  return (
    <>
      <header className="topo">
        <div className="topo-marca">
          <span className="topo-pata"><img src={logoIcone} alt="" /></span>
          <h1 className="logo">AMPARA</h1>
        </div>
        <div className="topo-usuario">
          <div className="avatar">{sessao.nome.charAt(0).toUpperCase()}</div>
          <div className="topo-info">
            <strong>{sessao.nome}</strong>
            <small>{PERFIL[sessao.perfil] ?? sessao.perfil}</small>
          </div>
          <button className="btn btn-sec" onClick={sair}>Sair</button>
        </div>
      </header>
      <main className="conteudo">
        <Animais sessao={sessao} />
      </main>
    </>
  );
}
