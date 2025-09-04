function mascaraCPF(campo) {
    let cpf = campo.value.replace(/\D/g, '');
    cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
    cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
    cpf = cpf.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    campo.value = cpf;
}

function mascaraData(campo) {
    let valor = campo.value.replace(/\D/g, '');
    if (valor.length > 2 && valor.length <= 4) {
        valor = valor.replace(/(\d{2})(\d+)/, '$1/$2');
    } else if (valor.length > 4) {
        valor = valor.replace(/(\d{2})(\d{2})(\d+)/, '$1/$2/$3');
    }
    campo.value = valor;
}

function converterDataParaISO(dataPtBr) {
    const partes = dataPtBr.split('/');
    if (partes.length !== 3) return null;
    const dia = partes[0].padStart(2, '0');
    const mes = partes[1].padStart(2, '0');
    const ano = partes[2];
    return `${ano}-${mes}-${dia}`;
}

document.getElementById('formCadastro').addEventListener('submit', async function(event) {
    event.preventDefault();

    const nome = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const cpf = document.getElementById('cpf').value.trim();
    const dataNascimentoPtBr = document.getElementById('dataNascimento').value.trim();
    const tipoSanguineo = document.getElementById('tipoSanguineo').value;
    const alturaCm = parseInt(document.getElementById('altura').value); // agora em centímetros
    const pesoKg = parseFloat(document.getElementById('peso').value);   // já em kg
    const senha = document.getElementById('password').value;
    const confirmarSenha = document.getElementById('cpassword').value;

    // Valida campos obrigatórios
    if (!nome || !email || !cpf || !dataNascimentoPtBr || !tipoSanguineo || !alturaCm || !pesoKg || !senha || !confirmarSenha) {
        alert('Preencha todos os campos.');
        return;
    }

    // Valida senhas
    if (senha !== confirmarSenha) {
        alert('As senhas não coincidem.');
        return;
    }

    // Valida data
    const dataNascimentoISO = converterDataParaISO(dataNascimentoPtBr);
    if (!dataNascimentoISO) {
        alert('Data de nascimento inválida.');
        return;
    }

    // Valida altura e peso de acordo com o backend
    if (alturaCm < 120 || alturaCm > 230) {
        alert('Altura inválida. Insira um valor entre 120 e 230 cm.');
        return;
    }
    if (pesoKg < 30 || pesoKg > 300) {
        alert('Peso inválido. Insira um valor entre 30 e 300 kg.');
        return;
    }

    // Monta objeto para envio compatível com a entidade Java
    const dadosCadastro = {
        nome: nome,
        email: email,
        cpf: cpf,
        dataNascimento: dataNascimentoISO,
        tipoSanguineo: tipoSanguineo,
        alturaCm: alturaCm,
        pesoKg: pesoKg,
        senha: senha
    };

    try {
        const response = await fetch('http://localhost:8080/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosCadastro)
        });

        const texto = await response.text(); // pega a resposta do backend
        console.log('Status HTTP:', response.status);
        console.log('Resposta do backend:', texto);

        if (response.ok) {
            alert('Cadastro realizado com sucesso! Você será redirecionado para o login.');
            window.location.href = '../login/login.html';
        } else {
            alert('Erro ao cadastrar. Veja o console do navegador para detalhes.');
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('Erro na comunicação com o servidor.');
    }
});
