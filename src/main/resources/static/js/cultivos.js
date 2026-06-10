const cultivosApiUrl = '/api/cultivos';
const formNuevoCultivo = document.getElementById('formNuevoCultivo');
const modalNuevoCultivoElement = document.getElementById('modalNuevoCultivo');
const modalNuevoCultivo = modalNuevoCultivoElement ? bootstrap.Modal.getOrCreateInstance(modalNuevoCultivoElement) : null;
const modalTitulo = document.getElementById('modalNuevoCultivoLabel');
const btnGuardarCultivo = document.getElementById('btnGuardarCultivo');
const cultivoAlert = document.getElementById('cultivoAlert');
const cultivoModalAlert = document.getElementById('cultivoModalAlert');
const inputBuscarCultivo = document.getElementById('inputBuscarCultivo');

let cultivoEditandoId = null;

function mostrarMensajeCultivo(tipo, mensaje) {
    if (!cultivoAlert) return;

    cultivoAlert.className = `alert alert-${tipo} fw-bold border-0 rounded-4 shadow-sm`;
    cultivoAlert.textContent = mensaje;
    cultivoAlert.classList.remove('d-none');
}

function mostrarMensajeModalCultivo(mensaje) {
    if (!cultivoModalAlert) return;

    cultivoModalAlert.textContent = mensaje;
    cultivoModalAlert.classList.remove('d-none');
}

function construirCultivoPayload(formData) {
    return {
        nombre: formData.get('nombre'),
        humedadMinOptima: Number(formData.get('humedadMinOptima')),
        humedadMaxOptima: Number(formData.get('humedadMaxOptima')),
        duracionRiegoMinutos: Number(formData.get('duracionRiegoMinutos')),
        tratoRecomendado: formData.get('tratoRecomendado') || null
    };
}

function enviarJson(url, opciones) {
    return fetch(url, {
        headers: {
            'Content-Type': 'application/json',
            ...(opciones.headers || {})
        },
        ...opciones
    }).then(async response => {
        if (!response.ok) {
            const body = await response.json().catch(() => ({}));
            throw new Error(body.error || 'No se pudo completar la operación.');
        }

        if (response.status === 204) {
            return null;
        }

        return response.json().catch(() => {
            throw new Error('El servidor no devolvió una respuesta JSON válida. Vuelve a iniciar sesión e inténtalo nuevamente.');
        });
    });
}

function resetearModalCultivo() {
    cultivoEditandoId = null;
    formNuevoCultivo.reset();
    cultivoModalAlert?.classList.add('d-none');
    modalTitulo.innerHTML = '<i class="fa-solid fa-seedling text-success me-2"></i>Nuevo perfil de cultivo';
    btnGuardarCultivo.innerHTML = '<i class="fa-solid fa-floppy-disk me-2"></i>Guardar';
}

function cargarCultivoParaEditar(id) {
    return fetch(`${cultivosApiUrl}/${id}`)
        .then(async response => {
            if (!response.ok) {
                const body = await response.json().catch(() => ({}));
                throw new Error(body.error || 'No se pudo cargar el perfil de cultivo.');
            }

            return response.json();
        });
}

function abrirModalEdicion(cultivo) {
    cultivoEditandoId = cultivo.id;

    formNuevoCultivo.elements.nombre.value = cultivo.nombre || '';
    formNuevoCultivo.elements.humedadMinOptima.value = cultivo.humedadMinOptima ?? '';
    formNuevoCultivo.elements.humedadMaxOptima.value = cultivo.humedadMaxOptima ?? '';
    formNuevoCultivo.elements.duracionRiegoMinutos.value = cultivo.duracionRiegoMinutos ?? '';
    formNuevoCultivo.elements.tratoRecomendado.value = cultivo.tratoRecomendado || '';

    modalTitulo.innerHTML = '<i class="fa-solid fa-pen text-success me-2"></i>Editar perfil de cultivo';
    btnGuardarCultivo.innerHTML = '<i class="fa-solid fa-floppy-disk me-2"></i>Actualizar';
    modalNuevoCultivo.show();
}

if (modalNuevoCultivoElement) {
    modalNuevoCultivoElement.addEventListener('hidden.bs.modal', resetearModalCultivo);
}

document.querySelectorAll('[data-bs-target="#modalNuevoCultivo"]').forEach(button => {
    button.addEventListener('click', resetearModalCultivo);
});

if (formNuevoCultivo) {
    formNuevoCultivo.addEventListener('submit', event => {
        event.preventDefault();

        const payload = construirCultivoPayload(new FormData(formNuevoCultivo));
        const esEdicion = cultivoEditandoId !== null;
        const url = esEdicion ? `${cultivosApiUrl}/${cultivoEditandoId}` : cultivosApiUrl;
        const method = esEdicion ? 'PUT' : 'POST';

        enviarJson(url, {
            method,
            body: JSON.stringify(payload)
        })
            .then(() => {
                modalNuevoCultivo.hide();
                mostrarMensajeCultivo('success', esEdicion ? 'Perfil de cultivo actualizado correctamente.' : 'Perfil de cultivo guardado correctamente.');
                window.location.reload();
            })
            .catch(error => {
                mostrarMensajeModalCultivo(error.message);
            });
    });
}

document.querySelectorAll('.btn-eliminar').forEach(button => {
    button.addEventListener('click', () => {
        const id = button.dataset.id;

        if (!confirm('¿Eliminar este perfil de cultivo?')) {
            return;
        }

        enviarJson(`${cultivosApiUrl}/${id}`, { method: 'DELETE' })
            .then(() => {
                mostrarMensajeCultivo('success', 'Perfil de cultivo eliminado.');
                window.location.reload();
            })
            .catch(error => {
                mostrarMensajeCultivo('danger', error.message);
            });
    });
});

document.querySelectorAll('.btn-editar').forEach(button => {
    button.addEventListener('click', () => {
        const id = button.dataset.id;

        cargarCultivoParaEditar(id)
            .then(abrirModalEdicion)
            .catch(error => {
                mostrarMensajeCultivo('danger', error.message);
            });
    });
});

if (inputBuscarCultivo) {
    inputBuscarCultivo.addEventListener('input', () => {
        const termino = inputBuscarCultivo.value.trim().toLowerCase();

        document.querySelectorAll('.cultivo-item').forEach(item => {
            const nombre = (item.dataset.nombre || '').toLowerCase();
            item.classList.toggle('d-none', !nombre.includes(termino));
        });
    });
}
