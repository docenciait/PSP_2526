### UT5. Seguridad en aplicaciones y comunicaciones

### Tema desarrollado: Seguridad en aplicaciones y comunicaciones

---

#### 1. Programación segura y principios de seguridad

La programación segura es un conjunto de prácticas y técnicas que buscan desarrollar software resistente a ataques y vulnerabilidades. Su objetivo es proteger la confidencialidad, integridad y disponibilidad de la información y los sistemas.

**Principios básicos de seguridad en programación:**

- **Principio de mínimo privilegio:** Cada componente o usuario debe tener solo los permisos estrictamente necesarios para realizar su función.
- **Validación y saneamiento de entradas:** Nunca confiar en datos externos; siempre validar y limpiar para evitar inyecciones (SQL, comandos, scripts).
- **Gestión segura de errores:** No mostrar mensajes de error detallados al usuario final para evitar revelar información sensible.
- **Autenticación y autorización robustas:** Verificar la identidad y controlar el acceso a recursos.
- **Cifrado de datos sensibles:** Proteger datos en reposo y en tránsito.
- **Actualización y parcheo:** Mantener el software actualizado para corregir vulnerabilidades conocidas.

---

#### 2. Criptografía: clave pública y privada, hash, firma digital

La criptografía es la ciencia que estudia técnicas para proteger la información mediante transformaciones matemáticas.

- **Clave pública y privada (criptografía asimétrica):** Utiliza un par de claves, una pública para cifrar y otra privada para descifrar. Ejemplo: RSA. Permite comunicaciones seguras sin compartir previamente una clave secreta.
- **Hash:** Función que transforma datos de cualquier tamaño en una cadena fija de caracteres (resumen). Es unidireccional y sirve para verificar integridad. Ejemplo: SHA-256.

- **Firma digital:** Es un hash cifrado con la clave privada del emisor, que garantiza autenticidad, integridad y no repudio del mensaje. El receptor usa la clave pública para verificarla.

---

#### 3. Certificados digitales y autoridades de certificación

Un **certificado digital** es un documento electrónico que vincula una clave pública con la identidad de una entidad (persona, empresa, servidor). Está firmado por una **Autoridad de Certificación (CA)** confiable, que valida la identidad.

Los certificados permiten establecer conexiones seguras y confiables, por ejemplo, en HTTPS, garantizando que el servidor es quien dice ser.

---

#### 4. Políticas de control de acceso

Las políticas de control de acceso definen quién puede acceder a qué recursos y bajo qué condiciones. Se basan en modelos como:

- **DAC (Discretionary Access Control):** El propietario decide quién accede.
- **MAC (Mandatory Access Control):** Acceso basado en reglas estrictas definidas por la organización.
- **RBAC (Role-Based Access Control):** Acceso basado en roles asignados a usuarios.

Estas políticas se implementan para proteger recursos y datos sensibles.

---

#### 5. Encriptación de datos almacenados y transmitidos

- **Datos almacenados:** Se cifran para protegerlos en caso de acceso no autorizado a discos o bases de datos. Ejemplo: cifrado AES.
- **Datos transmitidos:** Se cifran para evitar que sean interceptados y leídos durante la comunicación. Ejemplo: uso de protocolos seguros como TLS.

---

#### 6. Protocolos seguros (SSL/TLS, HTTPS)

- **SSL (Secure Sockets Layer) y TLS (Transport Layer Security):** Protocolos que proporcionan cifrado y autenticación en las comunicaciones por internet. TLS es la versión moderna y segura de SSL.
- **HTTPS:** Es HTTP sobre TLS, que garantiza que la comunicación entre navegador y servidor es segura y privada.

Estos protocolos evitan ataques como la interceptación (man-in-the-middle) y garantizan la integridad de los datos.

---

#### 7. Sockets seguros y autenticación

Los **sockets seguros** (por ejemplo, usando TLS) permiten establecer canales de comunicación cifrados entre aplicaciones. La autenticación puede ser mutua, donde ambas partes verifican su identidad mediante certificados digitales.

Esto es fundamental en aplicaciones distribuidas para proteger la información y evitar accesos no autorizados.

---

#### 8. Programación de aplicaciones seguras

Para desarrollar aplicaciones seguras se deben seguir buenas prácticas como:

- Uso de frameworks y librerías que implementen seguridad.
- Gestión segura de sesiones y tokens.
- Prevención de vulnerabilidades comunes (inyección SQL, XSS, CSRF).
- Implementación de autenticación multifactor.
- Registro y monitoreo de eventos de seguridad.
- Pruebas de seguridad y auditorías periódicas.

---
