const telemetriaApiUrl = '/api/estadisticas/telemetria';
const distribucionApiUrl = '/api/estadisticas/distribucion-modos';
const cultivosApiUrl = '/api/cultivos';

let chartTelemetria = null;
let chartDistribucion = null;

function setText(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

function formatoDecimal(value, decimals = 1) {
    const number = Number(value);
    return Number.isFinite(number) ? number.toFixed(decimals) : '--';
}

function actualizarKpis(telemetria, distribucion) {
    const ultimaLectura = telemetria.length ? telemetria[telemetria.length - 1] : null;

    setText('kpi-nivel-tanque', ultimaLectura ? `${formatoDecimal(ultimaLectura.distancia)} cm` : '--');
    setText('kpi-ultima-humedad', ultimaLectura ? `${ultimaLectura.humedad}%` : '--');
    setText('kpi-cultivo-activo', distribucion.cultivoActivo || 'Sin cultivo');
    setText('kpi-total-riegos', distribucion.total ?? 0);
}

function crearGradiente(ctx, color) {
    const gradient = ctx.createLinearGradient(0, 0, 0, 320);
    gradient.addColorStop(0, color);
    gradient.addColorStop(1, 'rgba(255, 255, 255, 0)');
    return gradient;
}

function unidadDataset(datasetLabel) {
    return datasetLabel.includes('Humedad') ? '%' : 'cm';
}

function pintarGraficoTelemetria(telemetria) {
    const canvas = document.getElementById('chartTelemetria');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    if (chartTelemetria) {
        chartTelemetria.destroy();
    }

    chartTelemetria = new Chart(ctx, {
        type: 'line',
        data: {
            labels: telemetria.map(item => item.etiqueta),
            datasets: [
                {
                    label: 'Humedad',
                    data: telemetria.map(item => item.humedad),
                    yAxisID: 'yHumedad',
                    borderColor: '#168aad',
                    backgroundColor: crearGradiente(ctx, 'rgba(22, 138, 173, 0.26)'),
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#168aad',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Distancia',
                    data: telemetria.map(item => item.distancia),
                    yAxisID: 'yDistancia',
                    borderColor: '#f59e0b',
                    backgroundColor: crearGradiente(ctx, 'rgba(245, 158, 11, 0.23)'),
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#f59e0b',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#334155',
                        font: { weight: '700' },
                        usePointStyle: true
                    }
                },
                tooltip: {
                    callbacks: {
                        label: context => `${context.dataset.label}: ${context.parsed.y} ${unidadDataset(context.dataset.label)}`
                    }
                }
            },
            scales: {
                yHumedad: {
                    type: 'linear',
                    position: 'left',
                    min: 0,
                    max: 100,
                    title: {
                        display: true,
                        text: 'Humedad (%)',
                        color: '#475569'
                    },
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: {
                        color: '#64748b',
                        callback: value => `${value}%`
                    }
                },
                yDistancia: {
                    type: 'linear',
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Distancia (cm)',
                        color: '#475569'
                    },
                    grid: { drawOnChartArea: false },
                    ticks: {
                        color: '#64748b',
                        callback: value => `${value} cm`
                    }
                },
                x: {
                    grid: { color: 'rgba(0,0,0,0.05)' },
                    ticks: { color: '#64748b' }
                }
            }
        }
    });
}

function pintarGraficoDistribucion(distribucion) {
    const canvas = document.getElementById('chartDistribucionModos');
    if (!canvas) return;

    if (chartDistribucion) {
        chartDistribucion.destroy();
    }

    chartDistribucion = new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels: ['Automático', 'Manual'],
            datasets: [{
                data: [distribucion.automatico || 0, distribucion.manual || 0],
                backgroundColor: ['#2563eb', '#14b8a6'],
                borderColor: '#ffffff',
                borderWidth: 5,
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#334155',
                        font: { weight: '700' },
                        padding: 18,
                        usePointStyle: true
                    }
                },
                tooltip: {
                    callbacks: {
                        label: context => `${context.label}: ${context.parsed} riegos`
                    }
                }
            },
            cutout: '68%'
        }
    });
}

function obtenerFiltroCultivo() {
    const filtro = document.getElementById('filtroCultivo');
    return filtro ? filtro.value : '';
}

function construirUrlConFiltro(baseUrl) {
    const cultivoId = obtenerFiltroCultivo();

    if (!cultivoId) {
        return baseUrl;
    }

    const parametros = new URLSearchParams();
    parametros.append('cultivoId', cultivoId);
    return `${baseUrl}?${parametros.toString()}`;
}

async function cargarFiltroCultivos() {
    const select = document.getElementById('filtroCultivo');
    if (!select) return;

    try {
        const response = await fetch(cultivosApiUrl);
        if (!response.ok) {
            throw new Error('No se pudieron cargar los cultivos.');
        }

        const cultivos = await response.json();
        cultivos.forEach(cultivo => {
            if (select.querySelector(`option[value="${cultivo.id}"]`)) {
                return;
            }

            const option = document.createElement('option');
            option.value = cultivo.id;
            option.textContent = cultivo.nombre;
            select.appendChild(option);
        });
    } catch (error) {
        console.error(error);
    }
}

async function cargarDashboardEstadisticas() {
    try {
        const [telemetriaResponse, distribucionResponse] = await Promise.all([
            fetch(construirUrlConFiltro(telemetriaApiUrl)),
            fetch(construirUrlConFiltro(distribucionApiUrl))
        ]);

        if (!telemetriaResponse.ok || !distribucionResponse.ok) {
            throw new Error('No se pudieron cargar las estadísticas.');
        }

        const telemetria = await telemetriaResponse.json();
        const distribucion = await distribucionResponse.json();

        actualizarKpis(telemetria, distribucion);
        pintarGraficoTelemetria(telemetria);
        pintarGraficoDistribucion(distribucion);
    } catch (error) {
        console.error(error);
    }
}

async function inicializarDashboardEstadisticas() {
    await cargarFiltroCultivos();

    const filtro = document.getElementById('filtroCultivo');
    if (filtro) {
        filtro.addEventListener('change', cargarDashboardEstadisticas);
    }

    cargarDashboardEstadisticas();
}

document.addEventListener('DOMContentLoaded', inicializarDashboardEstadisticas);
