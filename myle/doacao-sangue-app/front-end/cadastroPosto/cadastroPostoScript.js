    document.getElementById("formCadastroPosto").addEventListener("submit", function(event) {
    event.preventDefault();

    const posto = {
    nome: document.getElementById("nome").value,
    endereco: document.getElementById("endereco").value,
    cidade: document.getElementById("cidade").value,
    estado: document.getElementById("estado").value,
    horarioFuncionamento: document.getElementById("horarioFuncionamento").value
};

    fetch("http://localhost:8080/postos", {
    method: "POST",
    headers: {
    "Content-Type": "application/json"
},
    body: JSON.stringify(posto)
})
    .then(response => response.json())
    .then(data => {
    alert("Posto cadastrado com sucesso!");
    console.log("Resposta do servidor:", data);
})
    .catch(error => {
    console.error("Erro ao cadastrar o posto:", error);
    alert("Erro ao cadastrar o posto!");
});
});

