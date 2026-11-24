(() => {
  "use strict";
  const LANGS = ["kk", "ru", "en"];
  const KEY = "nuvion-state-v1";
  const load = () => { try { return JSON.parse(localStorage.getItem(KEY)) || {}; } catch { return {}; } };
  const S = Object.assign({ lang: 0, xp: 0, done: {}, right: 0, total: 0, ach: [], days: {}, last: null, streak: 0 }, load());
  const save = () => { try { localStorage.setItem(KEY, JSON.stringify(S)); } catch {} };

  const L = (a) => (Array.isArray(a) ? a[S.lang] ?? a[0] : a);
  const T = (k) => L(UI[k]);
  const app = document.getElementById("app");
  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

  const P = {
    back: '<path d="m12 19-7-7 7-7"/><path d="M19 12H5"/>',
    play: '<polygon points="6 3 20 12 6 21 6 3"/>',
    book: '<path d="M12 7v14"/><path d="M3 18a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1h5a4 4 0 0 1 4 4 4 4 0 0 1 4-4h5a1 1 0 0 1 1 1v13a1 1 0 0 1-1 1h-6a3 3 0 0 0-3 3 3 3 0 0 0-3-3z"/>',
    spark: '<path d="M9.94 14.06 8 20l-1.94-5.94L0 12l6.06-1.94L8 4l1.94 6.06L16 12z" transform="translate(3 0) scale(.9)"/><path d="M20 3v4M22 5h-4"/>',
    atom: '<circle cx="12" cy="12" r="1"/><path d="M20.2 20.2c2.04-2.03.02-7.36-4.5-11.9-4.54-4.52-9.87-6.54-11.9-4.5-2.04 2.03-.02 7.36 4.5 11.9 4.54 4.52 9.87 6.54 11.9 4.5Z"/><path d="M15.7 15.7c4.52-4.54 6.54-9.87 4.5-11.9-2.03-2.04-7.36-.02-11.9 4.5-4.52 4.54-6.54 9.87-4.5 11.9 2.03 2.04 7.36.02 11.9-4.5Z"/>',
    radio: '<path d="M4.9 19.1C1 15.2 1 8.8 4.9 4.9"/><path d="M7.8 16.2c-2.3-2.3-2.3-6.1 0-8.5"/><circle cx="12" cy="12" r="2"/><path d="M16.2 7.8c2.3 2.3 2.3 6.1 0 8.5"/><path d="M19.1 4.9C23 8.8 23 15.1 19.1 19"/>',
    zap: '<path d="M4 14a1 1 0 0 1-.78-1.63l9.9-10.2a.5.5 0 0 1 .86.46l-1.92 6.02A1 1 0 0 0 13 10h7a1 1 0 0 1 .78 1.63l-9.9 10.2a.5.5 0 0 1-.86-.46l1.92-6.02A1 1 0 0 0 11 14z"/>',
    flame: '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.07-2.14-.22-4.05 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.15.43-2.29 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
    factory: '<path d="M2 20a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8l-7 5V8l-7 5V4a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/><path d="M17 18h1M12 18h1M7 18h1"/>',
    clip: '<rect width="8" height="4" x="8" y="2" rx="1"/><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><path d="m9 14 2 2 4-4"/>',
    calc: '<rect width="16" height="20" x="4" y="2" rx="2"/><path d="M8 6h8M16 14v4M16 10h.01M12 10h.01M8 10h.01M12 14h.01M8 14h.01M12 18h.01M8 18h.01"/>',
    award: '<path d="m15.48 12.89 1.52 8.56a.5.5 0 0 1-.81.47l-3.6-2.7a1 1 0 0 0-1.18 0l-3.6 2.7a.5.5 0 0 1-.81-.47l1.51-8.56"/><circle cx="12" cy="8" r="6"/>',
    bulb: '<path d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5"/><path d="M9 18h6M10 22h4"/>',
    star: '<path d="M11.52 2.3a.53.53 0 0 1 .95 0l2.31 4.68a2.12 2.12 0 0 0 1.6 1.16l5.16.76a.53.53 0 0 1 .3.9l-3.74 3.64a2.12 2.12 0 0 0-.61 1.88l.88 5.14a.53.53 0 0 1-.77.56l-4.62-2.43a2.12 2.12 0 0 0-1.97 0L6.4 21.01a.53.53 0 0 1-.77-.56l.88-5.14a2.12 2.12 0 0 0-.61-1.88L2.16 9.8a.53.53 0 0 1 .29-.9l5.17-.76a2.12 2.12 0 0 0 1.6-1.16z"/>',
    trend: '<path d="M22 7 13.5 15.5 8.5 10.5 2 17"/><path d="M16 7h6v6"/>',
    rot: '<path d="M21 12a9 9 0 1 1-9-9c2.52 0 4.93 1 6.74 2.74L21 8"/><path d="M21 3v5h-5"/>',
    zoom: '<circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3M11 8v6M8 11h6"/>',
    info: '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>',
    lock: '<rect width="18" height="11" x="3" y="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>',
  };
  const I = (n, cls = "i") => `<svg class="${cls}" viewBox="0 0 24 24">${P[n] || ""}</svg>`;

  /* ---------- state helpers ---------- */
  const today = () => new Date().toISOString().slice(0, 10);
  function addXP(n) {
    S.xp += n;
    const d = today();
    S.days[d] = (S.days[d] || 0) + n;
    if (S.last !== d) {
      const y = new Date(Date.now() - 864e5).toISOString().slice(0, 10);
      S.streak = S.last === y ? S.streak + 1 : 1;
      S.last = d;
    }
    save();
  }
  const level = () => Math.floor(S.xp / 500) + 1;
  const topicPct = (t) => {
    const d = S.done[t.id] || {};
    return Math.round((((d.quiz || 0) / t.quiz.length) * 50) + (((d.probs || 0) / t.probs.length) * 50));
  };
  const finished = () => TOPICS.filter((t) => topicPct(t) >= 100).length;
  function unlock(id) {
    if (S.ach.includes(id)) return null;
    const a = ACH.find((x) => x.id === id);
    S.ach.push(id); addXP(a.xp); save();
    return a;
  }

  /* ---------- router ---------- */
  let stack = [];
  const go = (name, arg) => { stack.push([name, arg]); render(); };
  const back = () => { stack.pop(); if (!stack.length) stack = [["home"]]; render(); };
  let cleanup = null;
  function render() {
    if (cleanup) { cleanup(); cleanup = null; }
    const [name, arg] = stack[stack.length - 1];
    document.documentElement.lang = LANGS[S.lang];
    app.innerHTML = `<section class="screen ${name}">${V[name](arg)}</section>`;
    app.scrollTop = 0;
    (A[name] || (() => {}))(arg);
  }
  app.addEventListener("click", (e) => {
    const b = e.target.closest("[data-go],[data-back],[data-lang]");
    if (!b) return;
    if (b.dataset.back !== undefined) return back();
    if (b.dataset.lang !== undefined) { S.lang = +b.dataset.lang; save(); return render(); }
    go(b.dataset.go, b.dataset.arg);
  });
  function toast(msg) {
    const t = document.createElement("div"); t.className = "toast"; t.textContent = msg;
    document.getElementById("phone").appendChild(t); setTimeout(() => t.remove(), 1800);
  }
  const hdr = (title, sub, extra = "", dark = false) =>
    `<header class="hdr ${dark ? "dark" : ""}"><button class="back" data-back aria-label="back">${I("back")}</button><h1>${title}</h1>${sub ? `<p>${sub}</p>` : ""}${extra}</header>`;

  /* ---------- views ---------- */
  const V = {
    home: () => `
      <div class="screen home" style="flex:1">
        <span class="deco" style="width:64px;height:64px;left:40px;top:80px;border-color:#d6e4fb"></span>
        <span class="deco" style="width:46px;height:46px;right:56px;top:160px;border-color:#f7e3a8"></span>
        <div class="lang">${["KK", "RU", "EN"].map((l, i) => `<button data-lang="${i}" class="${S.lang === i ? "on" : ""}">${l}</button>`).join("")}</div>
        <div class="logo">NU<b>VI</b>ON</div>
        <div class="sub">${T("subtitle")}</div>
        <div class="orbit"><span class="ring"></span><span class="ring r2"></span><div class="core">${I("atom")}</div></div>
        <span class="badge-prof">Professor Proton</span>
        <div class="actions">
          <button class="btn primary" data-go="topics">${I("play")} ${T("start")}</button>
          <button class="btn gold" data-go="profile">${T("cont")}</button>
          <div class="row">
            <button class="btn ghost" data-go="topics">${I("book")} ${T("topics")}</button>
            <button class="btn ghost y" data-go="ar">${I("spark")} ${T("ar")}</button>
          </div>
        </div>
      </div>`,

    topics: () => `
      ${hdr(T("topics"), T("choose"))}
      <div class="body">${TOPICS.map((t, i) => {
        const p = topicPct(t);
        return `<article class="tcard">
          <div class="top"><div class="ticon">${I(t.icon)}</div>
            <div><h3>${L(t.t)}</h3><small>${L(t.s)}</small></div>
            <div class="ring-p" style="background:conic-gradient(var(--yellow) ${p * 3.6}deg,#fbf0cf 0)"><span style="background:#fff;width:30px;height:30px;border-radius:50%;display:grid;place-items:center">${p ? p + "%" : ""}</span></div>
          </div>
          <div class="tbtns">
            <button class="tb t" data-go="quiz" data-arg="${i}">${I("clip")} ${T("test")}</button>
            <button class="tb p" data-go="probs" data-arg="${i}">${I("calc")} ${T("problems")}</button>
          </div></article>`;
      }).join("")}</div>
      <div class="footer"><button data-go="library">${T("library")}</button><button data-go="map">${T("map")}</button></div>`,

    quiz: (i) => {
      const t = TOPICS[i];
      return `${hdr(T("test"), L(t.t), `<span class="xp">${I("award")} <span id="xp">0</span> XP</span>
        <div class="prog"><span id="cnt"></span><span id="pct"></span></div><div class="bar"><i id="bar"></i></div>`, true)}
        <div class="body" id="qb"></div>`;
    },

    probs: (i) => {
      const t = TOPICS[i];
      return `${hdr(T("practical"), L(t.t), `<span class="xp">${I("calc")} <span id="xp">0</span> XP</span>
        <div class="prog"><span id="cnt"></span><span id="pct"></span></div><div class="bar"><i id="bar"></i></div>`, true)}
        <div class="body" id="pb"></div>`;
    },

    profile: () => {
      const lv = level(), into = S.xp % 500, d7 = L(UI.days7);
      const now = new Date(), dow = (now.getDay() + 6) % 7;
      const vals = d7.map((_, k) => {
        const d = new Date(now - (dow - k) * 864e5).toISOString().slice(0, 10);
        return k > dow ? 0 : S.days[d] || 0;
      });
      const mx = Math.max(60, ...vals);
      const acc = S.total ? Math.round((S.right / S.total) * 100) : 0;
      return `<div class="pro">${hdr(T("profile"), "")}
        <div class="ucard"><div class="u"><div class="ava">${I("atom")}</div>
          <div><h2>${T("user")}</h2><div class="meta"><span style="color:var(--blue)">${I("award")} ${T("level")} ${lv}</span><span style="color:#e0b12c">${I("zap")} ${S.xp} XP</span></div></div></div>
          <div class="prog" style="color:var(--muted);opacity:1"><span>${T("nextLevel")}</span><span>${500 - into} XP</span></div>
          <div class="bar" style="background:#eef1f6"><i style="width:${(into / 500) * 100}%"></i></div></div></div>
        <div class="body">
          <div class="chart"><h3>${I("trend")} <span style="color:var(--ink)">${T("progress")}</span></h3>
            <div class="bars">${vals.map((v, k) => `<div class="${k === dow ? "today" : ""}"><i style="height:${Math.max(4, (v / mx) * 100)}%"></i>${d7[k]}</div>`).join("")}</div></div>
          <h3 class="sec">${T("achievements")}</h3>
          <div class="ach">${ACH.map((a, k) => S.ach.includes(a.id)
            ? `<button class="a${k % 2 ? 2 : 1}" data-go="win" data-arg="${a.id}"><span class="ai">${I(a.icon)}</span>${L(a.t)}</button>`
            : `<button class="a3" disabled><span class="ai">${I("lock")}</span>${T("locked")}</button>`).join("")}</div>
          <h3 class="sec">${T("stats")}</h3>
          <div class="stats"><div><span>${T("topics")}</span><b>${finished()} / ${TOPICS.length}</b></div>
            <div><span>${T("accuracy")}</span><b>${acc}%</b></div>
            <div><span>${T("streak")}</span><b>${S.streak} ${T("days")} 🔥</b></div></div>
          <button class="btn primary" data-go="topics">${I("play")} ${T("cont")}</button>
        </div>`;
    },

    win: (id) => {
      const a = ACH.find((x) => x.id === id) || ACH[0];
      return `<div class="screen win" style="flex:1">
        <button class="back" data-back>${I("back")}</button>
        <h1>${T("achTitle")}</h1>
        <div class="medal"><div>${I(a.icon)}</div></div>
        <div class="wtitle">${L(a.t)}</div>
        <p>${L(a.d)}</p>
        <div class="gain"><span style="color:var(--yellow)">${I("spark")}</span><div><small>${T("gained")}</small><b>+${a.xp} XP</b></div></div>
        <div class="actions"><button class="btn primary" data-back>${T("cont")}</button>
          <button class="btn ghost" id="share">${T("share")}</button></div></div>`;
    },

    ar: () => `<div class="screen ar" style="flex:1">
        <button class="back" data-back>${I("back")}</button>
        <h1>${T("ar")}</h1><p>${T("holo")}</p>
        <div class="stage"><span class="live"><i></i> AR Active</span>
          <div class="scene"><canvas id="cv" width="640" height="640" style="width:100%;max-width:320px;aspect-ratio:1"></canvas></div>
          <div id="inf"></div>
          <div class="counts"><div><b id="np" style="color:var(--yellow)"></b><small>${T("protons")}</small></div>
            <div><b id="ne" style="color:#5aa0ff"></b><small>${T("electrons")}</small></div>
            <div><b id="nn"></b><small>${T("neutrons")}</small></div></div></div>
        <div class="elsel">${ELEMENTS.map((e, k) => `<button data-el="${k}">${e.s} · ${L(e.t)}</button>`).join("")}</div>
        <div class="ctrl"><button id="b-rot" class="on">${I("rot")} ${T("rotate")}</button>
          <button id="b-zoom">${I("zoom")} ${T("zoom")}</button>
          <button id="b-info">${I("info")} ${T("info")}</button></div></div>`,

    library: () => `${hdr(T("library"), T("subtitle"))}
      <div class="body">${TOPICS.map((t) => `<article class="lib"><h3>${L(t.t)}</h3><p>${esc(L(t.lib))}</p></article>`).join("")}</div>`,

    map: () => `${hdr(T("map"), T("learnPath"))}
      <div class="body"><div class="path">${TOPICS.map((t, i) => {
        const p = topicPct(t);
        return `<button class="node ${p >= 100 ? "done" : ""}" data-go="quiz" data-arg="${i}"><b style="font-weight:500">${i + 1}. ${L(t.t)}</b><br><small>${L(t.s)} · ${p}%</small></button>`;
      }).join("")}</div></div>`,
  };

  /* ---------- behaviours ---------- */
  function progress(n, of, xp, label) {
    document.getElementById("cnt").textContent = `${label} ${n} / ${of}`;
    const pc = Math.round((n / of) * 100);
    document.getElementById("pct").textContent = pc + "%";
    document.getElementById("bar").style.width = pc + "%";
    document.getElementById("xp").textContent = xp;
  }
  function finishTopic(i, key, score) {
    const t = TOPICS[i]; const d = (S.done[t.id] = S.done[t.id] || {});
    d[key] = Math.max(d[key] || 0, score); save();
    let a = null;
    if (key === "quiz" && score >= 3) a = unlock("quest") || a;
    if (t.id === "models" && topicPct(t) >= 100) a = unlock("explorer") || a;
    if (finished() === TOPICS.length) a = unlock("nuclear") || a;
    return a;
  }
  function resultView(box, score, of, gained, a) {
    box.innerHTML = `<div class="qcard" style="flex-direction:column;align-items:center;text-align:center">
        <div style="font-size:15px;color:var(--muted)">${T("result")}</div>
        <div style="font-size:44px;color:var(--blue)">${score} / ${of}</div>
        <div style="color:#d9a91a">+${gained} XP</div></div>
      ${a ? `<button class="btn gold" data-go="win" data-arg="${a.id}">${I("award")} ${T("achTitle")}</button>` : ""}
      <button class="btn primary" data-back>${T("topics")}</button>`;
  }

  const A = {
    quiz(i) {
      const t = TOPICS[i]; const box = document.getElementById("qb");
      let k = 0, sel = -1, checked = false, score = 0, xp = 0;
      const draw = () => {
        const q = t.quiz[k];
        progress(k + 1, t.quiz.length, xp, T("question"));
        box.innerHTML = `<div class="qcard"><span class="qnum">${k + 1}</span><div>${esc(L(q.q))}</div></div>
          ${q.o.map((o, j) => `<button class="opt" data-o="${j}"><span class="dot"></span>${esc(L(o))}</button>`).join("")}
          <div id="fb"></div><button class="btn primary" id="go" disabled>${T("check")}</button>`;
      };
      box.addEventListener("click", (e) => {
        const o = e.target.closest("[data-o]");
        if (o && !checked) {
          sel = +o.dataset.o;
          box.querySelectorAll(".opt").forEach((b) => b.classList.toggle("sel", b === o));
          document.getElementById("go").disabled = false;
        }
        if (e.target.closest("#go")) {
          const q = t.quiz[k];
          if (!checked) {
            checked = true; S.total++;
            const opts = box.querySelectorAll(".opt");
            opts[q.a].classList.add("ok");
            const fb = document.getElementById("fb");
            if (sel === q.a) { score++; S.right++; xp += 20; addXP(20); fb.className = "fb ok"; fb.textContent = T("correct"); }
            else { opts[sel].classList.add("bad"); fb.className = "fb bad"; fb.textContent = `${T("wrong")} ${L(q.o[q.a])}`; }
            save(); document.getElementById("xp").textContent = xp;
            e.target.closest("#go").textContent = k === t.quiz.length - 1 ? T("finish") : T("next");
          } else if (k < t.quiz.length - 1) { k++; sel = -1; checked = false; draw(); }
          else resultView(box, score, t.quiz.length, xp, finishTopic(i, "quiz", score));
        }
      });
      draw();
    },

    probs(i) {
      const t = TOPICS[i]; const box = document.getElementById("pb");
      let k = 0, checked = false, score = 0, xp = 0;
      const draw = () => {
        const p = t.probs[k];
        progress(k + 1, t.probs.length, xp, T("problem"));
        box.innerHTML = `<div class="pcard">${esc(L(p.q))}
            <div class="given"><b>${T("given")}</b>${p.g.map(esc).join("<br>")}</div>
            <div class="given f"><b>${T("find")}</b>${esc(p.f)}</div></div>
          <div class="formula"><span class="ic">${I("book")}</span><div><small>${T("formula")}</small>${esc(p.fm)}</div></div>
          <button class="hint" id="hint">${I("bulb")}<span>${T("showHint")}</span></button>
          <div class="lbl">${T("yourAns")}</div>
          <label class="ans"><input id="in" inputmode="decimal" autocomplete="off" placeholder="0"><span class="lbl">${esc(p.u)}</span></label>
          <div id="fb"></div><button class="btn primary" id="go">${T("check")}</button>`;
      };
      box.addEventListener("click", (e) => {
        const p = t.probs[k];
        if (e.target.closest("#hint")) e.target.closest("#hint").querySelector("span").textContent = L(p.h);
        if (!e.target.closest("#go")) return;
        if (!checked) {
          const v = parseFloat(document.getElementById("in").value.replace(",", ".").replace(/\s/g, ""));
          if (Number.isNaN(v)) return document.getElementById("in").focus();
          checked = true; S.total++;
          const ok = Math.abs(v - p.a) <= Math.max(0.02 * Math.abs(p.a), 0.01);
          const fb = document.getElementById("fb");
          if (ok) { score++; S.right++; xp += 30; addXP(30); fb.className = "fb ok"; fb.textContent = T("correct").replace("20", "30"); }
          else { fb.className = "fb bad"; fb.textContent = `${T("wrong")} ${p.a} ${p.u}`; }
          save(); document.getElementById("xp").textContent = xp;
          e.target.closest("#go").textContent = k === t.probs.length - 1 ? T("finish") : T("next");
        } else if (k < t.probs.length - 1) { k++; checked = false; draw(); }
        else resultView(box, score, t.probs.length, xp, finishTopic(i, "probs", score));
      });
      box.addEventListener("keydown", (e) => { if (e.key === "Enter") document.getElementById("go").click(); });
      draw();
    },

    win() {
      document.getElementById("share").onclick = async () => {
        const url = location.href.split("#")[0];
        try { if (navigator.share) await navigator.share({ title: "NUVION", url }); else { await navigator.clipboard.writeText(url); toast(T("copied")); } } catch {}
      };
    },

    ar() {
      const cv = document.getElementById("cv"), ctx = cv.getContext("2d");
      let el = 3, auto = true, zoom = 1, ax = 0.5, ay = 0, info = false, drag = null, raf;
      const shells = (z) => { const c = [2, 8, 18, 32], r = []; for (const m of c) { if (z <= 0) break; r.push(Math.min(z, m)); z -= m; } return r; };
      const setEl = (k) => {
        el = k; const e = ELEMENTS[k];
        document.getElementById("np").textContent = e.p;
        document.getElementById("ne").textContent = e.p;
        document.getElementById("nn").textContent = e.n;
        document.querySelectorAll("[data-el]").forEach((b) => b.classList.toggle("on", +b.dataset.el === k));
        drawInfo();
      };
      const drawInfo = () => {
        const e = ELEMENTS[el];
        document.getElementById("inf").innerHTML = info ? `<div class="info"><b>${e.s} — ${L(e.t)}</b><br>Z = ${e.p}, A = ${e.p + e.n}<br>${shells(e.p).map((n, i) => `n=${i + 1}: ${n}e⁻`).join(" · ")}</div>` : "";
      };
      // nucleons positions (fixed sphere packing)
      const nuc = []; const rnd = (s) => { const x = Math.sin(s * 999) * 10000; return x - Math.floor(x); };
      for (let j = 0; j < 30; j++) { const u = rnd(j + 1) * 2 - 1, th = rnd(j + 50) * 6.283, r = Math.cbrt(rnd(j + 99)); nuc.push([Math.sqrt(1 - u * u) * Math.cos(th) * r, Math.sqrt(1 - u * u) * Math.sin(th) * r, u * r]); }
      const rot = ([x, y, z]) => {
        const cy = Math.cos(ay), sy = Math.sin(ay), cx = Math.cos(ax), sx = Math.sin(ax);
        const x1 = x * cy + z * sy, z1 = -x * sy + z * cy;
        return [x1, y * cx - z1 * sx, y * sx + z1 * cx];
      };
      const W = cv.width, C = W / 2;
      const proj = (p) => { const f = 600 / (600 + p[2] * zoom); return [C + p[0] * zoom * f, C + p[1] * zoom * f, f, p[2]]; };
      const frame = (ts) => {
        if (auto && !drag) ay += 0.006;
        ctx.clearRect(0, 0, W, W);
        const e = ELEMENTS[el], sh = shells(e.p), items = [];
        sh.forEach((cnt, s) => {
          const R = 160 + s * 70, tilt = [0.3, 1.2, -0.8, 2][s];
          const pt = (a) => { const x = Math.cos(a) * R, y = Math.sin(a) * R * Math.sin(tilt), z = Math.sin(a) * R * Math.cos(tilt); return rot([x, y, z]); };
          ctx.beginPath();
          for (let a = 0; a <= 6.3; a += 0.1) { const q = proj(pt(a)); a ? ctx.lineTo(q[0], q[1]) : ctx.moveTo(q[0], q[1]); }
          ctx.strokeStyle = "rgba(80,140,240,.75)"; ctx.lineWidth = 3; ctx.stroke();
          for (let j = 0; j < cnt; j++) items.push({ p: proj(pt(ts / (900 + s * 500) + (j * 6.283) / cnt)), c: "#3b8cff", r: 12 });
        });
        const A = e.p + e.n, nr = 20 + Math.cbrt(A) * 16;
        for (let j = 0; j < Math.min(A, 30); j++) items.push({ p: proj(rot(nuc[j].map((v) => v * nr))), c: j < Math.min(e.p, Math.ceil(Math.min(A, 30) * e.p / A)) ? "#f2c94c" : "#c8d3e6", r: 15 });
        items.sort((a, b) => b.p[3] - a.p[3]).forEach(({ p, c, r }) => {
          ctx.beginPath(); ctx.arc(p[0], p[1], r * p[2] * zoom, 0, 7);
          const g = ctx.createRadialGradient(p[0] - 3, p[1] - 3, 1, p[0], p[1], r * p[2] * zoom);
          g.addColorStop(0, "#fff"); g.addColorStop(0.3, c); g.addColorStop(1, c);
          ctx.fillStyle = g; ctx.shadowColor = c; ctx.shadowBlur = c === "#3b8cff" ? 18 : 6; ctx.fill(); ctx.shadowBlur = 0;
        });
        raf = requestAnimationFrame(frame);
      };
      cv.addEventListener("pointerdown", (ev) => { drag = [ev.clientX, ev.clientY]; cv.setPointerCapture(ev.pointerId); });
      cv.addEventListener("pointermove", (ev) => { if (!drag) return; ay += (ev.clientX - drag[0]) * 0.01; ax += (ev.clientY - drag[1]) * 0.01; drag = [ev.clientX, ev.clientY]; });
      cv.addEventListener("pointerup", () => (drag = null));
      document.querySelectorAll("[data-el]").forEach((b) => (b.onclick = () => setEl(+b.dataset.el)));
      const bR = document.getElementById("b-rot"), bZ = document.getElementById("b-zoom"), bI = document.getElementById("b-info");
      bR.onclick = () => { auto = !auto; bR.classList.toggle("on", auto); };
      bZ.onclick = () => { zoom = zoom === 1 ? 1.35 : zoom === 1.35 ? 0.7 : 1; bZ.classList.toggle("on", zoom !== 1); };
      bI.onclick = () => { info = !info; bI.classList.toggle("on", info); drawInfo(); };
      setEl(3); raf = requestAnimationFrame(frame);
      cleanup = () => cancelAnimationFrame(raf);
    },
  };

  stack = [["home"]];
  render();
})();
