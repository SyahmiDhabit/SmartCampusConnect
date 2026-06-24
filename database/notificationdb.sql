-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2026 at 08:56 AM
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
-- Database: `notificationdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `event_id` varchar(255) DEFAULT NULL,
  `event_type` varchar(255) DEFAULT NULL,
  `payload` varchar(2000) DEFAULT NULL,
  `received_at` datetime(6) DEFAULT NULL,
  `routing_key` varchar(255) DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `event_id`, `event_type`, `payload`, `received_at`, `routing_key`, `source`) VALUES
(47, 'cb8b8c35-44b9-48f8-b637-aae2a8f2d6f7', 'STUDENT_CREATED', 'Profile student created for Fiona Cole', '2026-06-23 10:46:26.000000', 'student.created', 'student-profile-service'),
(48, '46c738df-dd0e-48b3-a760-f3844f94bbfd', 'ENROLMENT_CREATED', 'Student S2003 enrolled in CSE310 (2026-S2). Status: CONFIRMED', '2026-06-23 10:46:32.000000', 'enrolment.created', 'course-enrolment-service'),
(49, '969b3354-6ee7-466a-a6a5-c8325677646a', 'LIBRARY_LOAN_CREATED', 'Book Loaned: \'Domain-Driven Design\' (B106) to Student S2003', '2026-06-23 10:47:07.000000', 'library.loan.created', 'library-booking-service');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_ac78n2m06mq3l84owt8kudisn` (`event_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=50;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
