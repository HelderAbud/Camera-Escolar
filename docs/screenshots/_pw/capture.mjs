import { chromium } from "playwright";
import { mkdir, writeFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const outDir = path.resolve(__dirname, "..");
const base = process.env.FACELOGAI_BASE_URL || "http://localhost:8082";
const email = process.env.FACELOGAI_ADMIN_EMAIL || "admin@facelogai.local";
const password =
  process.env.FACELOGAI_ADMIN_PASSWORD || "TestOnly-Admin-Password-2026!";

await mkdir(outDir, { recursive: true });

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({ viewport: { width: 1400, height: 900 } });

// 1) Swagger UI
await page.goto(`${base}/swagger-ui.html`, { waitUntil: "networkidle" });
await page.waitForSelector(".swagger-ui", { timeout: 30000 });
await page.waitForTimeout(1500);
await page.screenshot({
  path: path.join(outDir, "swagger.png"),
  fullPage: true,
});

// 2) Login + authorize + open GET /api/eventos with filters
const loginRes = await page.request.post(`${base}/api/auth/login`, {
  data: { email, password },
});
if (!loginRes.ok()) {
  throw new Error(`login failed: ${loginRes.status()} ${await loginRes.text()}`);
}
const { token } = await loginRes.json();

await page.goto(`${base}/swagger-ui/index.html`, { waitUntil: "networkidle" });
await page.waitForSelector(".swagger-ui", { timeout: 30000 });

// Authorize with bearer
const authBtn = page.locator("button.authorize").first();
if (await authBtn.count()) {
  await authBtn.click();
  const input = page.locator(".auth-container input").first();
  await input.fill(token);
  await page.locator(".auth-btn-wrapper button.authorize").click();
  await page.locator(".auth-btn-wrapper button.btn-done").click();
  await page.waitForTimeout(500);
}

// Expand eventos GET list operation
const eventosOp = page
  .locator('span.opblock-summary-path', { hasText: "/api/eventos" })
  .first();
await eventosOp.click();
await page.waitForTimeout(400);
const tryBtn = page.locator(".opblock.is-open button.try-out__btn").first();
if (await tryBtn.count()) {
  await tryBtn.click();
}
// fill a couple of filters for visual evidence
const cameraInput = page.locator('.opblock.is-open input[placeholder], .opblock.is-open input[data-param-name="cameraId"]').first();
const pageInput = page.locator('.opblock.is-open [data-param-name="page"] input, .opblock.is-open input').filter({ hasText: "" });
// Prefer named params via labels
for (const [name, value] of [
  ["cameraId", "1"],
  ["page", "0"],
  ["size", "10"],
]) {
  const loc = page.locator(`.opblock.is-open tr[data-param-name="${name}"] input`).first();
  if (await loc.count()) {
    await loc.fill(value);
  }
}
const execute = page.locator(".opblock.is-open button.execute").first();
if (await execute.count()) {
  await execute.click();
  await page.waitForTimeout(1200);
}
await page.locator(".opblock.is-open").first().screenshot({
  path: path.join(outDir, "eventos-filtro.png"),
});

// 3) Permissions matrix HTML render
const matrixHtml = `<!doctype html>
<html lang="pt-BR">
<head>
<meta charset="utf-8"/>
<title>FaceLogAI — Matriz de permissões</title>
<style>
  body { font-family: "Segoe UI", system-ui, sans-serif; margin: 40px; background: #0f172a; color: #e2e8f0; }
  h1 { font-size: 28px; margin: 0 0 8px; }
  p { color: #94a3b8; margin: 0 0 24px; }
  table { border-collapse: collapse; width: 100%; max-width: 920px; background: #1e293b; }
  th, td { border: 1px solid #334155; padding: 12px 14px; text-align: left; }
  th { background: #334155; }
  td.ok { color: #4ade80; font-weight: 600; }
  td.no { color: #64748b; }
  .brand { color: #38bdf8; font-size: 14px; letter-spacing: 0.04em; text-transform: uppercase; margin-bottom: 8px; }
</style>
</head>
<body>
  <div class="brand">FaceLogAI</div>
  <h1>Matriz de permissões (resumo)</h1>
  <p>Perfis JWT: ADMIN · COORDENACAO · PROFESSOR</p>
  <table>
    <thead>
      <tr><th>Endpoint</th><th>ADMIN</th><th>COORDENACAO</th><th>PROFESSOR</th></tr>
    </thead>
    <tbody>
      <tr><td>GET (leitura geral, autenticado)</td><td class="ok">✓</td><td class="ok">✓</td><td class="ok">✓</td></tr>
      <tr><td>POST /api/escolas</td><td class="ok">✓</td><td class="no">—</td><td class="no">—</td></tr>
      <tr><td>POST /api/cameras</td><td class="ok">✓</td><td class="ok">✓</td><td class="no">—</td></tr>
      <tr><td>DELETE /api/cameras</td><td class="ok">✓</td><td class="no">—</td><td class="no">—</td></tr>
      <tr><td>DELETE /api/alunos</td><td class="ok">✓</td><td class="ok">✓</td><td class="no">—</td></tr>
      <tr><td>POST /api/turmas</td><td class="ok">✓</td><td class="ok">✓</td><td class="no">—</td></tr>
      <tr><td>DELETE /api/turmas</td><td class="ok">✓</td><td class="no">—</td><td class="no">—</td></tr>
    </tbody>
  </table>
</body>
</html>`;
const matrixPath = path.join(__dirname, "matrix.html");
await writeFile(matrixPath, matrixHtml, "utf8");
const matrixPage = await browser.newPage({ viewport: { width: 1100, height: 700 } });
await matrixPage.goto("file://" + matrixPath.replace(/\\/g, "/"));
await matrixPage.screenshot({
  path: path.join(outDir, "permissions-matrix.png"),
  fullPage: true,
});

await browser.close();
console.log("OK screenshots written to", outDir);
