// Mostrar/Ocultar Senha 
const toggle = document.getElementById('togglePassword');
const password = document.getElementById('password');

toggle.addEventListener('change', () => {
    password.type = toggle.checked ? 'text' : 'password';
  });

document.getElementById("botaoRedirecionar").addEventListener("click", function() {
    window.location.href = "../sign-in/sign-in.html";
  });

