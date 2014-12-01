use Q2;

alter database Q2 character set utf8mb4;
set names utf8mb4;

DROP TABLE IF EXISTS tweets_q3;

CREATE TABLE tweets_q3 (
  origin int unsigned,
  retweet MEDIUMTEXT,
  PRIMARY KEY (origin)
);