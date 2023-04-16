CREATE DATABASE  IF NOT EXISTS `exams_journal` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `exams_journal`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: exams_journal
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attends`
--

DROP TABLE IF EXISTS `attends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attends` (
  `studentId` int NOT NULL,
  `roundId` int NOT NULL,
  `mark` tinyint DEFAULT NULL,
  `evaluationStatus` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`studentId`,`roundId`),
  KEY `roundId_idx` (`roundId`) /*!80000 INVISIBLE */,
  CONSTRAINT `roundId` FOREIGN KEY (`roundId`) REFERENCES `round` (`roundId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `studentId` FOREIGN KEY (`studentId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `eval_stat_range_check` CHECK (((`evaluationStatus` >= 0) and (`evaluationStatus` < 5))),
  CONSTRAINT `mark_range_check` CHECK (((`mark` > 14) and (`mark` < 32)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attends`
--

LOCK TABLES `attends` WRITE;
/*!40000 ALTER TABLE `attends` DISABLE KEYS */;
INSERT INTO `attends` VALUES (2,4,18,3),(3,5,23,4);
/*!40000 ALTER TABLE `attends` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `attends_BEFORE_INSERT` BEFORE INSERT ON `attends` FOR EACH ROW BEGIN
	DECLARE user_role integer;
    
    SET user_role = (select `user`.`role` from `user` where `user`.`userId`= new.studentId);

	if (user_role != 0) then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. The given user is not a student';
    end if;
    
    if ( new.evaluationStatus = 0 and new.mark is not NULL ) or (new.mark is NULL and new.evaluationStatus != 0) then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. Evaluation status doesn`t match the given mark';
    end if;
    
    if ( new.evaluationStatus = 4 ) then
		if ( select `reportIsCreated` from `round` where `round`.`roundId` = new.roundId ) = false then
			SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. The report for the relative round wasn`t yet created';
		end if;
    end if;
    

END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `attends_BEFORE_UPDATE` BEFORE UPDATE ON `attends` FOR EACH ROW BEGIN
	DECLARE user_role integer;
    
    SET user_role = (select `user`.`role` from `user` where `user`.`userId`= new.studentId);

	if (user_role != 0) then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Update canceled. The given user is not a student';
    end if;
    
    if ( new.evaluationStatus = 0 and new.mark is not NULL ) or (new.mark is NULL and new.evaluationStatus != 0) then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Update canceled. Evaluation status doesn`t match the given mark';
    end if;
    
    if ( new.evaluationStatus = 4 ) then
		if ( select `reportIsCreated` from `round` where `round`.`roundId` = new.roundId ) = false then
			SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Update canceled. The report for the relative round wasn`t yet created';
		end if;
    end if;
    
    if new.mark != old.mark then
		if( new.evaluationStatus = 2 and old.evaluationStatus = 2 ) then
			SIGNAL SQLSTATE '45000' 
			SET MESSAGE_TEXT = 'Update canceled. You can`t change the mark in the published state';
		end if;
        
        if( new.evaluationStatus = 4 and old.evaluationStatus = 4 ) then
			SIGNAL SQLSTATE '45000' 
			SET MESSAGE_TEXT = 'Update canceled. You can`t change the mark, the report was already created';
		end if;
    end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `courseId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `profId` int NOT NULL,
  PRIMARY KEY (`courseId`),
  KEY `userId_idx` (`profId`) /*!80000 INVISIBLE */,
  CONSTRAINT `profId` FOREIGN KEY (`profId`) REFERENCES `user` (`userId`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'course1',1),(7,'name2',1);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `course_BEFORE_INSERT_TAUGHT_BY_PROF` BEFORE INSERT ON `course` FOR EACH ROW BEGIN
	DECLARE user_role integer;
    
    SET user_role = 
    ( select `role`
		from `user`
    where `userId` = new.profId
    );
    
	if user_role = '0' then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. Attribute \'profId\' does not belong to a user with professor role ( role = \'1\' )';
    end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `course_TAUGHT_BY_PROF` BEFORE UPDATE ON `course` FOR EACH ROW BEGIN
	DECLARE user_role integer;
    
    SET user_role = 
    ( select `role`
		from `user`
    where `userId` = new.profId
    );
    
	if user_role = '0' then
		SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. Attribute \'profId\' does not belong to a user with professor role ( role = \'1\' )';
    end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `round`
--

DROP TABLE IF EXISTS `round`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `round` (
  `roundId` int NOT NULL AUTO_INCREMENT,
  `courseId` int NOT NULL,
  `date` date NOT NULL,
  `reportCode` int DEFAULT NULL,
  `reportDateTime` datetime(1) DEFAULT NULL,
  `reportIsCreated` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`roundId`),
  UNIQUE KEY `reportCode_UNIQUE` (`reportCode`),
  KEY `courseId_idx` (`courseId`),
  CONSTRAINT `courseId` FOREIGN KEY (`courseId`) REFERENCES `course` (`courseId`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `round`
--

LOCK TABLES `round` WRITE;
/*!40000 ALTER TABLE `round` DISABLE KEYS */;
INSERT INTO `round` VALUES (1,1,'1000-01-01',2,'2023-04-14 01:53:46.0',1),(3,1,'1000-01-02',3,'2023-04-14 01:54:11.0',1),(4,1,'2023-04-15',4,'2023-04-15 17:19:17.0',1),(5,7,'2023-04-15',5,'2023-04-15 21:20:53.0',1);
/*!40000 ALTER TABLE `round` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `round_BEFORE_INSERT_REPORT_VALIDITY` BEFORE INSERT ON `round` FOR EACH ROW BEGIN
	DECLARE max_report_code integer;
    
    SET max_report_code = (select MAX(reportCode) from `round`);
	
    if new.reportIsCreated = true then
		if max_report_code is null then
			SET new.reportCode = 1;
		else
			SET new.reportCode = max_report_code + 1;
		end if;
        
        SET new.reportDateTime = NOW();
	else 
		if new.reportIsCreated = false and (new.reportCode is not null or new.reportDateTime is not null) then
			SIGNAL SQLSTATE '45000' 
				SET MESSAGE_TEXT = 'Insertion canceled. Invalid set of parameters for the report';
		end if;
    end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `round_BEFORE_UPDATE_REPORT_IMMUTABLE` BEFORE UPDATE ON `round` FOR EACH ROW BEGIN
	
	DECLARE max_report_code integer;
    
    if new.reportIsCreated = false and old.reportIsCreated = true then
		SIGNAL SQLSTATE '45000' 
			SET MESSAGE_TEXT = 'Update went wrong! You can`t change reportIsCreated attribute' ;
    end if;
    
    if new.reportCode != old.reportCode and old.reportCode is not null then
		SIGNAL SQLSTATE '45000' 
			SET MESSAGE_TEXT = 'Update went wrong! You can`t change reportCode attribute' ;
    end if;
    
    if new.reportDateTime != old.reportDateTime and old.reportDateTime is not null then
		SIGNAL SQLSTATE '45000' 
			SET MESSAGE_TEXT = 'Update went wrong! You can`t change reportDateTime attribute' ;
    end if;
    
    
    
    SET max_report_code = (select MAX(reportCode) from `round`);
	
    if new.reportIsCreated = true then
		if max_report_code is null then
			SET new.reportCode = 1;
		else
			SET new.reportCode = max_report_code + 1;
		end if;
        
        SET new.reportDateTime = NOW();
	else 
		if new.reportIsCreated = false and (new.reportCode is not null or new.reportDateTime is not null) then
			SIGNAL SQLSTATE '45000' 
				SET MESSAGE_TEXT = 'Insertion canceled. Invalid set of parameters for the report';
		end if;
    end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `password` varchar(120) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(60) NOT NULL,
  `role` bit(1) NOT NULL,
  `studyCourse` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'pass1','name1','sur1','1@email.com',_binary '',NULL),(2,'pass2','name2','sur2','2@email.com',_binary '\0','2'),(3,'pass3','name3','sur3','3@email.com',_binary '\0','3'),(5,'pass4','name4','sur4','4@email.com',_binary '',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `user_ROLE_AND_STUDYCOURSE` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
	if ( new.role = '0' and new.studyCourse is null ) then
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. User of type 0 must have a not null studyCourse';
	end if;
    if ( new.role = '1' and new.studyCourse is not null ) then
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Insertion canceled. User of type 1 must have null studyCourse';
	end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `user_BEFORE_UPDATE_ROLE_AND_STUDYCOURSE` BEFORE UPDATE ON `user` FOR EACH ROW BEGIN
	if ( new.role = 0 and new.studyCourse is null ) then
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Update canceled. User of type 0 must have a not null studyCourse';
	end if;
    if ( new.role = 1 and new.studyCourse is not null ) then
        SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Update canceled. User of type 1 must have null studyCourse';
	end if;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping events for database 'exams_journal'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-15 22:21:18
