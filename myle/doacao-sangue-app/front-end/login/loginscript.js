// Mostrar/Ocultar Senha 
const toggle = document.getElementById('togglePassword');
const password = document.getElementById('password');

toggle.addEventListener('change', () => {
    password.type = toggle.checked ? 'text' : 'password';
  });

