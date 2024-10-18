-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.28-MariaDB-log - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for atm
CREATE DATABASE IF NOT EXISTS `atm` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `atm`;

-- Dumping structure for table atm.transactions
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_account` varchar(255) NOT NULL,
  `to_account` varchar(255) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `transaction_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table atm.transactions: ~8 rows (approximately)
DELETE FROM `transactions`;
INSERT INTO `transactions` (`id`, `from_account`, `to_account`, `amount`, `transaction_time`, `note`) VALUES
	(1, 'BANK', 'chau', 200000.00, '2024-10-17 15:23:27', 'NẠP TIỀN'),
	(2, 'BANK', 'dieu', 30000.00, '2024-10-17 15:23:37', 'NẠP TIỀN'),
	(3, 'BANK', 'ngoc', 50000.00, '2024-10-17 15:23:47', 'NẠP TIỀN'),
	(4, 'BANK', 'cham', 300000.00, '2024-10-17 15:23:57', 'NẠP TIỀN'),
	(5, 'BANK', 'chau', 30000.00, '2024-10-17 15:27:55', 'NẠP TIỀN'),
	(6, 'chau', 'BANK', 30000.00, '2024-10-17 15:34:24', 'RÚT TIỀN'),
	(7, 'cham', 'ngoc', 50000.00, '2024-10-17 16:01:58', 'cham ck ngoc'),
	(8, 'chau', 'dieu', 30000.00, '2024-10-17 16:31:31', 'chau ck dieu');

-- Dumping structure for table atm.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `balance` double DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table atm.users: ~4 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `username`, `password`, `balance`) VALUES
	(1, 'cham', '123', 250000),
	(2, 'ngoc', '000', 100000),
	(3, 'dieu', '111', 60000),
	(4, 'chau', '222', 170000),
	(5, 'dat', '333', 400000);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
