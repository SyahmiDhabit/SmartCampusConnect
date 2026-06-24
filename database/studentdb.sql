-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2026 at 06:53 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `studentdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gpa` double NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `programme` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `email`, `gpa`, `name`, `programme`) VALUES
('S1001', 'alice.tan@campus.edu', 3.9, 'Alice Tan', 'Computer Science'),
('S1002', 'aina.maisarah@smartcampus.edu', 3.91, 'Aina Maisarah', 'Information Systems'),
('S1003', 'muhammad.syahmi@smartcampus.edu', 3.75, 'Muhammad Syahmi', 'Software Engineering'),
('S1004', 'abdul.aziz@smartcampus.edu', 3.95, 'Abdul Aziz', 'Computer Science'),
('S1005', 'fiona@smartcampus.edu', 3.6, 'Fiona', 'Data Science'),
('S1006', 'ownercar@gmail.com', 4, 'dol', 'Information Systems'),
('S1009', 'Taufiq@gmaill.com', 3.95, 'Taufiq', 'Information Systems'),
('S1010', 'hariz@gmail.com', 3.5, 'Hariz', 'Mechanical Engineering'),
('S1011', 'ownerca2r@gmail.com', 3.54, 'alice', 'Computer Science'),
('S1013', 'smith@gmail.com', 2.22, 'smith', 'Electrical Engineering'),
('S1031', 'alice.tan@campus.edu', 3.8, 'Alice Tan', 'Computer Science'),
('S2001', 'evan.wright.jr@campus.edu', 3.85, 'Evan Wright Jr.', 'Computer Science'),
('S2003', 'fiona.cole@campus.edu', 3.65, 'Fiona Cole', 'Software Engineering'),
('S9999', 'test@test.com', 3.5, 'Test Student', 'Computer Science');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
