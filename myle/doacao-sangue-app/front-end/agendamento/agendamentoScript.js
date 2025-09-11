const daysContainer = document.getElementById('days');
const monthYear = document.getElementById('month-year');
const agendamentosContainer = document.getElementById('agendamentos');
const textoInicial = document.querySelector('.history p');

let currentDate = new Date();
let selectedDate = null;

// Função para renderizar o calendário
function renderCalendar(date) {
    const year = date.getFullYear();
    const month = date.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();
    const prevLastDate = new Date(year, month, 0).getDate();

    monthYear.textContent = `${date.toLocaleString('pt-BR', { month: 'long' })} ${year}`;

    let days = '';

    for (let i = firstDay - 1; i >= 0; i--) {
        days += `<div class="prev-month">${prevLastDate - i}</div>`;
    }

    for (let i = 1; i <= lastDate; i++) {
        const today = new Date();
        const isToday = i === today.getDate() && month === today.getMonth() && year === today.getFullYear();
        days += `<div class="${isToday ? 'today' : ''}" data-day="${i}">${i}</div>`;
    }

    const nextDays = 42 - (firstDay + lastDate);
    for (let i = 1; i <= nextDays; i++) {
        days += `<div class="next-month">${i}</div>`;
    }

    daysContainer.innerHTML = days;

    document.querySelectorAll('.days div').forEach(day => {
        day.addEventListener('click', () => {
            if (!day.classList.contains('prev-month') && !day.classList.contains('next-month')) {
                document.querySelectorAll('.days div').forEach(d => d.classList.remove('selected'));
                day.classList.add('selected');
                selectedDate = new Date(year, month, day.dataset.day);
                openModal(selectedDate);
            }
        });
    });
}

// Navegação do calendário
document.getElementById('prev').addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(currentDate);
});

document.getElementById('next').addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(currentDate);
});

// Função do modal
function openModal(date) {
    document.getElementById('selected-date').textContent = date.toLocaleDateString('pt-BR');
    document.getElementById('modal').style.display = 'flex';
}

// Fecha modal
document.getElementById('close-modal').addEventListener('click', () => {
    document.getElementById('modal').style.display = 'none';
});

// Função para preencher os dados do usuário na sidebar
function preencherSidebar(dados) {
    document.querySelector('.sidebar .Nome').textContent = dados.nome;
    document.querySelector('.sidebar .info:nth-child(3)').textContent = `CPF: ${dados.cpf}`;
    document.querySelector('.sidebar .info:nth-child(4)').textContent = `Tipo sanguíneo: ${dados.tipoSanguineo}`;
    document.querySelector('.sidebar .info:nth-child(5)').textContent = `Peso: ${dados.pesoKg} kg`;
    document.querySelector('.sidebar .info:nth-child(6)').textContent = `Altura: ${dados.alturaCm} cm`;
}

// ------------------- FUNÇÕES COM DEBUG -------------------

// Função para carregar dados do usuário com debug
async function carregarUsuario() {
    try {
        const token = localStorage.getItem('token');
        console.log('Token armazenado:', token); // DEBUG

        if (!token) {
            alert('Usuário não autenticado (token vazio)');
            return;
        }

        const response = await fetch('http://localhost:8080/auth/me', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        console.log('Status do fetch /auth/me:', response.status); // DEBUG

        if (!response.ok) throw new Error(`Erro ao carregar dados do usuário: status ${response.status}`);

        const dados = await response.json();
        console.log('Dados do usuário carregados:', dados); // DEBUG
        preencherSidebar(dados);

    } catch (error) {
        console.error('Erro em carregarUsuario():', error);
        alert('Não foi possível carregar os dados do usuário. Veja console.');
    }
}

// Função para carregar histórico com debug
async function carregarHistorico() {
    try {
        const token = localStorage.getItem('token');
        console.log('Token para histórico:', token); // DEBUG

        if (!token) return;

        const response = await fetch('http://localhost:8080/agendamentos/historico', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        console.log('Status do fetch /agendamentos/historico:', response.status); // DEBUG

        if (!response.ok) throw new Error(`Erro ao carregar histórico: status ${response.status}`);

        const historico = await response.json();
        console.log('Histórico carregado:', historico); // DEBUG

        if (historico.length > 0) {
            textoInicial.style.display = 'none';
        }

        historico.forEach(item => {
            const div = document.createElement('div');
            div.classList.add('agendamento-item');
            div.innerHTML = `<span>${new Date(item.data).toLocaleDateString('pt-BR')} - ${item.horario} - ${item.nomePosto} (${item.cidade}/${item.estado})</span>`;

            const vistoBtn = document.createElement('button');
            vistoBtn.textContent = '✔';
            vistoBtn.addEventListener('click', () => {
                div.style.textDecoration = 'line-through';
                div.style.opacity = '0.6';
                vistoBtn.disabled = true;
            });

            div.appendChild(vistoBtn);
            agendamentosContainer.appendChild(div);
        });

    } catch (error) {
        console.error('Erro em carregarHistorico():', error);
    }
}

// ------------------- CONFIRMAR AGENDAMENTO -------------------
document.getElementById('confirmar-agendamento').addEventListener('click', async () => {
    const hora = document.getElementById('hora').value;
    const cidade = document.getElementById('cidade').value;
    const estado = document.getElementById('estado').value;
    const posto = document.getElementById('posto').value;

    if (!hora || !cidade || !estado || !posto) {
        alert('Preencha todos os campos!');
        return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
        alert('Usuário não autenticado');
        return;
    }

    const agendamentoDTO = {
        data: selectedDate.toISOString().split('T')[0],
        horario: hora,
        cidade: cidade,
        estado: estado,
        nomePosto: posto
    };

    try {
        const response = await fetch('http://localhost:8080/agendamentos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(agendamentoDTO)
        });

        if (!response.ok) throw new Error('Erro ao salvar agendamento');

        const agendamentoSalvo = await response.json();

        const div = document.createElement('div');
        div.classList.add('agendamento-item');
        div.innerHTML = `<span>${selectedDate.toLocaleDateString('pt-BR')} - ${hora} - ${posto} (${cidade}/${estado})</span>`;

        const vistoBtn = document.createElement('button');
        vistoBtn.textContent = '✔';
        vistoBtn.addEventListener('click', () => {
            div.style.textDecoration = 'line-through';
            div.style.opacity = '0.6';
            vistoBtn.disabled = true;
        });

        div.appendChild(vistoBtn);
        agendamentosContainer.appendChild(div);

        if (textoInicial) textoInicial.style.display = 'none';

        document.getElementById('modal').style.display = 'none';
        document.getElementById('hora').value = '';
        document.getElementById('cidade').value = '';
        document.getElementById('estado').value = '';

    } catch (error) {
        console.error(error);
        alert('Não foi possível salvar o agendamento.');
    }
});

// Inicializa calendário
renderCalendar(currentDate);

// ------------------- CARREGA DADOS E HISTÓRICO -------------------
window.addEventListener('DOMContentLoaded', () => {
    carregarUsuario();
    carregarHistorico();
});
