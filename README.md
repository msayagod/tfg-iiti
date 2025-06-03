# 📱 Aplicación de Gestión de Máquinas Expendedoras

Este proyecto corresponde al Trabajo Fin de Grado (TFG) de Ingeniería Informática y consiste en una aplicación móvil Android orientada a la gestión de máquinas expendedoras no inteligentes. Permite a los operarios registrar de forma digital y estructurada todas las operaciones que se realizan sobre cada máquina.

## 🧩 Funcionalidades principales

- 🛠️ Gestión de máquinas y productos.
- 📅 Planificación de visitas con visualización diaria.
- 📦 Registro de operaciones: reposición de productos, recogida de dinero, stock, incidencias.
- 📈 Generación de informes organizados por máquina y día.
- 🗃️ Exportación de datos en formato PDF.
- 🔄 Copias de seguridad automáticas locales con restauración.
- 🌐 Internacionalización: interfaz disponible en español e inglés.
- 📶 Funcionamiento completamente offline.

## 📷 Capturas de pantalla

*Se incluirán en la versión final del proyecto.*

## 🏗️ Arquitectura

La aplicación está diseñada siguiendo el patrón **MVVM (Model-View-ViewModel)**, separando de forma clara la lógica de presentación, la lógica de negocio y los datos persistentes.

## 📊 Modelo de datos

Se ha implementado un modelo de clases coherente con los requisitos definidos, permitiendo una gestión eficiente del estado de cada máquina, los huecos, productos, visitas, operaciones e informes generados.

## 🌍 Idiomas disponibles

- Español (por defecto)
- Inglés  
El idioma de la interfaz se adapta automáticamente al idioma configurado en el dispositivo.

## 📂 Estructura del proyecto (ejemplo provisional)

```bash
├── app/
│   ├── data/
│   ├── domain/
│   ├── ui/
│   └── utils/
├── res/
│   ├── values/
│   └── values-en/
└── README.md
