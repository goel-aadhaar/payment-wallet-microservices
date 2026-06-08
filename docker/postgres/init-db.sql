-- Creates one database per service on first container initialisation.
-- The connecting role (POSTGRES_USER=payment) owns them all.
CREATE DATABASE userdb;
CREATE DATABASE walletdb;
CREATE DATABASE transactiondb;
CREATE DATABASE notificationdb;
CREATE DATABASE rewarddb;
