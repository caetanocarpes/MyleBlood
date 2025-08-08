// Mostrar/Ocultar Senha 
const toggle = document.getElementById('togglePassword');
const password = document.getElementById('password');

toggle.addEventListener('change', () => {
    password.type = toggle.checked ? 'text' : 'password';
  });

document.addEventListener('DOMContentLoaded', function() {
    const botao = document.getElementById('botaoRedirecionar');
    botao.addEventListener('click', function() {
        window.location.href = '../sign-in/sign-in.html'; // link desejado
    });
});

