(function(){
  const STORAGE_KEY = 'ecotrack-theme';
  const defaultTheme = 'dark';

  function getTheme(){
    return localStorage.getItem(STORAGE_KEY) || defaultTheme;
  }
  function setTheme(t){
    document.documentElement.setAttribute('data-theme', t);
    localStorage.setItem(STORAGE_KEY, t);
    updatePreviewImages(t);
    updateToggleIcon(t);
  }

  function updateToggleIcon(t){
    const btn = document.getElementById('theme-toggle-btn');
    if(!btn) return;
    btn.textContent = t === 'light' ? '🌙 Dark' : '☀ Light';
  }

  function swapToLight(src){
    if(!src) return src;
    if(src.includes('-light.svg')) return src;
    return src.replace('.svg','-light.svg');
  }
  function swapToDark(src){
    if(!src) return src;
    if(src.includes('-light.svg')) return src.replace('-light.svg','.svg');
    return src;
  }

  function updatePreviewImages(theme){
    // swap images inside preview-grid or any img with -preview.svg
    document.querySelectorAll('img').forEach(img=>{
      const s = img.getAttribute('src');
      if(!s || !s.endsWith('.svg')) return;
      if(theme === 'light') img.src = swapToLight(s);
      else img.src = swapToDark(s);
    });
  }

  function insertToggle(){
    const top = document.querySelector('.topbar');
    if(!top) return;
    const existing = document.getElementById('theme-toggle-btn');
    function attachHandler(btn){
      if(!btn.__theme_handler_attached){
        btn.addEventListener('click', ()=>{
          const current = document.documentElement.getAttribute('data-theme') || defaultTheme;
          setTheme(current === 'light' ? 'dark' : 'light');
        });
        btn.__theme_handler_attached = true;
      }
    }
    if(existing){
      attachHandler(existing);
      return;
    }
    const btn = document.createElement('button');
    btn.id = 'theme-toggle-btn';
    btn.style.marginLeft = '12px';
    btn.style.padding = '6px 10px';
    btn.style.borderRadius = '8px';
    btn.style.border = '1px solid rgba(255,255,255,0.06)';
    btn.style.background = 'transparent';
    btn.style.cursor = 'pointer';
    attachHandler(btn);
    top.appendChild(btn);
  }

  // init: run immediately if DOM already loaded, otherwise wait
  if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', ()=>{
      insertToggle();
      const t = getTheme();
      setTheme(t);
    });
  } else {
    insertToggle();
    const t = getTheme();
    setTheme(t);
  }
})();
