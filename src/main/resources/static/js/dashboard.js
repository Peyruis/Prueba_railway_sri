const toggle = document.getElementById('sidebarToggle');
const chartPanel = document.getElementById('chartPanel');
const cards = Array.from(document.querySelectorAll('.metric-card'));
const canvas = document.getElementById('humidityChart');
const ctx = canvas ? canvas.getContext('2d') : null;
const mqttStatusBadge = document.getElementById('mqttStatusBadge');
const mqttStatusIcon = document.getElementById('mqttStatusIcon');
const mqttStatusText = document.getElementById('mqttStatusText');

const dataPoints = [38, 55, 73, 90, 85, 73, 55, 73, 90];
const labels = ['06:00', '06:03', '06:06', '06:09', '06:12', '06:15', '06:18', '06:21', '06:24'];
const yTicks = [38, 55, 73, 90];

if (toggle) {
    toggle.addEventListener('click', () => {
        if (window.innerWidth <= 780) {
            document.body.classList.toggle('sidebar-open');
            return;
        }
        document.body.classList.toggle('sidebar-collapsed');
    });
}

document.querySelectorAll('.nav-item-link').forEach(item => {
    item.addEventListener('click', () => {
        document.querySelectorAll('.nav-item-link').forEach(link => link.classList.remove('active'));
        item.classList.add('active');
    });
});

function resizeCanvas() {
    if (!canvas || !ctx) return;

    const rect = canvas.getBoundingClientRect();
    const ratio = window.devicePixelRatio || 1;
    canvas.width = rect.width * ratio;
    canvas.height = rect.height * ratio;
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
    drawChart();
}

function yFor(value, top, graphH) {
    const min = 38;
    const max = 90;
    return top + graphH - ((value - min) / (max - min)) * graphH;
}

function drawChart() {
    if (!canvas || !ctx) return;

    const width = canvas.clientWidth;
    const height = canvas.clientHeight;
    if (!width || !height) return;

    ctx.clearRect(0, 0, width, height);

    const pad = { left: 54, right: 26, top: 18, bottom: 38 };
    const graphW = width - pad.left - pad.right;
    const graphH = height - pad.top - pad.bottom;

    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, width, height);

    ctx.strokeStyle = '#dcebd5';
    ctx.lineWidth = 1;
    ctx.font = "700 11px Inter, sans-serif";
    ctx.textBaseline = 'middle';

    yTicks.forEach(tick => {
        const y = yFor(tick, pad.top, graphH);
        ctx.beginPath();
        ctx.moveTo(pad.left, y);
        ctx.lineTo(width - pad.right, y);
        ctx.stroke();
        ctx.fillStyle = '#76966c';
        ctx.fillText(tick + '%', 14, y);
    });

    const step = graphW / (dataPoints.length - 1);
    const points = dataPoints.map((value, index) => ({
        x: pad.left + index * step,
        y: yFor(value, pad.top, graphH),
        value
    }));

    const gradient = ctx.createLinearGradient(0, pad.top, 0, pad.top + graphH);
    gradient.addColorStop(0, 'rgba(79, 163, 66, 0.26)');
    gradient.addColorStop(1, 'rgba(79, 163, 66, 0.02)');

    ctx.beginPath();
    points.forEach((point, index) => {
        if (index === 0) ctx.moveTo(point.x, point.y);
        else ctx.lineTo(point.x, point.y);
    });
    ctx.lineTo(points[points.length - 1].x, pad.top + graphH);
    ctx.lineTo(points[0].x, pad.top + graphH);
    ctx.closePath();
    ctx.fillStyle = gradient;
    ctx.fill();

    ctx.beginPath();
    points.forEach((point, index) => {
        if (index === 0) ctx.moveTo(point.x, point.y);
        else ctx.lineTo(point.x, point.y);
    });
    ctx.strokeStyle = '#3f9637';
    ctx.lineWidth = 3;
    ctx.lineJoin = 'round';
    ctx.lineCap = 'round';
    ctx.stroke();

    points.forEach(point => {
        ctx.beginPath();
        ctx.arc(point.x, point.y, 6, 0, Math.PI * 2);
        ctx.fillStyle = '#ffffff';
        ctx.fill();
        ctx.lineWidth = 3;
        ctx.strokeStyle = '#2f7d32';
        ctx.stroke();
    });

    ctx.font = "700 10px Inter, sans-serif";
    ctx.textBaseline = 'top';
    ctx.fillStyle = '#7d9d72';
    labels.forEach((label, index) => {
        if (index % 2 !== 0) return;
        const x = points[index].x;
        ctx.fillText(label, x - 14, pad.top + graphH + 13);
    });
}

function simulateRefresh() {
    if (!chartPanel) return;

    chartPanel.classList.remove('is-refreshing');
    cards.forEach(card => card.classList.remove('is-refreshing'));
    void chartPanel.offsetWidth;
    chartPanel.classList.add('is-refreshing');
    cards.forEach(card => card.classList.add('is-refreshing'));
}

function pintarEstadoMqtt(tipo) {
    if (!mqttStatusBadge || !mqttStatusIcon || !mqttStatusText) return;

    mqttStatusBadge.classList.remove('bg-success', 'bg-danger', 'text-success');

    if (tipo === 'conectado') {
        mqttStatusBadge.classList.add('bg-success');
        mqttStatusIcon.className = 'fa-solid fa-cloud-arrow-up';
        mqttStatusText.textContent = 'MQTT Conectado';
        return;
    }

    if (tipo === 'desconectado') {
        mqttStatusBadge.classList.add('bg-danger');
        mqttStatusIcon.className = 'fa-solid fa-triangle-exclamation';
        mqttStatusText.textContent = 'MQTT Desconectado';
        return;
    }

    mqttStatusBadge.classList.add('bg-danger');
    mqttStatusIcon.className = 'fa-solid fa-server';
    mqttStatusText.textContent = 'Servidor Caído';
}

function consultarEstadoVivo() {
    fetch('/api/estado-vivo')
        .then(response => {
            if (!response.ok) {
                throw new Error('Respuesta no válida del servidor');
            }
            return response.json();
        })
        .then(datos => {
            if (datos.mqtt_activo === true) {
                pintarEstadoMqtt('conectado');
            } else {
                pintarEstadoMqtt('desconectado');
            }
        })
        .catch(() => {
            pintarEstadoMqtt('servidor-caido');
        });
}

window.addEventListener('resize', resizeCanvas);
window.addEventListener('load', () => {
    resizeCanvas();
    simulateRefresh();
    consultarEstadoVivo();
    setInterval(simulateRefresh, 3000);
    setInterval(consultarEstadoVivo, 3000);
});
