/* ============================================================
   Lógica del frontend - Evaluación 03: Sistema de Biblioteca
   Consume los 3 microservicios por HTTP (fetch + JSON).
   ============================================================ */

// ---------- URLs base de los microservicios ----------
const URL_LIBRO   = 'http://localhost:8081/api/libros';
const URL_LECTOR  = 'http://localhost:8082/api/lectores';
const URL_PRESTAMO = 'http://localhost:8083/api/prestamos';

// ============================================================
//  UTILIDADES GENERALES
// ============================================================

/** Muestra una notificación flotante (éxito / error / info) */
function notificar(mensaje, tipo = 'exito') {
    const n = document.getElementById('notificacion');
    n.textContent = mensaje;
    n.className = 'notificacion visible ' + tipo;
    clearTimeout(n._temporizador);
    n._temporizador = setTimeout(() => { n.className = 'notificacion'; }, 4000);
}

/**
 * Realiza una petición HTTP y devuelve { ok, status, datos }.
 * Si falla, muestra el mensaje de error que devuelve la API.
 */
async function pedir(url, metodo = 'GET', cuerpo = null) {
    try {
        const opciones = { method: metodo, headers: { 'Content-Type': 'application/json' } };
        if (cuerpo) opciones.body = JSON.stringify(cuerpo);

        const respuesta = await fetch(url, opciones);

        // 204 No Content: no hay cuerpo que leer
        if (respuesta.status === 204) {
            return { ok: true, status: 204, datos: null };
        }

        const datos = await respuesta.json();

        if (!respuesta.ok) {
            // Arma un mensaje legible desde el JSON de error de la API
            let mensaje = datos.mensaje || ('Error ' + respuesta.status);
            if (datos.campos) {
                mensaje += '\n' + Object.entries(datos.campos)
                    .map(([campo, error]) => `• ${campo}: ${error}`).join('\n');
            }
            return { ok: false, status: respuesta.status, mensaje };
        }

        return { ok: true, status: respuesta.status, datos };
    } catch (error) {
        // Sin conexión: el microservicio no está corriendo
        return {
            ok: false,
            status: 0,
            mensaje: 'No se pudo conectar con el microservicio.\nVerifique que esté ejecutándose (mvn spring-boot:run).'
        };
    }
}

function escapar(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

/** Traduce el nombre del estado a su clase CSS de la insignia */
function claseEstado(estado) {
    const mapa = {
        DISPONIBLE: 'insignia-disponible', AGOTADO: 'insignia-agotado',
        ACTIVO: 'insignia-activo', DEVUELTO: 'insignia-devuelto',
        VENCIDO: 'insignia-vencido', INACTIVO: 'insignia-inactivo'
    };
    return mapa[estado] || 'insignia-activo';
}

function insignia(estado) {
    return `<span class="insignia ${claseEstado(estado)}">${escapar(estado)}</span>`;
}

// ============================================================
//  PESTAÑAS
// ============================================================
document.querySelectorAll('.pestana').forEach(boton => {
    boton.addEventListener('click', () => {
        document.querySelectorAll('.pestana').forEach(b => b.classList.remove('activa'));
        document.querySelectorAll('.contenido').forEach(c => c.classList.remove('activo'));
        boton.classList.add('activa');
        document.getElementById('panel-' + boton.dataset.pestana).classList.add('activo');

        // Al cambiar de pestaña se refrescan los datos de esa sección
        if (boton.dataset.pestana === 'libros') cargarLibros();
        if (boton.dataset.pestana === 'lectores') cargarLectores();
        if (boton.dataset.pestana === 'prestamos') cargarPrestamos();
    });
});

// ============================================================
//  LIBROS  (CRUD completo)
// ============================================================
async function cargarLibros() {
    const r = await pedir(URL_LIBRO);
    const cuerpo = document.getElementById('tabla-libros');
    const contador = document.getElementById('contador-libros');

    if (!r.ok) { notificar(r.mensaje, 'error'); return; }

    contador.textContent = r.datos.length + ' registro' + (r.datos.length === 1 ? '' : 's');

    if (r.datos.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="7" class="fila-vacia">No hay libros registrados</td></tr>';
        return;
    }

    cuerpo.innerHTML = r.datos.map(libro => `
        <tr>
            <td>${libro.id}</td>
            <td>${escapar(libro.isbn)}</td>
            <td>${escapar(libro.titulo)}</td>
            <td>${escapar(libro.autor)}</td>
            <td>${libro.stock}</td>
            <td>${insignia(libro.estado)}</td>
            <td>
                <button class="btn-tabla btn-editar" onclick='editarLibro(${JSON.stringify(libro)})'>Editar</button>
                <button class="btn-tabla btn-eliminar" onclick="eliminarLibro(${libro.id})">Eliminar</button>
            </td>
        </tr>`).join('');
}

document.getElementById('form-libro').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('libro-id').value;
    const cuerpo = {
        isbn:   document.getElementById('libro-isbn').value,
        titulo: document.getElementById('libro-titulo').value,
        autor:  document.getElementById('libro-autor').value,
        stock:  parseInt(document.getElementById('libro-stock').value, 10)
    };

    const r = id
        ? await pedir(`${URL_LIBRO}/${id}`, 'PUT', cuerpo)
        : await pedir(URL_LIBRO, 'POST', cuerpo);

    if (r.ok) {
        notificar(id ? `Libro actualizado (HTTP ${r.status})` : `Libro registrado con éxito (HTTP ${r.status})`);
        limpiarFormLibro();
        cargarLibros();
    } else {
        notificar(r.mensaje, 'error');
    }
});

function editarLibro(libro) {
    document.getElementById('libro-id').value = libro.id;
    document.getElementById('libro-isbn').value = libro.isbn;
    document.getElementById('libro-titulo').value = libro.titulo;
    document.getElementById('libro-autor').value = libro.autor;
    document.getElementById('libro-stock').value = libro.stock;
    document.getElementById('titulo-form-libro').textContent = `Editar libro #${libro.id}`;
    document.getElementById('libro-isbn').focus();
}

async function eliminarLibro(id) {
    if (!confirm(`¿Seguro de eliminar el libro con id ${id}?`)) return;
    const r = await pedir(`${URL_LIBRO}/${id}`, 'DELETE');
    if (r.ok) { notificar(`Libro eliminado (HTTP ${r.status})`); cargarLibros(); }
    else { notificar(r.mensaje, 'error'); }
}

function limpiarFormLibro() {
    document.getElementById('form-libro').reset();
    document.getElementById('libro-id').value = '';
    document.getElementById('titulo-form-libro').textContent = 'Registrar libro';
}

document.getElementById('btn-nuevo-libro').addEventListener('click', limpiarFormLibro);
document.getElementById('btn-cancelar-libro').addEventListener('click', limpiarFormLibro);

// ============================================================
//  LECTORES  (CRUD completo)
// ============================================================
async function cargarLectores() {
    const r = await pedir(URL_LECTOR);
    const cuerpo = document.getElementById('tabla-lectores');
    const contador = document.getElementById('contador-lectores');

    if (!r.ok) { notificar(r.mensaje, 'error'); return; }

    contador.textContent = r.datos.length + ' registro' + (r.datos.length === 1 ? '' : 's');

    if (r.datos.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="7" class="fila-vacia">No hay lectores registrados</td></tr>';
        return;
    }

    cuerpo.innerHTML = r.datos.map(lector => `
        <tr>
            <td>${lector.id}</td>
            <td>${escapar(lector.documento)}</td>
            <td>${escapar(lector.nombres)}</td>
            <td>${escapar(lector.apellidos)}</td>
            <td>${escapar(lector.correo)}</td>
            <td>${escapar(lector.telefono || '—')}</td>
            <td>
                <button class="btn-tabla btn-editar" onclick='editarLector(${JSON.stringify(lector)})'>Editar</button>
                <button class="btn-tabla btn-eliminar" onclick="eliminarLector(${lector.id})">Eliminar</button>
            </td>
        </tr>`).join('');
}

document.getElementById('form-lector').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('lector-id').value;
    const cuerpo = {
        documento: document.getElementById('lector-documento').value,
        nombres:   document.getElementById('lector-nombres').value,
        apellidos: document.getElementById('lector-apellidos').value,
        correo:    document.getElementById('lector-correo').value,
        telefono:  document.getElementById('lector-telefono').value
    };

    const r = id
        ? await pedir(`${URL_LECTOR}/${id}`, 'PUT', cuerpo)
        : await pedir(URL_LECTOR, 'POST', cuerpo);

    if (r.ok) {
        notificar(id ? `Lector actualizado (HTTP ${r.status})` : `Lector registrado con éxito (HTTP ${r.status})`);
        limpiarFormLector();
        cargarLectores();
    } else {
        notificar(r.mensaje, 'error');
    }
});

function editarLector(lector) {
    document.getElementById('lector-id').value = lector.id;
    document.getElementById('lector-documento').value = lector.documento;
    document.getElementById('lector-nombres').value = lector.nombres;
    document.getElementById('lector-apellidos').value = lector.apellidos;
    document.getElementById('lector-correo').value = lector.correo;
    document.getElementById('lector-telefono').value = lector.telefono || '';
    document.getElementById('titulo-form-lector').textContent = `Editar lector #${lector.id}`;
    document.getElementById('lector-documento').focus();
}

async function eliminarLector(id) {
    if (!confirm(`¿Seguro de eliminar el lector con id ${id}?`)) return;
    const r = await pedir(`${URL_LECTOR}/${id}`, 'DELETE');
    if (r.ok) { notificar(`Lector eliminado (HTTP ${r.status})`); cargarLectores(); }
    else { notificar(r.mensaje, 'error'); }
}

function limpiarFormLector() {
    document.getElementById('form-lector').reset();
    document.getElementById('lector-id').value = '';
    document.getElementById('titulo-form-lector').textContent = 'Registrar lector';
}

document.getElementById('btn-nuevo-lector').addEventListener('click', limpiarFormLector);
document.getElementById('btn-cancelar-lector').addEventListener('click', limpiarFormLector);

// ============================================================
//  PRÉSTAMOS  (registrar, listar, filtrar y devolver)
// ============================================================
async function cargarPrestamos() {
    const filtro = document.getElementById('filtro-estado').value;
    const url = filtro ? `${URL_PRESTAMO}/estado/${filtro}` : URL_PRESTAMO;
    const r = await pedir(url);
    const cuerpo = document.getElementById('tabla-prestamos');
    const contador = document.getElementById('contador-prestamos');

    if (!r.ok) { notificar(r.mensaje, 'error'); return; }

    contador.textContent = r.datos.length + ' registro' + (r.datos.length === 1 ? '' : 's');

    if (r.datos.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="7" class="fila-vacia">No hay préstamos para este filtro</td></tr>';
        return;
    }

    cuerpo.innerHTML = r.datos.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>${p.idLector}</td>
            <td>${p.idLibro}</td>
            <td>${p.fechaPrestamo}</td>
            <td>${p.fechaLimite}</td>
            <td>${insignia(p.estado)}</td>
            <td>
                ${p.estado !== 'DEVUELTO'
                    ? `<button class="btn-tabla btn-devolver" onclick="devolverPrestamo(${p.id})">Devolver</button>` : ''}
                <button class="btn-tabla btn-eliminar" onclick="eliminarPrestamo(${p.id})">Eliminar</button>
            </td>
        </tr>`).join('');
}

document.getElementById('filtro-estado').addEventListener('change', cargarPrestamos);

document.getElementById('form-prestamo').addEventListener('submit', async (e) => {
    e.preventDefault();
    const cuerpo = {
        idLector:     parseInt(document.getElementById('prestamo-lector').value, 10),
        idLibro:      parseInt(document.getElementById('prestamo-libro').value, 10),
        fechaPrestamo: document.getElementById('prestamo-fecha').value,
        fechaLimite:   document.getElementById('prestamo-limite').value
    };

    const r = await pedir(URL_PRESTAMO, 'POST', cuerpo);
    if (r.ok) {
        notificar(`Préstamo registrado con éxito (HTTP ${r.status})`);
        document.getElementById('form-prestamo').reset();
        cargarPrestamos();
    } else {
        notificar(r.mensaje, 'error');
    }
});

/** Devolución: PATCH /api/prestamos/{id}/devolucion -> estado DEVUELTO */
async function devolverPrestamo(id) {
    if (!confirm(`¿Confirmar la devolución del préstamo con id ${id}?`)) return;
    const r = await pedir(`${URL_PRESTAMO}/${id}/devolucion`, 'PATCH');
    if (r.ok) {
        notificar(`¡Devolución registrada! Estado: ${r.datos.estado} (HTTP ${r.status})`);
        cargarPrestamos();
    } else {
        notificar(r.mensaje, 'error');
    }
}

async function eliminarPrestamo(id) {
    if (!confirm(`¿Seguro de eliminar el préstamo con id ${id}?`)) return;
    const r = await pedir(`${URL_PRESTAMO}/${id}`, 'DELETE');
    if (r.ok) { notificar(`Préstamo eliminado (HTTP ${r.status})`); cargarPrestamos(); }
    else { notificar(r.mensaje, 'error'); }
}

document.getElementById('btn-cancelar-prestamo').addEventListener('click', () => {
    document.getElementById('form-prestamo').reset();
});

// ============================================================
//  CARGA DE LOS DATALIST (para elegir lector/libro al prestar)
// ============================================================
async function cargarSugerencias() {
    const libros = await pedir(URL_LIBRO);
    if (libros.ok) {
        document.getElementById('lista-libros').innerHTML = libros.datos
            .map(l => `<option value="${l.id}">${escapar(l.titulo)} (stock: ${l.stock})</option>`).join('');
    }

    const lectores = await pedir(URL_LECTOR);
    if (lectores.ok) {
        document.getElementById('lista-lectores').innerHTML = lectores.datos
            .map(l => `<option value="${l.id}">${escapar(l.nombres)} ${escapar(l.apellidos)} - ${escapar(l.documento)}</option>`).join('');
    }
}

// ============================================================
//  INICIO
// ============================================================
(function iniciar() {
    // Fecha de hoy por defecto en el formulario de préstamo
    const hoy = new Date().toISOString().slice(0, 10);
    document.getElementById('prestamo-fecha').value = hoy;

    cargarLibros();
    cargarLectores();
    cargarPrestamos();
    cargarSugerencias();
})();
