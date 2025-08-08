function mascaraCPF(campo) {
    let cpf = campo.value.replace(/\D/g, ''); // Remove tudo que não for número
    cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
    cpf = cpf.replace(/(\d{3})(\d)/, '$1.$2');
    cpf = cpf.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    campo.value = cpf;
}

function mascaraData(campo) {
    let valor = campo.value.replace(/\D/g, ''); // Remove tudo que não for número

    if (valor.length > 2 && valor.length <= 4) {
      valor = valor.replace(/(\d{2})(\d+)/, '$1/$2');
    } else if (valor.length > 4) {
      valor = valor.replace(/(\d{2})(\d{2})(\d+)/, '$1/$2/$3');
    }

    campo.value = valor;
}