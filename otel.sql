-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Anamakine: 127.0.0.1
-- Üretim Zamanı: 13 Haz 2024, 18:27:34
-- Sunucu sürümü: 10.4.32-MariaDB
-- PHP Sürümü: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `otel`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `kullanici`
--

CREATE TABLE `kullanici` (
  `id` int(11) NOT NULL,
  `kullaniciadi` varchar(30) NOT NULL,
  `sifre` int(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Tablo döküm verisi `kullanici`
--

INSERT INTO `kullanici` (`id`, `kullaniciadi`, `sifre`) VALUES
(11, 'deneme', 1542),
(12, 'merhaba', 1223);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `rezervasyon`
--

CREATE TABLE `rezervasyon` (
  `id` int(11) NOT NULL,
  `kullaniciadi` varchar(30) DEFAULT NULL,
  `odatipi` varchar(20) DEFAULT NULL,
  `odano` int(10) DEFAULT NULL,
  `giristarih` date DEFAULT NULL,
  `cikistarih` date DEFAULT NULL,
  `ucret` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Tablo döküm verisi `rezervasyon`
--

INSERT INTO `rezervasyon` (`id`, `kullaniciadi`, `odatipi`, `odano`, `giristarih`, `cikistarih`, `ucret`) VALUES
(10, 'furkan', 'Çift', 4, '2024-06-15', '2024-06-25', 1650),
(15, 'merhaba', 'Çift', 3, '2024-06-15', '2024-06-25', 1650),
(16, 'merhaba', 'Tek', 3, '2024-06-30', '2024-07-10', 1100);

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `yonetici`
--

CREATE TABLE `yonetici` (
  `id` int(11) NOT NULL,
  `kullaniciadi` varchar(30) NOT NULL,
  `sifre` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Tablo döküm verisi `yonetici`
--

INSERT INTO `yonetici` (`id`, `kullaniciadi`, `sifre`) VALUES
(6, 'furkan2323', '123'),
(7, 'mehmet', '123');

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `kullanici`
--
ALTER TABLE `kullanici`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`kullaniciadi`);

--
-- Tablo için indeksler `rezervasyon`
--
ALTER TABLE `rezervasyon`
  ADD PRIMARY KEY (`id`);

--
-- Tablo için indeksler `yonetici`
--
ALTER TABLE `yonetici`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `id` (`kullaniciadi`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `kullanici`
--
ALTER TABLE `kullanici`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Tablo için AUTO_INCREMENT değeri `rezervasyon`
--
ALTER TABLE `rezervasyon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Tablo için AUTO_INCREMENT değeri `yonetici`
--
ALTER TABLE `yonetici`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
