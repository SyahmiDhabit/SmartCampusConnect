-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2026 at 08:55 AM
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
-- Database: `enrolmentdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `cached_students`
--

CREATE TABLE `cached_students` (
  `student_id` varchar(255) NOT NULL,
  `last_cached` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `programme` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cached_students`
--

INSERT INTO `cached_students` (`student_id`, `last_cached`, `name`, `programme`) VALUES
('S1001', 1782180642876, 'Syahmi Dhabit', 'Computer Science'),
('S1002', 1782203805025, 'Aina Maisarah', 'Information Systems'),
('S1004', 1782180962160, 'Abdul Aziz', 'Computer Science'),
('S1009', 1782199990291, 'Taufiq', 'Information Systems'),
('S1010', 1782202707711, 'Hariz', 'Mechanical Engineering'),
('S1013', 1782208542245, 'smith', 'Electrical Engineering'),
('S1031', 1782210590936, 'Alice Tan', 'Computer Science'),
('S2001', 1782211268372, 'Evan Wright Jr.', 'Computer Science'),
('S2003', 1782211592806, 'Fiona Cole', 'Software Engineering'),
('S9999', 1782211353822, 'Test Student', 'Computer Science');

-- --------------------------------------------------------

--
-- Table structure for table `course_capacities`
--

CREATE TABLE `course_capacities` (
  `course_code` varchar(255) NOT NULL,
  `current_enrolled` int(11) NOT NULL,
  `max_capacity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `course_capacities`
--

INSERT INTO `course_capacities` (`course_code`, `current_enrolled`, `max_capacity`) VALUES
('CSE301', 5, 30),
('CSE302', 2, 30),
('CSE308', 1, 30),
('CSE310', 1, 30),
('ELE503', 1, 30),
('MEC404', 1, 30);

-- --------------------------------------------------------

--
-- Table structure for table `enrolments`
--

CREATE TABLE `enrolments` (
  `id` bigint(20) NOT NULL,
  `course_code` varchar(255) DEFAULT NULL,
  `semester` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `student_id` varchar(255) DEFAULT NULL,
  `timestamp` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrolments`
--

INSERT INTO `enrolments` (`id`, `course_code`, `semester`, `status`, `student_id`, `timestamp`) VALUES
(2, 'CSE301', '2026-S1', 'CONFIRMED', 'S1001', 1782180642878),
(3, 'CSE301', '2026-S1', 'CONFIRMED', 'S1004', 1782180962182),
(4, 'CSE301', '2026-S1', 'CONFIRMED', 'S1009', 1782199990293),
(5, 'MEC404', '2026-S1', 'CONFIRMED', 'S1010', 1782202707715),
(6, 'CSE308', '2026-S1', 'CONFIRMED', 'S1002', 1782203805026),
(7, 'ELE503', '2026-S1', 'CONFIRMED', 'S1013', 1782208542250),
(8, 'CSE301', '2026-S1', 'CONFIRMED', 'S1031', 1782210525047),
(9, 'CSE302', '2026-S1', 'CONFIRMED', 'S1031', 1782210590938),
(10, 'CSE301', '2026-S1', 'CONFIRMED', 'S9999', 1782210658060),
(12, 'CSE302', '2026-S1', 'CONFIRMED', 'S9999', 1782211353823),
(13, 'CSE310', '2026-S2', 'CONFIRMED', 'S2003', 1782211592807);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cached_students`
--
ALTER TABLE `cached_students`
  ADD PRIMARY KEY (`student_id`);

--
-- Indexes for table `course_capacities`
--
ALTER TABLE `course_capacities`
  ADD PRIMARY KEY (`course_code`);

--
-- Indexes for table `enrolments`
--
ALTER TABLE `enrolments`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `enrolments`
--
ALTER TABLE `enrolments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
