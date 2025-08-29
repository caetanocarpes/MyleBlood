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
    const alturaM = parseFloat(document.getElementById('altura').value); // em metros
    const pesoKg = parseFloat(document.getElementById('peso').value);
    const senha = document.getElementById('password').value;
    const confirmarSenha = document.getElementById('cpassword').value;

    // Valida campos obrigatórios
    if (!nome || !email || !cpf || !dataNascimentoPtBr || !tipoSanguineo || !alturaM || !pesoKg || !senha || !confirmarSenha) {
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

    // Valida altura e peso
    if (alturaM < 0.5 || alturaM > 2.5) {
        alert('Altura inválida. Insira um valor entre 0,50 e 2,50 metros.');
        return;
    }
    if (pesoKg < 30 || pesoKg > 300) {
        alert('Peso inválido. Insira um valor entre 30 e 300 kg.');
        return;
    }

    // Converte altura para cm (inteiro) para compatibilidade com Java
    const alturaCm = Math.round(alturaM * 100);

    // Monta objeto para envio compatível com sua entidade Java
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

        if (response.ok) {
            alert('Cadastro realizado com sucesso! Você será redirecionado para o login.');
            window.location.href = '../login/login.html';
        } else {
            const erro = await response.text();
            alert('Erro ao cadastrar: ' + erro);
        }
    } catch (error) {
        console.error('Erro na requisição:', error);
        alert('Erro na comunicação com o servidor.');
    }
});
