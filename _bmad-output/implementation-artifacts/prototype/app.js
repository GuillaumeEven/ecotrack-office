(function(){
  const desks = document.querySelectorAll('.desk');
  const drawer = document.getElementById('booking-drawer');
  const drawerTitle = document.getElementById('drawer-title');
  const drawerInfo = document.getElementById('drawer-info');
  const reserveBtn = document.getElementById('reserve-btn');
  const cancelBtn = document.getElementById('cancel-btn');
  const closeDrawer = document.getElementById('close-drawer');
  const snackbar = document.getElementById('snackbar');
  const bookingDate = document.getElementById('booking-date');
  const bookingNote = document.getElementById('booking-note');
  const checkinScreen = document.getElementById('checkin-screen');
  const checkinTitle = document.getElementById('checkin-title');
  const checkinDesc = document.getElementById('checkin-desc');
  const backToMap = document.getElementById('back-to-map');

  let activeDesk = null;

  function openDrawerFor(el){
    activeDesk = el;
    const name = el.dataset.name || el.id;
    drawerTitle.textContent = name;
    drawerInfo.innerHTML = `<strong>${name}</strong><div>Zone: ${el.dataset.zone}</div><div>Amenities: ${el.dataset.amenities}</div>`;
    bookingDate.value = new Date().toISOString().slice(0,10);
    drawer.classList.remove('hidden');
    drawer.setAttribute('aria-hidden','false');
  }

  function closeBooking(){
    activeDesk = null;
    drawer.classList.add('hidden');
    drawer.setAttribute('aria-hidden','true');
  }

  function showSnackbar(msg, timeout=2500){
    snackbar.textContent = msg;
    snackbar.classList.remove('hidden');
    setTimeout(()=> snackbar.classList.add('hidden'), timeout);
  }

  desks.forEach(d=>{
    d.addEventListener('click',(e)=>{
      openDrawerFor(d);
    });
  });

  reserveBtn.addEventListener('click', ()=>{
    if(!activeDesk) return;
    // optimistic update
    activeDesk.classList.remove('available','occupied');
    activeDesk.classList.add('reserved');
    closeBooking();
    showSnackbar(`${activeDesk.dataset.name} reserved — check your email for check-in link`);
  });

  cancelBtn.addEventListener('click', closeBooking);
  closeDrawer.addEventListener('click', closeBooking);

  // handle magic link check-in simulation: ?checkin=desk-4
  function handleCheckinQuery(){
    const q = new URLSearchParams(window.location.search);
    const checkin = q.get('checkin');
    if(!checkin) return;
    const target = document.getElementById(checkin);
    if(!target){
      checkinTitle.textContent = 'Invalid check-in token';
      checkinDesc.textContent = 'This token does not match any desk in this prototype.';
    } else {
      // simulate marking occupied
      target.classList.remove('available','reserved');
      target.classList.add('occupied');
      checkinTitle.textContent = 'Checked in';
      checkinDesc.textContent = `You are checked in at ${target.dataset.name}`;
    }
    checkinScreen.classList.remove('hidden');
  }

  backToMap.addEventListener('click', ()=>{
    checkinScreen.classList.add('hidden');
    const url = new URL(window.location.href);
    url.searchParams.delete('checkin');
    history.replaceState(null,'',url.toString());
  });

  // keyboard escape closes drawer
  window.addEventListener('keydown',(e)=>{
    if(e.key === 'Escape'){
      closeBooking();
      checkinScreen.classList.add('hidden');
    }
  });

  // init
  handleCheckinQuery();
})();
