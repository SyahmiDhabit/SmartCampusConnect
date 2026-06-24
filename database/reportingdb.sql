-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 24, 2026 at 08:57 AM
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
-- Database: `reportingdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `processed_events`
--

CREATE TABLE `processed_events` (
  `event_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `processed_events`
--

INSERT INTO `processed_events` (`event_id`) VALUES
('016e784d-a90f-42c2-bca4-a5d24f645b4a'),
('0f6c6c41-fdc4-4384-8389-d224cdef3127'),
('2052ec8a-18dd-4688-a5f4-e027bc21558a'),
('287df135-0e47-47a2-b0c4-f3b81badb69a'),
('2c1c8151-6c55-4dfc-8d88-93e0ad7c5054'),
('31f56a86-04b7-48ee-b1b7-7374221fb9c6'),
('372e7b20-1c17-4028-9c06-62c2ea35bcfb'),
('37932d9e-1588-436e-8828-f4d43fc6a774'),
('41451a0d-a2d4-44d8-8fab-bd805b0c535d'),
('46026ecb-81b2-4d3b-8ec0-77f701e167c7'),
('46c738df-dd0e-48b3-a760-f3844f94bbfd'),
('4851c108-b1c2-43a0-acc4-e203dd55bd56'),
('6346ce44-d0ec-45ee-b24b-3e8754da989d'),
('6d0fe653-f84d-4d29-b8c3-ac007a63658f'),
('73c3a21e-7dba-4a13-9080-83a2ba5b1668'),
('7a66ba72-b71d-49af-a1b8-53cf9f7670b9'),
('8b69eddc-8ff0-493e-9173-51e3882b5546'),
('8bfefe0e-0f8f-4770-8e7d-95368e377330'),
('8c8f14b1-6714-49cb-9cd2-fb7fe7e65ec1'),
('969b3354-6ee7-466a-a6a5-c8325677646a'),
('98c0706e-d32b-4eed-9d22-41ce9636abdf'),
('9bd08301-2a06-47ff-9461-a12450cefc7e'),
('a60c10c9-b3d2-449c-ad10-276262ec4392'),
('b0df8e79-249c-403a-864f-283c981b9222'),
('b449d1c3-a509-4711-b29c-1788d52999f5'),
('babc1b61-eedb-4dd3-a6d1-4aea0429fe05'),
('c60575ec-4bfa-4ee3-8d00-f05451b89019'),
('c8da184a-8181-4972-899e-0364bc8b0e66'),
('d19262e9-4c3a-4cc0-b867-0ccf89dbaf72'),
('db1c29ec-5ac9-4522-b910-59cc201a8c1e'),
('fb994137-99d0-4171-8711-94e245b435e5');

-- --------------------------------------------------------

--
-- Table structure for table `programme_enrolment_stats`
--

CREATE TABLE `programme_enrolment_stats` (
  `programme` varchar(255) NOT NULL,
  `confirmed_count` int(11) NOT NULL,
  `enrolment_count` int(11) NOT NULL,
  `provisional_count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `programme_enrolment_stats`
--

INSERT INTO `programme_enrolment_stats` (`programme`, `confirmed_count`, `enrolment_count`, `provisional_count`) VALUES
('Computer Science', 7, 7, 0),
('Electrical Engineering', 1, 1, 0),
('Information Systems', 3, 3, 0),
('Mechanical Engineering', 1, 1, 0),
('Software Engineering', 1, 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `report_metrics`
--

CREATE TABLE `report_metrics` (
  `metric_key` varchar(255) NOT NULL,
  `metric_value` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `report_metrics`
--

INSERT INTO `report_metrics` (`metric_key`, `metric_value`) VALUES
('total_enrolments', 13),
('total_enrolment_drops', 2),
('total_library_loans', 7),
('total_room_bookings', 9);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `processed_events`
--
ALTER TABLE `processed_events`
  ADD PRIMARY KEY (`event_id`);

--
-- Indexes for table `programme_enrolment_stats`
--
ALTER TABLE `programme_enrolment_stats`
  ADD PRIMARY KEY (`programme`);

--
-- Indexes for table `report_metrics`
--
ALTER TABLE `report_metrics`
  ADD PRIMARY KEY (`metric_key`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
