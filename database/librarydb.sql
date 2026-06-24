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
-- Database: `librarydb`
--

-- --------------------------------------------------------

--
-- Table structure for table `book_loans`
--

CREATE TABLE `book_loans` (
  `id` bigint(20) NOT NULL,
  `book_id` varchar(255) DEFAULT NULL,
  `book_title` varchar(255) DEFAULT NULL,
  `loan_date` bigint(20) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `student_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_loans`
--

INSERT INTO `book_loans` (`id`, `book_id`, `book_title`, `loan_date`, `status`, `student_id`) VALUES
(1, 'B101', 'Introduction to Distributed Systems', 1782180647072, 'RETURNED', 'S1001'),
(2, 'B103', 'Design Patterns', 1782180979615, 'ACTIVE', 'S1004'),
(7, 'B117', 'Modern Operating Systems', 1782202596368, 'ACTIVE', 'S1002'),
(8, 'B104', 'Refactoring: Improving the Design of Existing Code', 1782203880833, 'ACTIVE', 'S1004'),
(9, 'B101', 'Introduction to Distributed Systems', 1782209299161, 'ACTIVE', 'S1003'),
(10, 'B102', 'Clean Architecture', 1782210720079, 'ACTIVE', 'S1001'),
(11, 'B105', 'The Pragmatic Programmer', 1782211402883, 'RETURNED', 'S2001'),
(12, 'B105', 'The Pragmatic Programmer', 1782211465063, 'ACTIVE', 'S2001'),
(13, 'B106', 'Domain-Driven Design', 1782211627845, 'ACTIVE', 'S2003');

-- --------------------------------------------------------

--
-- Table structure for table `room_bookings`
--

CREATE TABLE `room_bookings` (
  `id` bigint(20) NOT NULL,
  `booking_date` varchar(255) DEFAULT NULL,
  `room_id` varchar(255) DEFAULT NULL,
  `room_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `student_id` varchar(255) DEFAULT NULL,
  `time_slot` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `room_bookings`
--

INSERT INTO `room_bookings` (`id`, `booking_date`, `room_id`, `room_name`, `status`, `student_id`, `time_slot`) VALUES
(1, '2026-06-23', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1004', '09:00 - 11:00'),
(2, '2026-06-25', 'R103', 'Project Room C', 'CONFIRMED', 'S1004', '13:00 - 15:00'),
(3, '2026-06-25', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1004', '09:00 - 11:00'),
(4, '2026-06-26', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1001', '09:00 - 11:00'),
(5, '2026-06-22', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1002', '09:00 - 11:00'),
(6, '2026-06-23', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1002', '11:00 - 13:00'),
(7, '2026-06-23', 'R102', 'Discussion Room B', 'CONFIRMED', 'S1002', '15:00 - 17:00'),
(8, '2026-06-23', 'R102', 'Discussion Room B', 'CONFIRMED', 'S9999', '13:00 - 15:00'),
(9, '2026-06-25', 'R101', 'Discussion Room A', 'CONFIRMED', 'S1001', '09:00 - 11:00'),
(10, '2026-06-25', 'R101', 'Discussion Room A', 'CONFIRMED', 'S1001', '10:00 - 12:00'),
(11, '2026-07-01', 'R102', 'Discussion Room B', 'CONFIRMED', 'S2001', '13:00 - 15:00'),
(12, '2026-07-02', 'R103', 'General Study Room', 'CONFIRMED', 'S2001', '09:00 - 11:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `book_loans`
--
ALTER TABLE `book_loans`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `room_bookings`
--
ALTER TABLE `room_bookings`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `book_loans`
--
ALTER TABLE `book_loans`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `room_bookings`
--
ALTER TABLE `room_bookings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
