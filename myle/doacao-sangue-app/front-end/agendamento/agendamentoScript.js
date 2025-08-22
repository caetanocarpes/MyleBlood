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

    // Dias do mês anterior
    for (let i = firstDay - 1; i >= 0; i--) {
        days += `<div class="prev-month">${prevLastDate - i}</div>`;
    }

    // Dias do mês atual
    for (let i = 1; i <= lastDate; i++) {
        const today = new Date();
        const isToday = i === today.getDate() && month === today.getMonth() && year === today.getFullYear();
        days += `<div class="${isToday ? 'today' : ''}" data-day="${i}">${i}</div>`;
    }

    // Dias do próximo mês
    const nextDays = 42 - (firstDay + lastDate);
    for (let i = 1; i <= nextDays; i++) {
        days += `<div class="next-month">${i}</div>`;
    }

    daysContainer.innerHTML = days;

    // Evento de clique nos dias atuais
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

// Confirmar agendamento
document.getElementById('confirmar-agendamento').addEventListener('click', () => {
    const hora = document.getElementById('hora').value;

    if (!hora) {
        alert('Preencha o horário!');
        return;
    }

    // Cria elemento do agendamento
    const agendamentoDiv = document.createElement('div');
    agendamentoDiv.classList.add('agendamento-item');

    // Botão de visto
    const vistoBtn = document.createElement('button');
    vistoBtn.textContent = '✔';
    vistoBtn.addEventListener('click', () => {
        agendamentoDiv.style.textDecoration = 'line-through';
        agendamentoDiv.style.opacity = '0.6';
        vistoBtn.disabled = true;
    });

    agendamentoDiv.innerHTML = `<span>${selectedDate.toLocaleDateString('pt-BR')} - ${hora}</span>`;
    agendamentoDiv.appendChild(vistoBtn);

    agendamentosContainer.appendChild(agendamentoDiv);

    // Esconde o texto inicial se for o primeiro agendamento
    if (textoInicial) {
        textoInicial.style.display = 'none';
    }

    // Fecha modal
    document.getElementById('modal').style.display = 'none';

    // Limpa input
    document.getElementById('hora').value = '';
});

// Inicializa calendário
renderCalendar(currentDate);
