document.addEventListener('DOMContentLoaded', () => {
    const botonDescarga = document.getElementById('btnDescargarReportePdf');
    const inputFechaInicio = document.getElementById('fechaInicioReporte');
    const inputFechaFin = document.getElementById('fechaFinReporte');
    const selectCultivo = document.getElementById('reporteCultivoId');

    cargarCultivosReporte(selectCultivo);

    if (!botonDescarga) {
        return;
    }

    botonDescarga.addEventListener('click', () => {
        const parametros = new URLSearchParams();

        if (inputFechaInicio && inputFechaInicio.value) {
            parametros.append('fechaInicio', inputFechaInicio.value);
        }

        if (inputFechaFin && inputFechaFin.value) {
            parametros.append('fechaFin', inputFechaFin.value);
        }

        if (selectCultivo && selectCultivo.value) {
            parametros.append('cultivoId', selectCultivo.value);
        }

        const queryString = parametros.toString();
        const url = queryString
            ? `/api/reportes/descargar-pdf?${queryString}`
            : '/api/reportes/descargar-pdf';

        window.location.href = url;
    });
});

async function cargarCultivosReporte(selectCultivo) {
    if (!selectCultivo) {
        return;
    }

    try {
        const response = await fetch('/api/cultivos');
        if (!response.ok) {
            throw new Error('No se pudieron cargar los cultivos.');
        }

        const cultivos = await response.json();
        cultivos.forEach(cultivo => {
            if (selectCultivo.querySelector(`option[value="${cultivo.id}"]`)) {
                return;
            }

            const option = document.createElement('option');
            option.value = cultivo.id;
            option.textContent = cultivo.nombre;
            selectCultivo.appendChild(option);
        });
    } catch (error) {
        console.error(error);
    }
}
