-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: localhost    Database: social_network
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `post_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh4c7lvsc298whoyd4w9ta25cr` (`post_id`),
  KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`),
  KEY `FKlri30okf66phtcgbe5pok7cc0` (`parent_id`),
  CONSTRAINT `FK8omq0tc18jd43bu5tjh6jvraq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKh4c7lvsc298whoyd4w9ta25cr` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FKlri30okf66phtcgbe5pok7cc0` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendship`
--

DROP TABLE IF EXISTS `friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendship` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('PENDING','ACCEPTED','REJECTED') DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5kuaxbwscap950h164t23sgby` (`receiver_id`),
  KEY `FKljirh3vtab8kelivnofrfyueo` (`sender_id`),
  CONSTRAINT `FK5kuaxbwscap950h164t23sgby` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKljirh3vtab8kelivnofrfyueo` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendship`
--

LOCK TABLES `friendship` WRITE;
/*!40000 ALTER TABLE `friendship` DISABLE KEYS */;
INSERT INTO `friendship` VALUES (1,NULL,'ACCEPTED',2,1),(2,NULL,'PENDING',2,3),(3,NULL,'ACCEPTED',1,3),(4,NULL,'ACCEPTED',1,4);
/*!40000 ALTER TABLE `friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `post_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2jovqhqo324cubdomovkex03b` (`user_id`,`post_id`),
  KEY `FKry8tnr4x2vwemv2bb0h5hyl0x` (`post_id`),
  CONSTRAINT `FKnvx9seeqqyy71bij291pwiwrg` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKry8tnr4x2vwemv2bb0h5hyl0x` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (11,NULL,10,3),(14,NULL,5,3),(15,NULL,8,2),(18,NULL,7,2),(20,NULL,5,1);
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `timestamp` datetime(6) DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt05r0b6n0iis8u7dfna4xdh73` (`receiver_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt05r0b6n0iis8u7dfna4xdh73` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (3,'Tôi đây','2025-05-31 21:12:38.856145',2,1),(23,'1','2025-06-02 22:17:21.521828',2,1),(24,'3','2025-06-02 22:17:23.384523',1,2),(25,'hello','2025-06-02 22:17:54.530252',2,1),(26,'hello con cặc','2025-06-02 22:18:01.048681',1,2),(52,'biết anh hiệu t không','2025-06-07 10:54:59.492344',2,1),(53,'biết','2025-06-07 10:55:02.381054',1,2),(54,'biết quả bòng không\\','2025-06-07 10:55:07.606633',1,2),(55,'biết','2025-06-07 10:55:11.135255',2,1),(56,'ok','2025-06-07 10:55:12.736000',2,1),(57,'Chào Nguyễn Văn Nhót Mình rất vui khi chúng ta là bạn.','2025-06-08 16:43:43.000000',3,1),(58,'hello','2025-06-08 16:46:47.507565',3,1),(59,'hi','2025-06-08 16:47:11.777469',1,3),(60,'Chào Nguyễn Quang Anh! Mình rất vui khi chúng ta là bạn.','2025-06-08 21:52:16.000000',1,4),(61,'ok','2025-06-09 22:36:16.192799',4,1);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `author_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6xvn0811tkyo3nfjk2xvqx6ns` (`author_id`),
  CONSTRAINT `FK6xvn0811tkyo3nfjk2xvqx6ns` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (5,'1','2025-06-05 21:41:44.575769',NULL,NULL,1),(6,'12121','2025-06-05 21:44:02.042998',NULL,NULL,1),(7,'1','2025-06-05 21:45:44.066955','https://res.cloudinary.com/ddmlizo29/image/upload/v1749134746/stimvatikzdg9acyvvj0.jpg',NULL,1),(8,'Hello','2025-06-06 12:37:00.085878',NULL,NULL,1),(9,'12','2025-06-07 10:45:40.174453',NULL,NULL,2),(10,'Anh hiệu ơi e xin chùm vải','2025-06-07 10:52:09.146673',NULL,NULL,2);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `last_used_at` datetime(6) DEFAULT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  `refresh_token_created_at` datetime(6) NOT NULL,
  `refresh_token_expires_at` datetime(6) DEFAULT NULL,
  `refresh_token_issued_at` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `token_created_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj8rfw4x0wjjyibfqq566j4qng` (`user_id`),
  CONSTRAINT `FKj8rfw4x0wjjyibfqq566j4qng` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (18,'2025-06-09 23:26:09.707947','a92dadad-31f7-447d-a16b-747573d2df59','2025-06-09 22:35:56.453353','2025-07-09 22:35:56.453353','2025-06-09 22:35:56.453353','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJucWEiLCJpYXQiOjE3NDk0ODYzNjksImV4cCI6MTc0OTQ4NzI2OX0.soOS86Pau6mRh1kqKzh7BeWa3CaSRFaSAoRiNwIHBA8','2025-06-09 23:26:09.707947',1),(19,'2025-06-09 22:55:13.622605','c1b80728-9282-4fbf-9a5e-229ee5623255','2025-06-09 22:55:13.622605','2025-07-09 22:55:13.622605','2025-06-09 22:55:13.622605','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJucWEiLCJpYXQiOjE3NDk0ODQ1MTMsImV4cCI6MTc0OTQ4NTQxM30.x-oMqcISxlQi-wJKT75jDyM1YtKyn5LSGCFxWm3gPnQ','2025-06-09 22:55:13.622605',1),(20,'2025-06-09 23:25:51.284529','4c912413-2bc5-47de-8eb9-dcc538eaaea5','2025-06-09 23:10:28.547445','2025-07-09 23:10:28.547445','2025-06-09 23:10:28.547445','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJucWEiLCJpYXQiOjE3NDk0ODYzNTEsImV4cCI6MTc0OTQ4NzI1MX0.iQcnhf1OzYZzXkubjwSPgNsNVwsYjvewyqUJ9PRx_9I','2025-06-09 23:25:51.284529',1);
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_followers`
--

DROP TABLE IF EXISTS `user_followers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_followers` (
  `user_id` bigint NOT NULL,
  `follower_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`follower_id`),
  KEY `FKsauvjgnbgys3gbeharkga2omh` (`follower_id`),
  CONSTRAINT `FKox7c2m7d9qhhpu45d83luq19q` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKsauvjgnbgys3gbeharkga2omh` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_followers`
--

LOCK TABLES `user_followers` WRITE;
/*!40000 ALTER TABLE `user_followers` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bio` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `facebook_id` varchar(255) DEFAULT NULL,
  `github_id` varchar(255) DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jmubronqnn4q0cwe2egqsgvnl` (`facebook_id`),
  UNIQUE KEY `UK_g9s8emobrgjmob2ty2va0l354` (`github_id`),
  UNIQUE KEY `UK_ovh8xmu9ac27t18m56gri58i1` (`google_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,'2025-05-29 22:04:06.700917','nguyenquanganh07042004@gmail.com','Nguyễn','Quang Anh','$2a$10$3C0LpelIScaJzWBQ9h0Un.znPpsotADnAIcul2Mk2Pm6ir9bWJwGC',NULL,'USER',NULL,'nqa','Nguyễn Quang Anh',NULL,NULL,NULL),(2,NULL,'2025-05-30 13:15:23.633071','dangcapnr2@gmail.com','Nguyễn','Văn A','$2a$10$yASfB/9ljfYzB4FJtei0AuOtII7s6txbQeAAu7p6C7Txw5wjtaQ1K',NULL,'USER',NULL,'nqa123','Nguyễn Văn A',NULL,NULL,NULL),(3,NULL,'2025-06-08 11:41:53.714446','test@123.com','Nguyễn','Văn Nhót','$2a$10$TvzbQLghvcR6ICJxzU2dtuUPGxzJLvXKYzUZLX6vp8Xh6uzwc9vS2',NULL,'USER',NULL,'nqa12345','Nguyễn Văn Nhót',NULL,NULL,NULL),(4,NULL,NULL,'huu336192@gmail.com','Huu','H','5a763d71-473a-4474-beca-ff05bdbd61e2',NULL,'USER',NULL,'huu336192@gmail.com','Huu H','',NULL,'huu336192@gmail.com');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-16 22:00:30
