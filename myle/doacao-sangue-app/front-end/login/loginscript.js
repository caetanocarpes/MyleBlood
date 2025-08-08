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

document.querySelector("form").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const senha = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, senha: senha })
        });

        if (response.ok) {
            const data = await response.json();
            alert("Login realizado com sucesso!");
            localStorage.setItem("token", data.token);
            window.location.href = "../dashboard/index.html";
        } else {
            // Tenta pegar o texto da resposta
            const erroTexto = await response.text();
            // Pega o status HTTP
            const status = response.status;
            alert(`Erro no login: status ${status}\nMensagem: ${erroTexto || "(sem mensagem)"}`);
        }
    } catch (error) {
        console.error("Erro ao fazer login:", error);
        alert("Erro na comunicação com o servidor.");
    }
});




