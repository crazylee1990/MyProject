USE Q2;

alter database Q2 character set utf8mb4;
set names utf8mb4;

DROP TABLE IF EXISTS `tweets`;


CREATE TABLE tweets(
  user_id int unsigned,
  date_time varchar(20),
  tweet_id text,
  text text,
  score int
) ENGINE=MyISAM;

load data infile 'q2_data' into table tweets fields terminated by '\t' lines terminated by '\n';


CREATE INDEX idx_user_time ON tweets (user_id, date_time);