-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 30-10-2025 a las 11:13:08
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.1.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sistemaventa`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `id` int(11) NOT NULL,
  `dni` varchar(8) NOT NULL,
  `nombre` varchar(180) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `direccion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id`, `dni`, `nombre`, `telefono`, `direccion`) VALUES
(1, '1001', 'Yamil fernando', '78454251', 'La Paz - Bolivia'),
(2, '1002', 'Juan Perez', '2323929', 'Munaypata - La Paz'),
(3, '1003', 'Lucas Dalto', '12138291', 'Villa armonia - Tarija'),
(4, '1004', 'Mariana López Perez', '78788884', 'Zona Sur - Cochabamba');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `config`
--

CREATE TABLE `config` (
  `id` int(11) NOT NULL,
  `dni_empresa` int(15) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `telefono` int(11) NOT NULL,
  `direccion` text NOT NULL,
  `mensaje` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `config`
--

INSERT INTO `config` (`id`, `dni_empresa`, `nombre`, `telefono`, `direccion`, `mensaje`) VALUES
(1, 71347267, 'Harina de Grillo', 925491523, 'La Paz - Bolivia', 'Gracias por su preferencia');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle`
--

CREATE TABLE `detalle` (
  `id` int(11) NOT NULL,
  `id_pro` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `id_venta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `detalle`
--

INSERT INTO `detalle` (`id`, `id_pro`, `cantidad`, `precio`, `id_venta`) VALUES
(21, 2, 11, 34.00, 21),
(22, 1, 1, 3000.00, 22);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `inventario_eoq`
--

CREATE TABLE `inventario_eoq` (
  `id` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `demanda_anual` decimal(10,2) NOT NULL,
  `costo_orden` decimal(10,2) NOT NULL DEFAULT 50.00,
  `costo_mantener` decimal(10,2) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `eoq_calculado` decimal(10,2) DEFAULT NULL,
  `costo_total_minimo` decimal(10,2) DEFAULT NULL,
  `fecha_calculo` datetime DEFAULT current_timestamp(),
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `inventario_eoq`
--

INSERT INTO `inventario_eoq` (`id`, `id_producto`, `demanda_anual`, `costo_orden`, `costo_mantener`, `precio_unitario`, `eoq_calculado`, `costo_total_minimo`, `fecha_calculo`, `activo`) VALUES
(16, 1, 604.00, 50.00, 90.00, 450.00, 25.91, 2331.52, '2025-10-30 06:07:24', 1),
(17, 2, 599.00, 50.00, 26.80, 134.00, 61.08, 1637.07, '2025-10-30 06:00:51', 1),
(18, 3, 181.00, 50.00, 70.00, 350.00, 16.08, 1125.61, '2025-10-30 06:10:23', 1),
(19, 4, 809.00, 50.00, 156.00, 780.00, 25.32, 3949.68, '2025-10-30 06:01:00', 1),
(20, 5, 703.00, 50.00, 83.00, 415.00, 29.10, 2415.55, '2025-10-30 06:06:19', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id` int(11) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `nombre` text NOT NULL,
  `proveedor` int(11) NOT NULL,
  `stock` int(11) NOT NULL,
  `precio` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `codigo`, `nombre`, `proveedor`, `stock`, `precio`) VALUES
(1, '3003', 'Creatine Pure 100%', 1, 15, 450.00),
(2, '3002', 'Silimarina - cardio mariano', 1, 99, 134.00),
(3, '3001', 'Profit con Omega 3', 2, 54, 350.00),
(4, '3004', 'Proteina Hidrolizada en polvo Dymatize', 2, 15, 780.00),
(5, '3005', 'Cretine Monohidrate', 4, 29, 415.00),
(6, '3006', 'Dr. Feaar - Electrolito ', 1, 121, 365.00),
(7, '3007', 'Syntrax Goliat', 3, 38, 915.00),
(8, '3008', 'Proteina con Antioxidante', 1, 18, 200.00),
(9, '3009', 'Ovo Protein', 2, 56, 300.00),
(10, '3010', 'Isofi Protein 5lb', 1, 12, 920.00),
(11, '3011', 'Creatina ronnie Coleman 200g', 3, 320, 50.00),
(12, '3012', 'Omega 3 Forte', 5, 45, 120.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedor`
--

CREATE TABLE `proveedor` (
  `id` int(11) NOT NULL,
  `ruc` varchar(15) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `telefono` varchar(15) NOT NULL,
  `direccion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `proveedor`
--

INSERT INTO `proveedor` (`id`, `ruc`, `nombre`, `telefono`, `direccion`) VALUES
(1, '2001', 'Natural Diet', '7989788', 'La Paz - Bolivia'),
(2, '2002', 'Jes FIT Suplementos', '78748133', 'La Paz - Bolivia'),
(3, '2003', 'Spartans Nutrition Suplementos', '7534313', 'Yanacocha, Galeria Cristal'),
(4, '2004', 'Dragon Pharma', '25483153', 'Santa Cruz - Bolivia'),
(5, '2005', 'Katigua', '787864864', 'Sacaba - Cochabamba');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(200) NOT NULL,
  `correo` varchar(200) NOT NULL,
  `pass` varchar(50) NOT NULL,
  `rol` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `correo`, `pass`, `rol`) VALUES
(2, 'Administrador', 'admin@gmail.com', 'admin', 'Administrador'),
(3, 'Nelson pedro', 'nelson@gmail.com', 'nelson', 'Asistente'),
(4, 'Pedro Antonio', 'pedro@gmail.com', 'pedro', 'Asistente'),
(5, 'Felipe Tuco', 'felipe@gmail.com', 'felipe', 'Asistente'),
(6, 'gerger', 'erge', 'gerge', 'Administrador');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

CREATE TABLE `ventas` (
  `id` int(11) NOT NULL,
  `cliente` int(11) NOT NULL,
  `vendedor` varchar(60) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `fecha` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`id`, `cliente`, `vendedor`, `total`, `fecha`) VALUES
(21, 1, 'Felipe Tuco', 374.00, '28/10/2025'),
(22, 1, 'Felipe Tuco', 3000.00, '28/10/2025');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `detalle`
--
ALTER TABLE `detalle`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_venta` (`id_venta`),
  ADD KEY `id_pro` (`id_pro`);

--
-- Indices de la tabla `inventario_eoq`
--
ALTER TABLE `inventario_eoq`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_producto` (`id_producto`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `proveedor` (`proveedor`);

--
-- Indices de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cliente` (`cliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `config`
--
ALTER TABLE `config`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `detalle`
--
ALTER TABLE `detalle`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `inventario_eoq`
--
ALTER TABLE `inventario_eoq`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `ventas`
--
ALTER TABLE `ventas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `detalle`
--
ALTER TABLE `detalle`
  ADD CONSTRAINT `detalle_ibfk_1` FOREIGN KEY (`id_pro`) REFERENCES `productos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `detalle_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `inventario_eoq`
--
ALTER TABLE `inventario_eoq`
  ADD CONSTRAINT `fk_inventario_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`proveedor`) REFERENCES `proveedor` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `ventas`
--
ALTER TABLE `ventas`
  ADD CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`cliente`) REFERENCES `clientes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
