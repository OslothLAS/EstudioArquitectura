// Recibe el formulario de contacto y lo envía por mail usando Resend (https://resend.com).
// Variables de entorno (se cargan en Vercel > Settings > Environment Variables):
//   RESEND_API_KEY  clave de Resend
//   CONTACTO_PARA   mail del estudio que recibe las consultas
//   CONTACTO_DESDE  (opcional) remitente; por defecto el de prueba de Resend

const LIMITES = { nombre: 100, email: 150, servicio: 100, mensaje: 5000 };
const TIEMPO_MINIMO_MS = 3000;      // menos que esto desde que abrió la página = bot

function escapeHtml(texto) {
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function limpiar(valor, max) {
    return typeof valor === 'string' ? valor.trim().slice(0, max) : '';
}

module.exports = async (req, res) => {
    if (req.method !== 'POST') {
        res.setHeader('Allow', 'POST');
        return res.status(405).json({ error: 'Método no permitido' });
    }

    const datos = req.body || {};

    // Campo trampa: las personas no lo ven, los bots de spam lo completan
    if (datos.sitio) return res.status(200).json({ ok: true });

    // Enviado demasiado rápido para haberlo escrito una persona: se descarta en silencio
    if (!(Number(datos.tiempo) >= TIEMPO_MINIMO_MS)) return res.status(200).json({ ok: true });

    const nombre = limpiar(datos.nombre, LIMITES.nombre);
    const email = limpiar(datos.email, LIMITES.email);
    const servicio = limpiar(datos.servicio, LIMITES.servicio);
    const mensaje = limpiar(datos.mensaje, LIMITES.mensaje);

    if (!nombre || !mensaje || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        return res.status(400).json({ error: 'Revisá que el nombre, el correo y el mensaje estén completos.' });
    }

    const { RESEND_API_KEY, CONTACTO_PARA, CONTACTO_DESDE } = process.env;
    if (!RESEND_API_KEY || !CONTACTO_PARA) {
        console.error('Faltan las variables RESEND_API_KEY o CONTACTO_PARA');
        return res.status(500).json({ error: 'El formulario todavía no está configurado.' });
    }

    const respuesta = await fetch('https://api.resend.com/emails', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${RESEND_API_KEY}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            from: CONTACTO_DESDE || 'Web del Estudio <onboarding@resend.dev>',
            to: [CONTACTO_PARA],
            reply_to: email,
            subject: `Nueva consulta de ${nombre}${servicio ? ` · ${servicio}` : ''}`,
            text: `Nombre: ${nombre}\nCorreo: ${email}\nServicio: ${servicio || '-'}\n\n${mensaje}`,
            html: `
                <p><strong>Nombre:</strong> ${escapeHtml(nombre)}</p>
                <p><strong>Correo:</strong> ${escapeHtml(email)}</p>
                <p><strong>Servicio:</strong> ${escapeHtml(servicio || '-')}</p>
                <p><strong>Mensaje:</strong></p>
                <p style="white-space: pre-line">${escapeHtml(mensaje)}</p>`
        })
    });

    if (!respuesta.ok) {
        console.error('Error de Resend', respuesta.status, await respuesta.text());
        return res.status(502).json({ error: 'No pudimos enviar tu mensaje. Probá de nuevo en unos minutos.' });
    }

    return res.status(200).json({ ok: true });
};
