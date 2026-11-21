import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  base: "/Ampara-Pet/",
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    allowedHosts: [".trycloudflare.com", "localhost"],
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        configure: (proxy) => {
          // O túnel envia Origin: https://*.trycloudflare.com, que o Spring recusa.
          // Removendo o header, o Spring trata a chamada como same-origin.
          proxy.on("proxyReq", (proxyReq) => {
            proxyReq.removeHeader("origin");
          });
        },
      },
    },
  },
});