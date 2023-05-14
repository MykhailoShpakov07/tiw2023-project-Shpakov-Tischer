-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: exams_journal
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
INSERT INTO `attends` (`studentId`, `roundId`, `mark`, `evaluationStatus`) VALUES (8,18,NULL,0),(8,19,NULL,0),(8,24,NULL,0),(8,28,NULL,0),(9,19,NULL,0),(9,28,NULL,0),(10,18,NULL,0),(10,24,NULL,0),(10,25,NULL,0),(10,28,NULL,0),(10,29,NULL,0),(13,18,NULL,0),(13,20,NULL,0),(13,22,NULL,0),(13,23,NULL,0),(13,24,NULL,0),(13,28,NULL,0),(13,31,NULL,0),(14,18,NULL,0),(14,19,NULL,0),(14,22,NULL,0),(14,23,NULL,0),(14,30,NULL,0),(15,18,NULL,0),(15,19,NULL,0),(15,20,NULL,0),(15,23,NULL,0),(15,29,NULL,0);
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
/*!50032 DROP TRIGGER IF EXISTS attends_BEFORE_INSERT */;
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
/*!50032 DROP TRIGGER IF EXISTS attends_BEFORE_UPDATE */;
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` (`courseId`, `name`, `profId`) VALUES (8,'Analisi I',7),(9,'Informazione e Stima',11),(10,'Fondamenti di Informatica',12),(11,'Geometria e Algebra Lineare',7),(13,'Probabilità e Statistica',11),(14,'Fondamenti di Elettronica',12);
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
/*!50032 DROP TRIGGER IF EXISTS course_BEFORE_INSERT_TAUGHT_BY_PROF */;
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
/*!50032 DROP TRIGGER IF EXISTS course_TAUGHT_BY_PROF */;
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `round`
--

LOCK TABLES `round` WRITE;
/*!40000 ALTER TABLE `round` DISABLE KEYS */;
INSERT INTO `round` (`roundId`, `courseId`, `date`, `reportCode`, `reportDateTime`, `reportIsCreated`) VALUES (18,8,'2023-05-21',NULL,NULL,0),(19,8,'2023-06-11',NULL,NULL,0),(20,9,'2023-05-18',NULL,NULL,0),(21,9,'2023-06-08',NULL,NULL,0),(22,10,'2023-05-24',NULL,NULL,0),(23,10,'2023-06-05',NULL,NULL,0),(24,11,'2023-05-26',NULL,NULL,0),(25,11,'2023-06-07',NULL,NULL,0),(28,13,'2023-05-31',NULL,NULL,0),(29,13,'2023-06-12',NULL,NULL,0),(30,14,'2023-06-01',NULL,NULL,0),(31,14,'2023-06-19',NULL,NULL,0);
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
/*!50032 DROP TRIGGER IF EXISTS round_BEFORE_INSERT_REPORT_VALIDITY */;
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
/*!50032 DROP TRIGGER IF EXISTS round_BEFORE_UPDATE_REPORT_IMMUTABLE */;
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
-- Temporary view structure for view `samecoursenextdate`
--

DROP TABLE IF EXISTS `samecoursenextdate`;
/*!50001 DROP VIEW IF EXISTS `samecoursenextdate`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `samecoursenextdate` AS SELECT 
 1 AS `studentId`,
 1 AS `roundId`*/;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`userId`, `password`, `name`, `surname`, `email`, `role`, `studyCourse`) VALUES (7,'pass1','Mario','Rossi','mario.rossi@email.com',_binary '',NULL),(8,'pass2','Riccardo','Romito','riccardo.romito@email.com',_binary '\0','Ingegneria Biomedica'),(9,'pass3','Marco','Zanotti','marco.zanotti@email.com',_binary '\0','Ingegneria Biomedica'),(10,'pass4','Chiara','Casali','chiara.casali@email.com',_binary '\0','Ingegneria Fisica'),(11,'pass5','Giovanni','Bianchi','giovanni.bianchi@email.com',_binary '',NULL),(12,'pass6','Francesca','Costa','francesca.costa@email.com',_binary '',NULL),(13,'pass7','Matteo','Gallo','matteo.gallo@email.com',_binary '\0','Ingegneria Informatica'),(14,'pass8','Alessandro','Pignati','alessandro.pignati@email.com',_binary '\0','Ingegneria Gestionale'),(15,'pass9','Carlo','Ferrari','carlo.ferrari@email.com',_binary '\0','Ingegneria Informatica');
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
/*!50032 DROP TRIGGER IF EXISTS user_ROLE_AND_STUDYCOURSE */;
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
/*!50032 DROP TRIGGER IF EXISTS user_BEFORE_UPDATE_ROLE_AND_STUDYCOURSE */;
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
-- Final view structure for view `samecoursenextdate`
--

/*!50001 DROP VIEW IF EXISTS `samecoursenextdate`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `samecoursenextdate` AS select `attends`.`studentId` AS `studentId`,`subquery`.`roundId` AS `roundId` from ((select `round`.`roundId` AS `roundId` from `round` where ((`round`.`courseId` = (select `round`.`courseId` from `round` where (`round`.`roundId` = 10))) and (`round`.`date` > (select `round`.`date` from `round` where (`round`.`roundId` = 10))))) `subquery` join `attends` on((`subquery`.`roundId` = `attends`.`roundId`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-13 22:37:34
